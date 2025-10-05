package tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import dto.AddWorkout;
import org.testng.annotations.Test;

public class CalendarTests extends BaseTest {

  private static final String ACTIVITY_TYPE_ERROR =
      "×\n" + "Please fix the following errors:\n" + "*Please select a valid Activity Type.";
  private static final String INFO_FROM_DASHBOARD = "You have no past workouts within the last 14 days.";

  @Test(groups = {"regression", "smoke"})
  public void positiveAddQuickWorkOutTodayByButton() {
    AddWorkout quickWorkout = AddWorkout.builder()
        .activityType("Run")
        .build();
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen()
        .clickQuickAddToggle()
        .selectActivityType(quickWorkout)
        .clickSaveWorkoutButton();
    assertTrue(calendarPage.isWorkoutDisplayed(), "Тренировка не отображается в календаре");
    deleteTodayWorkout();
  }

  @Test(groups = {"regression"})
  public void negativeAddQuickWorkout() {
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen()
        .clickQuickAddToggle()
        .clickSaveWorkoutButton();
    assertEquals(calendarPage.getActivityTypeErrorMessage(), ACTIVITY_TYPE_ERROR,
        "Сообщение об ошибке не совпадает с ожидаемым");
  }

  @Test(groups = {"regression"})
  public void addFullFromCalendar() {
    AddWorkout fullWorkout = AddWorkout.builder()
        .name("morning run")
        .build();
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen();
    calendarPage.clickFullWorkoutFromCalendar();
    addWorkoutPage.selectActivityType("run", "Long Run");
    addWorkoutPage.addWorkoutName(fullWorkout);
    addWorkoutPage.clickSaveWorkoutButton();
    assertEquals(addWorkoutPage.getWorkoutNameText(), fullWorkout.getName());
    dashboardPage.clickCalendarMenu();
    deleteTodayWorkout();
  }

  @Test(groups = {"regression","smoke"})
  public void editWorkout() {
    AddWorkout quickWorkout = AddWorkout.builder()
        .activityType("Swim")
        .name("Плавание")
        .build();
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen()
        .addQuickWorkoutFromCalendar()
        .selectActivityType(quickWorkout)
        .clickSaveWorkoutButton();
    assertTrue(calendarPage.isWorkoutDisplayed());
    calendarPage.editWorkout();
    workoutDetailsPage.isOpen()
        .clickWorkoutActionsDropdown();
    AddWorkout editWorkout = AddWorkout.builder()
        .timeOfDay("9:00 PM")
        .name("Плавание утром")
        .description("Плавание в бассейне")
        .showPlannedDistance(true)
        .distance("1")
        .plannedDistanceType("km")
        .duration("00:50:00")
        .distance("5.100")
        .distanceType("km")
        .duration("00:40:00")
        .paceType("min/km")
        .perceivedEffort("4 (Moderate)")
        .overallFeeling("Good")
        .caloriesBurned("500")
        .saveToLibrary(true)
        .build();
    workoutDetailsPage.fillWorkoutEditDetails(editWorkout);
    workoutDetailsPage.clickSaveUpdatedWorkout();
    workoutDetailsPage.isOpen();
    dashboardPage.clickCalendarMenu();
    assertTrue(calendarPage.isWorkoutDisplayed(), "Тренировка измененная не отображается");
    deleteTodayWorkout();
  }

  @Test(groups = {"regression"})
  public void viewFutureTrainingFromDashboardPage() {
    AddWorkout quickWorkout = AddWorkout.builder()
        .activityType("Bike")
        .build();
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen()
        .clickQuickAddToggle()
        .setWorkoutDate(1)
        .selectActivityType(quickWorkout)
        .clickSaveWorkoutButton();
    dashboardPage.clickDashboardMenu2()
        .clickDashboardMenu();
    assertTrue(dashboardPage.isUpcomingWorkoutsDisplayed(), "Тренировка не отображается");
    dashboardPage.clickWorkoutDetailsLink();
    workoutDetailsPage.isOpen();
    deleteWorkout();
  }

  @Test(groups = {"regression"})
  public void viewPastTrainingFromDashboardPage() {
    AddWorkout quickWorkout = AddWorkout.builder()
        .activityType("Walk")
        .build();
    preCondition();
    dashboardPage.clickCalendarMenu();
    calendarPage.isOpen()
        .clickQuickAddToggle()
        .setWorkoutDate(-1)
        .selectActivityType(quickWorkout)
        .clickSaveWorkoutButton();
    dashboardPage.clickDashboardMenu2()
        .clickPastWorkoutsSection();
    assertTrue(dashboardPage.isPastWorkoutsDisplayed());
    dashboardPage.clickWorkoutDetailsLink();
    workoutDetailsPage.isOpen()
        .clickWorkoutActionsDropdown();
    addWorkoutPage.deleteWorkout();
    calendarPage.isOpen();
    dashboardPage.clickDashboardMenu2();
    assertEquals(dashboardPage.getPastWorkoutDetailsText(), INFO_FROM_DASHBOARD,
        "Информация не совпадает");
  }

  @Test(groups = {"regression"})
  public void fileUploadTest() {
    preCondition();
    calendarPage.isOpen()
        .clickPlusIconInCalendar()
        .clickUploadWorkoutButton()
        .uploadWorkoutFile("src/test/resources/example.tcx", "Upload Workout");
    workoutDetailsPage.isOpen();
    calendarPage.verifyDownloadButtonClickable();
    assertEquals(workoutDetailsPage.getDisplayedWorkoutName(), "Upload Workout");
    deleteWorkout();
  }

  @Test(groups = {"regression", "smoke"})
  public void fileDownloadTest() {
    preCondition();
    calendarPage.isOpen()
        .clickPlusIconInCalendar()
        .clickUploadWorkoutButton()
        .uploadWorkoutFile("src/test/resources/example.tcx", "Upload Workout");
    workoutDetailsPage.isOpen();
    String downloadedFileName = calendarPage.downloadWorkoutFile();
    assertTrue(downloadedFileName.endsWith(".tcx"),
        "Имя файла должно заканчиваться с '.tcx', но было: " + downloadedFileName);
    deleteWorkout();
  }
}