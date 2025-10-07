package pages;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import dto.AddWorkout;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

@Log4j2
public class CalendarPage{


  private static final String QUICK_ADD_TOGGLE_SELECTOR = "#QuickAddToggle";
  private static final By LOGO = By.xpath("//img[contains(@src, 'finalsurge_logo.png')]");
  private static final By TODAY_DAY_LOCATOR = By.cssSelector("[class*='fc-today']");
  private static final String PLUS_ICON_SELECTOR = ".icon-plus";
  private static final String QUICK_ADD_LINK_SELECTOR = "a.quick-add";
  private static final String FULL_ADD_LINK_SELECTOR = ".full-add";
  private static final String WORKOUT_DATE_INPUT_SELECTOR = "#WorkoutDate";
  private static final String SAVE_WORKOUT_BUTTON_SELECTOR = "#saveButton";
  private static final String ACTIVITY_TYPE_DROPDOWN_SELECTOR = "#ActivityType";
  private static final String WORKOUT_NAME_INPUT_SELECTOR = "#Name";
  private static final By ERROR_ALERT_LOCATOR = By.cssSelector(".alert.alert-error");
  private static final By QUICK_UPLOAD_LINK_LOCATOR = By.cssSelector("a.quick-upload");
  private static final By QUICK_DELETE_LINK_LOCATOR = By.cssSelector("a.quick-delete");
  private static final By DAY_CONTENT_LOCATOR = By.cssSelector(".fc-day-content");
  private static final By EVENT_ACTIVITY_TITLE_LOCATOR = By.cssSelector(".fc-event-activity-title");
  private static final By FULL_VIEW_LINK_LOCATOR = By.cssSelector("a.full-view");
  private static final By DOWNLOAD_BUTTON_LOCATOR = By.cssSelector(
      "button[onclick*='Delivery/WorkoutSourceFile.cshtml']");
  private static final String WORKOUT_BY_KEY_TEMPLATE = ".calendarworkout[data-wkey='%s']";  private static final String WORKOUT_UPLOAD_IFRAME_SELECTOR = "#WorkoutUploadiFrame";
  private static final By FILE_INPUT_LOCATOR = By.cssSelector("input[type='file']");
  private static final By WORKOUT_EVENTS_LOCATOR = By.cssSelector(".calendarworkout[data-wkey]");
  private static final By WORKOUT_TITLE_LOCATOR = By.cssSelector(".fc-event-activity-title");
  private static final By QUICK_DELETE_BUTTON_LOCATOR = By.cssSelector("a.quick-delete");
  private static final By MODAL_CONFIRM_BUTTON_LOCATOR = By.cssSelector(".modal-footer a:nth-of-type(1)");

  @Step("Page is open")
  public CalendarPage isOpen() {
    log.info("Page is open");
    $(LOGO).isDisplayed();
    return this;
  }

  @Step("Нажать на кнопку 'Быстрое добавление' тренировки")
  public CalendarPage clickQuickAddToggle() {
    log.info("Нажать на кнопку 'Быстрое добавление' тренировки");
    $(QUICK_ADD_TOGGLE_SELECTOR).click();
    return this;
  }

  @Step("Добавить быструю тренировку на сегодня через календарь")
  public CalendarPage addQuickWorkoutFromCalendar() {
    log.info("Добавить быструю тренировку на сегодня через календарь");
    $(TODAY_DAY_LOCATOR).$(PLUS_ICON_SELECTOR).hover().click();
    $(TODAY_DAY_LOCATOR).$(QUICK_ADD_LINK_SELECTOR).shouldBe(visible).hover().click();
    return this;
  }

  @Step("Нажать на плюс в календаре")
  public CalendarPage clickPlusIconInCalendar() {
    log.info("Нажать на плюс в календаре");
    $(TODAY_DAY_LOCATOR).$(PLUS_ICON_SELECTOR).hover().click();
    return this;
  }

  @Step("Найти и удалить все тренировки на текущую дату")
  public void deleteAllWorkoutsOnCurrentDate() {
    log.info("Поиск и удаление всех тренировок на текущую дату");
    ElementsCollection workouts = getTodayWorkouts();
    if (workouts.isEmpty()) {
      log.info("На текущую дату нет тренировок для удаления");
      return;
    }
    log.info("Найдено тренировок для удаления: {}", workouts.size());
    while (hasWorkoutsToday()) {
      deleteFirstWorkout();
    }
    log.info("Все тренировки на текущую дату успешно удалены");
  }

  @Step("Удалить первую тренировку")
  private void deleteFirstWorkout() {
    if (!hasWorkoutsToday()) {
      log.info("Нет тренировок для удаления");
      return;
    }

    SelenideElement firstWorkout = getTodayWorkouts().first();
    String workoutName = firstWorkout.$(WORKOUT_TITLE_LOCATOR).shouldBe(visible).getText();
    String workoutKey = firstWorkout.getAttribute("data-wkey");
    log.info("Удаление тренировки: {} (key: {})", workoutName, workoutKey);
    try {
      firstWorkout.$(WORKOUT_TITLE_LOCATOR).click();
      firstWorkout.$(QUICK_DELETE_BUTTON_LOCATOR)
          .shouldBe(visible, enabled)
          .click();
      $(MODAL_CONFIRM_BUTTON_LOCATOR)
          .shouldBe(visible, enabled)
          .click();
      waitForWorkoutToDisappear(workoutKey);
      log.info("Тренировка '{}' успешно удалена", workoutName);
    } catch (Exception e) {
      log.error("Ошибка при удалении тренировки '{}': {}", workoutName, e.getMessage());
    }
  }

