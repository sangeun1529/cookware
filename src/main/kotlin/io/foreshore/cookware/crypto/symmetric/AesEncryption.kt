/**
 * AES (Advanced Encryption Standard) 대칭키 암호화 및 복호화 기능을 제공합니다.
 * GCM (Galois/Counter Mode) 운영 모드를 사용하여 데이터의 기밀성과 무결성을 보장합니다.
 *
 * 주요 기능:
 * - AES 비밀키 생성
 * - 문자열 및 바이트 배열 암호화 (IV 자동 생성 및 Ciphertext에 포함)
 * - 암호화된 데이터 복호화
 *
 * 암복호화 시 사용되는 키는 `io.foreshore.cookware.crypto.core.KeyConversion.kt`의
 * 유틸리티를 통해 생성하거나 Base64 문자열로부터 변환할 수 있습니다.
 *
 * @file AesEncryption.kt
 */
@file:JvmName("AesEncryption") // Java 코드에서 정적 유틸리티 클래스로 사용될 때의 이름
package io.foreshore.cookware.crypto.symmetric

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
// SecretKeySpec will not be needed here as toAesKey is moving.

// KDocs removed from constants and functions below

const val AES_GCM_NO_PADDING_ALGORITHM = "AES/GCM/NoPadding"

const val AES_KEY_SIZE_BITS = 256

const val GCM_IV_LENGTH_BYTES = 12

const val GCM_TAG_LENGTH_BITS = 128

fun generateAesKey(keySize: Int = AES_KEY_SIZE_BITS): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(keySize, SecureRandom())
    return keyGenerator.generateKey()
}

fun ByteArray.encryptAes(key: SecretKey): ByteArray {
    val iv = ByteArray(GCM_IV_LENGTH_BYTES)
    SecureRandom().nextBytes(iv) // Generate a new random IV

    val cipher = Cipher.getInstance(AES_GCM_NO_PADDING_ALGORITHM)
    val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
    cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec)

    val cipherText = cipher.doFinal(this)
    return iv + cipherText // Prepend IV to ciphertext
}

fun String.encryptAes(key: SecretKey): String {
    val plainTextBytes = this.toByteArray(Charsets.UTF_8)
    val ivAndCipherText = plainTextBytes.encryptAes(key)
    return Base64.getEncoder().encodeToString(ivAndCipherText)
}

fun ByteArray.decryptAes(key: SecretKey): ByteArray {
    if (this.size < GCM_IV_LENGTH_BYTES) {
        throw IllegalArgumentException("Input data is too short to contain an IV.")
    }

    val iv = this.copyOfRange(0, GCM_IV_LENGTH_BYTES)
    val cipherText = this.copyOfRange(GCM_IV_LENGTH_BYTES, this.size)

    val cipher = Cipher.getInstance(AES_GCM_NO_PADDING_ALGORITHM)
    val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
    cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec)

    return cipher.doFinal(cipherText)
}

fun String.decryptAes(key: SecretKey): String {
    val decodedIvAndCipherText = try {
        Base64.getDecoder().decode(this)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid Base64 input string.", e)
    }
    val plainTextBytes = decodedIvAndCipherText.decryptAes(key)
    return String(plainTextBytes, Charsets.UTF_8)
}
