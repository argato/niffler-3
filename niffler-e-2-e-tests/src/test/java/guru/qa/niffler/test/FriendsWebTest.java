package guru.qa.niffler.test;


import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.User.UserType.WITH_FRIENDS;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends BaseWebTest {

  @BeforeEach
  void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
    open("http://127.0.0.1:3000/main");
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(userForTest.getUsername());
    $("input[name='password']").setValue(userForTest.getPassword());
    $("button[type='submit']").click();
  }

  @Test
  @AllureId("101")
  @DisplayName("Отображается один друг в таблице друзей")
  void friendShouldBeDisplayedInTableFriends(@User(userType = WITH_FRIENDS) UserJson userForTest) {
    $("[data-tooltip-id=friends]").click();
    ElementsCollection rows = $(".people-content .main-content__section table tbody")
        .$$("tr");
    rows.shouldHave(size(1));
    rows.get(0).shouldHave(text(("You are friends")));
  }

  @Test
  @AllureId("102")
  @DisplayName("Отображается один друг в таблице пользователей")
  void friendShouldBeDisplayedInTablePeople(@User(userType = WITH_FRIENDS) UserJson userForTest) {
    $("[data-tooltip-id=people]").click();
    $(".people-content .main-content__section table tbody")
        .$$("tr")
        .filter(text(("You are friends")))
        .shouldHave(size(1));
  }

  @Test
  @AllureId("103")
  @DisplayName("Отображается кнопка удаления друга")
  void deletingButtonShouldBeDisplayedInTablePeople(
      @User(userType = WITH_FRIENDS) UserJson userForTest) {
    $("[data-tooltip-id=people]").click();
    SelenideElement row = $(".people-content .main-content__section table tbody")
        .$$("tr").filter(text("You are friends")).get(0);
    row.find("[data-tooltip-id='remove-friend']").shouldBe(visible, enabled);
  }
}