  @Step("Дождаться исчезновения тренировки с key: {workoutKey}")
  private void waitForWorkoutToDisappear(String workoutKey) {
    String workoutLocator = String.format(WORKOUT_BY_KEY_TEMPLATE, workoutKey);
    $(workoutLocator).should(disappear);
  }

  @Step("Проверить наличие тренировок на сегодня")
  public boolean hasWorkoutsToday() {
    return !getTodayWorkouts().isEmpty();
  }

  @Step("Получить список тренировок на сегодня")
  public ElementsCollection getTodayWorkouts() {
    return $(TODAY_DAY_LOCATOR)
        .$$(WORKOUT_EVENTS_LOCATOR)
        .filter(visible);
  }

  @Step("Нажать кнопку 'Сохранить тренировку'")
  public CalendarPage clickSaveWorkoutButton() {
    log.info("Нажать кнопку 'Сохранить тренировку");
    $(SAVE_WORKOUT_BUTTON_SELECTOR).click();
    isWorkoutDisplayed();
    return this;
  }

  @Step("Добавить полную тренировку через календарь")
  public CalendarPage clickFullWorkoutFromCalendar() {
    log.info("Добавить полную тренировку через календарь");
    $(TODAY_DAY_LOCATOR).$(PLUS_ICON_SELECTOR).hover().click();
    $(TODAY_DAY_LOCATOR).$(FULL_ADD_LINK_SELECTOR).shouldBe(visible).hover().click();
    return this;
  }

  @Step("Выбрать тип активности при быстром добавлении")
  public CalendarPage selectActivityType(AddWorkout quickWorkout) {
    log.info("Выбрать тип активности при быстром добавлении");
    $(ACTIVITY_TYPE_DROPDOWN_SELECTOR).selectOption(quickWorkout.getActivityType());
    return this;
  }

  @Step("Проверить отображение тренировки в календаре")
  public boolean isWorkoutDisplayed() {
    log.info("Проверить отображение тренировки в календаре");
    return $(TODAY_DAY_LOCATOR).$(DAY_CONTENT_LOCATOR).$(EVENT_ACTIVITY_TITLE_LOCATOR)
        .isDisplayed();
  }

  @Step("Получить текст ошибки типа активности")
  public String getActivityTypeErrorMessage() {
    log.info("Получить текст ошибки типа активности");
    return $(ERROR_ALERT_LOCATOR).getText();
  }

  @Step("Редактировать тренировку")
  public CalendarPage editWorkout() {
    log.info("Редактировать тренировку");
    $(EVENT_ACTIVITY_TITLE_LOCATOR).click();
    $(FULL_VIEW_LINK_LOCATOR).click();
    return this;
  }

  @Step("Установить дату тренировки")
  public CalendarPage setWorkoutDate(int daysOffset) {
    log.info("Установить дату тренировки");
    LocalDate targetDate = LocalDate.now().plusDays(daysOffset);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
    String formattedDate = targetDate.format(formatter);
    $(WORKOUT_DATE_INPUT_SELECTOR).setValue(formattedDate);
    return this;
  }

  @Step("Нажать кнопку 'Загрузить тренировку'")
  public CalendarPage clickUploadWorkoutButton() {
    log.info("Нажать кнопку 'Загрузить тренировку'");
    $(TODAY_DAY_LOCATOR).$(QUICK_UPLOAD_LINK_LOCATOR).click();
    return this;
  }

  @Step("Загрузить файл тренировки")
  public CalendarPage uploadWorkoutFile(String relativePathToFile, String Workout) {
    log.info("Загрузить файл тренировки");
    switchTo().frame($(WORKOUT_UPLOAD_IFRAME_SELECTOR));
    $(SAVE_WORKOUT_BUTTON_SELECTOR).shouldHave(clickable);
    $(WORKOUT_NAME_INPUT_SELECTOR).setValue(Workout);
    String pathToFile = System.getProperty("user.dir") + File.separator + relativePathToFile;
    File fileUpload = new File(pathToFile);
    $(FILE_INPUT_LOCATOR).uploadFile(fileUpload);
    $(SAVE_WORKOUT_BUTTON_SELECTOR).click();
    switchTo().defaultContent();
    return this;
  }

  @Step("Проверить доступность кнопки скачивания")
  public CalendarPage verifyDownloadButtonClickable() {
    log.info("Проверить доступность кнопки скачивания");
    $(DOWNLOAD_BUTTON_LOCATOR).shouldBe(clickable);
    return this;
  }

  @Step("Скачать файл тренировки")
  public String downloadWorkoutFile() {
    log.info("Скачать файл тренировки");
    File downloadedFile = $(DOWNLOAD_BUTTON_LOCATOR).download();
    return downloadedFile.getName();
  }
}


