package com.api.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.steps"},
        plugin = {"pretty", "summary"},
        monochrome = true
)
public class ApiCucumberTest extends AbstractTestNGCucumberTests {
}

