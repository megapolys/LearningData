package model;

import java.util.Arrays;

public class Headers {

    public static enum Header {

        DATE("Date"),
        OPEN("Open"),
        CLOSE("Close"),
        MIN("Min"),
        MAX("Max"),
        DELTA("Delta"),
        UNKNOWN("");

        private String name;
        private int pos;

        Header(String name) {
            this.name = name;
            this.pos = -1;
        }

        public static Header from(String str) {
            return Arrays.stream(Header.values())
                .filter(h -> h.name.equalsIgnoreCase(str))
                .findFirst().orElse(UNKNOWN);
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

    }

    public Headers(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].replaceAll("\\W", "");
            Header.from(headers[i]).setPos(i);
        }
    }

    public int date() {
        return Header.DATE.pos;
    }

    public int open() {
        return Header.OPEN.pos;
    }

    public int close() {
        return Header.CLOSE.pos;
    }

    public int min() {
        return Header.MIN.pos;
    }

    public int max() {
        return Header.MAX.pos;
    }

    public int delta() {
        return Header.DELTA.pos;
    }
}
