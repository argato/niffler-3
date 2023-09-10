package guru.qa.niffler.test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_SENT;

import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InvitationTwoUsersTest extends BaseWebTest {

  private LoginPage loginPage;

  @BeforeEach
  void openPage() {
    open("http://127.0.0.1:3000/main");
    loginPage = new LoginPage();
  }

  @Test
  @AllureId("106")
  @DisplayName("Нет записей в таблице Друзья у пользователя, отправившего запрос, одна запись у пользователя, получившего запрос")
  void shouldBeDisplayedWaitingApprovedFriend(
      @User(userType = INVITATION_SENT) UserJson sentUser,
      @User(userType = INVITATION_RECEIVED) UserJson receivedUser) {
    loginPage.login(sentUser.getUsername(), sentUser.getPassword());
    $("[data-tooltip-id=friends]").click();
    $(".people-content .main-content__section table tbody")
        .$$("tr")
        .shouldHave(size(0));

    $(".header__logout").click();

    loginPage.login(receivedUser.getUsername(), receivedUser.getPassword());
    $("[data-tooltip-id=friends]").click();
    $(".people-content .main-content__section table tbody")
        .$$("tr")
        .shouldHave(size(1));
  }
}
