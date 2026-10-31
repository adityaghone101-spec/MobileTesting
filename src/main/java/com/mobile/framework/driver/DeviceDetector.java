package com.mobile.framework.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetector {

    private static final Logger logger = LoggerFactory.getLogger(DeviceDetector.class);

    public enum Platform {
        ANDROID, IOS, NONE
    }

    public static Platform detectPlatform() {
        if (!getAndroidDevices().isEmpty()) {
            return Platform.ANDROID;
        } else if (!getIOSDevices().isEmpty()) {
            return Platform.IOS;
        }
        return Platform.NONE;
    }

    public static List<String> getAndroidDevices() {
        List<String> devices = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("adb devices");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith("device")) {
                    devices.add(line.split("\t")[0]);
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting Android devices: {}", e.getMessage());
        }
        return devices;
    }

    public static List<String> getIOSDevices() {
        List<String> devices = new ArrayList<>();
        // Try real devices first using idevice_id (requires libimobiledevice)
        try {
            Process process = Runtime.getRuntime().exec("idevice_id -l");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    devices.add(line.trim());
                }
            }
        } catch (Exception e) {
            // If idevice_id fails, try simctl for simulators
            try {
                Process process = Runtime.getRuntime().exec("xcrun simctl list devices");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("(Booted)")) {
                        // Extract UUID from line like "iPhone 14 (UUID) (Booted)"
                        int start = line.lastIndexOf("(") + 1;
                        int end = line.lastIndexOf(")");
                        if (start > 0 && end > start) {
                            devices.add(line.substring(start, end));
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("Error detecting iOS devices: {}", ex.getMessage());
            }
        }
        return devices;
    }
}
