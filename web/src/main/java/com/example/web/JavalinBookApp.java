package com.example.web;

import com.example.core.ports.RepositoryPort;
import com.example.core.service.GuestbookService;
import com.example.persistence.JdbcRepository;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class JavalinBookApp {

    public static void main(String[] args) {
        // 1. Ручна конфігурація залежностей (Manual DI)
        DataSource dataSource = createDataSource();
        RepositoryPort repository = new JdbcRepository(dataSource);

        // Тут ми викликаємо initSchema, бо Spring @PostConstruct більше не працює
        repository.initSchema();

        GuestbookService service = new GuestbookService(repository);

        // 2. Запуск Javalin
        Javalin app = Javalin.create(config -> {
            // Налаштування CORS, статики тощо (якщо треба)
            config.requestLogger.http((ctx, ms) -> {
                // Логування кожного запиту
                System.out.println(ctx.method() + " " + ctx.path() + " took " + ms + " ms");
            });
        }).start(8081);

        // 3. Middleware (Interceptor)
        // Виконується перед кожним запитом до /books/*
        app.before("/books/*", ctx -> {
            System.out.println("INFO: Accessing books API from IP: " + ctx.ip());
        });

        // 4. Маршрути (Routes)

        // GET /books
        app.get("/books", ctx -> {
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(10);
            String sort = ctx.queryParam("sort");
            String q = ctx.queryParam("q");

            ctx.json(service.searchBooks(page, size, sort, q));
        });

        // GET /books/{id}
        app.get("/books/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            ctx.json(service.getBook(id));
        });

        // POST /comments
        app.post("/comments", ctx -> {
            // Отримуємо параметри форми (x-www-form-urlencoded)
            String author = ctx.formParam("author");
            String text = ctx.formParam("text");

            service.addComment(author, text);
            ctx.status(HttpStatus.CREATED);
        });

        // DELETE /comments/{id}
        app.delete("/comments/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            service.deleteComment(id);
            ctx.status(HttpStatus.NO_CONTENT);
        });

        // 5. Global Exception Handling (Замість @ControllerAdvice)
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse(e.getMessage()));
        });

        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(HttpStatus.CONFLICT).json(new ErrorResponse(e.getMessage()));
        });

        app.exception(RuntimeException.class, (e, ctx) -> {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                ctx.status(HttpStatus.NOT_FOUND).json(new ErrorResponse(e.getMessage()));
            } else {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse(e.getMessage()));
            }
        });
    }

    // Helper: Створення підключення до БД вручну
    private static DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:file:./data/guestbook_javalin;AUTO_SERVER=TRUE");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    // Record для JSON відповіді помилки
    record ErrorResponse(String error) {}
}