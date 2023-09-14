package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {

    private final ElementsCollection viewPaymentPage = $$(".heading");
    private final SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement fieldMonth = $("input[placeholder='08']");
    private final SelenideElement fieldYear = $("input[placeholder='22']");
    private final SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
    private final SelenideElement fieldCode = $("input[placeholder='999']");
    private final ElementsCollection button = $$(".button__content");
    private final SelenideElement massageSuccess = $(".notification.notification_status_ok");
    private final SelenideElement massageError = $(".notification.notification_status_error");
    private final ElementsCollection sub = $$(".input__sub");

    public PaymentPage() {
        viewPaymentPage.findBy(text("Оплата по карте")).shouldBe(visible);
    }

    // massage
    public boolean massagePositive(String exactText) {
        try {
            massageSuccess.shouldHave(Condition.exactText(exactText), Duration.ofSeconds(25)).shouldBe(visible);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean massageError(String exactText) {
        try {
            massageError.shouldHave(Condition.exactText(exactText), Duration.ofSeconds(25)).shouldBe(visible);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //TO DO => input
    public StartPage validPaymentActiveCard() {
        activeCard();
        validDate();
        validName();
        validCode();
        button.findBy(text("Продолжить")).click();
        return new StartPage();
    }

    public StartPage rejectionInPaymentInactiveCard() {
        inactiveCard();
        validDate();
        validName();
        validCode();
        button.findBy(text("Продолжить")).click();
        return new StartPage();
    }

    //cards
    public void activeCard() {
        fieldCard.sendKeys(DataHelper.getValidActiveCard().getCard());
    }

    public void inactiveCard() {
        fieldCard.sendKeys(DataHelper.getValidInactiveCard().getCard());
    }

    public void randomCard() {
        fieldCard.sendKeys(DataHelper.getRandomCard());
    }

    //date
    public void validDate() {
        var date = DataHelper.getValidDateOfCard();
        fieldMonth.sendKeys(date.getMonth());
        fieldYear.sendKeys(date.getYear());
    }

    public void localMonth() {
        fieldMonth.sendKeys(DataHelper.getLocalMonth());
    }

    public void dateOfPassedMonth() {
        var date = DataHelper.getDateMinusMonth();
        fieldMonth.sendKeys(date.getMonth());
        fieldYear.sendKeys(date.getYear());
    }

    public void localYear() {
        fieldYear.sendKeys(DataHelper.getLocalYear());
    }

    //name
    public void validName() {
        fieldName.sendKeys(DataHelper.getValidName().getName());
    }

    public void validNameWithDash() {
        fieldName.sendKeys(DataHelper.getValidNameWithDash().getName());
    }

    public void invalidName() {
        fieldName.sendKeys(DataHelper.getCyrillicName().getName());
    }

    //code
    public void validCode() {
        fieldCode.sendKeys(DataHelper.getValidCode().getCode());
    }

    //random
    public void inputRandomNumbers(SelenideElement selenideElement, int min, int max) {
        selenideElement.sendKeys(String.valueOf(DataHelper.getRandomNumber(min, max)));
    }

    public void inputRandomLetters(SelenideElement selenideElement, int num) {
        selenideElement.sendKeys(DataHelper.getRandomLetters(num));
    }

    // TO DO => click
    public void clickButton() {
        button.findBy(text("Продолжить")).click();
    }

    // TO DO => check
    public boolean checkEmptyField(SelenideElement selenideElement, Condition condition, int num, String exactText) {
        try {
            selenideElement.shouldBe(condition);
            sub.get(num).shouldHave(Condition.exactText(exactText));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkNotEmptyField(SelenideElement selenideElement, Condition condition, int num, String exactText) {
        try {
            selenideElement.shouldNotBe(condition);
            sub.get(num).shouldHave(Condition.exactText(exactText));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
