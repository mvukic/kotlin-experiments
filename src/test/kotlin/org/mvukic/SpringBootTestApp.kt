package org.mvukic

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with


@TestConfiguration(proxyBeanMethods = false)
class SpringBootTestApp

fun main(args: Array<String>) {
    fromApplication<SpringBootApp>().with(SpringBootTestApp::class).run(*args)
}
