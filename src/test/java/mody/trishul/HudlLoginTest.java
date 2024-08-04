package mody.trishul;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class HudlLoginTest {

    private static WebDriver driver;
    private static ExtentReports extent;
    private static ExtentTest test;

    private static final String correct_username = "emailed_username"; // add username sent in email;
    private static final String correct_password = "emailed_password"; //add password sent in email;

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
    public void captureScreenshot(String filePath) {
        TakesScreenshot screenshotTaker = (TakesScreenshot) driver;
        File screenshotFile = screenshotTaker.getScreenshotAs(OutputType.FILE);
        try {
            FileHandler.copy(screenshotFile, new File(filePath));
            System.out.println("Screenshot saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
    private void login(String username, String password)
    {
        driver.get("https://www.hudl.com/login");
        test.info("Opened Hudl login page");

        WebElement usernameField = driver.findElement(By.id("email"));
        usernameField.sendKeys(username);
        test.info("Entered username");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(password);
        test.info("Entered password");

        WebElement submitButton = driver.findElement(By.id("logIn"));
        submitButton.click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        test.info("Submitted the login form");
    }

    private void verifyText(String xpath, String expectedText) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            String actualText = element.getText();
            test.info("Extracted text: " + actualText);
            Assert.assertEquals(expectedText, actualText);
            test.pass("Text extraction successful");
        } catch (Exception e) {
            test.fail("Text extraction failed: " + e.getMessage());
        }
    }

    @Test
    public void testValidLogin() throws IOException
    {
        test = extent.createTest("Correct Credentials");

        String username = correct_username;
        String password = correct_password;

        login(username, password);
        verifyText("//span[text()='Trishul M']", "Trishul M");
        captureScreenshot("screenshots/valid_login.png");
        test.info("CORRECT LOGIN TEST SCREENSHOT: ").addScreenCaptureFromPath("screenshots/valid_login.png");
    }

    @Test
    public void testEmptyLogin() throws IOException
    {
        test = extent.createTest("Blank Credentials");

        login("", "");
        verifyText("//p[@data-qa-id='undefined-text']", "Please fill in all of the required fields");
        captureScreenshot("screenshots/empty_login.png");
        test.info("EMPTY LOGIN TEST SCREENSHOT: ").addScreenCaptureFromPath("screenshots/empty_login.png");
    }

    @Test
    public void testIncorrectLogin() throws IOException
    {
        test = extent.createTest("Incorrect Credentials");

        String username = "incorrect@incorrect.com";
        String password = "incorrect";

        login(username, password);
        verifyText("//p[@data-qa-id='undefined-text']", "We don't recognize that email and/or password");
        captureScreenshot("screenshots/incorrect_login.png");
        test.info("INCORRECT LOGIN TEST SCREENSHOT: ").addScreenCaptureFromPath("screenshots/incorrect_login.png");

    }

    @Test
    public void testForgotPasswordLink() throws IOException
    {
        test = extent.createTest("Forgot Password Link");

        driver.get("https://www.hudl.com/login");
        test.info("Opened Hudl login page");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement forgotPasswordLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("forgot-password")));
        forgotPasswordLink.click();
        test.info("Clicked on Forgot Password link");

        WebElement headlineElement = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//h2[text()='Forgot Password']"))));
        String headlineText = headlineElement.getText();
        Assert.assertEquals("Forgot Password",headlineText);

        WebElement paragraphElement = driver.findElement(By.id("email-reset-help"));
        String paragraphText = paragraphElement.getText();
        Assert.assertEquals("We need to verify it's you. You'll receive an email with a verification code to reset your password.",paragraphText);
        test.pass("Navigated to Forgot Password page successfully");
        captureScreenshot("screenshots/forgot_password.png");
        test.info("FORGOT PASSWORD TEST SCREENSHOT: ").addScreenCaptureFromPath("screenshots/forgot_password.png");


    }

    @Test
    public void testSqlInjectionLogin() throws IOException
    {
        test = extent.createTest("SQL Injection Attempt");

        String username = "' OR '1'='1";
        String password = "' OR '1'='1";

        login(username, password);
        verifyText("//p[@data-qa-id='undefined-text']", "We don't recognize that email and/or password");
        captureScreenshot("screenshots/sql_injection.png");
        test.info("SQL INJECTION TEST SCREENSHOT: ").addScreenCaptureFromPath("screenshots/sql_injection.png");

    }

    @Test
    public void testXssAttemptLogin() throws IOException
    {
        test = extent.createTest("XSS Attempt");

        String username = "<script>alert('XSS');</script>";
        String password = "<script>alert('XSS');</script>";

        login(username, password);
        verifyText("//p[@data-qa-id='undefined-text']", "We don't recognize that email and/or password");
        captureScreenshot("screenshots/xss_attempt.png");
        test.info("XSS ATTEMPT TEST SCREENSHOT: ").addScreenCaptureFromPath("screenshots/xss_attempt.png");

    }


    @Test
    public void testLogoutFunctionality() throws IOException
    {
        test = extent.createTest("Logout Functionality");

        String username = correct_username;
        String password = correct_password;

        login(username, password);
        verifyText("//span[text()='Trishul M']", "Trishul M");

        WebElement dropdownMenu = driver.findElement(By.className("hui-globaluseritem__display-name"));
        Actions actions = new Actions(driver);
        actions.moveToElement(dropdownMenu).perform();


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        List<WebElement> logoutLink = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//a[@data-qa-id='webnav-usermenu-logout']")
        ));
        captureScreenshot("screenshots/before_logout.png");
        test.info("BEFORE LOGOUT SCREENSHOT: ").addScreenCaptureFromPath("screenshots/before_logout.png");

        logoutLink.get(0).click();

        test.info("Clicked on Log Out");

        WebElement loginElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@data-qa-id='login-select']")));
        String text = loginElement.getText().trim();
        Assert.assertEquals(text, "Log in");
        test.pass("Logout functionality works as expected");
        captureScreenshot("screenshots/after_logout.png");
        test.info("AFTER LOGOUT SCREENSHOT: ").addScreenCaptureFromPath("screenshots/after_logout.png");

    }

}
