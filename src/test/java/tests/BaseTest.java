package tests;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import pages.AddWorkoutPage;
import pages.BikesPage;
import pages.CalculatorPage;
import pages.CalendarPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.LogoutPage;
import pages.ReportPage;
import pages.ShoesPage;
import pages.WorkoutDetailsPage;
import utils.PropertyReader;
import utils.TestListener;

import java.util.Arrays;

@Listeners({TestListener.class})
public class BaseTest {
    protected static final String BASE_LOGIN = PropertyReader.getProperty("login");
    protected static final String BASE_PASSWORD = PropertyReader.getProperty("password");
    protected String BASE_URL = PropertyReader.getProperty("url");

    // Инициализируем страницы сразу, чтобы избежать NullPointerException
    protected LoginPage loginPage = new LoginPage();
    protected CalendarPage calendarPage = new CalendarPage();
    protected AddWorkoutPage addWorkoutPage = new AddWorkoutPage();
    protected DashboardPage dashboardPage = new DashboardPage();
    protected WorkoutDetailsPage workoutDetailsPage = new WorkoutDetailsPage();
    protected ReportPage reportPage = new ReportPage();
    protected CalculatorPage calculatorPage = new CalculatorPage();
    protected LogoutPage logoutPage = new LogoutPage();
    protected ShoesPage shoesPage = new ShoesPage();
    protected BikesPage bikesPage = new BikesPage();

    @BeforeClass(alwaysRun = true)
    @Parameters({"browserName"})
    public void setUp(@Optional("chrome") String browser, ITestContext testContext) {
        // Сначала проверяем, что свойства загружены корректно
        validateProperties();

        Configuration.baseUrl = BASE_URL;
        Configuration.browserSize = "1920x1080";
        Configuration.browser = browser;
 //       Configuration.headless = Boolean.parseBoolean(PropertyReader.getProperty("headless", "false"));
        Configuration.timeout = 10000;
        Configuration.fileDownload = FileDownloadMode.FOLDER;
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @BeforeMethod(alwaysRun = true)
    public void preCondition() {
        try {
            open("/");
            // Явно переинициализируем loginPage на случай параллельного выполнения
            this.loginPage = new LoginPage();
            loginPage.login(BASE_LOGIN, BASE_PASSWORD);
        } catch (Exception e) {
            System.err.println("Error in preCondition: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to execute preCondition", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(ITestResult result) {
        try {
            if (isWebDriverActive()) {
                performGroupSpecificCleanup(result);
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        } finally {
            if (isWebDriverActive()) {
                closeWebDriver();
            }
        }
    }

    private void performGroupSpecificCleanup(ITestResult result) {
        String[] groups = result.getMethod().getGroups();

        try {
            if (Arrays.asList(groups).contains("shoesDelete") && shoesPage != null) {
                shoesPage.deleteShoesWithConfirmation();
            }
            if (Arrays.asList(groups).contains("workoutDelete") && workoutDetailsPage != null && addWorkoutPage != null) {
                workoutDetailsPage.clickWorkoutActionsDropdown();
                addWorkoutPage.deleteWorkout();
            }
            if (Arrays.asList(groups).contains("workoutDeleteToday") && calendarPage != null) {
                calendarPage.deleteTodayWorkout();
            }
        } catch (Exception e) {
            System.err.println("Error in group-specific cleanup: " + e.getMessage());
        }
    }

    private boolean isTestInGroup(ITestResult result, String group) {
        return Arrays.asList(result.getMethod().getGroups()).contains(group);
    }

    private boolean isWebDriverActive() {
        try {
            return com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted();
        } catch (Exception e) {
            return false;
        }
    }

    private void validateProperties() {
        if (BASE_LOGIN == null || BASE_LOGIN.isEmpty()) {
            throw new RuntimeException("Property 'login' is not set or empty");
        }
        if (BASE_PASSWORD == null || BASE_PASSWORD.isEmpty()) {
            throw new RuntimeException("Property 'password' is not set or empty");
        }
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            throw new RuntimeException("Property 'url' is not set or empty");
        }
        System.out.println("Properties loaded successfully:");
        System.out.println("URL: " + BASE_URL);
        System.out.println("Login: " + BASE_LOGIN);
      //  System.out.println("Headless: " + PropertyReader.getProperty("headless", "false"));
    }
}