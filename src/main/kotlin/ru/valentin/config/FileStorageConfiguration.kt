package ru.valentin.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileStorageConfig {

    @Value("\${file.storage.dir:uploads}")
    lateinit var storageDir: String

    @Bean
    fun fileStorageLocation(): Path {
        val path = Paths.get(storageDir).toAbsolutePath().normalize()
        Files.createDirectories(path)
        return path
    }
}