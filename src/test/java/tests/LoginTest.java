package tests;

import static com.codeborne.selenide.Selenide.open;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

  @Test(groups = {"regression", "smoke"})
  public void positiveLoginTest() {
    preCondition();
    dashboardPage.isOpen();
  }

  @Test(groups = {"regression", "smoke"}, dataProvider = "Негативные тестовые данные для логина")
  public void negativeLoginTest(String email, String password, String errorMessage) {
    open("/");
    loginPage.isOpen()
        .login(email, password)
        .verifyEmailErrorMessageDisplayed();
    assertEquals(loginPage.getEmailErrorMessageText(), errorMessage,
        "Сообщение не верного логина не совпадает с ожидаемым");
  }

  @DataProvider(name = "Негативные тестовые данные для логина")
  public Object[][] testDataForLoginTest() {
    String invalidEmailErrorText = "Please enter a valid email address.";
    String emptyEmailErrorText = "Please enter your e-mail address.";
    return new Object[][]{
        {"agsrtest", "fdsfssd", invalidEmailErrorText},
        {"", "mail.ru", emptyEmailErrorText}
    };
  }
}