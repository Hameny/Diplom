package tests;

import static com.codeborne.selenide.Selenide.switchTo;
import static org.testng.Assert.assertTrue;

import dto.AddWorkout;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportTests extends BaseTest {

  private static final String ERROR_MESSAGE =
      "×\n" + "Please fix the following errors:\n" + "*Please select a valid Activity Zone Type.";

  @Test(groups = {"regression"})
  public void negativeViewZoneReport() {
    preCondition();
    dashboardPage.clickWorkoutReportPage();
    reportPage.isOpen()
        .clickZoneReportLink()
        .setWorkoutStartDate(-1)
        .setWorkoutEndDate(1)
        .clickViewReportButton();
    Assert.assertEquals(reportPage.getErrorMessage(), ERROR_MESSAGE,
        "Сообщение об ошибке не совпадает с ожидаемым");
  }

  @Test(groups = {"regression", "smoke"})
  public void positiveViewWorkoutReport() {
    AddWorkout quickWorkout = AddWorkout.builder()
        .activityType("Walk")
        .build();
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen()
        .clickQuickAddToggle()
        .selectActivityType(quickWorkout)
        .clickSaveWorkoutButton();
    dashboardPage.clickWorkoutReportPage();
    reportPage.isOpen();
    reportPage.setWorkoutStartDate(-1);
    reportPage.setWorkoutEndDate(1);
    reportPage.clickViewReportButton();
    assertTrue(reportPage.hasAtLeastOneRecord());
    dashboardPage.clickWorkoutDetailsLink();
    switchTo().window(1);
    workoutDetailsPage.isOpen();
    deleteWorkout();
    deleteTodayWorkout();
  }
}
