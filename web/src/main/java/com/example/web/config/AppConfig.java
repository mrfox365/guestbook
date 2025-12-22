package com.example.web.config;

import com.example.core.ports.RepositoryPort;
import com.example.core.service.GuestbookService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.lang.reflect.Constructor;

@WebListener
public class AppConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Динамічне завантаження класу Persistence, щоб уникнути compile-time залежності
            Class<?> repoClass = Class.forName("com.example.persistence.JdbcRepository");
            Constructor<?> constructor = repoClass.getConstructor(String.class, String.class, String.class);

            // Інжекція залежностей
            RepositoryPort repository = (RepositoryPort) constructor.newInstance(
                    "jdbc:h2:file:./data/guestbook;AUTO_SERVER=TRUE", "sa", ""
            );

            repository.initSchema();

            GuestbookService service = new GuestbookService(repository);

            // Збереження в контекст для сервлетів
            sce.getServletContext().setAttribute("guestbookService", service);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize app", e);
        }
    }
}