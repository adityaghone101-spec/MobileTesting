package com.mobile.framework.pages;

import com.mobile.framework.utils.Constants;
import java.time.Duration;

/**
 * LoginPage uses dynamic parameterized actions inherited from BasePage.
 * This keeps the page class lean and centralizes locators in Constants.
 */
public class LoginPage extends BasePage {

    /**
     * Performs login using unified Accessibility IDs (same for Android & iOS).
     */
    public void login(String username, String password) {
        enterTextByAccessibilityId(Constants.UNIFIED_USERNAME, username);
        enterTextByAccessibilityId(Constants.UNIFIED_PASSWORD, password);
        tapByAccessibilityId(Constants.UNIFIED_LOGIN_BUTTON);
    }

    public String getAlertMessage() {
        try {
            return waitForAlert().getText();
        } catch (Exception e) {
            return null;
        }
    }

    public void acceptLoginAlert() {
        acceptAlert();
    }
}
