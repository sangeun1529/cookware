package io.foreshore.cookware.lang


/**
 * cookware Root Exception.
 */
open class CookwareException(message: String? = "", cause: Throwable? = null) : RuntimeException(message ?: "", cause)
