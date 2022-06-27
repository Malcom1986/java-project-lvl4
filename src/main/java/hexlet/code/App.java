package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static Javalin getApp() {
        Javalin app = Javalin.create();

        app.create(config -> {
            config.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(8000);
    }
}
