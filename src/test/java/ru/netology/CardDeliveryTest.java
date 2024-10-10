package ru.netology;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;


import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.nio.file.Files.exists;
import static java.time.LocalDate.*;


public class CardDeliveryTest {
    @BeforeEach

    public void Setup() {
        open("http://localhost:9999/");
    }

    public String generateDate(long addDays, String pattern) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern(pattern));
    }


    @Test
    public void shouldSendForm() {
        String planningDate = generateDate(7, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=notification] .notification__title").shouldHave(exactText("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content").shouldHave(exactText("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15));
    }

    @Test
    public void shouldValidateCity() {
        String planningDate = generateDate(9, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Нью-Йорк");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"), Duration.ofSeconds(15));

    }

    @Test
    public void shouldNotCity() {
        String planningDate = generateDate(1, "dd.MM.yyyy");

        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"), Duration.ofSeconds(15));

    }

    @Test
    public void shouldValidateDate() {

        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(exactText("Неверно введена дата"), Duration.ofSeconds(15));

    }

    @Test
    public void shouldValidateName() {
        String planningDate = generateDate(6, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Ivan Ivanoff@!");
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."), Duration.ofSeconds(15));

    }

    @Test
    public void shouldNoName() {
        String planningDate = generateDate(6, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"), Duration.ofSeconds(15));

    }

    @Test
    public void shouldValidatePhone() {
        String planningDate = generateDate(5, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79001002");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."), Duration.ofSeconds(15));
    }

    @Test
    public void shouldNoPhone() {
        String planningDate = generateDate(5, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"), Duration.ofSeconds(15));
    }

    @Test
    public void shouldInvalidCheckBox() {
        String planningDate = generateDate(5, "dd.MM.yyyy");
        $("[data-test-id=city] input").setValue("Екатеринбург");
        $(".calendar-input__custom-control input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79001002002");
        $(".button").click();
        $("[data-test-id=agreement].input_invalid").shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"), Duration.ofSeconds(15));
    }


}


