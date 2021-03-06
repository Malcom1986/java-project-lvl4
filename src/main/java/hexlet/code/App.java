package hexlet.code;

import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class App {

    private static int getPort() {
        var port = System.getenv().getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static void addRoutes(Javalin app) {
        app.routes(() -> {
            path("/", () -> {
                get(RootController.welcome);

            });
            path("/urls", () -> {
                post(UrlController.addUrl);
                get(UrlController.listUrls);
                path("{id}", () -> {
                    get(UrlController.showUrl);
                    path("/checks", () -> {
                        post(UrlController.checkUrl);
                    });
                });
            });
        });


    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (getMode().equals("development")) {
                config.enableDevLogging();
            }
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        addRoutes(app);

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        var port = getPort();
        app.start(port);
    }
}
