package com.example.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTest {
    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example");

    @Test
    public void coreLayerShouldNotDependOnInfrastructure() {
        // Найважливіше правило залишається: Core (бізнес-логіка) має бути чистим.
        // Він не повинен залежати від Web (Javalin) або Persistence (JDBC реалізації).
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAPackage("..web..")
                .orShould().dependOnClassesThat().resideInAPackage("..persistence..")
                .orShould().dependOnClassesThat().resideInAPackage("io.javalin.."); // Забороняємо Javalin у ядрі

        rule.check(importedClasses);
    }

}