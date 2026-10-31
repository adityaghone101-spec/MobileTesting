package com.mobile.framework.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * AppiumServerManager handles starting and stopping the Appium server programmatically.
 */
public class AppiumServerManager {

    private static final Logger logger = LoggerFactory.getLogger(AppiumServerManager.class);
    private static AppiumDriverLocalService service;

    /**
     * Starts the Appium server on a random available port.
     */
    public static void startServer() {
        if (service == null || !service.isRunning()) {
            logger.info("Starting Appium Server...");
            AppiumServiceBuilder builder = new AppiumServiceBuilder();
            builder.withIPAddress("127.0.0.1")
                   .usingAnyFreePort()
                   .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                   .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                   .withArgument(GeneralServerFlag.ALLOW_INSECURE, "*:chromedriver_autodownload")
                   .withTimeout(Duration.ofSeconds(60));

            service = AppiumDriverLocalService.buildService(builder);
            service.start();
            logger.info("Appium Server started on URL: {}", service.getUrl());
        }
    }

    /**
     * Stops the Appium server if it's running.
     */
    public static void stopServer() {
        if (service != null && service.isRunning()) {
            logger.info("Stopping Appium Server...");
            service.stop();
            logger.info("Appium Server stopped.");
        }
    }

    /**
     * Gets the URL of the running Appium server.
     */
    public static String getServerUrl() {
        if (service != null && service.isRunning()) {
            return service.getUrl().toString();
        }
        return null;
    }
}
