package com.github.katushka.devopswithkubernetescourse.todoproject.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

@Named
@RequestScoped
public class Today {

    public String getDateString() {
        LocalDate today = LocalDate.now();
        return DateTimeFormatter.ISO_DATE.format(today);
    }

    public String getWeekString() {
        LocalDate today = LocalDate.now();
        String dayOfWeekPrintable = getDayOfWeek(today);
        return MessageFormat.format("{0}, {1} week",
                dayOfWeekPrintable,
                today.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
        );
    }

    private String getDayOfWeek(LocalDate today) {
        String dayOfWeek = DayOfWeek.of(today.get(ChronoField.DAY_OF_WEEK)).toString();
        return Character.toUpperCase(dayOfWeek.charAt(0)) + dayOfWeek.substring(1).toLowerCase();
    }
}
