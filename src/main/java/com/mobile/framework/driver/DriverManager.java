package com.mobile.framework.driver;

import com.mobile.framework.utils.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class DriverManager {

    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private static DriverManager instance = null;

    private DriverManager() {
    }

    public static DriverManager getInstance() {
        if (instance == null) {
            synchronized (DriverManager.class) {
                if (instance == null) {
                    instance = new DriverManager();
                }
            }
        }
        return instance;
    }

    public AppiumDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    private void initializeDriver() {
        DeviceDetector.Platform platform = DeviceDetector.detectPlatform();
        logger.info("Detected Platform: {}", platform);
        
        URL url;
        try {
            String serverUrl = AppiumServerManager.getServerUrl();
            if (serverUrl == null) {
                serverUrl = ConfigReader.getProperty("appium.server.url");
            }
            logger.info("Connecting to Appium Server at: {}", serverUrl);
            url = new URL(serverUrl);
        } catch (MalformedURLException e) {
            logger.error("Appium server URL is invalid: {}", e.getMessage());
            throw new RuntimeException("Appium server URL is invalid", e);
        }

        switch (platform) {
            case ANDROID:
                List<String> androidDevices = DeviceDetector.getAndroidDevices();
                if (androidDevices.isEmpty()) {
                    throw new RuntimeException("No Android devices found even though Platform.ANDROID was detected.");
                }
                logger.info("Total Android devices connected: {}", androidDevices.size());
                String androidId = androidDevices.get(0); // Picking the first available device
                logger.info("Initializing Android Driver for device: {}", androidId);
                
                String appPath = System.getProperty("user.dir") + ConfigReader.getProperty("android.app.path");
                logger.info("App Path: {}", appPath);
                int uiAutomator2Timeout = ConfigReader.getIntProperty("android.uiautomator2.server.launch.timeout", 120000);
                int adbExecTimeout = ConfigReader.getIntProperty("android.adb.exec.timeout", 120000);
                int appWaitDuration = ConfigReader.getIntProperty("android.app.wait.duration", 45000);
                int androidInstallTimeout = ConfigReader.getIntProperty("android.install.timeout", 120000);
                boolean autoGrantPermissions = ConfigReader.getBooleanProperty("android.auto.grant.permissions", true);
                boolean disableWindowAnimation = ConfigReader.getBooleanProperty("android.disable.window.animation", true);

                UiAutomator2Options androidOptions = new UiAutomator2Options()
                        .setDeviceName(androidId)
                        .setUdid(androidId)
                        .setApp(appPath)
                        .setAutomationName(ConfigReader.getProperty("android.automation.name"))
                        .setAppPackage(ConfigReader.getProperty("android.app.package"))
                        .setAppActivity(ConfigReader.getProperty("android.app.activity"))
                        .setNoReset(false)
                        .setAutoGrantPermissions(autoGrantPermissions)
                        .setDisableWindowAnimation(disableWindowAnimation);
                androidOptions.setCapability("uiautomator2ServerLaunchTimeout", uiAutomator2Timeout);
                androidOptions.setCapability("adbExecTimeout", adbExecTimeout);
                androidOptions.setCapability("appWaitDuration", appWaitDuration);
                androidOptions.setCapability("androidInstallTimeout", androidInstallTimeout);
                driver.set(new AndroidDriver(url, androidOptions));
                break;

            case IOS:
                List<String> iosDevices = DeviceDetector.getIOSDevices();
                if (iosDevices.isEmpty()) {
                    throw new RuntimeException("No iOS devices/simulators found even though Platform.IOS was detected.");
                }
                logger.info("Total iOS devices connected: {}", iosDevices.size());
                String iosId = iosDevices.get(0);
                logger.info("Initializing iOS Driver for device: {}", iosId);
                
                XCUITestOptions iosOptions = new XCUITestOptions()
                        .setDeviceName(iosId)
                        .setUdid(iosId)
                        .setAutomationName(ConfigReader.getProperty("ios.automation.name"))
                        .setBundleId(ConfigReader.getProperty("ios.bundle.id"))
                        .setNoReset(false);
                driver.set(new IOSDriver(url, iosOptions));
                break;

            default:
                logger.error("No mobile devices connected. Please check your adb/simctl connection.");
                throw new RuntimeException("No mobile devices connected (Android or iOS)");
        }

        int waitTime = ConfigReader.getIntProperty("implicit.wait");
        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTime));
        logger.info("Driver initialized successfully");
    }

    public void quitDriver() {
        if (driver.get() != null) {
            logger.info("Quitting driver");
            driver.get().quit();
            driver.remove();
        }
    }
}
