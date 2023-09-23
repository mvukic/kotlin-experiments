package org.mvukic

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.context.Context
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_CLEF
import io.klogging.sending.STDOUT
import io.klogging.slf4j.KloggingServiceProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


// https://github.com/spring-projects/spring-framework/issues/27522
// https://github.com/sdeleuze/spring-framework/commit/gh-26977
@SpringBootApplication
class SpringBootApp

fun main(args: Array<String>) {
    configureLogging()
    runApplication<SpringBootApp>(*args)
}

fun configureLogging() {
    loggingConfiguration {
        sink("stdout", RENDER_CLEF, STDOUT)
        logging {
            fromMinLevel(Level.INFO) {
                toSink("stdout")
            }
        }
    }
    Context.addBaseContext(
        "app" to "LoggingDemo",
        "version" to "1.2.0"
    )
}
