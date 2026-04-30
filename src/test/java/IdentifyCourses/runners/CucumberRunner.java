package IdentifyCourses.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features   = "src/test/resources/features",
        glue       = "IdentifyCourses.stepDefs",
        plugin     = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)   // sequential — one scenario at a time, member by member
    public Object[][] scenarios() {
        return super.scenarios();
    }
}