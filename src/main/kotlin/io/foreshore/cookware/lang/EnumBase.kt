@file:Suppress("unused")

package io.foreshore.cookware.lang

import kotlin.reflect.KClass


/**
 * Enum for storing code into DB.
 */
interface EnumBase<T> {
    val code: T

    companion object {
        fun <T, E : EnumBase<T>> createValuesMap(enumValues: Array<E>?): Map<T, E> =
            enumValues?.associate { it.code to it } ?: mapOf()

        fun <T, E : EnumBase<T>> createValuesMap(clazz: Class<E>) = createValuesMap(clazz.enumConstants)
        fun <T, E : EnumBase<T>> createValuesMap(clazz: KClass<E>) = createValuesMap(clazz.java)
    }

    open class Values<T, E> where E : EnumBase<T>, E : Enum<E> {
        @Suppress("UNCHECKED_CAST")
        val valuesMap: Map<T, E> by lazy {
            this::class.java.declaringClass.enumConstants.associate { (it as EnumBase<T>).code to it as E }
        }

        @Suppress("UNCHECKED_CAST")
        val namesMap: Map<String, E> by lazy {
            this::class.java.declaringClass.enumConstants.associate { (it as E).name to it }
        }

        fun valueOfCode(code: T) = valuesMap[code]

        fun valueOfCodeOrDefault(code: T, default: E) = valuesMap[code] ?: default

        fun valueOfOrNull(value: String): E? = namesMap[value]

        fun valueOfOrDefault(value: String, default: E): E = namesMap[value] ?: default
    }
}

enum class EEnabled(override val code: String) : EnumBase<String> {
    YES("Y"),
    NO("N")
    ;

    companion object : EnumBase.Values<String, EEnabled>()
}

enum class ESuccess(override val code: String) : EnumBase<String> {
    YES("Y"),
    NO("N")
    ;

    companion object : EnumBase.Values<String, ESuccess>()
}

// boolean 을 그냥 저장하면 'TRUE', 'FALSE', 혹은 0, 1 이 된다.
enum class EBoolean(override val code: String) : EnumBase<String> {
    TRUE("Y"),
    FALSE("N")
    ;

    val isTrue get() = this == TRUE
    val isFalse get() = this != TRUE

    companion object : EnumBase.Values<String, EBoolean>() {
        fun valueOf(b: Boolean) = if (b) TRUE else FALSE
    }
}
