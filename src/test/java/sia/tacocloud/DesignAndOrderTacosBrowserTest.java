package sia.tacocloud;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sia.tacocloud.pages.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DesignAndOrderTacosBrowserTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    // Use a counter to ensure unique usernames for each test run
    private static final AtomicInteger userCounter = new AtomicInteger(1);

    // Increased timeout for more reliable testing
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    @BeforeAll
    void setupWebDriverManager() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        // Modern Chrome options for better stability
        options.addArguments(
                "--headless=new", // Use new headless mode
                "--disable-gpu",
                "--disable-dev-shm-usage", // Overcome limited resource problems
                "--no-sandbox", // Required for Docker/CI environments
                "--disable-extensions",
                "--disable-background-timer-throttling",
                "--disable-backgrounding-occluded-windows",
                "--disable-renderer-backgrounding",
                "--window-size=1920,1080",
                "--force-device-scale-factor=1" // Ensure 100% zoom level
        );

        driver = new ChromeDriver(options);
        // Set page load and script timeouts
        driver.manage().timeouts().pageLoadTimeout(DEFAULT_TIMEOUT);
        driver.manage().timeouts().scriptTimeout(DEFAULT_TIMEOUT);

        baseUrl = "http://localhost:" + port;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Log but don't fail test on cleanup issues
                System.err.println("Error during driver cleanup: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Full happy path: Register, login, design two tacos, and place order")
    void testDesignATacoPage_HappyPath() {
        try {
            // --- Phase 1: Registration and Login ---
            OrderFormPage orderPage = setupUserAndFirstTaco();

            // --- Phase 2: Design a second taco ---
            DesignPage designPage = orderPage.clickBuildAnotherTaco();
            designPage.assertOnDesignPage();
            designPage.buildAndSubmitTaco("Another Taco", "COTO", "CARN", "JACK", "LETC", "SRCR");

            // --- Phase 3: Fill order form and submit ---
            OrderFormPage finalOrderPage = new OrderFormPage(driver);
            finalOrderPage.fillAndSubmitOrder("Ima Hungry", "1234 Culinary Blvd.", "Foodsville", "CO", "81019", "4111111111111111", "10/29", "123");

            // --- Phase 4: Assert final state and logout ---
            // More robust waiting for redirect
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlToBe(baseUrl + "/"),
                    ExpectedConditions.urlMatches(".*/$")
            ));

            assertEquals(baseUrl + "/", driver.getCurrentUrl(), "Should be redirected to home page after order");

            HomePage homePage = new HomePage(driver);
            homePage.logout();

        } catch (Exception e) {
            // Take screenshot on failure for debugging
            System.err.println("Test failed at: " + driver.getCurrentUrl());
            throw e;
        }
    }

    @Test
    @DisplayName("Test order submission with an empty form, then correct it")
    void testDesignATacoPage_EmptyOrderInfo() {
        try {
            // --- Setup: Register, login, create first taco ---
            OrderFormPage orderPage = setupUserAndFirstTaco();

            // --- Action: Submit empty form and check for errors ---
            orderPage.clearAndSubmitEmpty();

            // Wait for validation errors to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    org.openqa.selenium.By.className("validationError")
            ));

            List<String> validationErrors = orderPage.getValidationErrors();

            assertEquals(9, validationErrors.size(), "Should have 9 validation errors");
            assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
            assertTrue(validationErrors.contains("Delivery name is required"));
            assertTrue(validationErrors.contains("Street is required"));
            assertTrue(validationErrors.contains("City is required"));
            assertTrue(validationErrors.contains("State is required"));
            assertTrue(validationErrors.contains("Zip code is required"));
            assertTrue(validationErrors.contains("Not a valid credit card number"));
            assertTrue(validationErrors.contains("Must be formatted MM/YY"));
            assertTrue(validationErrors.contains("Invalid CVV"));

            // --- Recovery: Fill form correctly and submit ---
            orderPage.fillAndSubmitOrder("Ima Hungry", "1234 Culinary Blvd.", "Foodsville", "CO", "81019", "4111111111111111", "10/29", "123");

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlToBe(baseUrl + "/"),
                    ExpectedConditions.urlMatches(".*/$")
            ));

            new HomePage(driver).logout();

        } catch (Exception e) {
            System.err.println("Empty form test failed at: " + driver.getCurrentUrl());
            throw e;
        }
    }

    @Test
    @DisplayName("Test order submission with an invalid form, then correct it")
    void testDesignATacoPage_InvalidOrderInfo() {
        try {
            OrderFormPage orderPage = setupUserAndFirstTaco();

            // Submit invalid form
            orderPage.fillAndSubmitOrder("I", "1", "F", "C", "8", "1234123412341234", "99/99", "abc");

            // Wait for validation errors to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    org.openqa.selenium.By.className("validationError")
            ));

            // Assert invalid state
            List<String> validationErrors = orderPage.getValidationErrors();
            assertEquals(4, validationErrors.size(), "Should have 4 validation errors for invalid data");
            assertTrue(validationErrors.contains("Not a valid credit card number"));

            // Submit valid form for recovery
            orderPage.fillAndSubmitOrder("Ima Hungry", "1234 Culinary Blvd.", "Foodsville", "CO", "81019", "4111111111111111", "10/29", "123");

            // More robust URL waiting - wait for any change from orders page
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/orders")));

            // Now assert the final URL
            assertEquals(baseUrl + "/", driver.getCurrentUrl(), "Should be redirected to home page after successful order");

            new HomePage(driver).logout();

        } catch (Exception e) {
            System.err.println("Invalid form test failed at: " + driver.getCurrentUrl());
            throw e;
        }
    }

    /**
     * Helper method to perform the common setup steps with improved error handling:
     * 1. Navigates to the home page.
     * 2. Clicks 'Design a Taco' to go to the login page.
     * 3. Registers a new, unique user.
     * 4. Logs in as that new user.
     * 5. Designs and submits the first taco.
     * @return An {@link OrderFormPage} instance, ready for the next action.
     */
    private OrderFormPage setupUserAndFirstTaco() {
        // Navigate to home page with retry logic
        driver.get(baseUrl);

        // Wait for page to be fully loaded
        wait.until(ExpectedConditions.presenceOfElementLocated(
                org.openqa.selenium.By.tagName("body")
        ));

        HomePage homePage = new HomePage(driver);

        // Go to login page, then to registration
        LoginPage loginPage = homePage.clickDesignATaco();
        RegistrationPage regPage = loginPage.goToRegistration();

        // Register a unique user for this test
        String username = "testuser" + userCounter.getAndIncrement() + "_" + System.currentTimeMillis();
        String password = "testpassword";
        regPage.register(username, password, "Test User", "123 Test St", "Testville", "TS", "12345", "123-123-1234");

        // Wait for successful registration and redirect to login
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("username"))
        ));

        loginPage = new LoginPage(driver);
        DesignPage designPage = loginPage.login(username, password);

        // Assert we landed on the design page and its elements are correct
        designPage.assertOnDesignPage();
        assertDesignPageElements(designPage);

        // Build and submit the first taco
        return designPage.buildAndSubmitTaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    }

    private void assertDesignPageElements(DesignPage designPage) {
        // Add waits to ensure elements are loaded
        wait.until(ExpectedConditions.presenceOfElementLocated(
                org.openqa.selenium.By.className("ingredient-group")
        ));

        assertEquals(5, designPage.getIngredientGroupCount());
        assertTrue(designPage.hasIngredient("Flour Tortilla"));
        assertTrue(designPage.hasIngredient("Corn Tortilla"));
        assertTrue(designPage.hasIngredient("Ground Beef"));
        assertTrue(designPage.hasIngredient("Carnitas"));
        assertTrue(designPage.hasIngredient("Cheddar"));
        assertTrue(designPage.hasIngredient("Monterrey Jack"));
        assertTrue(designPage.hasIngredient("Diced Tomatoes"));
        assertTrue(designPage.hasIngredient("Lettuce"));
        assertTrue(designPage.hasIngredient("Salsa"));
        assertTrue(designPage.hasIngredient("Sour Cream"));
    }
}