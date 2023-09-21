package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CreditPage {

    private final ElementsCollection viewCreditPage = $$(".heading");
    private final SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement fieldMonth = $("input[placeholder='08']");
    private final SelenideElement fieldYear = $("input[placeholder='22']");
    private final SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
    private final SelenideElement fieldCode = $("input[placeholder='999']");
    private final ElementsCollection button = $$(".button__content");
    private final SelenideElement massageSuccess = $(".notification.notification_status_ok");
    private final SelenideElement massageError = $(".notification.notification_status_error");
    private final ElementsCollection sub = $$(".input__sub");

    public CreditPage() {
        viewCreditPage.findBy(text("Кредит по данным карты")).shouldBe(visible);
    }

    public StartPage creditForTour(DataHelper.UserInfo info) {
        fieldCard.sendKeys(info.getCard());
        fieldMonth.sendKeys(info.getMonth());
        fieldYear.sendKeys(info.getYear());
        fieldName.sendKeys(info.getName());
        fieldCode.sendKeys(info.getCode());
        button.findBy(text("Продолжить")).click();
        return new StartPage();
    }

    // messages
    public void messagePositive() {
        massageSuccess.shouldHave(Condition.exactText("Успешно\n" + "Операция одобрена Банком."), Duration.ofSeconds(25)).shouldBe(visible);
    }

    public void messageError() {
        massageError.shouldHave(Condition.exactText("Ошибка\n" + "Ошибка! Банк отказал в проведении операции."), Duration.ofSeconds(25)).shouldBe(visible);
    }

    // to check notifications
    public void notificationFormat(int num) {
        sub.get(num).shouldBe(visible).shouldHave(Condition.exactText("Неверный формат"));
    }

    public void notificationExpired(int num) {
        sub.get(num).shouldBe(visible).shouldHave(Condition.exactText("Истёк срок действия карты"));
    }

    public void notificationIncorrectDeadline(int num) {
        sub.get(num).shouldBe(visible).shouldHave(Condition.exactText("Неверно указан срок действия карты"));
    }

    public void notificationRequiredField(int num) {
        sub.get(num).shouldBe(visible).shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }
}
