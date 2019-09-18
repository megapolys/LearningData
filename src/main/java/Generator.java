import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Generator {

    private final Path csvPath;
    private final Path outDir;

    private String dateColumnName = "Date";
    private String valueColumnName = "Value";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD.MM.YYYY");

    private List<String[]> rows;
    private int datePos;
    private int valuePos;

    public static void main(String[] args) throws IOException {
        final CSVReader csvReader = new CSVReader(new FileReader("src\\main\\resources\\GAZR.csv"));
        csvReader.readNext();
        final List<String[]> rows = csvReader.readAll();
        rows.forEach(row -> System.out.println(Arrays.toString(row)));
    }

    public void readFile() {
        try {
            final CSVReader csvReader = new CSVReader(new FileReader(csvPath.toFile()));
            final String[] headers = csvReader.readNext();
            int dateColumnPos = -1;
            int valueColumnPos = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(dateColumnName)) {
                    dateColumnPos = i;
                }
                if (headers[i].equals(valueColumnName)) {
                    valueColumnPos = i;
                }
            }
            if (dateColumnPos == -1 || valueColumnPos == -1) {
                throw new RuntimeException("headers not found");
            }
            rows = csvReader.readAll();
            datePos = dateColumnPos;
            valuePos = valueColumnPos;
            if (simpleDateFormat.parse(rows.get(0)[datePos])
                    .compareTo(simpleDateFormat.parse(rows.get(1)[datePos])) > 0) {
                Collections.reverse(rows);
            }
        } catch (Exception e) {
            throw new RuntimeException("prepare file data error ", e);
        }
    }

    public void generate(int learnLen, int resultLen, boolean printDate) throws IOException {



    }

    private void writeFile(String outPath, int learnLen, int resultLen, int startPoint, boolean printDate) {


        try (final FileWriter writer = new FileWriter(outPath);
             final CSVWriter csvWriter = new CSVWriter(writer);) {
            if (printDate) {
                csvWriter.writeNext(new String[]{dateColumnName, valueColumnName});
            } else {
                csvWriter.writeNext(new String[]{valueColumnName});
            }
            for (int i = 0; i < learnLen; i++) {
                if (printDate) {
                    csvWriter.writeNext(new String[]{rows.get(startPoint + i)[datePos], rows.get(startPoint + i)[valuePos]});
                } else {
                    csvWriter.writeNext(new String[]{rows.get(startPoint + i)[valuePos]});
                }
            }
            startPoint += learnLen;
            for (int i = 0; i < resultLen; i++) {
                if (printDate) {
                    csvWriter.writeNext(new String[]{rows.get(startPoint + i)[datePos], rows.get(startPoint + i)[valuePos]});
                } else {
                    csvWriter.writeNext(new String[]{rows.get(startPoint + i)[valuePos]});
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("file writing error", ioe);
        }
    }

    public Generator(String csvPath, String outDir) {
        final Path csv = Paths.get(csvPath);
        final Path out = Paths.get(outDir);
        if (!Files.isRegularFile(csv) || !Files.isDirectory(out)) {
            throw new RuntimeException("paths invalid");
        }
        this.csvPath = csv;
        this.outDir = out;
    }

    public void setDateColumnName(String dateColumnName) {
        this.dateColumnName = dateColumnName;
    }

    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

    public void setSimpleDateFormat(String simpleDateFormat) {
        this.simpleDateFormat = new SimpleDateFormat(simpleDateFormat);
    }
}
