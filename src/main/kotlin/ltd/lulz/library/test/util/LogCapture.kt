package ltd.lulz.library.test.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Level.DEBUG
import ch.qos.logback.classic.Level.ERROR
import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Level.WARN
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ltd.lulz.library.test.exception.TestLibraryException
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory

@Suppress("unused")
class LogCapture(
    private val name: String,
    private vararg var levels: Level = arrayOf<Level>(DEBUG, INFO, WARN, ERROR),
) : AppenderBase<ILoggingEvent>() {

    private val logEvents: MutableList<ILoggingEvent> = mutableListOf()

    init {
        (LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger).addAppender(this)
        start()
    }

    override fun append(logEvent: ILoggingEvent) {
        if (levels.contains(logEvent.level) && logEvent.loggerName.startsWith(name)) {
            logEvents.add(logEvent)
        }
    }

    fun get(): List<ILoggingEvent> = logEvents

    operator fun get(index: Int): ILoggingEvent = try {
        logEvents[index]
    } catch (e: IndexOutOfBoundsException) {
        throw TestLibraryException("Log events size: ${logEvents.size}, requested index: $index.", e)
    }

    fun size(): Int = logEvents.size
}
