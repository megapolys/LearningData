package loader;

import com.opencsv.CSVReader;
import logger.Logger;
import model.Headers;
import model.Row;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CsvLoader {

    private final static Logger log = new Logger(CsvLoader.class);

    private Headers headers;
    private final String csvFile;

    public CsvLoader(String csvFile) {
        this.csvFile = csvFile;
    }

    public List<Row> readFile() {
        List<String[]> rows;
        List<Row> rowsRes = new ArrayList<>();
        try {
            final CSVReader csvReader = new CSVReader(new FileReader(csvFile));
            headers = new Headers(csvReader.readNext());
            rows = csvReader.readAll();
            rows.forEach(strings -> {
                final Row row = Row.of(
                    strings[headers.date()],
                    strings[headers.open()],
                    strings[headers.close()],
                    strings[headers.min()],
                    strings[headers.max()],
                    strings[headers.delta()]
                );
                rowsRes.add(row);
            });
            if (rowsRes.get(0).date.compareTo(rowsRes.get(1).date) > 0) {
                Collections.reverse(rowsRes);
            }
            System.out.println("Rows count : " + rows.size());
        } catch (Exception e) {
            throw new RuntimeException("prepare file data error ", e);
        }
        return rowsRes;
    }

    public Headers getHeaders() {
        return headers;
    }
}
