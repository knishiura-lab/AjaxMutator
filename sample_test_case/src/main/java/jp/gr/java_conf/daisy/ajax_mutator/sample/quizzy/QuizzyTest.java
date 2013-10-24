package jp.gr.java_conf.daisy.ajax_mutator.sample.quizzy;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

/**
 * This is a sample test case for our sample app using quizzy library
 * {@see http://quizzy.sourceforge.net/}.
 * You need to set test.properties file so that this class can refer URL of web app and php file
 * of the app.
 *
 * @author Kazuki Nishiura
 */
public class QuizzyTest {
    private static final String PROPERTIES_FILE_PATH = "test.properties";
    private static final String TARGET_URL_KEY = "target_url";
    private static final String CONFIG_PHP_FILE_PATH = "config_php_file_path";
    private static final String FIRST_QUIZ_TITLE = "Some quizzes about Japan";
    private static String targetURL;
    // We possess this reference so that test we can easily emulate server-side delay.
    private static File configPhpFile;
    private static boolean propertiesSuccessfullyRead;
    private static WebDriver driver;
    private static long WAIT_LIMIT_SEC = 3;
    private WebDriverWait wait;

    @BeforeClass
    static public void setUpBrowser() {
        driver = new FirefoxDriver();
        readProperties();
    }

