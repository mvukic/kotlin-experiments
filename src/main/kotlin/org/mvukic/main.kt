package org.mvukic

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.context.Context
import io.klogging.rendering.RENDER_ANSI
import io.klogging.sending.STDOUT
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


//https://github.com/spring-projects/spring-framework/issues/27522

@SpringBootApplication
class SpringBootApp

fun main(args: Array<String>) {
    configureLogging()
    runApplication<SpringBootApp>(*args)
}

fun configureLogging() {
    loggingConfiguration {
        sink("stdout", RENDER_ANSI, STDOUT)
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