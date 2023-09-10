package guru.qa.niffler.test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InvitationReceivedWebTest extends BaseWebTest {

  @BeforeEach
  void doLogin() {
    open("http://127.0.0.1:3000/main");
  }

  @Test
  @AllureId("105")
  @DisplayName("В таблице пользователей есть пользователь с доступными кнопками принять/отказать")
  void shouldBeDisplayedWaitingApprovedFriend(
      @User(userType = INVITATION_RECEIVED) UserJson userForTest) {
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(userForTest.getUsername());
    $("input[name='password']").setValue(userForTest.getPassword());
    $("button[type='submit']").click();
    $("[data-tooltip-id=friends]").click();
    ElementsCollection rows = $(".people-content .main-content__section table tbody")
        .$$("tr");
    rows.shouldHave(size(1));
    rows.get(0).find("[data-tooltip-id='submit-invitation']").shouldBe(visible, enabled);
    rows.get(0).find("[data-tooltip-id='decline-invitation']").shouldBe(visible, enabled);
  }

}
