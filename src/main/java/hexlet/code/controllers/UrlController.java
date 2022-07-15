package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import io.javalin.http.Handler;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UrlController {

    private static String normaliseUrl(URL url) {
        var path = url.getPath();
        return url.toString().replace(path, "");
    }


    public static Handler addUrl = ctx -> {
        var rawUrl = ctx.formParam("url");
        URL url;

        try {
            url = new URL(rawUrl);
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        var name = normaliseUrl(url);

        if (new QUrl().name.equalTo(name).findOne() != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.redirect("/");
            return;
        }

        var newUrl = new Url(name);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/");
    };

    public static Handler listUrls = ctx -> {
        List<Url> urls = new QUrl().findList();
        ctx.attribute("urls", urls);
        ctx.render("urls/index.html");

    };

    public static Handler showUrl = ctx -> {
        var id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
        var url = new QUrl().id.equalTo(id).findOne();

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        var urlId = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
        var url = new QUrl().id.equalTo(urlId).findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        var response = Unirest.get(url.getName()).asString();
        var document = Jsoup.parse(response.getBody());
        var title = document.title();
        var h1 = document.selectFirst("h1").text();
        var description = document.selectFirst("meta[name=description]").attr("content");

        var check = new UrlCheck(
                response.getStatus(),
                title,
                h1,
                description
        );
        url.getChecks().add(check);
        url.save();

        ctx.sessionAttribute("flash", "Страница успешно проверена!");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + urlId);
    };
}
