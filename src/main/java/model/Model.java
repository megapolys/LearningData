package model;

import loader.CsvLoader;

import java.util.Collections;
import java.util.List;

public class Model {

    public List<Row> rows;

    public void init(String csvFileName) {
        final CsvLoader csvLoader = new CsvLoader(csvFileName);
        rows = Collections.unmodifiableList(csvLoader.readFile());
    }

}