    private static void readProperties() {
        try {
            Properties prop = new Properties();
            prop.load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTIES_FILE_PATH));
            targetURL = prop.getProperty(TARGET_URL_KEY);
            String phpFilePath = prop.getProperty(CONFIG_PHP_FILE_PATH);
            if (targetURL == null || phpFilePath == null) {
                System.err.println("Cannot read properties file's content. You MUST define "
                        + TARGET_URL_KEY + " and " + CONFIG_PHP_FILE_PATH);
                return;
            }
            configPhpFile = new File(prop.getProperty(CONFIG_PHP_FILE_PATH));
            if (!configPhpFile.exists()) {
                System.err.println("File " + configPhpFile.getAbsolutePath() + " does not exist.");
                return;
            }
            propertiesSuccessfullyRead = true;
        } catch (FileNotFoundException e) {
            System.err.println("Property file not found. You MUST create " + PROPERTIES_FILE_PATH
            + " file and define " + TARGET_URL_KEY + " and " + CONFIG_PHP_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Cannot open properties file " + PROPERTIES_FILE_PATH);
        }
    }

    @AfterClass
    static public void terminateBrowser() {
        driver.close();
    }

    @Before
    public void setup() {
        Assume.assumeTrue(propertiesSuccessfullyRead);
        loadUrl();
        wait = new WebDriverWait(driver, WAIT_LIMIT_SEC);
    }

    private void loadUrl() {
        driver.get(targetURL);
    }

    // Test what should happen is actually happens
    @Test
    public void followUseCase() {
        WebElement element = wait.until(visibilityOfElementLocated(By.tagName("input")));
        element.click();
        WebElement desc = wait.until(visibilityOfElementLocated(By.className("quizzy_quiz_desc")));
        assertEquals(FIRST_QUIZ_TITLE, desc.getText());

        // start quiz
        click(By.id("quizzy_start_b"));
        WebElement checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

        // choice first option and answer
        click(By.id("quizzy_q0_opt0"));
        checkButton.click();

        WebElement nextButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
        WebElement best = findElement(By.className("quizzy_opt_best"));
        assertTrue(best != null);
        assertEquals("✓", best.getText());
        nextButton.click();

        // choice first option and answer
        checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));
        click(By.id("quizzy_q1_opt0"));
        checkButton.click();
        WebElement mid = wait.until(visibilityOfElementLocated(By.className("quizzy_opt_mid")));
        assertEquals("5", mid.getText());
        desc = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_exp")));
        desc = desc.findElement(By.tagName("p"));
        assertEquals("It's on February", desc.getText());

        nextButton = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_nxt")));
        nextButton.click();

        // get score
        WebElement score = wait.until(visibilityOfElementLocated(By.className("quizzy_result_score")));
        assertEquals("20", score.getText());

        WebElement againButton = findElement(By.className("quizzy_result_foot")).findElement(By.tagName("input"));
        againButton.click();

        // user can start quiz again
        wait.until(elementToBeClickable(By.id("quizzy_start_b")));
    }

    // test what shouldn't happen actually do not happen
    @Test
    public void tryInvalidOperation() {
        WebElement startButton = wait.until(visibilityOfElementLocated(By.id("quizzy_start_b")));
        // we cannot start quiz before selecting option
        startButton.click();
        try {
            wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));
            fail("test should not start");
        } catch (TimeoutException e) {
            // expected exception
        }

        // start quiz
        click(By.tagName("input"));
        click(By.id("quizzy_start_b"));
        WebElement checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

        // choice first option and answer
        click(By.id("quizzy_q0_opt0"));
        checkButton.click();
        WebElement nextButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
        nextButton.click();

        checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));

        // we cannot check answer unless we choice option
        checkButton.click();
        try {
            wait.until(visibilityOfElementLocated(By.className("quizzy_opt_mid")));
            fail("we cannot check answer unless choosing option");
        } catch (TimeoutException e) {
            // expected exception
        }
    }

    // test for sliding up. when user choose option, the one user choose and the
    // best one is left, other options must be hidden.
    @Test
    public void testSlideUp() throws InterruptedException {
        WebElement element = wait.until(visibilityOfElementLocated(By.tagName("input")));
        element.click();

        // start quiz
        click(By.id("quizzy_start_b"));
        WebElement checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

        // choice first option and answer
        click(By.id("quizzy_q0_opt0"));
        checkButton.click();

        WebElement nextButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
        assertTrue(findElement(By.id("quizzy_q0_opt0")).isDisplayed());
        assertFalse(findElement(By.id("quizzy_q0_opt1")).isDisplayed());
        assertFalse(findElement(By.id("quizzy_q0_opt2")).isDisplayed());
        nextButton.click();

        // choice first option and answer
        checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));
        click(By.id("quizzy_q1_opt2"));
        checkButton.click();
        Thread.sleep(1000);
        nextButton = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_nxt")));

        assertFalse(findElement(By.id("quizzy_q1_opt0")).isDisplayed());
        assertTrue(findElement(By.id("quizzy_q1_opt1")).isDisplayed());
        assertTrue(findElement(By.id("quizzy_q1_opt2")).isDisplayed());
    }

    @Test
    public void testDescription() {
        WebElement element1 = wait.until(visibilityOfElementLocated(By.tagName("input")));
        assertFalse(findElement(By.className("quizzy_quiz_desc")).isDisplayed());
        element1.click();
        WebElement desc = wait.until(visibilityOfElementLocated(By.className("quizzy_quiz_desc")));
        assertEquals(FIRST_QUIZ_TITLE, desc.getText());
        assertFalse(driver.findElements(By.className("quizzy_quiz_desc")).get(1).isDisplayed());
        WebElement element2 = driver.findElements(By.tagName("input")).get(1);
        element2.click();
        desc = wait.until(visibilityOfElementLocated(By.id("quizzy_quiz_desc1")));
        assertFalse(driver.findElement(By.id("quizzy_quiz_desc0")).isDisplayed());
    }

    // test when playing test two times
    @Test
    public void doQuizTwice() throws InterruptedException {
        followAnotherUseCaseClickingLabel();
        Thread.sleep(200);
        WebElement againButton = findElement(By.className("quizzy_result_foot")).findElement(By.tagName("input"));
        againButton.click();
        Thread.sleep(3000);
        assertFalse(driver.findElement(By.id("quizzy_quiz_desc0")).isDisplayed());
        assertFalse(driver.findElement(By.id("quizzy_quiz_desc1")).isDisplayed());
        assertFalse(driver.findElements(By.tagName("input")).get(0).isSelected());
        assertFalse(driver.findElements(By.tagName("input")).get(1).isSelected());
        followUseCase();
    }

    // test when clicking label, and when clicking second option
    @Test
    public void followAnotherUseCaseClickingLabel() {
        WebElement label = wait.until(
                visibilityOfElementLocated(By.className("quizzy_quiz_lbl")));
        label.click();
        WebElement desc = wait.until(visibilityOfElementLocated(By.className("quizzy_quiz_desc")));
        assertEquals(FIRST_QUIZ_TITLE, desc.getText());

        // start quiz
        click(By.id("quizzy_start_b"));
        WebElement checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

        WebElement nextButton = findElement(By.id("quizzy_q0_foot_nxt"));
        assertFalse("Next button must be hidden before choosing option",
                nextButton.isDisplayed());

        // choice second option and answer
        click(By.id("quizzy_q0_opt1"));
        checkButton.click();

        nextButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
        WebElement worst = findElement(By.className("quizzy_opt_worst"));
        assertTrue(worst != null);
        nextButton.click();

        // choice second option and answer
        checkButton
            = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));
        assertFalse("Previous next button must be disappear",
                driver.findElement(By.id("quizzy_q0_foot_nxt")).isDisplayed());
        click(By.id("quizzy_q1_opt1"));
        checkButton.click();
        WebElement best = wait.until(visibilityOfElementLocated(
                By.xpath("//*[@id='quizzy_q1_opt1_val']/*[@class='quizzy_opt_best']")));
        assertEquals("30", best.getText());
        desc = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_exp")));
        desc = desc.findElement(By.tagName("p"));
        assertEquals("Exactly.", desc.getText());

        nextButton = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_nxt")));
        nextButton.click();

        // get score
        WebElement score = wait.until(visibilityOfElementLocated(By.className("quizzy_result_score")));
        assertEquals("30", score.getText());
    }

    // test 'Loading..' message is correctly shown. This test insert sleep
    // into the PHP file so that WebDriver certainly capture the message.
    @Test
    public void testLoadingByInsertingDelay() throws IOException, InterruptedException {
        insertOrRemoveDelayToConfigFile(true);
        loadUrl();
        try {
            WebElement loading = driver.findElement(By.id("quizzy"));
            if (!loading.getText().startsWith("Loading")) {
                throw new IllegalStateException("Loading message must be shown");
            }
            WebElement element = wait.until(visibilityOfElementLocated(By.tagName("input")));
            element.click();
            // start quiz
            click(By.id("quizzy_start_b"));
            wait.until(presenceOfElementLocated(By.xpath("//*[@id='quizzy']/*[@class='loading bottom left']")));
            WebElement checkButton
                = wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

            // choice first option and answer
            click(By.id("quizzy_q0_opt0"));
            checkButton.click();
            wait.until(visibilityOfElementLocated(By.xpath("//*[@id='quizzy']/*[@class='loading bottom left']")));

            insertOrRemoveDelayToConfigFile(false);
        } catch (Exception e) { // Catch all Exception here to make sure we can clean up configPhpFile.
            insertOrRemoveDelayToConfigFile(false);
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            fail(writer.toString());
        }
    }

    private void insertOrRemoveDelayToConfigFile(boolean isInsert) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(configPhpFile));
        // Reading
        List<String> contents = new ArrayList<String>();
        String content;
        while ((content = reader.readLine()) != null)
            contents.add(content);
        reader.close();

        // Writing
        BufferedWriter writer = new BufferedWriter(new FileWriter(configPhpFile));
        if (isInsert) {
            writer.write("<?php sleep(1); ?>");
            writer.write(System.lineSeparator());
        }
        for (int i = isInsert ? 0 : 1; i < contents.size(); i++) {
            writer.write(contents.get(i));
            writer.write(System.lineSeparator());
        }
        writer.flush();
        writer.close();
    }

    private void click(By by) {
        WebElement target = findElement(by);
        target.click();
    }

    private WebElement findElement(By by) {
        return driver.findElement(by);
    }
}
