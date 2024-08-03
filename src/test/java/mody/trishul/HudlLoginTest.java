package mody.trishul;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class HudlLoginTest {

    private static WebDriver driver;
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeClass
    public static void setupClass() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @Before
    public void setupTest() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void testLogin() {
        test = extent.createTest("Hudl Login Test");

        // Replace with credentials sent to you in email
        String username = "trishulmody@gmail.com";
        String password = "incorrect@007";

        // Open Hudl login page
        driver.get("https://www.hudl.com/login");
        test.info("Opened Hudl login page");

        // Find the username field and enter the username
        WebElement usernameField = driver.findElement(By.id("email"));
        usernameField.sendKeys(username);
        test.info("Entered username");

        // Find the password field and enter the password
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(password);
        test.info("Entered password");

        // Submit the login form
        passwordField.submit();
        test.info("Submitted the login form");

        // Check if login was successful by looking for a unique element on the dashboard
        try {
            WebElement dashboardElement = driver.findElement(By.id("some_unique_element_on_dashboard"));
            test.pass("Login successful");
        } catch (Exception e) {
            test.fail("Login failed: " + e.getMessage());
        }
    }

    @After
    public void teardownTest() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public static void teardownClass() {
        if (extent != null) {
            extent.flush();
        }
    }
}
