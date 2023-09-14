package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.StartPage;

import java.util.function.BooleanSupplier;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class CreditTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:8080", StartPage.class);
        SQLHelper.cleanDB();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    // !!!!!!! positive tests - credit
    @Test
    @DisplayName("Should be approved credit all fields are valid")
    void shouldBeApprovedCreditAllFieldsAreValid() {
        StartPage startPage = new StartPage();

        var creditPage = startPage.checkCreditSystem();
        creditPage.validCreditActiveCard();

        assertTrue(creditPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should be approved credit if double name is in name field")
    void shouldBeApprovedCreditIfDoubleNameIsInNameField() {
        StartPage startPage = new StartPage();

        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.validDate();
        creditPage.validNameWithDash();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should  be approved credit if one letter is in name field")
    void shouldBeApprovedCreditIfOneLetterIsInNameField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.validDate();
        creditPage.inputRandomLetters(fieldName, 1);
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should be approved credit if max letters are in name field")
    void shouldBeApprovedCreditIfMaxLettersAreInNameField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.validDate();
        creditPage.inputRandomLetters(fieldName, 30);
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterCredit());
    }

    //bug результат - успешно
    @Test
    @DisplayName("Should get credit rejection on valid inactive card")
    public void shouldGetCreditRejectionOnValidInactiveCard() {
        StartPage startPage = new StartPage();

        var creditPage = startPage.checkCreditSystem();
        creditPage.rejectionInCreditInactiveCard();

        assertTrue(creditPage.massageError("Ошибка\n" + "Ошибка! Банк отказал в проведении операции."));
        assertEquals("DECLINED", SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should be approved credit if card validity period is in current month")
    public void shouldBeApprovedCreditIfValidPeriodIsInCurrentMonth() {
        StartPage startPage = new StartPage();

        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.localMonth();
        creditPage.localYear();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterCredit());
    }

    // !!!!!!! negative tests - credit
    @Test
    @DisplayName("Should not be send credit request if all fields are empty")
    public void shouldNotBeSendCreditRequestIfAllFieldsAreEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");
        SelenideElement fieldMonth = $("input[placeholder='08']");
        SelenideElement fieldYear = $("input[placeholder='22']");
        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        SelenideElement fieldCode = $("input[placeholder='999']");

        var creditPage = startPage.checkCreditSystem();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldCard, Condition.empty, 0, "Неверный формат"));
        assertTrue(creditPage.checkEmptyField(fieldMonth, Condition.empty, 1, "Неверный формат"));
        assertTrue(creditPage.checkEmptyField(fieldYear, Condition.empty, 2, "Неверный формат"));
        assertTrue(creditPage.checkEmptyField(fieldName, Condition.empty, 3, "Поле обязательно для заполнения"));
        assertTrue(creditPage.checkEmptyField(fieldCode, Condition.empty, 4, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card field is empty")
    public void shouldNotBeSendCreditRequestIfCardFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");

        var creditPage = startPage.checkCreditSystem();
        creditPage.validDate();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldCard, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if month field is empty")
    public void shouldNotBeSendCreditRequestIfMonthFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");

        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.localYear();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldMonth, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if year field is empty")
    public void shouldNotBeSendCreditRequestIfYearFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");

        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.localMonth();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldYear, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if name field is empty")
    public void shouldNotBeSendCreditRequestIfNameFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));

        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.validDate();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldName, Condition.empty, 0, "Поле обязательно для заполнения"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    //bug поле Владелец заполнено корректно, но при пустом поле Код появляется запись, что поле Владелец обязательно для заполнения
    @Test
    @DisplayName("Should not be send credit request if code field is empty")
    public void shouldNotBeSendCreditRequestIfCodeFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.validDate();
        creditPage.validName();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldCode, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should get credit rejection with unreal random card")
    public void shouldGetCreditRejectionInPaymentWithRandomUnrealCard() {
        StartPage startPage = new StartPage();

        var creditPage = startPage.checkCreditSystem();

        creditPage.randomCard();
        creditPage.validDate();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.massageError("Ошибка\n" + "Ошибка! Банк отказал в проведении операции."));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 15 numbers are in card field")
    public void shouldNotBeSendCreditRequestIfFifteenNumbersAreInCardField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");
        var creditPage = startPage.checkCreditSystem();

        fieldCard.sendKeys("444444444444444");
        creditPage.validDate();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldCard, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card is overdue in passed month")
    public void shouldNotBeSendCreditRequestIfCardIsOverdueInPassedMonth() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.dateOfPassedMonth();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if one number in month field ")
    public void shouldNotBeSendCreditRequestIfOneNumberInMonthField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.inputRandomNumbers(fieldMonth, 1, 9);
        creditPage.localYear();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 13 in month field")
    public void shouldNotBeSendCreditRequestIf13InMonthField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.inputRandomNumbers(fieldMonth, 13, 13);
        creditPage.localYear();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 00 in month field")
    public void shouldNotBeSendCreditRequestIf00InMonthField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        fieldMonth.sendKeys("00");
        creditPage.localYear();
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card expiry date more then 6 years")
    public void shouldNotBeSendCreditRequestIfCardExpiryDateMoreThen6Years() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var creditPage = startPage.checkCreditSystem();
        var year = String.valueOf((Integer.parseInt(DataHelper.getLocalYear()) + 6));

        creditPage.activeCard();
        creditPage.localMonth();
        fieldYear.sendKeys(year);
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldYear, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card expiry date is last year")
    public void shouldNotBeSendCreditRequestIfCardExpiryDateIsLastYear() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var creditPage = startPage.checkCreditSystem();
        var year = String.valueOf((Integer.parseInt(DataHelper.getLocalYear()) - 1));

        creditPage.activeCard();
        creditPage.localMonth();
        fieldYear.sendKeys(year);
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldYear, Condition.empty, 0, "Истёк срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if one number is in card field")
    public void shouldNotBeSendCreditRequestIfOneNumberIsInCardField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.localMonth();
        creditPage.inputRandomNumbers(fieldYear, 1, 9);
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldYear, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if two letters are in year field")
    public void shouldNotBeSendCreditRequestIfTwoLettersAreInYearField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.localMonth();
        creditPage.inputRandomLetters(fieldYear, 2);
        creditPage.validName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldYear, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    //bug результат-успешно
    @Test
    @DisplayName("Should not be send credit request if name in Cyrillic")
    public void shouldNotBeSendCreditRequestIfNameInCyrillic() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.validDate();
        creditPage.invalidName();
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldName, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    //bug результат-успешно
    @Test
    @DisplayName("Should not be send credit request if number is in field name")
    public void shouldNotBeSendCreditRequestIfNumberIsInFieldName() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.validDate();
        creditPage.inputRandomNumbers(fieldName, 10, 100000);
        creditPage.validCode();
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldName, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if one number is in field code")
    public void shouldNotBeSendCreditRequestIfOneNumberIsInFieldCode() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        var creditPage = startPage.checkCreditSystem();

        creditPage.activeCard();
        creditPage.validDate();
        creditPage.validName();
        creditPage.inputRandomNumbers(fieldCode, 1, 9);
        creditPage.clickButton();

        assertTrue(creditPage.checkNotEmptyField(fieldCode, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    //bug поле Владелец заполнено корректно, но при пустом поле Код появляется запись, что поле Владелец обязательно для заполнения
    @Test
    @DisplayName("Should not be send credit request if three letters are in field code")
    void shouldNotBeSendCreditRequestIfThreeLettersAreInFieldCode() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.validDate();
        creditPage.validName();
        creditPage.inputRandomLetters(fieldCode, 3);
        creditPage.clickButton();

        assertTrue(creditPage.checkEmptyField(fieldCode, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be more then three numbers in field code for credit request")
    void shouldNotBeMoreThenThreeNumbersInFieldCodeForCreditRequest() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        int code = DataHelper.getRandomNumber(1000, 9999);
        var creditPage = startPage.checkCreditSystem();
        creditPage.activeCard();
        creditPage.validDate();
        creditPage.validName();
        fieldCode.sendKeys(String.valueOf(code));
        fieldCode.shouldHave(Condition.exactValue(String.valueOf(code / 10)));
        creditPage.clickButton();

        assertTrue(creditPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterCredit());
    }
}
