package sia.tacocloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By nameField = By.cssSelector("input#name");
    private final By tacoForm = By.id("tacoForm");
    private final By ingredientGroups = By.className("ingredient-group");

    public DesignPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Wait for a key element to ensure the page is loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(ingredientGroups));
    }

    public void selectIngredients(String... ingredientCodes) {
        for (String code : ingredientCodes) {
            By ingredientCheckbox = By.cssSelector("input[value='" + code + "']");
            wait.until(ExpectedConditions.elementToBeClickable(ingredientCheckbox)).click();
        }
    }

    public void nameTaco(String name) {
        driver.findElement(nameField).sendKeys(name);
    }

    public OrderFormPage submitTaco() {
        driver.findElement(tacoForm).submit();
        wait.until(ExpectedConditions.urlContains("/orders/current"));
        return new OrderFormPage(driver);
    }

    public OrderFormPage buildAndSubmitTaco(String name, String... ingredientCodes) {
        nameTaco(name);
        selectIngredients(ingredientCodes);
        return submitTaco();
    }

    // --- Assertion Methods ---
    public void assertOnDesignPage() {
        String expectedUrl = driver.getCurrentUrl().split("\\?")[0]; // remove query params if any
        assertTrue(expectedUrl.endsWith("/design"), "Not on the design page");
    }

    public int getIngredientGroupCount() {
        return driver.findElements(ingredientGroups).size();
    }

    public boolean hasIngredient(String ingredientName) {
        By ingredientSpan = By.xpath("//span[text()='" + ingredientName + "']");
        return !driver.findElements(ingredientSpan).isEmpty();
    }
}