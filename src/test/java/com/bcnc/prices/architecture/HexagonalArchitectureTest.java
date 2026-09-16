package com.bcnc.prices.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * La arquitectura hexagonal es un criterio de evaluacion, asi que se verifica
 * automaticamente en lugar de confiar en la disciplina al nombrar paquetes.
 */
@AnalyzeClasses(packages = "com.bcnc.prices", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule el_dominio_no_depende_de_las_capas_externas =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..infrastructure..")
                    .because("la regla de dependencia apunta hacia dentro: el nucleo no conoce a quien lo usa");

    @ArchTest
    static final ArchRule el_dominio_no_depende_de_frameworks =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "com.fasterxml.jackson..")
                    .because("el modelo de negocio debe poder compilar y probarse sin Spring ni JPA");

    @ArchTest
    static final ArchRule la_aplicacion_no_depende_de_la_infraestructura =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .because("el caso de uso se expresa contra puertos, no contra adaptadores");

    @ArchTest
    static final ArchRule el_adaptador_rest_no_toca_la_persistencia =
            noClasses()
                    .that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..adapter.out..")
                    .because("los adaptadores se comunican a traves del nucleo, nunca entre ellos");
}
