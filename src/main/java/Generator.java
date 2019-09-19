import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Generator {
    private final static Logger log = Logger.getLogger(Generator.class.getName());

    private final Path csvPath;
    private final Path outDir;

    private String dateColumnName = "Date";
    private String valueColumnName = "Value";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private List<String[]> rows;
    private int datePos;
    private int valuePos;

    private int totalFiles;
    private AtomicInteger nowCreated = new AtomicInteger();

    public static void main(String[] args) throws IOException {
        String csvPath = "src\\main\\resources\\GAZR.csv";
        String outDir = "E:\\Daniil\\YandexDisk\\Папка успеха\\learning_data";
        Generator generator = new Generator(csvPath, outDir);
        generator.readFile();
        generator.generate(200, 10, 1);

    }

    public void generate(int learnLen, int resultLen, int step) throws IOException {

        final int allLen = learnLen + resultLen;
        totalFiles += (rows.size() - allLen) / step;

        final String[] split = csvPath.toString().split("[/\\\\]");
        final String dataName = split[split.length - 1].replaceAll("\\.csv", "");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(outDir.toString()).append("\\");
        stringBuilder.append(dataName).append('_');
        stringBuilder.append(learnLen).append('%').append(resultLen).append('_');

        new File(stringBuilder.toString()).mkdir();

        for (int i = 0; (i + allLen) < rows.size(); i += step) {
            writeFile(stringBuilder.toString() + "\\" + nowCreated.get() + ".txt", learnLen, resultLen, i);
        }

    }

    public void readFile() {
        try {
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

    private void writeFile(String outPath, int learnLen, int resultLen, int startPoint) throws IOException {

        final File outFile = new File(outPath);
        outFile.createNewFile();
        try (final FileWriter writer = new FileWriter(outPath)) {
            final int iLeft = startPoint + learnLen;
            final int iRight = startPoint + learnLen + resultLen;
            for (int i = startPoint; i < iLeft - 1; i++) {
                writer.write(rows.get(i)[valuePos] + " ");
            }
            writer.write(rows.get(iLeft - 1)[valuePos]);
            writer.write(";");
            for (int i = iLeft; i < iRight - 1; i++) {
                writer.write(rows.get(i)[valuePos] + " ");
            }
            writer.write(rows.get(iRight - 1)[valuePos]);
        } catch (Exception e) {
            throw new RuntimeException("file writing error", e);
        } finally {
            log.info("created : " + nowCreated.incrementAndGet() + " of total : " + totalFiles);
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
