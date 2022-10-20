package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.annotation.Transactional;
import io.javalin.Javalin;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static Url existingUrl;

    private static MockWebServer server;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", fileName).toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        return Files.readString(getFixturePath(fileName));
    }

    @BeforeAll
    public static void startApp() throws Exception {
        app = App.getApp();
        app.start(0);
        var port = app.port();
        var baseUrl = "http://localhost:" + port;
        existingUrl = new Url("https://testurl.ru");
        existingUrl.save();
        Unirest.config().defaultBaseUrl(baseUrl);

        server = new MockWebServer();
        var testBody = readFixture("index.html");
        MockResponse response = new MockResponse().setBody(testBody);
        server.enqueue(response);
        server.start();

    }

    @AfterAll
    public static void stopApp() throws Exception {
        server.shutdown();
        app.stop();
    }

    @Test
    void testRootPage() {
        var response = Unirest.get("/").asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    @Test
    void testIndex() {
        var response = Unirest.get("/urls").asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("https://testurl.ru");
    }

    @Test
    @Transactional
    void testNewUrl() {
        var postResponse = Unirest
                .post("/urls")
                .field("url", "http://example.com")
                .asEmpty();

        assertThat(postResponse.getStatus()).isEqualTo(302);
        assertThat(postResponse.getHeaders().getFirst("Location")).isEqualTo("/urls");

        var getResponse = Unirest.get("/urls").asString();
        var body = getResponse.getBody();
        assertThat(body).contains("http://example.com");
        assertThat(body).contains("Страница успешно добавлена");

        var actual = new QUrl()
                .name
                .equalTo("http://example.com")
                .findOne();

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("http://example.com");
    }

    @Test
    @Transactional
    void testAddIncorrectUrl() {
        var postResponse = Unirest
                .post("/urls")
                .field("url", "incorrect")
                .asEmpty();

        assertThat(postResponse.getStatus()).isEqualTo(302);

        var getResponse = Unirest.get("/urls").asString();

        assertThat(getResponse.getBody()).contains("Некорректный URL");

        var actual = new QUrl()
                .name
                .equalTo("incorrect")
                .findOne();

        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    void testAddDuplicateUrl() {
        var postResponse = Unirest
                .post("/urls")
                .field("url", "https://testurl.ru")
                .asEmpty();

        assertThat(postResponse.getStatus()).isEqualTo(302);
        assertThat(postResponse.getHeaders().getFirst("Location")).isEqualTo("/urls");

        var getResponse = Unirest.get("/urls").asString();

        assertThat(getResponse.getBody()).contains("Страница уже существует");

        var actual = new QUrl()
                .name
                .equalTo("https://testurl.ru")
                .findList();

        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void testShowUrl() {
        var response = Unirest.get("/urls/" + existingUrl.getId()).asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("https://testurl.ru");
    }

    @Test
    @Transactional
    void testCheckUrl() {
        var mockUrl = server.url("/").toString().replaceAll("/$", "");
        var addResponse = Unirest
                .post("/urls")
                .field("url", mockUrl)
                .asString();

        assertThat(addResponse.getStatus()).isEqualTo(302);

        var actualUrl = new QUrl().name.equalTo(mockUrl).findOne();

        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(mockUrl);

        var postResponse = Unirest
                .post("/urls/" + actualUrl.getId() + "/checks")
                .asString();

        assertThat(postResponse.getStatus()).isEqualTo(302);

        var getResponse = Unirest
                .get("/urls/" + actualUrl.getId())
                .asString();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getBody()).contains("Страница успешно проверена!");

        var urlCheck = new QUrlCheck()
                .url
                .equalTo(actualUrl).
                findOne();

        assertThat(urlCheck).isNotNull();
        assertThat(urlCheck.getStatusCode()).isEqualTo(200);
        assertThat(urlCheck.getH1()).isEqualTo("Test header");
        assertThat(urlCheck.getTitle()).isEqualTo("Test title");
        assertThat(urlCheck.getDescription()).isEqualTo("Test description");
    }
}
