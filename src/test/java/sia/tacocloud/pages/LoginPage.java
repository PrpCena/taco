package sia.tacocloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By registerLink = By.linkText("here");
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginForm = By.id("loginForm");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    public RegistrationPage goToRegistration() {
        driver.findElement(registerLink).click();
        return new RegistrationPage(driver);
    }

    public DesignPage login(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginForm).submit();
        return new DesignPage(driver);
    }
}