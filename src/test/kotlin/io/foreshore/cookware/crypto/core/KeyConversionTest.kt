package io.foreshore.cookware.crypto.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.foreshore.cookware.crypto.symmetric.generateAesKey // For AES key generation
import io.foreshore.cookware.crypto.asymmetric.generateRsaKeyPair // For RSA key generation

/**
 * `KeyConversion.kt` 파일에 정의된 암호화 키(`SecretKey`, `PublicKey`, `PrivateKey`)와
 * Base64 인코딩된 문자열 간의 상호 변환 기능의 정확성을 검증하는 테스트 클래스입니다.
 *
 * 주요 검증 항목:
 * - AES 비밀키와 Base64 문자열 간의 양방향 변환 정확성
 * - RSA 공개키와 Base64 문자열 간의 양방향 변환 정확성
 * - RSA 비밀키와 Base64 문자열 간의 양방향 변환 정확성
 * - 잘못된 형식의 Base64 문자열 입력 시 예외 발생 여부
 *
 * @see io.foreshore.cookware.crypto.core.KeyConversion
 * @see io.foreshore.cookware.crypto.symmetric.AesEncryption (AES 키 생성 관련)
 * @see io.foreshore.cookware.crypto.asymmetric.RsaEncryption (RSA 키 쌍 생성 관련)
 */
class KeyConversionTest : StringSpec({

    // AES Key Conversion Tests
    "SecretKey.toBase64String() and String.toAesKey() should perform round-trip conversion for AES keys" {
        val originalKey = generateAesKey()
        val base64Key = originalKey.toBase64String() // Uses the core function
        val reconstructedKey = base64Key.toAesKey()  // Uses the core function

        reconstructedKey.algorithm shouldBe originalKey.algorithm
        reconstructedKey.encoded shouldBe originalKey.encoded
    }

    // RSA Key Conversion Tests
    "PublicKey.toBase64String() and String.toRsaPublicKey() should perform round-trip conversion for RSA public keys" {
        val keyPair = generateRsaKeyPair()
        val originalPublicKey = keyPair.public
        val base64PublicKey = originalPublicKey.toBase64String() // Uses the core function
        val reconstructedPublicKey = base64PublicKey.toRsaPublicKey() // Uses the core function

        reconstructedPublicKey.encoded shouldBe originalPublicKey.encoded
        reconstructedPublicKey.algorithm shouldBe "RSA"
    }

    "PrivateKey.toBase64String() and String.toRsaPrivateKey() should perform round-trip conversion for RSA private keys" {
        val keyPair = generateRsaKeyPair()
        val originalPrivateKey = keyPair.private
        val base64PrivateKey = originalPrivateKey.toBase64String() // Uses the core function
        val reconstructedPrivateKey = base64PrivateKey.toRsaPrivateKey() // Uses the core function

        reconstructedPrivateKey.encoded shouldBe originalPrivateKey.encoded
        reconstructedPrivateKey.algorithm shouldBe "RSA"
    }

    "String.toRsaPublicKey() should throw IllegalArgumentException for invalid Base64 string" {
        val invalidBase64 = "This is not valid Base64!"
        shouldThrow<IllegalArgumentException> {
            invalidBase64.toRsaPublicKey()
        }
    }

    "String.toRsaPrivateKey() should throw IllegalArgumentException for invalid Base64 string" {
        val invalidBase64 = "This is not valid Base64!"
        shouldThrow<IllegalArgumentException> {
            invalidBase64.toRsaPrivateKey()
        }
    }

    "String.toAesKey() should throw IllegalArgumentException for invalid Base64 string" {
        val invalidBase64 = "This is not valid Base64!"
        // AES keys are typically shorter, but the Base64 decoding itself should fail first
        // or SecretKeySpec might throw InvalidKeySpecException which could be wrapped or not.
        // For simplicity, expecting IllegalArgumentException from Base64 decoder.
        shouldThrow<IllegalArgumentException> {
            invalidBase64.toAesKey()
        }
    }
})
