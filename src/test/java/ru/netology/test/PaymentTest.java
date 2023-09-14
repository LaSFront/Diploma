package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.StartPage;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:8080", StartPage.class);
        SQLHelper.cleanDB();                                            //очищать "ДО" для проверки того, что в БД не ушел запрос
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    // !!!!!!! positive tests - payment
    @Test
    @DisplayName("Should be success payment all fields are valid")
    void shouldBeSuccessPaymentAllFieldsAreValid() {
        StartPage startPage = new StartPage();

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.validPaymentActiveCard();

        assertTrue(paymentPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should be success payment if double name is in name field")
    void shouldBeSuccessPaymentIfDoubleNameIsInNameField() {
        StartPage startPage = new StartPage();

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.validNameWithDash();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should be success payment if one letter is in name field")
    void shouldBeSuccessPaymentIfOneLetterIsInNameField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.inputRandomLetters(fieldName, 1);
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should be success payment if max letters are in name field")
    void shouldBeSuccessPaymentIfMaxLettersAreInNameField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.inputRandomLetters(fieldName, 30);
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    // bug результат - успешно
    @Test
    @DisplayName("Should get rejection in payment with valid inactive card")
    public void shouldGetRejectionInPaymentWithInactiveCard() {
        StartPage startPage = new StartPage();

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.rejectionInPaymentInactiveCard();

        assertTrue(paymentPage.massageError("Ошибка\n" + "Ошибка! Банк отказал в проведении операции."));
        assertEquals("DECLINED", SQLHelper.getStatusOfCardAfterPayment());
        assertNull(SQLHelper.getAmountOfPayment());
    }

    @Test
    @DisplayName("Should be success payment if card validity period is in current month")
    public void shouldBeSuccessPaymentIfValidPeriodIsInCurrentMonth() {
        StartPage startPage = new StartPage();

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.localMonth();
        paymentPage.localYear();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    // !!!!!!! negative tests - payment
    @Test
    @DisplayName("Should not be send payment request if all fields are empty")
    public void shouldNotBeSendPaymentRequestIfAllFieldsAreEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");
        SelenideElement fieldMonth = $("input[placeholder='08']");
        SelenideElement fieldYear = $("input[placeholder='22']");
        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        SelenideElement fieldCode = $("input[placeholder='999']");

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldCard, Condition.empty, 0, "Неверный формат"));
        assertTrue(paymentPage.checkEmptyField(fieldMonth, Condition.empty, 1, "Неверный формат"));
        assertTrue(paymentPage.checkEmptyField(fieldYear, Condition.empty, 2, "Неверный формат"));
        assertTrue(paymentPage.checkEmptyField(fieldName, Condition.empty, 3, "Поле обязательно для заполнения"));
        assertTrue(paymentPage.checkEmptyField(fieldCode, Condition.empty, 4, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());

    }

    @Test
    @DisplayName("Should not be send payment request if card field is empty")
    public void shouldNotBeSendPaymentRequestIfCardFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.validDate();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldCard, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if month field is empty")
    public void shouldNotBeSendPaymentRequestIfMonthFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.localYear();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldMonth, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if year field is empty")
    public void shouldNotBeSendPaymentRequestIfYearFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.localMonth();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldYear, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if name field is empty")
    public void shouldNotBeSendPaymentRequestIfNameFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldName, Condition.empty, 0, "Поле обязательно для заполнения"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    //bug поле Владелец заполнено корректно, но при пустом поле Код появляется запись, что поле Владелец обязательно для заполнения
    @Test
    @DisplayName("Should not be send payment request if code field is empty")
    public void shouldNotBeSendPaymentRequestIfCodeFieldIsEmpty() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");

        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.validName();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldCode, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should get rejection in payment with random unreal card")
    public void shouldGetRejectionInPaymentWithRandomUnrealCard() {
        StartPage startPage = new StartPage();

        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.randomCard();
        paymentPage.validDate();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.massageError("Ошибка\n" + "Ошибка! Банк отказал в проведении операции."));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
        assertNull(SQLHelper.getAmountOfPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 15 numbers are in card field")
    public void shouldNotBeSendPaymentRequestIfFifteenNumbersAreInCardField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCard = $("input[placeholder='0000 0000 0000 0000']");
        var paymentPage = startPage.checkPaymentSystem();

        fieldCard.sendKeys("444444444444444");
        paymentPage.validDate();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldCard, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card is overdue in passed month")
    public void shouldNotBeSendPaymentRequestIfCardIsOverdueInPassedMonth() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.dateOfPassedMonth();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if one number in month field ")
    public void shouldNotBeSendPaymentRequestIfOneNumberInMonthField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.inputRandomNumbers(fieldMonth, 1, 9);
        paymentPage.localYear();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 13 in month field")
    public void shouldNotBeSendPaymentRequestIf13InMonthField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.inputRandomNumbers(fieldMonth, 13, 13);
        paymentPage.localYear();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 00 in month field")
    public void shouldNotBeSendPaymentRequestIf00InMonthField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldMonth = $("input[placeholder='08']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        fieldMonth.sendKeys("00");
        paymentPage.localYear();
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldMonth, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card expiry date more then 6 years")
    public void shouldNotBeSendPaymentRequestIfCardExpiryDateMoreThen6Years() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var paymentPage = startPage.checkPaymentSystem();
        var year = String.valueOf((Integer.parseInt(DataHelper.getLocalYear()) + 6));

        paymentPage.activeCard();
        paymentPage.localMonth();
        fieldYear.sendKeys(year);
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldYear, Condition.empty, 0, "Неверно указан срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card expiry date is last year")
    public void shouldNotBeSendPaymentRequestIfCardExpiryDateIsLastYear() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var paymentPage = startPage.checkPaymentSystem();
        var year = String.valueOf((Integer.parseInt(DataHelper.getLocalYear()) - 1));

        paymentPage.activeCard();
        paymentPage.localMonth();
        fieldYear.sendKeys(year);
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldYear, Condition.empty, 0, "Истёк срок действия карты"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if one number is in year field")
    public void shouldNotBeSendPaymentRequestIfOneNumberIsInYearField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.localMonth();
        paymentPage.inputRandomNumbers(fieldYear, 1, 9);
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldYear, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if two letters are in year field")
    public void shouldNotBeSendPaymentRequestITwoLettersAreInYearField() {
        StartPage startPage = new StartPage();

        SelenideElement fieldYear = $("input[placeholder='22']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.localMonth();
        paymentPage.inputRandomLetters(fieldYear, 2);
        paymentPage.validName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldYear, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }


    //bug результат-успешно
    @Test
    @DisplayName("Should not be send payment request if name in Cyrillic")
    public void shouldNotBeSendPaymentRequestIfNameInCyrillic() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.invalidName();
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldName, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());

    }

    //bug результат-успешно
    @Test
    @DisplayName("Should not be send payment request if number is in field name")
    public void shouldNotBeSendPaymentRequestIfNumberIsInFieldName() {
        StartPage startPage = new StartPage();

        SelenideElement fieldName = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.inputRandomNumbers(fieldName, 10, 100000);
        paymentPage.validCode();
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldName, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if one number is in field code")
    public void shouldNotBeSendPaymentRequestIfOneNumberIsInFieldCode() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        var paymentPage = startPage.checkPaymentSystem();

        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.validName();
        paymentPage.inputRandomNumbers(fieldCode, 1, 9);
        paymentPage.clickButton();

        assertTrue(paymentPage.checkNotEmptyField(fieldCode, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    //bug поле Владелец заполнено корректно, но при пустом поле Код появляется запись, что поле Владелец обязательно для заполнения
    @Test
    @DisplayName("Should not be send payment request if three letters are in field code")
    void shouldNotBeSendPaymentRequestIfThreeLettersAreInFieldCode() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.validName();
        paymentPage.inputRandomLetters(fieldCode, 3);
        paymentPage.clickButton();

        assertTrue(paymentPage.checkEmptyField(fieldCode, Condition.empty, 0, "Неверный формат"));
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be more then three numbers in field code for payment request")
    void shouldNotBeMoreThenThreeNumbersInFieldCodeForPaymentRequest() {
        StartPage startPage = new StartPage();

        SelenideElement fieldCode = $("input[placeholder='999']");
        int code = DataHelper.getRandomNumber(1000, 9999);
        var paymentPage = startPage.checkPaymentSystem();
        paymentPage.activeCard();
        paymentPage.validDate();
        paymentPage.validName();
        fieldCode.sendKeys(String.valueOf(code));
        fieldCode.shouldHave(Condition.exactValue(String.valueOf(code / 10)));
        paymentPage.clickButton();

        assertTrue(paymentPage.massagePositive("Успешно\n" + "Операция одобрена Банком."));
        assertEquals("APPROVED", SQLHelper.getStatusOfCardAfterPayment());
    }
}
