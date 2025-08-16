package ru.valentin

import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.nio.file.Paths

class TestContainersInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    companion object {
        private val log = LoggerFactory.getLogger(javaClass)

        val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withExposedPorts(5432)
            .withReuse(true)
            .withCommand(
             "postgres",
                "-c", "listen_addresses=*",           // разрешаем подключения с любого IP
                "-c", "password_encryption=scram-sha-256"
            )
            .withCopyFileToContainer(
                MountableFile.forHostPath(Paths.get("src/test/resources/01-schema.sql")),
                "/docker-entrypoint-initdb.d/01-schema.sql"
            )
            .withCopyFileToContainer(
                MountableFile.forHostPath(Paths.get("src/test/resources/02-data.sql")),
                "/docker-entrypoint-initdb.d/02-data.sql"
            )

        init {
            postgres.start()
            log.info("Port: ${postgres.getMappedPort(5432)}")
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.datasource.url=${postgres.jdbcUrl}",
            "spring.datasource.username=${postgres.username}",
            "spring.datasource.password=${postgres.password}",
            "spring.jpa.hibernate.ddl-auto=none",
            "spring.jpa.show-sql=true"
        ).applyTo(applicationContext.environment)
    }
}
