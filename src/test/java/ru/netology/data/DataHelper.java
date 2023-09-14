package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.RandomStringUtils;

public class DataHelper {

    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static String getRandomLetters(int numbers) {
        return RandomStringUtils.randomAlphabetic(numbers);
    }

    private DataHelper() {
    }

    @Value
    public static class Card {
        private String card;
    }

    public static Card getValidActiveCard() {
        return new Card("4444 4444 4444 4441");
    }

    public static Card getValidInactiveCard() {
        return new Card("4444 4444 4444 4442");
    }

    @Value
    public static class DateOfCard {
        private String month;
        private String year;
    }

    public static DateOfCard getValidDateOfCard() {
        int plusDay;
        int localMonth = LocalDate.now().getMonthValue();
        int maxDays = (12 - localMonth) * 30 + 1825;
        plusDay = getRandomNumber(1, maxDays);
        String year = String.valueOf(LocalDate.now().plusDays(plusDay).getYear()).substring(2, 4);
        int monthNew = LocalDate.now().plusDays(plusDay).getMonthValue();
        StringBuilder monthBuilder = new StringBuilder();
        if (monthNew < 10) {
            monthBuilder.append(0);
            monthBuilder.append(monthNew);
        } else {
            monthBuilder.append(monthNew);
        }
        String month = monthBuilder.toString();
        return new DateOfCard(month, year);
    }

    public static DateOfCard getDateMinusMonth() {
        LocalDate fullDate = LocalDate.now().minusMonths(1);
        int month = fullDate.getMonthValue();
        StringBuilder builder = new StringBuilder();
        if (month < 10) {
            builder.append(0);
            builder.append(month);
        } else {
            builder.append(month);
        }
        String passedMonth = builder.toString();
        String yearOfPassedMonth = String.valueOf(fullDate.getYear()).substring(2, 4);
        return new DateOfCard(passedMonth, yearOfPassedMonth);

    }

    @Value
    public static class Name {
        private String name;
    }

    public static Name getValidName() {
        Faker faker = new Faker(new Locale("ru"));
        String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String firstName = toLatinTrans.transliterate(faker.name().firstName()).replaceAll("[^a-zA-Z ]", "");
        String lastName = toLatinTrans.transliterate(faker.name().lastName()).replaceAll("[^a-zA-Z ]", "");
        String builder = lastName + " " + firstName;
        return new Name(builder.toUpperCase(Locale.forLanguageTag("en")));
    }

    public static Name getValidNameWithDash() {
        Faker faker = new Faker(new Locale("ru"));
        String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String lastName1 = toLatinTrans.transliterate(faker.name().lastName()).replaceAll("[^a-zA-Z ]", "");
        String lastName2 = toLatinTrans.transliterate(faker.name().lastName()).replaceAll("[^a-zA-Z ]", "");
        String firsName = toLatinTrans.transliterate(faker.name().firstName()).replaceAll("[^a-zA-Z ]", "");
        String builder = lastName1 + "-" + lastName2 + " " + firsName;
        return new Name(builder.toUpperCase(Locale.forLanguageTag("en")));
    }

    public static Name getCyrillicName() {
        Faker faker = new Faker(new Locale("ru"));
        return new Name(faker.name().fullName().toUpperCase(Locale.forLanguageTag("ru")));
    }

    @Value
    public static class Code {
        private String code;
    }

    public static Code getValidCode() {
        int max = 1;
        int min = 999;
        int code = getRandomNumber(min, max);
        StringBuilder builder = new StringBuilder();
        if (code < 100 && code >= 10) {
            builder.append("0");
            builder.append(code);
        } else if (code < 10) {
            builder.append("0");
            builder.append("0");
            builder.append(code);
        } else {
            builder.append(code);
        }
        return new Code(builder.toString());
    }

    ///////////////////////////////////////////////////////
    public static String getRandomCard() {
        Random random = new Random();
        int counter = 0;
        StringBuilder builder = new StringBuilder();
        while (counter <= 16) {
            int generate = random.nextInt(9);
            builder.append(generate);
            counter++;
        }
        return builder.toString();
    }

    public static String getLocalMonth() {
        int localMonth = LocalDate.now().getMonthValue();
        StringBuilder builder = new StringBuilder();
        if (localMonth < 10) {
            builder.append(0);
            builder.append(localMonth);
        }
        return builder.toString();
    }

    public static String getLocalYear() {
        String localYear = String.valueOf(LocalDate.now().getYear()).substring(2, 4);
        return localYear;
    }
}



