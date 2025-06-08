/**
 * MD5 해시 알고리즘을 사용하여 문자열 또는 바이트 배열로부터
 * 해시 값을 계산하는 확장 함수들을 제공합니다.
 * 계산된 해시 값은 16진수 문자열 형태로 반환됩니다.
 *
 * **경고:** MD5는 암호학적으로 안전하지 않으며, 충돌에 취약합니다.
 * 비밀번호 해싱과 같은 보안이 중요한 용도로 사용해서는 안 됩니다.
 * 데이터 무결성 검증 등 비암호화적 목적으로만 제한적으로 사용해야 합니다.
 *
 * @file Md5Hashing.kt
 */
@file:JvmName("Md5Hashing") // Java 코드에서 정적 유틸리티 클래스로 사용될 때의 이름
package io.foreshore.cookware.crypto.hash

import java.security.MessageDigest

// KDocs removed from individual functions below

fun String.md5(): String = this.toByteArray(Charsets.UTF_8).md5()

fun ByteArray.md5(): String = hash("MD5", this).toHexString()

/**
 * Private helper function to perform the actual hashing logic.
 * @param algorithm The hashing algorithm to use (e.g., "SHA-256", "SHA-512", "MD5").
 * @param input The byte array to hash.
 * @return The hashed byte array.
 */
private fun hash(algorithm: String, input: ByteArray): ByteArray {
    val digest = MessageDigest.getInstance(algorithm)
    return digest.digest(input)
}

/**
 * Private helper function to convert a byte array to its hexadecimal string representation.
 * Each byte is converted to two hexadecimal characters.
 * @return The hexadecimal string.
 */
private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
