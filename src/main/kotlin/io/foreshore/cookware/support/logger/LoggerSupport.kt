package io.foreshore.cookware.support.logger

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Logger Support.
 * Class 내에서 다음과 같이 선언하면 logger variable 을 사용할 수 있다.
 * companion object : cookwareLog()
 *
 */
open class MiskaLog {
    @Suppress("JAVA_CLASS_ON_COMPANION")
    val logger: Logger by lazy { loadLogger() }

    private fun loadLogger() = LoggerFactory.getLogger(javaClass.enclosingClass)
}

object LoggerSupport {
    fun addCustomAppender(loggerName: String, appender: Appender<ILoggingEvent>) {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        val logger = context.getLogger(loggerName)
        appender.context = context
        if (!appender.isStarted) appender.start()
        logger.addAppender(appender)
    }
}
