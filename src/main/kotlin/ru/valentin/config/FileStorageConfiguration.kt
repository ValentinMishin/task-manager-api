//package ru.valentin.config
//
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.boot.context.properties.ConfigurationProperties
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import java.nio.file.Files
//import java.nio.file.Path
//import java.nio.file.Paths
//
//@Configuration
//class FileStorageConfig {
//    @Value("\${file.upload-dir}")
//    private lateinit var uploadDir: String
//
//    @Bean
//    fun fileStorageProperties(): FileStorageProperties {
//        return FileStorageProperties(uploadDir)
//    }
//
//    @Bean
//    fun fileStorageService(): FileStorageService {
//        return FileStorageService(fileStorageProperties())
//    }
//}
//@ConfigurationProperties(prefix = "file")
//data class FileStorageProperties(
//    var uploadDir: String = "uploads"
//) {
//    init {
//        Files.createDirectories(Paths.get(uploadDir))
//    }
//}