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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testValidLogin() {
        test = extent.createTest("Correct Credentials");

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

        //Click submit button
        WebElement submitButton = driver.findElement(By.id("logIn"));
        submitButton.click();
        test.info("Submitted the login form");

        // Check if login was successful by looking for a unique element on the dashboard
        try {
            WebElement spanElement = driver.findElement(By.xpath("//span[text()='Trishul M']"));
            String spanText = spanElement.getText();
            test.info("Extracted text: " + spanText);
            Assert.assertEquals("Trishul M", spanText);
            test.pass("login successful");
        } catch (Exception e) {
            test.fail("Login failed: " + e.getMessage());
        }
    }
    @Test
    public void testEmptyLogin()
    {
        test = extent.createTest("Blank Credentials");

        String username = "";
        String password = "";

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

        //Click submit button
        WebElement submitButton = driver.findElement(By.id("logIn"));
        submitButton.click();
        test.info("Submitted the login form");

        // Check error message
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement errorText = driver.findElement(By.xpath("//p[@data-qa-id='undefined-text']"));

            Boolean isTextPresent = wait.until(ExpectedConditions.textToBePresentInElement(
                    (errorText),
                    "Please fill in all of the required fields"
            ));
            if (isTextPresent) {
                WebElement errorMessageElement = driver.findElement(By.xpath("//p[@data-qa-id='undefined-text']"));
                String errorMessageText = errorMessageElement.getText();
                test.info("Extracted text: " + errorMessageText);
                Assert.assertEquals("Please fill in all of the required fields", errorMessageText);
                test.pass("Text extraction successful");
            } else {
                test.fail("Text not found in the element");
            }
        } catch (Exception e) {
            test.fail("Text extraction failed: " + e.getMessage());
        }
    }

    @Test
    public void testIncorrectLogin()
    {
        test = extent.createTest("Incorrect Credentials");

        String username = "incorrect@incorrect.com";
        String password = "incorrect";

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

        //Click submit button
        WebElement submitButton = driver.findElement(By.id("logIn"));
        submitButton.click();
        test.info("Submitted the login form");

        // Check error message


        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement errorText = driver.findElement(By.xpath("//p[@data-qa-id='undefined-text']"));
            Boolean isTextPresent = wait.until(ExpectedConditions.textToBePresentInElement(
                    (errorText),
                    "Please fill in all of the required fields"
            ));
            if (isTextPresent) {
                WebElement errorMessageElement = driver.findElement(By.xpath("//p[@data-qa-id='undefined-text']"));
                String errorMessageText = errorMessageElement.getText();
                test.info("Extracted text: " + errorMessageText);
                Assert.assertEquals("We don't recognize that email and/or password", errorMessageText);
                test.pass("Text extraction successful");
            } else {
                test.fail("Text not found in the element");
            }
        } catch (Exception e) {
            test.fail("Text extraction failed: " + e.getMessage());
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
