package io.foreshore.cookware.util

import kotlin.random.Random

/** 주어진 컴포넌트 chars 로 주어진 길이의 키를 얻는다.
 *
 */
open class RandomKeyGenerator(
    private val components: String = CODES_CHARS_DEFAULT,
    private val predicate: (String) -> Boolean = { true }
) {
    init {
        require(components.isNotEmpty())
    }

    private val componentSize = this.components.length

    /**
     *  주어진 길이의 랜덤 키를 생성한다. 무한 loop 에 빠지지 않기 위해 100번의 재시도에도 predicate 가 true 를 리턴하지 않으면 IllegalStateException 을 throw 한다.
     *  클래스에 정의된 predicate 를 사용한다.
     *  @throws IllegalStateException - 10 번 시도중에 predicate 가 true 를 리턴하지 않은 경우.
     */
    @Throws(IllegalStateException::class)
    open fun nextKey(length: Int = LENGTH_DEFAULT): String = nextKey(length, predicate)

    /**
     * 주어진 길이의 랜덤 키를 생성한다. predicate 클래스의 것을 사용하지 않고 별도의 predicate 를 사용한다.
     */
    @Throws(IllegalStateException::class)
    open fun nextKey(length: Int = LENGTH_DEFAULT, predicate: (String) -> Boolean): String =
        synchronized(this) {
            var count = 0 // prevent infinite loop
            var code: String
            do {
                code = (1..length).map { Random.nextInt(until = componentSize) }.map { components[it] }.joinToString("")
                if (++count == RETRY_MAX) throw IllegalStateException("predicate never return true")
            } while (!predicate.invoke(code))
            return code
        }

    companion object {
        const val CODES_CHARS_DEFAULT = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val RETRY_MAX = 10
        const val LENGTH_DEFAULT = 8
    }
}
