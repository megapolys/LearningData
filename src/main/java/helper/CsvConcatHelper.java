package helper;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CsvConcatHelper {

    private String dirName;
    private List<File> csvFiles;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private String[] headers = new String[] { "Date", "Close", "Open", "Max", "Min", "Delta" };

    public static void main(String[] args) {
        final CsvConcatHelper csvConcatHelper = new CsvConcatHelper("E:\\валютные пары");
        csvConcatHelper.concatAll();
    }

    public CsvConcatHelper(String dirName) {
        this.dirName = dirName;
        final File dir = new File(dirName);
        if (!dir.isDirectory()) {
            throw new RuntimeException("не директория");
        }
        csvFiles = Arrays.asList(dir.listFiles(file -> file.getName().endsWith(".csv") && file.getName().length() > 11));
    }

    public void concatAll() {
        csvFiles.sort(Comparator.comparing(File::getName));
        Collections.reverse(csvFiles);
        for (int i = 0; i < csvFiles.size(); i += 2) {
            concat(csvFiles.get(i), csvFiles.get(i + 1));
        }
    }

    private void concat(File f1, File f2) {
        System.out.println(f1.getName() + " " + f2.getName());
        final List<String[]> rows1 = getOneFile(f1);
        final List<String[]> rows2 = getOneFile(f2);
        rows1.addAll(rows2);
        final File outFile = new File(f1.getParent(), f1.getName().substring(17, 24) + ".csv");
        try {
            outFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("create out file error");
        }
        writeNewFullyFile(rows1, outFile);
    }

    private List<String[]> getOneFile(File f) {
        List<String[]> rows;
        try (final CSVReader csvReader = new CSVReader(new FileReader(f))){
            csvReader.readNext();
            rows = csvReader.readAll();
            if (simpleDateFormat.parse(rows.get(0)[0])
                    .compareTo(simpleDateFormat.parse(rows.get(1)[0])) > 0) {
                Collections.reverse(rows);
            }
        } catch (Exception e) {
            throw new RuntimeException("file concat error", e);
        }
        return rows;
    }
    private void writeNewFullyFile(List<String[]> rows, File outFile) {
        try (final CSVWriter csvWriter = new CSVWriter(new FileWriter(outFile))) {
            csvWriter.writeNext(headers);
            csvWriter.writeAll(rows);
        } catch (Exception e) {
            throw new RuntimeException("file concat write error", e);
        }
    }

}
