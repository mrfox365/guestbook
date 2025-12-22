package com.example.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTest {
    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example");

    @Test
    public void webLayerShouldNotDependOnPersistenceLayer() {
        // Перевіряє, що у коді пакету 'web' немає імпортів з 'persistence'
        ArchRule rule = noClasses()
                .that().resideInAPackage("..web..")
                .should().dependOnClassesThat().resideInAPackage("..persistence..");

        rule.check(importedClasses);
    }

    @Test
    public void coreLayerShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAPackage("..web..")
                .orShould().dependOnClassesThat().resideInAPackage("..persistence..")
                .orShould().dependOnClassesThat().resideInAPackage("jakarta.servlet..");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldBeInWeb() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Servlet")
                .should().resideInAPackage("..web..");
        rule.check(importedClasses);
    }
}