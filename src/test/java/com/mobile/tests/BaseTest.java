package com.mobile.tests;

import com.mobile.framework.driver.AppiumServerManager;
import com.mobile.framework.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    @BeforeSuite
    public void startAppiumServer() {
        AppiumServerManager.startServer();
    }

    @BeforeMethod
    public void setUp() {
        // Driver is initialized automatically on first getDriver() call
        DriverManager.getInstance().getDriver();
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.getInstance().quitDriver();
    }

    @AfterSuite
    public void stopAppiumServer() {
        AppiumServerManager.stopServer();
    }
}
