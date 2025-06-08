package io.foreshore.cookware.crypto.hash

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
// Import specific functions if needed, or rely on wildcard import if preferred style
// For example: import io.foreshore.cookware.crypto.hash.sha256

/**
 * `ShaHashing.kt` 파일에 정의된 SHA-256 및 SHA-512 해시 생성 기능의 정확성과
 * 다양한 입력값에 대한 동작을 검증하는 테스트 클래스입니다.
 *
 * 주요 검증 항목:
 * - 알려진 입력값에 대한 정확한 해시 값 생성 여부
 * - 빈 문자열 및 빈 바이트 배열 처리
 * - 특수 문자를 포함한 문자열 처리
 * - 생성된 해시 값의 길이 검증
 *
 * @see io.foreshore.cookware.crypto.hash.ShaHashing
 */
class ShaHashingTest : StringSpec({

    // Known Hashes
    val helloString = "hello"
    val helloBytes = helloString.toByteArray(Charsets.UTF_8)

    val helloSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
    val helloSha512 = "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043"

    val emptyString = ""
    val emptyBytes = byteArrayOf()

    val emptySha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    val emptySha512 = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"

    val specialString = "你好, world! @#$%^&*()"
    // val specialBytes = specialString.toByteArray(Charsets.UTF_8) // Not used in SHA tests directly

    "String.sha256() should compute correct hash for known string" {
        helloString.sha256() shouldBe helloSha256
    }

    "String.sha256() should compute correct hash for empty string" {
        emptyString.sha256() shouldBe emptySha256
    }

    "String.sha256() should compute hash for string with special characters" {
        val hash = specialString.sha256()
        hash.length shouldBe 64 // SHA-256 produces a 32-byte hash, 64 hex characters
    }

    "ByteArray.sha256() should compute correct hash for known byte array" {
        helloBytes.sha256() shouldBe helloSha256
    }

    "ByteArray.sha256() should compute correct hash for empty byte array" {
        emptyBytes.sha256() shouldBe emptySha256
    }

    "String.sha512() should compute correct hash for known string" {
        helloString.sha512() shouldBe helloSha512
    }

    "String.sha512() should compute correct hash for empty string" {
        emptyString.sha512() shouldBe emptySha512
    }

    "String.sha512() should compute hash for string with special characters" {
        val hash = specialString.sha512()
        hash.length shouldBe 128 // SHA-512 produces a 64-byte hash, 128 hex characters
    }

    "ByteArray.sha512() should compute correct hash for known byte array" {
        helloBytes.sha512() shouldBe helloSha512
    }

    "ByteArray.sha512() should compute correct hash for empty byte array" {
        emptyBytes.sha512() shouldBe emptySha512
    }
})
