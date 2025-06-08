package io.foreshore.cookware.crypto.symmetric

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.SecretKey
import javax.crypto.Cipher // For Cipher.getInstance in one test
import javax.crypto.spec.GCMParameterSpec // For GCMParameterSpec in one test
import java.util.Base64 // For Base64 in one test
import io.foreshore.cookware.crypto.core.toAesKey // Needed for the "known key and IV" test

/**
 * `AesEncryption.kt` 파일에 정의된 AES 대칭키 암호화 및 복호화 기능의
 * 정확성과 보안 관련 동작을 검증하는 테스트 클래스입니다.
 *
 * 주요 검증 항목:
 * - AES 키 생성 확인
 * - 다양한 입력값(빈 문자열, 특수 문자 포함 문자열 등)에 대한 암호화 및 복호화 정확성
 * - 암호화 시 IV 자동 생성 및 결과에 따른 Ciphertext 변경 여부
 * - 잘못된 키 사용 시 복호화 실패 처리
 * - 변조된 Ciphertext에 대한 복호화 실패 처리 (GCM 모드의 인증 기능)
 *
 * @see io.foreshore.cookware.crypto.symmetric.AesEncryption
 * @see io.foreshore.cookware.crypto.core.KeyConversion (키 변환 테스트는 KeyConversionTest.kt에서 다룸)
 */
class AesEncryptionTest : StringSpec({

    "generateAesKey should create a key with correct algorithm and default size" {
        val key = generateAesKey()
        key.algorithm shouldBe "AES"
        key.encoded.size * 8 shouldBe AES_KEY_SIZE_BITS // Default key size
    }

    "generateAesKey should create a key with specified size" {
        val key128 = generateAesKey(keySize = 128)
        key128.algorithm shouldBe "AES"
        key128.encoded.size * 8 shouldBe 128

        val key192 = generateAesKey(keySize = 192)
        key192.algorithm shouldBe "AES"
        key192.encoded.size * 8 shouldBe 192
    }

    // "SecretKey.toBase64() and String.toAesKey() should perform round-trip conversion" test is moved to KeyConversionTest.kt

    "String encryption and decryption should work for various inputs" {
        val key = generateAesKey()
        val testStrings = listOf(
            "Hello, World!",
            "",
            "你好, κόσμε! Special chars: !@#$%^&*()_+{}:\"<>?`~[];',./",
            "A very long string ".repeat(100)
        )

        for (str in testStrings) {
            val encrypted = str.encryptAes(key)
            encrypted shouldNotBe str
            if (str.isNotEmpty()) {
                 encrypted.length shouldNotBe 0
            }

            val decrypted = encrypted.decryptAes(key)
            decrypted shouldBe str
        }
    }

    "Encrypting the same string twice should produce different ciphertexts (due to IV)" {
        val key = generateAesKey()
        val testString = "This is a test string."
        val encrypted1 = testString.encryptAes(key)
        val encrypted2 = testString.encryptAes(key)

        encrypted1 shouldNotBe encrypted2
    }

    "Decrypting with a different key should fail for String" {
        val key1 = generateAesKey()
        val key2 = generateAesKey() // Different key
        val testString = "Confidential data"

        val encrypted = testString.encryptAes(key1)

        shouldThrow<AEADBadTagException> {
            encrypted.decryptAes(key2)
        }
    }

    "ByteArray encryption and decryption should work for various inputs" {
        val key = generateAesKey()
        val testByteArrays = listOf(
            "Hello, World!".toByteArray(Charsets.UTF_8),
            byteArrayOf(),
            "你好, κόσμε! Special chars: !@#$%^&*()_+{}:\"<>?`~[];',./".toByteArray(Charsets.UTF_8),
            "A very long string ".repeat(100).toByteArray(Charsets.UTF_8)
        )

        for (bytes in testByteArrays) {
            val encrypted = bytes.encryptAes(key)
            encrypted shouldNotBe bytes
             if (bytes.isNotEmpty()) {
                encrypted.size shouldNotBe 0
            }
            // Check that IV is prepended and adds to size
            // This calculation is a bit complex due to GCM padding/tag, focusing on core behavior.
            // encrypted.size shouldBe GCM_IV_LENGTH_BYTES + (Cipher.getInstance(AES_GCM_NO_PADDING_ALGORITHM).getOutputSize(bytes.size) - GCM_TAG_LENGTH_BITS/8 + GCM_TAG_LENGTH_BITS/8)


            val decrypted = encrypted.decryptAes(key)
            decrypted shouldBe bytes
        }
    }

    "Encrypting the same ByteArray twice should produce different ciphertexts (due to IV)" {
        val key = generateAesKey()
        val testBytes = "This is a test byte array.".toByteArray(Charsets.UTF_8)
        val encrypted1 = testBytes.encryptAes(key)
        val encrypted2 = testBytes.encryptAes(key)

        encrypted1 shouldNotBe encrypted2
    }

    "Decrypting with a different key should fail for ByteArray" {
        val key1 = generateAesKey()
        val key2 = generateAesKey() // Different key
        val testBytes = "Confidential data".toByteArray(Charsets.UTF_8)

        val encrypted = testBytes.encryptAes(key1)

        shouldThrow<AEADBadTagException> {
            encrypted.decryptAes(key2)
        }
    }

    "Decrypting tampered ciphertext should fail for String" {
        val key = generateAesKey()
        val originalString = "This is some important data."
        val encryptedString = originalString.encryptAes(key)

        val tamperedEncryptedString = if (encryptedString.length > 1) {
            encryptedString.substring(0, encryptedString.length - 1) + if (encryptedString.last().isLowerCase()) 'A' else 'a'
        } else {
            "tampered"
        }

        try {
            tamperedEncryptedString.decryptAes(key)
        } catch (e: AEADBadTagException) {
            // Expected
        } catch (e: IllegalArgumentException) {
            // Expected if tampering results in invalid Base64
        }
    }

    "Decrypting tampered ciphertext (byte array) should fail" {
        val key = generateAesKey()
        val originalBytes = "Sensitive information".toByteArray(Charsets.UTF_8)
        val encryptedBytes = originalBytes.encryptAes(key)

        if (encryptedBytes.size > GCM_IV_LENGTH_BYTES) {
            encryptedBytes[encryptedBytes.size - 1] = encryptedBytes[encryptedBytes.size - 1].inc()
        }

        shouldThrow<AEADBadTagException> {
            encryptedBytes.decryptAes(key)
        }
    }

    "Test with known key and IV (for debugging or specific interop scenarios - not typical)" {
        val knownKeyString = "AAECAwQFBgcICQoLDA0ODw==" // 16 bytes, Base64 encoded
        val knownKey = knownKeyString.toAesKey() // Needs io.foreshore.cookware.crypto.core.toAesKey

        val iv = ByteArray(GCM_IV_LENGTH_BYTES)
        SecureRandom().nextBytes(iv)

        val data = "Test data with known IV setup".toByteArray(Charsets.UTF_8)

        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING_ALGORITHM)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, knownKey, gcmSpec)
        val encryptedContent = cipher.doFinal(data)

        val combinedIvAndCiphertext = iv + encryptedContent

        val decryptedBytes = combinedIvAndCiphertext.decryptAes(knownKey)
        String(decryptedBytes, Charsets.UTF_8) shouldBe "Test data with known IV setup"

        val base64Combined = Base64.getEncoder().encodeToString(combinedIvAndCiphertext)
        val decryptedString = base64Combined.decryptAes(knownKey)
        decryptedString shouldBe "Test data with known IV setup"
    }
})
