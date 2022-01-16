package ca.mcgill.ecse.flexibook.features;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
// AppointmentManagement.feature
// UpdateAppointment.feature
@CucumberOptions(plugin = "pretty", features = "src/test/resources", monochrome = true)
public class CucumberFeaturesTestRunner {
}
