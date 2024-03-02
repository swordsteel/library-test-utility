package ltd.lulz.library.test.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Level.DEBUG
import ch.qos.logback.classic.Level.ERROR
import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Level.WARN
import ch.qos.logback.classic.Logger
import ltd.lulz.library.test.exception.TestLibraryException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.slf4j.LoggerFactory

class LogCaptureTest {
    companion object {
        const val PACKAGE_NAME = "ltd.lulz.test"
        const val PACKAGE_LIBRARY_NAME = "ltd.lulz.test.library"
        const val PACKAGE_SERVICE_NAME = "ltd.lulz.test.service"
    }

    private val logTest: Logger = LoggerFactory.getLogger(PACKAGE_NAME) as Logger
    private val logLibrary: Logger = LoggerFactory.getLogger(PACKAGE_LIBRARY_NAME) as Logger
    private val logService: Logger = LoggerFactory.getLogger(PACKAGE_SERVICE_NAME) as Logger

    private fun logAllTypesForLogger(logger: Logger) {
        logger.trace("TRACE")
        logger.debug("DEBUG")
        logger.info("INFO")
        logger.warn("WARN")
        logger.error("ERROR")
    }

    @Test
    fun `capture default log events`() {
        // given
        val logEvent = LogCapture(PACKAGE_NAME)

        // when
        logAllTypesForLogger(logTest)

        // then
        assertThat(logEvent.size()).isEqualTo(4)
        assertThat(logEvent[0].formattedMessage).isEqualTo("DEBUG")
        assertThat(logEvent[1].formattedMessage).isEqualTo("INFO")
        assertThat(logEvent[2].formattedMessage).isEqualTo("WARN")
        assertThat(logEvent[3].formattedMessage).isEqualTo("ERROR")
    }

    @ParameterizedTest
    @CsvSource(
        "TRACE",
        "DEBUG",
        "INFO",
        "WARN",
        "ERROR",
    )
    fun `capture specific level`(level: String) {
        // given
        val logEvent = LogCapture(PACKAGE_NAME, Level.toLevel(level))

        // when
        logAllTypesForLogger(logTest)

        // then
        assertThat(logEvent.size()).isEqualTo(1)
        assertThat(logEvent[0].formattedMessage).isEqualTo(level)
    }

    @Test
    fun `get all capture events as list`() {
        // given
        val logEvent = LogCapture(PACKAGE_NAME, INFO, WARN, ERROR)
        logAllTypesForLogger(logTest)

        // when
        val eventList = logEvent.get()

        // then
        assertThat(eventList.size).isEqualTo(3)
        assertThat(logEvent[0].formattedMessage).isEqualTo("INFO")
        assertThat(logEvent[1].formattedMessage).isEqualTo("WARN")
        assertThat(logEvent[2].formattedMessage).isEqualTo("ERROR")
    }

    @ParameterizedTest
    @CsvSource(
        "0, TRACE",
        "1, DEBUG",
        "2, INFO",
        "3, WARN",
        "4, ERROR",
    )
    fun `get specific capture event`(position: Int, expected: String) {
        // given
        val logEvent = LogCapture(PACKAGE_NAME, TRACE, DEBUG, INFO, WARN, ERROR)
        logAllTypesForLogger(logTest)

        // when
        val event = logEvent[position]

        // then
        assertThat(logEvent.size()).isEqualTo(5)
        assertThat(event.level.toString()).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "$PACKAGE_NAME, 8",
        "$PACKAGE_LIBRARY_NAME, 4",
        "$PACKAGE_SERVICE_NAME, 4",
    )
    fun `capture specific package events`(packagesName: String, expected: Int) {
        // given
        val logEvent = LogCapture(packagesName)

        // when
        logAllTypesForLogger(logLibrary)
        logAllTypesForLogger(logService)

        // then
        assertThat(logEvent.size()).isEqualTo(expected)
    }

    @Test
    fun `no log event`() {
        val logEvent = LogCapture(PACKAGE_NAME)
        logTest.info("just one")
        assertThatThrownBy { logEvent[1] }
            .isInstanceOf(TestLibraryException::class.java)
            .hasMessage("Log events size: 1, requested index: 1.")
    }
}
