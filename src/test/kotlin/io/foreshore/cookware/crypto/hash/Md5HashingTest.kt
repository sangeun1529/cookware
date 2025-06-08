package io.foreshore.cookware.crypto.hash

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
// Import specific functions if needed, or rely on wildcard import if preferred style
// For example: import io.foreshore.cookware.crypto.hash.md5

/**
 * `Md5Hashing.kt` 파일에 정의된 MD5 해시 생성 기능의 정확성과
 * 다양한 입력값에 대한 동작을 검증하는 테스트 클래스입니다.
 *
 * 주요 검증 항목:
 * - 알려진 입력값에 대한 정확한 해시 값 생성 여부
 * - 빈 문자열 및 빈 바이트 배열 처리
 * - 생성된 해시 값의 길이 검증
 *
 * **참고:** MD5 해시의 보안 취약성은 소스 코드 KDoc에 명시되어 있으며,
 * 이 테스트는 기능의 정확성만을 검증합니다.
 *
 * @see io.foreshore.cookware.crypto.hash.Md5Hashing
 */
class Md5HashingTest : StringSpec({

    // Known Hashes
    val helloString = "hello"
    val helloBytes = helloString.toByteArray(Charsets.UTF_8)
    val helloMd5 = "5d41402abc4b2a76b9719d911017c592"

    val emptyString = ""
    val emptyBytes = byteArrayOf()
    val emptyMd5 = "d41d8cd98f00b204e9800998ecf8427e"

    val specialString = "你好, world! @#$%^&*()"
    // val specialBytes = specialString.toByteArray(Charsets.UTF_8) // Not used in MD5 tests directly

    "String.md5() should compute correct hash for known string" {
        helloString.md5() shouldBe helloMd5
    }

    "String.md5() should compute correct hash for empty string" {
        emptyString.md5() shouldBe emptyMd5
    }

    "String.md5() should compute hash for string with special characters" {
        val hash = specialString.md5()
        hash.length shouldBe 32 // MD5 produces a 16-byte hash, 32 hex characters
    }

    "ByteArray.md5() should compute correct hash for known byte array" {
        helloBytes.md5() shouldBe helloMd5
    }

    "ByteArray.md5() should compute correct hash for empty byte array" {
        emptyBytes.md5() shouldBe emptyMd5
    }
})
