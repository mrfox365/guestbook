package com.example.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTest {
    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example");

    @Test
    public void webLayerShouldNotDependOnPersistenceLayer() {
        // Web (Controller) має спілкуватися з Persistence ТІЛЬКИ через Core (Service)
        ArchRule rule = noClasses()
                .that().resideInAPackage("..web..")
                .should().dependOnClassesThat().resideInAPackage("..persistence..");

        rule.check(importedClasses);
    }

    @Test
    public void coreLayerShouldNotDependOnInfrastructure() {
        // Core не повинен залежати від Web, Persistence або низькорівневого Servlet API
        // Але тепер Core залежить від Spring Framework (@Service, @Autowired).
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAPackage("..web..")
                .orShould().dependOnClassesThat().resideInAPackage("..persistence..")
                .orShould().dependOnClassesThat().resideInAPackage("jakarta.servlet..");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldBeInWeb() {
        // Тепер шукаємо класи з анотацією @RestController або суфіксом Controller
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .or().areAnnotatedWith(RestController.class)
                .or().areAnnotatedWith(Controller.class)
                .should().resideInAPackage("..web..");

        rule.check(importedClasses);
    }

    @Test
    public void servicesShouldBeInCore() {
        // Перевіряємо, що @Service живуть тільки в Core
        ArchRule rule = classes()
                .that().areAnnotatedWith(Service.class)
                .should().resideInAPackage("..core..");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldBeInPersistence() {
        // Перевіряємо, що @Repository живуть тільки в Persistence
        ArchRule rule = classes()
                .that().areAnnotatedWith(Repository.class)
                .should().resideInAPackage("..persistence..");

        rule.check(importedClasses);
    }
}