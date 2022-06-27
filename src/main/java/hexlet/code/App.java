package hexlet.code;

import io.javalin.Javalin;

public class App {

    private static int getPort() {
        var port = System.getenv().getOrDefault("PORT", 3000);
        return Integer.valueOf(port);
    }

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
        var port = getPort();
        app.start(port);
    }
}
