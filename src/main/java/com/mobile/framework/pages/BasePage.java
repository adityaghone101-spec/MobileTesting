package com.mobile.framework.pages;

import com.mobile.framework.driver.DriverManager;
import com.mobile.framework.utils.Constants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * BasePage provides the core engine for all interactions.
 * It includes both WebElement-based methods and Parameterized methods (using XPaths from Constants).
 */
public abstract class BasePage {

    protected AppiumDriver driver;
    protected WebDriverWait wait;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public BasePage() {
        this.driver = DriverManager.getInstance().getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Parameterized Dynamic Methods (Using Constants) ---

    protected void tapByText(String text) {
        String xpath = String.format(Constants.XPATH_TEXT, text, text);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    /**
     * Unified method to tap on an element using its Accessibility ID.
     * Works for both Android (content-desc) and iOS (accessibility-id).
     */
    protected void tapByAccessibilityId(String id) {
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId(id))).click();
    }

    protected void tapById(String id) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id(id))).click();
    }

    protected void enterTextById(String id, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Unified method to enter text using Accessibility ID.
     * Works for both Android and iOS.
     */
    protected void enterTextByAccessibilityId(String id, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(id)));
        element.clear();
        element.sendKeys(text);
    }

    protected String getTextByContentDesc(String contentDesc) {
        String xpath = String.format(Constants.XPATH_CONTENT_DESC, contentDesc);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))).getText();
    }

    public boolean isElementVisibleByText(String text) {
        try {
            String xpath = String.format(Constants.XPATH_TEXT, text, text);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // --- Popup / Alert Actions ---

    protected Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    protected void acceptAlert() {
        try {
            waitForAlert().accept();
        } catch (Exception e) {
            String[] acceptTexts = {"Accept", "Allow", "OK", "Yes", "Confirm"};
            for (String text : acceptTexts) {
                try {
                    String xpath = String.format(Constants.XPATH_TEXT, text, text);
                    WebElement btn = driver.findElement(By.xpath(xpath));
                    if (btn.isDisplayed()) {
                        btn.click();
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    protected void declineAlert() {
        try {
            driver.switchTo().alert().dismiss();
        } catch (Exception e) {
            String[] declineTexts = {"Decline", "Deny", "Cancel", "No", "Dismiss"};
            for (String text : declineTexts) {
                try {
                    String xpath = String.format(Constants.XPATH_TEXT, text, text);
                    WebElement btn = driver.findElement(By.xpath(xpath));
                    if (btn.isDisplayed()) {
                        btn.click();
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    // --- Scrolling Methods ---

    protected void scroll(Direction direction, double scrollRatio) {
        if (scrollRatio < 0 || scrollRatio > 1) scrollRatio = 0.5;
        Dimension size = driver.manage().window().getSize();
        int centerX = size.getWidth() / 2;
        int centerY = size.getHeight() / 2;
        int startX, startY, endX, endY;

        switch (direction) {
            case UP:
                startX = centerX;
                startY = (int) (size.getHeight() * (0.5 - scrollRatio / 2));
                endX = centerX;
                endY = (int) (size.getHeight() * (0.5 + scrollRatio / 2));
                break;
            case DOWN:
                startX = centerX;
                startY = (int) (size.getHeight() * (0.5 + scrollRatio / 2));
                endX = centerX;
                endY = (int) (size.getHeight() * (0.5 - scrollRatio / 2));
                break;
            case LEFT:
                startX = (int) (size.getWidth() * (0.5 + scrollRatio / 2));
                startY = centerY;
                endX = (int) (size.getWidth() * (0.5 - scrollRatio / 2));
                endY = centerY;
                break;
            case RIGHT:
                startX = (int) (size.getWidth() * (0.5 - scrollRatio / 2));
                startY = centerY;
                endX = (int) (size.getWidth() * (0.5 + scrollRatio / 2));
                endY = centerY;
                break;
            default:
                throw new IllegalArgumentException("Invalid scroll direction");
        }
        performSwipe(startX, startY, endX, endY, Duration.ofMillis(1000));
    }

    protected boolean scrollToText(String text, Direction direction, int maxSwipes) {
        String xpath = String.format(Constants.XPATH_TEXT, text, text);
        By locator = By.xpath(xpath);
        for (int i = 0; i < maxSwipes; i++) {
            try {
                if (driver.findElement(locator).isDisplayed()) return true;
            } catch (Exception e) {
                scroll(direction, 0.5);
            }
        }
        return false;
    }

    // --- Private Helper Methods for W3C Actions ---

    private void performTap(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
    }

    private void performSwipe(int startX, int startY, int endX, int endY, Duration duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }
}
