import model.Model;
import view.SimpleInFileView;
import view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class Generator {
    private final static Logger log = Logger.getLogger(Generator.class.getName());

    private final String csvFile;
    private final String outDir;

    private Model model;
    private View view;

    private int totalLines = 0;
    private int nowCreated;

    public static void main(String[] args) throws IOException {
        String csvPath = "src\\main\\resources\\GAZR.csv";
//        String outDir = "E:\\Daniil\\YandexDisk\\Java\\LearningData\\src\\main\\resources\\generated";
//        String outDir = "E:\\Daniil\\YandexDisk\\Папка успеха\\learning_data";
        String outDir = "E:\\Daniil\\YandexDisk\\Java\\LearningData\\src\\main\\resources\\out";
        final Model model = new Model();
        final View view = new SimpleInFileView();
        Generator generator = new Generator(csvPath, outDir, model, view);
        generator.init();
        generator.generate(200, 10, 1, 2000);
        generator.generateChecksData(200, 10, 1, 2000);

    }
    public Generator(String csvFile, String outDir, Model model, View view) {
        if (!new File(csvFile).isFile() || !new File(outDir).isDirectory()) {
            throw new RuntimeException("paths invalid");
        }
        this.csvFile = csvFile;
        this.outDir = outDir;
        this.model = model;
        this.view = view;
    }

    public void init() {
        model.init(csvFile);
        view.init();
    }

    public void generate(int learnLen, int resultLen, int step, int separator) throws IOException {

        final int allLen = learnLen + resultLen;
        totalLines += (model.rows.size() - allLen) / step;

        StringBuilder stringBuilder = createDirAndGetName();
        stringBuilder.append("\\").append(learnLen).append('%').append(resultLen);
        final String pathname = stringBuilder.toString() + "_" + step + "_learn.txt";
        final File outFile = new File(pathname);
        outFile.createNewFile();

        try (final FileWriter writer = new FileWriter(pathname)) {
            for (int i = 0; (i + allLen) < separator; i += step) {
                writeLine(writer, learnLen, resultLen, i);
            }
        } catch (Exception e) {
            throw new RuntimeException("file writing error", e);
        }

        log.info("created : " + nowCreated + " of total : " + totalLines);
    }

    public void generateChecksData(int learnLen, int resultLen, int step, int separator) throws IOException {
        final int allLen = learnLen + resultLen;
        totalLines += (model.rows.size() - allLen) / step;

        StringBuilder stringBuilder = createDirAndGetName();
        stringBuilder.append("\\").append(learnLen).append('%').append(resultLen);
        final String pathname = stringBuilder.toString() + "_" + step + "_";
        final File dataFile = new File(pathname + "check.txt");
        final File resultFile = new File(pathname + "result.txt");
        dataFile.createNewFile();
        resultFile.createNewFile();


        try (final FileWriter dataWriter = new FileWriter(dataFile);
             final FileWriter resultWriter = new FileWriter(resultFile)) {
            for (int i = separator; (i + allLen) < model.rows.size(); i += step) {
                final int iLeft = i + learnLen;
                final int iRight = i + learnLen + resultLen;
                for (int j = iLeft; j < iRight - 1; j++) {
                    dataWriter.write(model.rows.get(j).close + " ");
                }
                dataWriter.write(model.rows.get(iLeft - 1).close);
                dataWriter.write("\n");
                for (int j = iLeft; j < iRight - 1; j++) {
                    resultWriter.write(model.rows.get(j).close + " ");
                }
                resultWriter.write(model.rows.get(iRight - 1).close);
                resultWriter.write("\n");
                if (++nowCreated % 100 == 0) {
                    log.info("created : " + nowCreated + " of total : " + totalLines);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("file writing error", e);
        }

        log.info("created : " + nowCreated + " of total : " + totalLines);
    }

    private StringBuilder createDirAndGetName() {
        final String[] split = csvFile.split("[/\\\\]");
        final String dataName = split[split.length - 1].replaceAll("\\.csv", "");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(outDir).append("\\");
        stringBuilder.append(dataName);
        new File(stringBuilder.toString()).mkdir();
        return stringBuilder;
    }

    private void writeLine(FileWriter writer, int learnLen, int resultLen, int startPoint) throws IOException {
        final int iLeft = startPoint + learnLen;
        final int iRight = startPoint + learnLen + resultLen;
        for (int i = startPoint; i < iLeft - 1; i++) {
            writer.write(model.rows.get(i).close + " ");
        }
        writer.write(model.rows.get(iLeft - 1).close);
        writer.write(";");
        for (int i = iLeft; i < iRight - 1; i++) {
            writer.write(model.rows.get(i).close + " ");
        }
        writer.write(model.rows.get(iRight - 1).close);
        writer.write("\n");
        if (++nowCreated % 100 == 0) {
            log.info("created : " + nowCreated + " of total : " + totalLines);
        }
    }
}
