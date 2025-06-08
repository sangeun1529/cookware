/**
 * 암호화 키(`SecretKey`, `PublicKey`, `PrivateKey`)와 Base64 인코딩된 문자열 간의
 * 상호 변환을 위한 확장 함수들을 제공합니다.
 *
 * 지원하는 키 유형:
 * - AES 비밀키 (`SecretKey`)
 * - RSA 공개키 (`PublicKey`)
 * - RSA 비밀키 (`PrivateKey`)
 *
 * 이 유틸리티들은 키를 안전하게 저장하거나 전송 가능한 형태로 변환하고,
 * 다시 키 객체로 복원하는 데 사용됩니다.
 *
 * @file KeyConversion.kt
 */
@file:JvmName("KeyConversion") // Java 코드에서 정적 유틸리티 클래스로 사용될 때의 이름
package io.foreshore.cookware.crypto.core

import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

// KDocs removed from individual functions below

fun String.toAesKey(): SecretKey {
    val decodedKey = Base64.getDecoder().decode(this)
    return SecretKeySpec(decodedKey, "AES")
}

fun SecretKey.toBase64String(): String {
    return Base64.getEncoder().encodeToString(this.encoded)
}

fun PublicKey.toBase64String(): String {
    return Base64.getEncoder().encodeToString(this.encoded)
}

fun String.toRsaPublicKey(): PublicKey {
    val keyBytes = Base64.getDecoder().decode(this)
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}

fun PrivateKey.toBase64String(): String {
    return Base64.getEncoder().encodeToString(this.encoded)
}

fun String.toRsaPrivateKey(): PrivateKey {
    val keyBytes = Base64.getDecoder().decode(this)
    val keySpec = PKCS8EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePrivate(keySpec)
}
