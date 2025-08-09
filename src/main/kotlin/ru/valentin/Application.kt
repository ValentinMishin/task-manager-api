package ru.valentin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {
    val logger = LoggerFactory.getLogger(Application::class.java)

    fun main(args: Array<String>) {
        runApplication<Application>(*args)
    }
}