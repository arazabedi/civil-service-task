package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "server.port=0" // Explicitly set server.port to 0 for random port (had issues with conflicting ports)
)
class TaskApiSmokeTest {

    @LocalServerPort
    private int port;

    private String testUrl;

    @BeforeEach
    public void setUp() {
        testUrl = "http://localhost:" + port;
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
        System.out.println("Test running on port: " + port); // Keep this for debugging
    }

    @Test
    void getAllTasksEndpointIsAvailable() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks")
            .then()
            .extract()
            .response();

        assertEquals(200, response.statusCode(), "Expected 200 OK from /tasks endpoint");

        assertTrue(response.asString().startsWith("[") || response.asString().equals("[]"), "Expected JSON array response");
    }
}
