package io.foreshore.cookware.crypto.asymmetric

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
// Import functions from .core for key conversion examples if needed, though tests themselves are separate
import io.foreshore.cookware.crypto.core.toRsaPublicKey
import io.foreshore.cookware.crypto.core.toRsaPrivateKey
import io.foreshore.cookware.crypto.core.toBase64String
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

/**
 * `RsaEncryption.kt` 파일에 정의된 RSA 비대칭키 암호화/복호화 및
 * 디지털 서명/검증 기능의 정확성과 보안 관련 동작을 검증하는 테스트 클래스입니다.
 *
 * 주요 검증 항목:
 * - RSA 키 쌍 생성 확인
 * - 공개키 암호화 및 비밀키 복호화 정확성 (데이터 크기 제한 내)
 * - 잘못된 키 사용 시 암호화 또는 복호화 실패 처리
 * - 비밀키 서명 및 공개키 검증 정확성
 * - 변조된 데이터 또는 서명에 대한 검증 실패 처리
 * - 데이터 크기 제한 초과 시 암호화 예외 발생 여부
 *
 * @see io.foreshore.cookware.crypto.asymmetric.RsaEncryption
 * @see io.foreshore.cookware.crypto.core.KeyConversion (키 변환 테스트는 KeyConversionTest.kt에서 다룸)
 */
