package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Row {

    public final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public final LocalDate date;
    public final String open;
    public final String close;
    public final String min;
    public final String max;
    public final String delta;

    private Row(LocalDate date, String open, String close, String min, String max, String delta) {
        this.date = date;
        this.open = open;
        this.close = close;
        this.min = min;
        this.max = max;
        this.delta = delta;
    }

    public static Row of(String date, String open, String close, String min, String max, String delta) {
        return new Row(
            LocalDate.parse(date, dateFormatter),
            toPointSeparator(open),
            toPointSeparator(close),
            toPointSeparator(min),
            toPointSeparator(max),
            toPointSeparator(delta)
        );
    }

    private static String toPointSeparator(String f) {
        return f.replace(",", ".");
    }

    public String get(Headers.Header header) {
        switch (header) {
            case DATE: return date.format(dateFormatter);
            case OPEN: return open;
            case CLOSE: return close;
            case MIN: return min;
            case MAX: return max;
            case DELTA: return delta;
        }
        return null;
    }
}
