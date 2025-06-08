/**
 * RSA 공개키 암호화 알고리즘을 사용한 비대칭키 암호화, 복호화, 디지털 서명 및 검증 기능을 제공합니다.
 *
 * 주요 기능:
 * - RSA 키 쌍(공개키, 비밀키) 생성
 * - 공개키를 사용한 데이터 암호화 (문자열 및 바이트 배열)
 * - 비밀키를 사용한 데이터 복호화
 * - 비밀키를 사용한 데이터 서명 (SHA256withRSA 알고리즘 사용)
 * - 공개키를 사용한 서명 검증
 *
 * **참고:** RSA 암호화는 키 크기에 따라 한 번에 암호화할 수 있는 데이터 크기에 제한이 있습니다.
 * 큰 데이터의 암호화에는 직접 사용하기 부적합하며, 이 경우 하이브리드 암호화 방식이 권장됩니다.
 *
 * 암복호화 및 서명/검증에 사용되는 키는 `io.foreshore.cookware.crypto.core.KeyConversion.kt`의
 * 유틸리티를 통해 Base64 문자열로부터 변환하거나 생성할 수 있습니다.
 *
 * @file RsaEncryption.kt
 */
@file:JvmName("RsaEncryption") // Java 코드에서 정적 유틸리티 클래스로 사용될 때의 이름
package io.foreshore.cookware.crypto.asymmetric

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
// KeyFactory, PKCS8EncodedKeySpec, X509EncodedKeySpec are not directly used here, but in KeyConversion.kt
import java.util.Base64
import javax.crypto.Cipher

// KDocs removed from constants and functions below

const val RSA_ECB_PKCS1PADDING_ALGORITHM = "RSA/ECB/PKCS1Padding"

const val RSA_SIGNATURE_ALGORITHM = "SHA256withRSA"

const val RSA_KEY_SIZE_BITS = 2048

fun generateRsaKeyPair(keySize: Int = RSA_KEY_SIZE_BITS): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(keySize, SecureRandom())
    return keyPairGenerator.genKeyPair()
}

fun ByteArray.encryptRsa(publicKey: PublicKey): ByteArray {
    val cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING_ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    return cipher.doFinal(this)
}

fun String.encryptRsa(publicKey: PublicKey): String {
    val encryptedBytes = this.toByteArray(Charsets.UTF_8).encryptRsa(publicKey)
    return Base64.getEncoder().encodeToString(encryptedBytes)
}

fun ByteArray.decryptRsa(privateKey: PrivateKey): ByteArray {
    val cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING_ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    return cipher.doFinal(this)
}

fun String.decryptRsa(privateKey: PrivateKey): String {
    val decryptedBytes = try {
        Base64.getDecoder().decode(this).decryptRsa(privateKey)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid Base64 input string.", e)
    }
    return String(decryptedBytes, Charsets.UTF_8)
}

fun ByteArray.signRsa(privateKey: PrivateKey): ByteArray {
    val signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM)
    signature.initSign(privateKey)
    signature.update(this)
    return signature.sign()
}

fun String.signRsa(privateKey: PrivateKey): String {
    val signatureBytes = this.toByteArray(Charsets.UTF_8).signRsa(privateKey)
    return Base64.getEncoder().encodeToString(signatureBytes)
}

fun ByteArray.verifyRsaSignature(publicKey: PublicKey, signature: ByteArray): Boolean {
    val verifier = Signature.getInstance(RSA_SIGNATURE_ALGORITHM)
    verifier.initVerify(publicKey)
    verifier.update(this)
    return verifier.verify(signature)
}

fun String.verifyRsaSignature(publicKey: PublicKey, signature: String): Boolean {
    val signatureBytes = try {
        Base64.getDecoder().decode(signature)
    } catch (e: IllegalArgumentException) {
        // If signature is not valid Base64, it cannot be a valid signature
        return false
    }
    return this.toByteArray(Charsets.UTF_8).verifyRsaSignature(publicKey, signatureBytes)
}
