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

    public Row(LocalDate date, String open, String close, String min, String max, String delta) {
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
            open,
            close,
            min,
            max,
            delta
        );
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
