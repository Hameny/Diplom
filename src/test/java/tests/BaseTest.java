package tests;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.assertj.core.util.Arrays;
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
import pages.EquipmentPage;
import pages.LoginPage;
import pages.LogoutPage;
import pages.ReportPage;
import pages.ShoesPage;
import pages.WorkoutDetailsPage;
import utils.PropertyReader;
import utils.TestListener;

//@Listeners({TestListener.class})
public class BaseTest {
    protected static final String BASE_LOGIN = PropertyReader.getProperty("login");
    protected static final String BASE_PASSWORD = PropertyReader.getProperty("password");
    protected String BASE_URL = PropertyReader.getProperty("url");
    protected LoginPage loginPage;
    protected CalendarPage calendarPage;
    protected AddWorkoutPage addWorkoutPage;
    protected DashboardPage dashboardPage;
    protected WorkoutDetailsPage workoutDetailsPage;
    protected ReportPage reportPage;
    protected CalculatorPage calculatorPage;
    protected LogoutPage logoutPage;
    protected EquipmentPage equipmentPage;
    protected ShoesPage shoesPage;
    protected BikesPage bikesPage;

    @BeforeClass(alwaysRun = true)
    @Parameters({"browserName"})
    public void setUp(@Optional("chrome") String browser, ITestContext testContext) {
        Configuration.baseUrl = BASE_URL;
        Configuration.browserSize = "1920x1080";
        Configuration.browser = browser;
        Configuration.headless = Boolean.parseBoolean(PropertyReader.getProperty("headless"));
        Configuration.timeout = 10000;
        Configuration.fileDownload = FileDownloadMode.FOLDER;
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));

        this.loginPage = new LoginPage();
        this.calendarPage = new CalendarPage();
        this.addWorkoutPage = new AddWorkoutPage();
        this.dashboardPage = new DashboardPage();
        this.workoutDetailsPage = new WorkoutDetailsPage();
        this.reportPage = new ReportPage();
        this.calculatorPage = new CalculatorPage();
        this.logoutPage = new LogoutPage();
        this.equipmentPage = new EquipmentPage();
        this.shoesPage = new ShoesPage();
        this.bikesPage = new BikesPage();
    }

    //@BeforeMethod(alwaysRun = true)
    public void preCondition() {
        open("/");
        loginPage.login(BASE_LOGIN, BASE_PASSWORD);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(ITestResult result) {
        try {
            // Определяем порядок очистки явно
            if (isTestInGroup(result, "shoesDelete")) {
                shoesPage.deleteShoesWithConfirmation();
            }
            if (isTestInGroup(result, "workoutDelete")) {
                workoutDetailsPage.clickWorkoutActionsDropdown();
                addWorkoutPage.deleteWorkout();
            }
            if (isTestInGroup(result, "workoutDeleteToday")) {
                calendarPage.deleteTodayWorkout();
            }
        } finally {
            // Всегда закрываем браузер в конце
            closeWebDriver();
        }
    }

    private boolean isTestInGroup(ITestResult result, String group) {
        return Arrays.asList(result.getMethod().getGroups()).contains(group);
    }

    @AfterMethod(alwaysRun = true)
    public void postCondition() {
        closeWebDriver();
    }
}