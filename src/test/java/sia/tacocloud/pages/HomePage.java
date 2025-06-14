package sia.tacocloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By designTacoButton = By.cssSelector("a#design");
    private final By logoutButton = By.cssSelector("form#logoutForm input[type='submit']");
    private final By header = By.tagName("h1");
    private final By logo = By.tagName("img");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage clickDesignATaco() {
        wait.until(ExpectedConditions.elementToBeClickable(designTacoButton)).click();
        return new LoginPage(driver);
    }

    public void logout() {
        if (!driver.findElements(logoutButton).isEmpty()) {
            driver.findElement(logoutButton).click();
        }
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getHeaderText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(header)).getText();
    }

    public String getLogoSrc() {
        WebElement logoElement = wait.until(ExpectedConditions.presenceOfElementLocated(logo));
        String src = logoElement.getDomAttribute("src");
        // Use getAttribute for src instead of getDomAttribute for better compatibility
        assertTrue(src.endsWith("/images/TacoCloud.png"), "Logo src attribute is incorrect");
        return src;
    }
}