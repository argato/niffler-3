package guru.qa.niffler.test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_SENT;

import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InvitationSentWebTest extends BaseWebTest {

  @BeforeEach
  void doLogin(@User(userType = INVITATION_SENT) UserJson userForTest) {
    open("http://127.0.0.1:3000/main");
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(userForTest.getUsername());
    $("input[name='password']").setValue(userForTest.getPassword());
    $("button[type='submit']").click();
  }

  @Test
  @AllureId("104")
  @DisplayName("В таблице пользователей есть пользователь с текстом 'Pending invitation'")
  void shouldBeDisplayedPendingInvitation(@User(userType = INVITATION_SENT) UserJson userForTest)
      throws InterruptedException {
    $("[data-tooltip-id=people]").click();
    $(".people-content .main-content__section table tbody")
        .$$("tr")
        .filter(text(("Pending invitation")))
        .shouldHave(size(1));
  }
}