/**
 * SHA-256 및 SHA-512 해시 알고리즘을 사용하여 문자열 또는 바이트 배열로부터
 * 해시 값을 계산하는 확장 함수들을 제공합니다.
 * 계산된 해시 값은 16진수 문자열 형태로 반환됩니다.
 *
 * @file ShaHashing.kt
 */
@file:JvmName("ShaHashing") // Java 코드에서 정적 유틸리티 클래스로 사용될 때의 이름
package io.foreshore.cookware.crypto.hash

import java.security.MessageDigest

// KDocs removed from individual functions below

fun String.sha256(): String = this.toByteArray(Charsets.UTF_8).sha256()

fun ByteArray.sha256(): String = hash("SHA-256", this).toHexString()

fun String.sha512(): String = this.toByteArray(Charsets.UTF_8).sha512()

fun ByteArray.sha512(): String = hash("SHA-512", this).toHexString()

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
