package guru.qa.niffler.page;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  public void login(String username, String password) {
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(username);
    $("input[name='password']").setValue(password);
    $("button[type='submit']").click();
    $("[data-tooltip-id=friends]").click();
  }

}
