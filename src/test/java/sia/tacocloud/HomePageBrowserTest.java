package sia.tacocloud;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sia.tacocloud.pages.HomePage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HomePageBrowserTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void init() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testHomePageLoadsCorrectly() {
        String url = "http://localhost:" + port;
        driver.get(url);

        HomePage homePage = new HomePage(driver);
        Assertions.assertEquals("Taco Cloud", homePage.getTitle());
        Assertions.assertEquals("Welcome to...", homePage.getHeaderText());
        Assertions.assertTrue(
                homePage.getLogoSrc().endsWith("/images/TacoCloud.png"),
                "Logo path should end with '/images/TacoCloud.png'"
        );
    }
}
