package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class StartPage {
    private final ElementsCollection viewStartPage = $$(".heading");
    private final ElementsCollection button = $$(".button");

    public StartPage() {
        viewStartPage.findBy(text("Путешествие дня")).shouldBe(visible);
    }

    public PaymentPage checkPaymentSystem() {
        button.findBy(text("Купить")).click();
        return new PaymentPage();
    }

    public CreditPage checkCreditSystem() {
        button.findBy(text("Купить в кредит")).click();
        return new CreditPage();
    }
}
