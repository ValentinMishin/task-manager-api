package ru.valentin

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.CreateShortTagDto
import java.time.LocalDate

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
class MVCTest () {

    @LocalServerPort
    protected var port: Int = 0

    private val log = LoggerFactory.getLogger(javaClass)

    @BeforeEach
    fun setup() {
        RestAssured.port = port
        RestAssured.baseURI = "http://localhost"
    }

    @Test
    fun `except 400 invalid date at parameter date` () {
        RestAssured.given()
            .param("date", "invalid-date")
            .`when`()
            .get("/api/tasks/by-date")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `except 400 validation failed tag field title must not be blank` () {
        val newTag1 = CreateShortTagDto("")
        RestAssured
            .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(newTag1)
            .`when`()
                .post("/api/tags")
            .then()
                .statusCode(400)
                .body(containsString("Название тега не может быть нул или пустым"))
    }

    @Test
    fun `except 400 validation failed for new tag with invalid field applied to task`() {
        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

        val newTag1 = CreateShortTagDto("")
        val newTag2 = CreateShortTagDto("")
        val newTags = setOf(newTag1, newTag2)
        val existingTags = setOf(1L, 2L)

        val createTaskWithTags = CreateTaskDto(
            title = "test_task1",
            typeId = 1L,
            description = "test description for test_task1",
            dueDate = LocalDate.now().plusDays(5),
            existingTags,
            newTags = newTags
        ).let { mapper.writeValueAsString(it) }

        RestAssured
            .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createTaskWithTags)
            .`when`()
                .post("/api/tasks")
            .then()
                .statusCode(400)
                .body(containsString("Название тега не может быть нул или пустым"))
    }

    @Test
    fun `except 404 validation failed for invalid due date task` () {
        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

        val createTaskWithTags = CreateTaskDto(
            title = "test_task1",
            typeId = 1L,
            description = "test description for test_task1",
            dueDate = LocalDate.now().minusDays(5),
            null,
            newTags = null
        ).let { mapper.writeValueAsString(it) }

        RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(createTaskWithTags)
            .`when`()
            .post("/api/tasks")
            .then()
            .statusCode(400)
            .body(containsString("Запланированная дата не может быть нул или в прошлом"))
    }

    @Test
    fun `expect 404 for non existing tag with id=999`() {
        val nonExistentTagId = 999L

        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/tags/$nonExistentTagId/tasks")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }
}