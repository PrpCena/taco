package sia.tacocloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFormPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By nameField = By.cssSelector("input[name='deliveryName']");
    private final By streetField = By.cssSelector("input[name='deliveryStreet']");
    private final By cityField = By.cssSelector("input[name='deliveryCity']");
    private final By stateField = By.cssSelector("input[name='deliveryState']");
    private final By zipField = By.cssSelector("input[name='deliveryZip']");
    private final By ccNumberField = By.cssSelector("input[name='ccNumber']");
    private final By ccExpirationField = By.cssSelector("input[name='ccExpiration']");
    private final By ccCVVField = By.cssSelector("input[name='ccCVV']");
    private final By orderForm = By.id("orderForm");
    private final By anotherTacoButton = By.id("another");
    private final By validationErrorMessages = By.className("validationError");

    public OrderFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
    }

    public void fillField(By locator, String value) {
        WebElement field = driver.findElement(locator);
        field.clear();
        field.sendKeys(value);
    }

    public void fillAndSubmitOrder(String name, String street, String city, String state, String zip,
                                       String ccNumber, String ccExpiration, String ccCVV) {
        fillField(nameField, name);
        fillField(streetField, street);
        fillField(cityField, city);
        fillField(stateField, state);
        fillField(zipField, zip);
        fillField(ccNumberField, ccNumber);
        fillField(ccExpirationField, ccExpiration);
        fillField(ccCVVField, ccCVV);

        driver.findElement(orderForm).submit();
    }

    public void clearAndSubmitEmpty() {
        fillField(nameField, "");
        fillField(streetField, "");
        fillField(cityField, "");
        fillField(stateField, "");
        fillField(zipField, "");
        // Don't clear CC fields as they are not pre-populated
        driver.findElement(orderForm).submit();
    }

    public DesignPage clickBuildAnotherTaco() {
        wait.until(ExpectedConditions.elementToBeClickable(anotherTacoButton)).click();
        return new DesignPage(driver);
    }

    public List<String> getValidationErrors() {
        List<WebElement> errorElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(validationErrorMessages));
        return errorElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}