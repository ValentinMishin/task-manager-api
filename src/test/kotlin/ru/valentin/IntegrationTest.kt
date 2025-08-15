//package ru.valentin
//
//import io.restassured.RestAssured
//import io.restassured.http.ContentType
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.server.LocalServerPort
//import org.springframework.http.HttpStatus
//import org.springframework.test.context.DynamicPropertyRegistry
//import org.springframework.test.context.DynamicPropertySource
//import org.testcontainers.containers.PostgreSQLContainer
//import org.testcontainers.junit.jupiter.Container
//import org.testcontainers.junit.jupiter.Testcontainers
//import org.testcontainers.utility.MountableFile
//import java.nio.file.Paths
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class IntegrationTest () {
//
//    @LocalServerPort
//    protected var port: Int = 0
//
//    companion object {
//        @Container
//        val postgres: PostgreSQLContainer<*>? = PostgreSQLContainer("postgres:15")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test")
//            .withExposedPorts(5432)
//            .withCopyFileToContainer(
//                MountableFile.forHostPath(Paths.get("src/test/resources/01-schema.sql").toAbsolutePath()),
//                "/docker-entrypoint-initdb.d/01-schema.sql"
//            )
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun registerProperties(registry: DynamicPropertyRegistry) {
//            postgres?.let { registry.add("spring.datasource.url", it::getJdbcUrl) }
//            postgres?.let { registry.add("spring.datasource.username", it::getUsername) }
//            postgres?.let { registry.add("spring.datasource.password", it::getPassword) }
//            registry.add("spring.jpa.hibernate.ddl-auto") { "none" }
//            registry.add("spring.sql.init.mode") { "always" }
//            registry.add("spring.sql.init.data-locations") { "classpath:02-data.sql" }
//        }
//    }
//
//    @BeforeEach
//    fun setup() {
//        RestAssured.port = port
//        RestAssured.baseURI = "http://localhost"
//    }
//
//    @Test
//    fun contextLoads() {
//        // Тест будет использовать реальный контейнер PostgreSQL
//    }
//
//    @Test
//    fun `GET non-existent tag should return 404`() {
//        val nonExistentTagId = 999L
//
//        RestAssured.given()
//            .contentType(ContentType.JSON)
//            .`when`()
//            .get("/api/tags/$nonExistentTagId/tasks")
//            .then()
//            .statusCode(HttpStatus.NOT_FOUND.value())
//    }
//}