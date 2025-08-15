package ru.valentin

import org.springframework.boot.SpringApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans

class TestApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder()
                .sources(Application::class.java)
                .initializers(beans {
                    bean<TestContainersInitializer>()
                })
                .profiles("test")  // Используем тестовый профиль
                .run(*args)
        }
    }
}