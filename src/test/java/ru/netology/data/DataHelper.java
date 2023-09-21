package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.RandomStringUtils;

public class DataHelper {

    private static final Faker faker = new Faker(new Locale("ru"));

    private DataHelper() {
    }

    @Value
    public static class UserInfo {
        private String card;
        private String month;
        private String year;
        private String name;
        private String code;
    }

    // card
    @Value
    public static class Card {
        private String card;
        private String status;
    }

    public static Card getValidActiveCard() {
        return new Card("4444 4444 4444 4441", "APPROVED");
    }

    public static Card getValidInactiveCard() {
        return new Card("4444 4444 4444 4442", "DECLINED");
    }

    // date

    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    // выберет любую дату в диапазоне от сегодня до 31 декабря года=текущему + 5
    //1825 = 365*5 дней за 5 лет

    public static LocalDate getDateInValidRange() {
        int localMonth = LocalDate.now().getMonthValue();
        return LocalDate.now().plusDays(getRandomNumber(1, (12 - localMonth) * 30 + 1825));
    }

    public static String getValidMonth() {
        return getDateInValidRange().format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getValidYear() {
        return getDateInValidRange().format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateValidMonth(int num) {
        return LocalDate.now().plusMonths(num).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateValidYear(int num) {
        return LocalDate.now().plusYears(num).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateInvalidMonth(int num) {
        return LocalDate.now().minusMonths(num).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateInvalidYear(int num) {
        return LocalDate.now().minusYears(num).format(DateTimeFormatter.ofPattern("yy"));
    }

    //name
    public static String getValidName() {
        String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String firstName = toLatinTrans.transliterate(faker.name().firstName()).replaceAll("[^a-zA-Z ]", "");
        String lastName = toLatinTrans.transliterate(faker.name().lastName()).replaceAll("[^a-zA-Z ]", "");
        return (lastName + " " + firstName);
    }

    public static String getValidNameWithDash() {
        String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String lastName2 = toLatinTrans.transliterate(faker.name().lastName()).replaceAll("[^a-zA-Z ]", "");
        return lastName2 + "-" + getValidName();
    }

    public static String getCyrillicName() {
        return (faker.name().lastName().toUpperCase(Locale.forLanguageTag("ru")) + " " + faker.name().firstName().toUpperCase(Locale.forLanguageTag("ru")));
    }

    //commons
    public static String generateRandomNumber(int number) {
        Faker faker = new Faker();
        return String.valueOf(faker.number().digits(number));
    }

    public static String getRandomLetters(int numbers) {
        return RandomStringUtils.randomAlphabetic(numbers);
    }

    public static String getRandomSymbols(int numbers) {
        String symbols = "!~`@#$%^&*()_+*/|,.?{}[]:;'<> ";
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numbers; i++) {
            builder.append(symbols.charAt(random.nextInt(symbols.length() - 1)));
        }
        return builder.toString();
    }
}



