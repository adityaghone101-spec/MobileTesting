package com.mobile.tests;

import com.mobile.framework.pages.LoginPage;
import com.mobile.framework.utils.DatabaseUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Verify successful login and alert acceptance")
    public void testSuccessfulLoginAndAcceptAlert() {
        LoginPage loginPage = new LoginPage();
        loginPage.login("admin", "admin123");
        
        String alertText = loginPage.getAlertMessage();
        Assert.assertNotNull(alertText, "Alert should be present on successful login!");
        Assert.assertTrue(alertText.contains("Login Successful"), "Success message mismatch! Actual: " + alertText);
        
        loginPage.acceptLoginAlert();
        // Additional check: verify alert is gone
        Assert.assertNull(loginPage.getAlertMessage(), "Alert should be dismissed after accepting!");
    }

    @Test(priority = 2, description = "Verify invalid login and alert message")
    public void testInvalidLoginAndVerifyAlert() {
        LoginPage loginPage = new LoginPage();
        loginPage.login("wrong_user", "wrong_pass");
        
        String alertText = loginPage.getAlertMessage();
        Assert.assertNotNull(alertText, "Alert should be present on invalid login!");
        Assert.assertTrue(alertText.contains("Login Failed"), "Failure message mismatch! Actual: " + alertText);
        Assert.assertTrue(alertText.contains("Wrong username or password"), "Error details mismatch!");
        
        loginPage.acceptLoginAlert();
    }

    @Test(priority = 3, description = "Verify login using data retrieved from database")
    public void testLoginWithDBData() {
        DatabaseUtils db = new DatabaseUtils();
        
        // Retrieve valid credentials from database (example query)
        String query = "SELECT username, password FROM users WHERE role='admin' LIMIT 1";
        Map<String, String> dbData = db.getDataRow(query);
        
        if (dbData.isEmpty()) {
            db.close();
            Assert.fail("No data found in database for the given query!");
        }

        String dbUser = dbData.get("username");
        String dbPass = dbData.get("password");

        LoginPage loginPage = new LoginPage();
        loginPage.login(dbUser, dbPass);
        
        String alertText = loginPage.getAlertMessage();
        Assert.assertNotNull(alertText, "Alert should be present after login with DB data!");
        Assert.assertTrue(alertText.contains("Login Successful"), "Login failed using credentials from DB: " + dbUser);
        
        loginPage.acceptLoginAlert();
        db.close();
    }
}
