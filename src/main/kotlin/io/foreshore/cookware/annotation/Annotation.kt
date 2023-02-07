@file:Suppress("unused")

package io.foreshore.cookware.annotation

/**
 * kotlin data class 에 no-arg plug-in 을 사용하기 위한 Annotation.
 * build.gradle.kts 의 plugins 에   kotlin("plugin.noarg") version kotlinVersion 추가.
 * configuration 추가.
 * <pre>
 *     noArg {
         annotation("io.foreshore.cookware.annotation.NoArg")
       }
 * </pre>
 *
 * plugin-in 은 해당 클래스에 자동으로 default constructor 를 만들어준다. 그러나 이는 compile time 에서는 사용 불가능 하며,
 * reflection 으로만 사용가능하다. JPA 에서 주로 사용 한다.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoArg
