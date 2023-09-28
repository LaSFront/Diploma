package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class CreditTest {
    private static String url = System.getProperty("SUT.url");

    @BeforeEach
    void setUp() {
        open(url);
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

    // positive tests
    @Test
    @DisplayName("Should be approved credit all fields are valid")
    void shouldBeApprovedCreditAllFieldsAreValid() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should be approved credit if double name is in name field")
    void shouldBeApprovedCreditIfDoubleNameIsInNameField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidNameWithDash(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should  be approved credit if one letter is in name field")
    void shouldBeApprovedCreditIfOneLetterIsInNameField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getRandomLetters(1),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should be approved credit if max letters are in name field")
    void shouldBeApprovedCreditIfMaxLettersAreInNameField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getRandomLetters(30),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should get credit rejection on valid inactive card")
    public void shouldGetCreditRejectionOnValidInactiveCard() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidInactiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.messageError();
        assertEquals(DataHelper.getValidInactiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should be approved credit if card validity period is in current month")
    public void shouldBeApprovedCreditIfValidPeriodIsInCurrentMonth() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(0),
                        DataHelper.generateValidYear(0),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);
        creditPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }

    // negative tests
    @Test
    @DisplayName("Should not be send credit request if all fields are empty")
    public void shouldNotBeSendCreditRequestIfAllFieldsAreEmpty() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        StringUtils.EMPTY);
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        creditPage.notificationFormat(1);
        creditPage.notificationFormat(2);
        creditPage.notificationRequiredField(3);
        creditPage.notificationFormat(4);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card field is empty")
    public void shouldNotBeSendCreditRequestIfCardFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        StringUtils.EMPTY,
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if month field is empty")
    public void shouldNotBeSendCreditRequestIfMonthFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        StringUtils.EMPTY,
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if year field is empty")
    public void shouldNotBeSendCreditRequestIfYearFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        StringUtils.EMPTY,
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);
        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if name field is empty")
    public void shouldNotBeSendCreditRequestIfNameFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        StringUtils.EMPTY,
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationRequiredField(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if code field is empty")
    public void shouldNotBeSendCreditRequestIfCodeFieldIsEmpty() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        StringUtils.EMPTY);
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should get credit rejection with unreal random card")
    public void shouldGetCreditRejectionInPaymentWithRandomUnrealCard() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.generateRandomNumber(16),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.messageError();
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 15 numbers are in card field")
    public void shouldNotBeSendCreditRequestIfFifteenNumbersAreInCardField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        "444444444444444",
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card is overdue in passed month")
    public void shouldNotBeSendCreditRequestIfCardIsOverdueInPassedMonth() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateValidMonth(-1),
                        DataHelper.generateValidYear(0),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if one number in month field ")
    public void shouldNotBeSendCreditRequestIfOneNumberInMonthField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        DataHelper.generateRandomNumber(1),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 13 in month field")
    public void shouldNotBeSendCreditRequestIf13InMonthField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        "13",
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 00 in month field and current year")
    public void shouldNotBeSendCreditRequestIf00InMonthFieldAndCurrentYear() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        "00",
                        DataHelper.generateValidYear(0),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if 00 in month field and not current year")
    public void shouldNotBeSendCreditRequestIf00InMonthFieldAndNotCurrentYear() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        "00",
                        DataHelper.generateValidYear(1),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card expiry date more then 5 years")
    public void shouldNotBeSendCreditRequestIfCardExpiryDateMoreThen5Years() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        DataHelper.generateValidYear(6),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationIncorrectDeadline(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if card expiry date is passed year")
    public void shouldNotBeSendCreditRequestIfCardExpiryDateIsPassedYear() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        DataHelper.generateValidYear(-1),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationExpired(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if one number is in year field")
    public void shouldNotBeSendCreditRequestIfOneNumberIsInYearField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        DataHelper.generateRandomNumber(1),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if two letters are in year field")
    public void shouldNotBeSendCreditRequestIfTwoLettersAreInYearField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        DataHelper.getRandomLetters(2),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if name in Cyrillic")
    public void shouldNotBeSendCreditRequestIfNameInCyrillic() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getCyrillicName(),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if symbols are in name field")
    public void shouldNotBeSendCreditRequestIfSymbolsAreInNameField() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getRandomSymbols(18),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if number is in field name")
    public void shouldNotBeSendCreditRequestIfNumberIsInFieldName() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.generateRandomNumber(6),
                        DataHelper.generateRandomNumber(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if one number is in field code")
    public void shouldNotBeSendCreditRequestIfOneNumberIsInFieldCode() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(1));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be send credit request if three letters are in field code")
    void shouldNotBeSendCreditRequestIfThreeLettersAreInFieldCode() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.getRandomLetters(3));
        creditPage.creditForTour(userInfo);

        creditPage.notificationFormat(0);
        assertNull(SQLHelper.getStatusOfCardAfterCredit());
    }

    @Test
    @DisplayName("Should not be more then three numbers in field code for credit request")
    void shouldNotBeMoreThenThreeNumbersInFieldCodeForCreditRequest() {
        StartPage startPage = new StartPage();
        var creditPage = startPage.checkCreditSystem();
        var date = DataHelper.getValidDate();
        DataHelper.UserInfo userInfo =
                new DataHelper.UserInfo(
                        DataHelper.getValidActiveCard().getCard(),
                        date.getMonth(),
                        date.getYear(),
                        DataHelper.getValidName(),
                        DataHelper.generateRandomNumber(4));
        creditPage.creditForTour(userInfo);

        creditPage.messagePositive();
        assertEquals(DataHelper.getValidActiveCard().getStatus(), SQLHelper.getStatusOfCardAfterCredit());
    }
}
