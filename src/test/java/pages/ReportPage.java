package pages;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

@Log4j2
public class ReportPage {

  private static final String VIEW_REPORT_BUTTON_SELECTOR = "#saveButton";
  private static final String ZONE_REPORT_LINK_SELECTOR = ("a[href='WorkoutZoneReport']");
  private static final String WORKOUT_START_DATE_INPUT_SELECTOR = "#WorkoutDate";
  private static final String WORKOUT_END_DATE_INPUT_SELECTOR = "#WorkoutDateEnd";
  private static final String ERROR_ALERT_SELECTOR = ".alert.alert-error";
  private static final String DATE_PATTERN = "M/dd/yyyy";
  private static final By REPORT_TABLE_LOCATOR = By.cssSelector(".w-box-content table.table");
  private static final By TABLE_ROWS_LOCATOR = By.cssSelector(
      ".w-box-content table.table tbody tr");

  @Step("Page is open")
  public ReportPage isOpen() {
    log.info("Page is open");
    $(VIEW_REPORT_BUTTON_SELECTOR).shouldBe(clickable);
    return this;
  }

  @Step("Установить дату в поле")
  private ReportPage setDateField(int daysOffset, String selector) {
    log.info("Установить дату в поле");
    LocalDate targetDate = LocalDate.now().plusDays(daysOffset);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    String formattedDate = targetDate.format(formatter);
    $(selector).setValue(formattedDate);
    return this;
  }

  @Step("Установить начальную дату тренировки")
  public ReportPage setWorkoutStartDate(int daysOffsetFromToday) {
    log.info("Установить начальную дату тренировки");
    setDateField(daysOffsetFromToday, WORKOUT_START_DATE_INPUT_SELECTOR);
    return this;
  }

  @Step("Установить конечную дату тренировки")
  public ReportPage setWorkoutEndDate(int daysOffsetFromToday) {
    log.info("Установить конечную дату тренировки");
    setDateField(daysOffsetFromToday, WORKOUT_END_DATE_INPUT_SELECTOR);
    return this;
  }

  @Step("Нажать кнопку 'Просмотреть отчет'")
  public ReportPage clickViewReportButton() {
    log.info("Нажать кнопку 'Просмотреть отчет'");
    $(VIEW_REPORT_BUTTON_SELECTOR).click();
    return this;
  }

  @Step("Нажать ссылку 'Отчет по зонам'")
  public ReportPage clickZoneReportLink() {
    log.info("Нажать ссылку 'Отчет по зонам'");
    $(ZONE_REPORT_LINK_SELECTOR).click();
    return this;
  }

  @Step("Проверить, что в таблице есть хотя бы одна запись")
  public boolean hasAtLeastOneRecord() {
    log.info("Проверка наличия записей в таблице");

    // Проверяем что таблица существует и видима
    $(REPORT_TABLE_LOCATOR).shouldBe(visible);

    // Проверяем наличие строк в tbody
    ElementsCollection rows = $$(TABLE_ROWS_LOCATOR);
    boolean hasRecords = !rows.isEmpty();

    log.info("В таблице {} записей", hasRecords ? "есть" : "нет");
    return hasRecords;
  }

  @Step("Получить текст ошибки")
  public String getErrorMessage() {
    log.info("Получить текст ошибки");
    return $(ERROR_ALERT_SELECTOR).getText();
  }
}