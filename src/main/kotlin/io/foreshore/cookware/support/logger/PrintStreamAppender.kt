package io.foreshore.cookware.support.logger

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import java.io.PrintStream

class PrintStreamAppender(val out: PrintStream) : AppenderBase<ILoggingEvent>() {
    constructor(out: PrintStream, name: String) : this(out) {
        setName(name)
    }

    override fun append(eventObject: ILoggingEvent?) {
        out.println(eventObject.toString())
    }

    companion object {
        val NAME_DEFAULT = PrintStreamAppender::javaClass.name
    }
}
