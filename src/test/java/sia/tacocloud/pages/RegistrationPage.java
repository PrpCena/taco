package sia.tacocloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegistrationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By confirmField = By.name("confirm");
    private final By fullnameField = By.name("fullname");
    private final By streetField = By.name("street");
    private final By cityField = By.name("city");
    private final By stateField = By.name("state");
    private final By zipField = By.name("zip");
    private final By phoneField = By.name("phone");
    private final By registerForm = By.id("registerForm");

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    public void register(String username, String password, String fullname, String street, String city, String state, String zip, String phone) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(confirmField).sendKeys(password); // Assuming confirm is same as password
        driver.findElement(fullnameField).sendKeys(fullname);
        driver.findElement(streetField).sendKeys(street);
        driver.findElement(cityField).sendKeys(city);
        driver.findElement(stateField).sendKeys(state);
        driver.findElement(zipField).sendKeys(zip);
        driver.findElement(phoneField).sendKeys(phone);
        driver.findElement(registerForm).submit();
    }
}