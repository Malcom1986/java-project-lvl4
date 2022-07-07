package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.annotation.Transactional;
import io.javalin.Javalin;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static Url existingUrl;

    @BeforeAll
    public static void startApp() {
        app = App.getApp();
        app.start(0);
        var port = app.port();
        var baseUrl = "http://localhost:" + port;
        existingUrl = new Url("https://testurl.ru");
        existingUrl.save();
        Unirest.config().defaultBaseUrl(baseUrl);
    }

    @AfterAll
    public static void stopApp() {
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

        var getResponse = Unirest.get("/urls").asString();
        var body = getResponse.getBody();
        assertThat(body).contains("http://example.com");
        assertThat(body).contains("Страница успешно добавлена");

        var actual = new QUrl()
                .name
                .equalTo("http://example.com")
                .findOne();

        assertThat(actual).isNotNull();
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
}
