package com.mobile.framework.utils;

public class Constants {

    // --- Unified Locators (Same for Android & iOS) ---
    // Best Practice: Use Accessibility IDs (content-desc in Android, accessibility-id in iOS)
    public static final String UNIFIED_USERNAME = "username_field";
    public static final String UNIFIED_PASSWORD = "password_field";
    public static final String UNIFIED_LOGIN_BUTTON = "login_button";
    public static final String UNIFIED_ERROR_MESSAGE = "error_message";

    // Dynamic XPath Templates
    public static final String XPATH_TEXT = "//*[@text='%s' or @label='%s']";
    public static final String XPATH_INPUT = "//android.widget.EditText[@resource-id='com.example.app:id/%s']";
    public static final String XPATH_CONTENT_DESC = "//*[@content-desc='%s']";
    public static final String XPATH_IOS_NAME_OR_LABEL = "//*[@name='%s' or @label='%s']";
    public static final String XPATH_IOS_VALUE = "//*[@value='%s']";
    public static final String XPATH_IOS_TYPE_AND_NAME = "//%s[@name='%s']";
}
