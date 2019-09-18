import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Generator {
    private final static Logger log = Logger.getLogger(Generator.class.getName());

    private final Path csvPath;
    private final Path outDir;
    private final String dataName;

    private String dateColumnName = "Date";
    private String valueColumnName = "Value";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private SimpleDateFormat simpleDateFrmatOut = new SimpleDateFormat("MM.yyyy");

    private List<String[]> rows;
    private int datePos;
    private int valuePos;

    private int totalFiles;
    private AtomicInteger nowCreated = new AtomicInteger();

    public static void main(String[] args) throws IOException {
        String csvPath = "src\\main\\resources\\GAZR.csv";
        String outDir = "src\\main\\resources\\out";
        Generator generator = new Generator(csvPath, outDir);
        generator.readFile();
        generator.generate(100, 10, 30, true);
    }

    public void generate(int learnLen, int resultLen, int count, boolean printDate) throws IOException {

        totalFiles += count;

        for (int i = 0; i < rows.size(); i += rows.size() / count) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(outDir.toString()).append("\\");
            stringBuilder.append(dataName).append('_');
            stringBuilder.append(learnLen).append('%').append(resultLen).append('_');
            stringBuilder.append(nowCreated.get()).append(".csv");

            writeFile(stringBuilder.toString(), learnLen, resultLen, i, printDate);
        }

    }

    public void readFile() {
        try {
            Files.walk(outDir).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            final CSVReader csvReader = new CSVReader(new FileReader(csvPath.toFile()));
            final String[] headers = csvReader.readNext();
            int dateColumnPos = -1;
            int valueColumnPos = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].contains(dateColumnName)) {
                    dateColumnPos = i;
                }
                if (headers[i].contains(valueColumnName)) {
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
            System.out.println("Rows count : " + rows.size());
        } catch (Exception e) {
            throw new RuntimeException("prepare file data error ", e);
        }
    }

    private void writeFile(String outPath, int learnLen, int resultLen, int startPoint, boolean printDate) throws IOException {

        final File outFile = new File(outPath);
        outFile.createNewFile();
        try (final FileWriter writer = new FileWriter(outPath);
             final CSVWriter csvWriter = new CSVWriter(writer)) {
            final int iLeft = startPoint + learnLen;
            final int iRight = startPoint + learnLen + resultLen;
            if (printDate) {
                csvWriter.writeNext(new String[]{dateColumnName, valueColumnName});
                for (int i = startPoint; i < iLeft; i++) {
                    writeRowWithDate(csvWriter, startPoint + i);
                }
                for (int i = iLeft; i < iRight; i++) {
                    writeRowWithDate(csvWriter, startPoint + i);
                }
            } else {
                csvWriter.writeNext(new String[]{valueColumnName});
                for (int i = startPoint; i < iLeft; i++) {
                    writeRow(csvWriter, startPoint + i);
                }
                for (int i = iLeft; i < iRight; i++) {
                    writeRow(csvWriter, startPoint + i);
                }
            }
        } catch (Exception e) {
            outFile.deleteOnExit();
            if (!(e instanceof IndexOutOfBoundsException)) throw new RuntimeException("file writing error", e);
        } finally {
            log.info("created : " + nowCreated.incrementAndGet() + " of total : " + totalFiles);
        }
    }

    private void writeRow(CSVWriter csvWriter, int i) throws Exception {
        csvWriter.writeNext(new String[]{rows.get(i)[valuePos]});
    }

    private void writeRowWithDate(CSVWriter csvWriter, int i) throws Exception {
        csvWriter.writeNext(new String[]{rows.get(i)[datePos], rows.get(i)[valuePos]});
    }

    public Generator(String csvPath, String outDir) {
        final Path csv = Paths.get(csvPath);
        final Path out = Paths.get(outDir);
        if (!Files.isRegularFile(csv) || !Files.isDirectory(out)) {
            throw new RuntimeException("paths invalid");
        }
        this.csvPath = csv;
        this.outDir = out;
        final String[] split = csvPath.split("[/\\\\]");
        this.dataName = split[split.length - 1].replaceAll("\\.csv", "");
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
