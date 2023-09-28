package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PaymentTest {
    private static String url = System.getProperty("SUT.url");

    @BeforeEach
    void setUp() { //в build.gradle
        open(url);
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

    // positive tests
    @Test
    @DisplayName("Should be success payment all fields are valid")
    void shouldBeSuccessPaymentAllFieldsAreValid() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should be success payment if double name is in name field")
    void shouldBeSuccessPaymentIfDoubleNameIsInNameField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidNameWithDash(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should be success payment if one letter is in name field")
    void shouldBeSuccessPaymentIfOneLetterIsInNameField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getRandomLetters(1),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should be success payment if max letters are in name field")
    void shouldBeSuccessPaymentIfMaxLettersAreInNameField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getRandomLetters(30),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    @Test
    @DisplayName("Should get rejection in payment on valid inactive card")
    public void shouldGetRejectionInPaymentOnInactiveCard() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidInactiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messageError();
        assertEquals(DataHelper.getValidInactiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
        assertNull(SQLHelper.getAmountOfPayment());
    }

    @Test
    @DisplayName("Should be success payment if card validity period is in current month")
    public void shouldBeSuccessPaymentIfValidPeriodIsInCurrentMonth() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(0),
                        DataHelper.generateValidYear(0),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
        assertEquals(45000, SQLHelper.getAmountOfPayment() / 100);
    }

    // !!!!!!! negative tests - payment
    @Test
    @DisplayName("Should not be send payment request if all fields are empty")
    public void shouldNotBeSendPaymentRequestIfAllFieldsAreEmpty() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        StringUtils.EMPTY);
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        paymentPage.notificationFormat(1);
        paymentPage.notificationFormat(2);
        paymentPage.notificationRequiredField(3);
        paymentPage.notificationFormat(4);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card field is empty")
    public void shouldNotBeSendPaymentRequestIfCardFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        StringUtils.EMPTY,
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if month field is empty")
    public void shouldNotBeSendPaymentRequestIfMonthFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        StringUtils.EMPTY,
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if year field is empty")
    public void shouldNotBeSendPaymentRequestIfYearFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        StringUtils.EMPTY,
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if name field is empty")
    public void shouldNotBeSendPaymentRequestIfNameFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        StringUtils.EMPTY,
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationRequiredField(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if code field is empty")
    public void shouldNotBeSendPaymentRequestIfCodeFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        StringUtils.EMPTY);
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should get rejection in payment with random unreal card")
    public void shouldGetRejectionInPaymentWithRandomUnrealCard() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.generateRandomNumber(16),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.messageError();
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
        assertNull(SQLHelper.getAmountOfPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 15 numbers are in card field")
    public void shouldNotBeSendPaymentRequestIfFifteenNumbersAreInCardField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        "444444444444444",
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card is overdue in passed month")
    public void shouldNotBeSendPaymentRequestIfCardIsOverdueInPassedMonth() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(-1),
                        DataHelper.generateValidYear(0),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if one number in month field ")
    public void shouldNotBeSendPaymentRequestIfOneNumberInMonthField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateRandomNumber(1),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 13 in month field")
    public void shouldNotBeSendPaymentRequestIf13InMonthField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        "13",
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 00 in month field and current year")
    public void shouldNotBeSendPaymentRequestIf00InMonthFieldAndCurrentYear() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        "00",
                        DataHelper.generateValidYear(0),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if 00 in month field and not current year")
    public void shouldNotBeSendPaymentRequestIf00InMonthFieldAndNotCurrentYear() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        "00",
                        DataHelper.generateValidYear(1),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card expiry date more then 5 years")
    public void shouldNotBeSendPaymentRequestIfCardExpiryDateMoreThen5Years() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(0),
                        DataHelper.generateValidYear(6),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if card expiry date is passed year")
    public void shouldNotBeSendPaymentRequestIfCardExpiryDateIsPassedYear() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(0),
                        DataHelper.generateValidYear(-1),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationExpired(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if one number is in year field")
    public void shouldNotBeSendPaymentRequestIfOneNumberIsInYearField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(0),
                        DataHelper.generateRandomNumber(1),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if two letters are in year field")
    public void shouldNotBeSendPaymentRequestIfTwoLettersAreInYearField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(0),
                        DataHelper.getRandomLetters(2),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if name in Cyrillic")
    public void shouldNotBeSendPaymentRequestIfNameInCyrillic() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getCyrillicName(),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());

    }

    @Test
    @DisplayName("Should not be send payment request if symbols are in name field")
    public void shouldNotBeSendPaymentRequestIfSymbolsAreInNameField() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getRandomSymbols(18),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if number is in field name")
    public void shouldNotBeSendPaymentRequestIfNumberIsInFieldName() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.generateRandomNumber(6),
                        DataHelper.generateRandomNumber(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if one number is in field code")
    public void shouldNotBeSendPaymentRequestIfOneNumberIsInFieldCode() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(1));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be send payment request if three letters are in field code")
    void shouldNotBeSendPaymentRequestIfThreeLettersAreInFieldCode() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.getRandomLetters(3));
        paymentPage.orderTour(userInfo);

        paymentPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterPayment());
    }

    @Test
    @DisplayName("Should not be more then three numbers in field code for payment request")
    void shouldNotBeMoreThenThreeNumbersInFieldCodeForPaymentRequest() {
        StartPage startPage = new StartPage();
        var paymentPage = startPage.checkPaymentSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(4));
        paymentPage.orderTour(userInfo);

        paymentPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterPayment());
    }
}