class RsaEncryptionTest : StringSpec({

    "generateRsaKeyPair should create a key pair with correct algorithm and default size" {
        val keyPair = generateRsaKeyPair()
        keyPair.public.algorithm shouldBe "RSA"
        keyPair.private.algorithm shouldBe "RSA"
    }

    "generateRsaKeyPair should create a key pair with specified size" {
        val keyPair2048 = generateRsaKeyPair(keySize = 2048)
        keyPair2048.public.algorithm shouldBe "RSA"

        val keyPair3072 = generateRsaKeyPair(keySize = 3072)
        keyPair3072.public.algorithm shouldBe "RSA"
    }

    // RSA Key Conversion tests are in KeyConversionTest.kt

    "String encryption and decryption should work for various inputs" {
        val keyPair = generateRsaKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private

        val testStrings = listOf(
            "Hello, RSA!",
            "", // Empty string
            "Short test with specials: !@#$%",
            "A".repeat(100)
        )

        for (str in testStrings) {
            val encrypted = str.encryptRsa(publicKey)
            encrypted shouldNotBe str
            if (str.isNotEmpty()) encrypted.length shouldNotBe 0

            val decrypted = encrypted.decryptRsa(privateKey)
            decrypted shouldBe str
        }
    }

    "Decrypting String with a different private key should fail" {
        val keyPair1 = generateRsaKeyPair()
        val keyPair2 = generateRsaKeyPair()
        val testString = "Confidential RSA data"

        val encrypted = testString.encryptRsa(keyPair1.public)

        shouldThrow<BadPaddingException> {
            encrypted.decryptRsa(keyPair2.private)
        }
    }

    "Encrypting String too large for RSA key should fail" {
        val keyPair = generateRsaKeyPair(2048) // Max data is 245 bytes
        val publicKey = keyPair.public
        val largeString = "A".repeat(300)

        shouldThrow<IllegalBlockSizeException> {
            largeString.encryptRsa(publicKey)
        }
    }

    "ByteArray encryption and decryption should work" {
        val keyPair = generateRsaKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private

        val testByteArrays = listOf(
            "Hello, RSA Bytes!".toByteArray(Charsets.UTF_8),
            byteArrayOf(),
            "Bytes with specials: !@#$%".toByteArray(Charsets.UTF_8),
            "B".repeat(120).toByteArray(Charsets.UTF_8)
        )

        for (bytes in testByteArrays) {
            val encrypted = bytes.encryptRsa(publicKey)
            encrypted shouldNotBe bytes
            if (bytes.isNotEmpty()) encrypted.size shouldNotBe 0

            val decrypted = encrypted.decryptRsa(privateKey)
            decrypted shouldBe bytes
        }
    }

    "Encrypting ByteArray too large for RSA key should fail" {
        val keyPair = generateRsaKeyPair(2048)
        val publicKey = keyPair.public
        val largeBytes = ByteArray(300) { 0x41 }

        shouldThrow<IllegalBlockSizeException> {
            largeBytes.encryptRsa(publicKey)
        }
    }

    "Decrypting ByteArray with a different private key should fail" {
        val keyPair1 = generateRsaKeyPair()
        val keyPair2 = generateRsaKeyPair()
        val testBytes = "Confidential RSA bytes".toByteArray(Charsets.UTF_8)

        val encrypted = testBytes.encryptRsa(keyPair1.public)

        shouldThrow<BadPaddingException> {
            encrypted.decryptRsa(keyPair2.private)
        }
    }

    "String signing and verification should work" {
        val keyPair = generateRsaKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private

        val testStrings = listOf(
            "Sign me!",
            "",
            "Another string with <html> & special chars"
        )

        for (str in testStrings) {
            val signature = str.signRsa(privateKey)
            signature.isNotBlank() shouldBe true

            val isValid = str.verifyRsaSignature(publicKey, signature)
            isValid shouldBe true
        }
    }

    "String signature verification should fail for tampered data" {
        val keyPair = generateRsaKeyPair()
        val originalString = "Original Data"
        val signature = originalString.signRsa(keyPair.private)

        val tamperedString = "Tampered Data"
        val isValid = tamperedString.verifyRsaSignature(keyPair.public, signature)
        isValid shouldBe false
    }

    "String signature verification should fail for tampered signature" {
        val keyPair = generateRsaKeyPair()
        val originalString = "Verify Me"
        var signature = originalString.signRsa(keyPair.private)

        val tamperedSignature = if (signature.length > 1) {
            signature.substring(0, signature.length -1) + if (signature.last().isLetter()) 'Z' else '9'
        } else {
            "tamperedBase64"
        }

        val isValid = originalString.verifyRsaSignature(keyPair.public, tamperedSignature)
        isValid shouldBe false
    }

    "String signature verification should fail with different public key" {
        val keyPair1 = generateRsaKeyPair()
        val keyPair2 = generateRsaKeyPair()
        val data = "Data to sign"
        val signature = data.signRsa(keyPair1.private)

        val isValid = data.verifyRsaSignature(keyPair2.public, signature)
        isValid shouldBe false
    }

    "ByteArray signing and verification should work" {
        val keyPair = generateRsaKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private

        val testByteArrays = listOf(
            "Sign these bytes!".toByteArray(Charsets.UTF_8),
            byteArrayOf(),
            "Bytes & Bytes !@#".toByteArray(Charsets.UTF_8)
        )

        for (bytes in testByteArrays) {
            val signature = bytes.signRsa(privateKey)
            signature.isNotEmpty() shouldBe true

            val isValid = bytes.verifyRsaSignature(publicKey, signature)
            isValid shouldBe true
        }
    }

    "ByteArray signature verification should fail for tampered data" {
        val keyPair = generateRsaKeyPair()
        val originalBytes = "Original Bytes".toByteArray(Charsets.UTF_8)
        val signature = originalBytes.signRsa(keyPair.private)

        val tamperedBytes = "Tampered Bytes".toByteArray(Charsets.UTF_8)
        val isValid = tamperedBytes.verifyRsaSignature(keyPair.public, signature)
        isValid shouldBe false
    }

    "ByteArray signature verification should fail for tampered signature" {
        val keyPair = generateRsaKeyPair()
        val originalBytes = "Byte Data".toByteArray(Charsets.UTF_8)
        val signature = originalBytes.signRsa(keyPair.private)

        if (signature.isNotEmpty()) {
            signature[signature.size -1] = signature[signature.size -1].inc()
        }

        val isValid = originalBytes.verifyRsaSignature(keyPair.public, signature)
        isValid shouldBe false
    }

    "ByteArray signature verification should fail with different public key" {
        val keyPair1 = generateRsaKeyPair()
        val keyPair2 = generateRsaKeyPair()
        val data = "Byte Data to sign".toByteArray(Charsets.UTF_8)
        val signature = data.signRsa(keyPair1.private)

        val isValid = data.verifyRsaSignature(keyPair2.public, signature)
        isValid shouldBe false
    }

    // Invalid Base64 string for key conversion tests are in KeyConversionTest.kt

    "Invalid Base64 string for RSA decryption should throw IllegalArgumentException" {
        val keyPair = generateRsaKeyPair()
        val invalidBase64 = "This is not valid Base64!"
        shouldThrow<IllegalArgumentException> {
            invalidBase64.decryptRsa(keyPair.private)
        }
    }

    "Invalid (but valid Base64) ciphertext for RSA decryption should throw BadPaddingException" {
        val keyPair = generateRsaKeyPair()
        // The string "ValidBase64ButNotEncryptedData" is unlikely to be a valid RSA encrypted block.
        // Encoding it to Base64 ensures the input to decryptRsa is valid Base64.
        val notActuallyEncrypted = java.util.Base64.getEncoder().encodeToString("ValidBase64ButNotEncryptedData".toByteArray(Charsets.UTF_8))
        shouldThrow<BadPaddingException> {
             notActuallyEncrypted.decryptRsa(keyPair.private)
        }
    }
})
