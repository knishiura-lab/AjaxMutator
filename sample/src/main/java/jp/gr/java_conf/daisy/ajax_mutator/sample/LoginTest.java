package jp.gr.java_conf.daisy.ajax_mutator.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test case for <a href="http://www.jamesdam.com/ajax_login/login.html">
 * AjaxLogin</a>. Before executing this class, you must install AjaxLogin in
 * your environment and set it up properly so that you can access it via browser
 *
 * @author Kazuki Nishiura
 */
public class LoginTest {
    private static WebDriver driver;
    private static long WAIT_LIMIT_SEC = 1;

    @BeforeClass
    static public void setUpBrowser() {
        driver = new FirefoxDriver();
    }

    @AfterClass
    static public void terminateBrowser() {
        driver.close();
    }

    @Before
    public void accessURL() {
        driver.get("Path_to_AjaxLogin/login.html");
    }

    @Test
    public void testLoginSuccessAndLogout() {
        tryLogin("name", "pass");
        WebElement loginResult = getLoginResult();
        assertEquals("full.name", loginResult.getText());

        WebElement logoutA = driver.findElement(By.id("login")).findElement(By.tagName("a"));
        logoutA.click();
        WebDriverWait wait = new WebDriverWait(driver, WAIT_LIMIT_SEC);
        WebElement result = null;
        try {
            result = wait.until(new ExpectedCondition<WebElement>(){
                @Override
                public WebElement apply(WebDriver driver) {
                    WebElement loginDiv = driver.findElement(By.id("login"));
                    WebElement p = loginDiv.findElement(By.tagName("p"));
                    if ("Enter your username and password to log in.".equals(p.getText())) {
                        return p;
                    } else {
                        return null;
                    }
                }});
        } catch (Exception e) {
            System.err.print(e.getClass());
            fail("Afgter logout text should be set appropriately");
            return;
        }
        assertTrue("After logout text should be set", result != null);
    }

    @Test
    public void testLoginInvalidPassword() {
        tryLogin("name", "passs");
        WebElement loginResult = getLoginResult();
        assertEquals("Invalid username and password combination.", loginResult.getText());
    }

    @Test
    public void testLoginInvalidUserName() {
        tryLogin("invalid_name", "pass");
        WebElement loginResult = getLoginResult();
        assertEquals("Invalid username and password combination.", loginResult.getText());
    }

    private void tryLogin(String userName, String password) {
        WebElement userNameInput = driver.findElement(By.name("username"));
        userNameInput.sendKeys(userName);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.click();
        passwordInput.sendKeys(password);

        // set focus to another element
        driver.findElement(By.id("comments")).click();
    }

    private WebElement getLoginResult() {
        WebElement loginedResult = null;
        try {
            loginedResult = (new WebDriverWait(driver, WAIT_LIMIT_SEC))
                .until(new ExpectedCondition<WebElement>(){
                    @Override
                    public WebElement apply(WebDriver driver) {
                        WebElement loginDiv = driver.findElement(By.id("login"));
                        WebElement p = loginDiv.findElement(By.tagName("p"));
                        return p.findElement(By.tagName("strong"));
                    }});
        } catch (Exception e) {
            fail("Timeout!");
        }
        return loginedResult;
    }
}
