import model.Headers;
import model.Model;
import view.SimpleInFileView;
import view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Generator {
    private final static Logger log = Logger.getLogger(Generator.class.getName());

    private final String csvFile;
    private final String outDir;

    private Model model;
    private View view;

    private int totalFiles;
    private AtomicInteger nowCreated = new AtomicInteger();

    public static void main(String[] args) throws IOException {
        String csvPath = "src\\main\\resources\\GAZR.csv";
//        String outDir = "E:\\Daniil\\YandexDisk\\Папка успеха\\learning_data";
        String outDir = "C:\\Users\\dlosev.NBKI\\IdeaProjects\\LearningData\\src\\main\\resources\\out";
        final Model model = new Model();
        final View view = new SimpleInFileView();
        Generator generator = new Generator(csvPath, outDir, model, view);
        generator.init();
        generator.generate(200, 10, 1);

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

    public void generate(int learnLen, int resultLen, int step) throws IOException {

        final int allLen = learnLen + resultLen;
        totalFiles += (model.rows.size() - allLen) / step;

        final String[] split = csvFile.split("[/\\\\]");
        final String dataName = split[split.length - 1].replaceAll("\\.csv", "");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(outDir).append("\\");
        stringBuilder.append(dataName).append('_');
        stringBuilder.append(learnLen).append('%').append(resultLen).append('_');

        new File(stringBuilder.toString()).mkdir();

        for (int i = 0; (i + allLen) < model.rows.size(); i += step) {
            writeFile(stringBuilder.toString() + "\\" + nowCreated.get() + ".txt", learnLen, resultLen, i);
        }

    }

    private void writeFile(String outPath, int learnLen, int resultLen, int startPoint) throws IOException {

        final File outFile = new File(outPath);
        outFile.createNewFile();
        try (final FileWriter writer = new FileWriter(outPath)) {
            final int iLeft = startPoint + learnLen;
            final int iRight = startPoint + learnLen + resultLen;
            for (int i = startPoint; i < iLeft - 1; i++) {
                writer.write(model.rows.get(i).get(Headers.Header.CLOSE) + " ");
            }
            writer.write(model.rows.get(iLeft - 1).get(Headers.Header.CLOSE));
            writer.write(";");
            for (int i = iLeft; i < iRight - 1; i++) {
                writer.write(model.rows.get(i).get(Headers.Header.CLOSE) + " ");
            }
            writer.write(model.rows.get(iRight - 1).get(Headers.Header.CLOSE));
        } catch (Exception e) {
            throw new RuntimeException("file writing error", e);
        } finally {
            log.info("created : " + nowCreated.incrementAndGet() + " from total : " + totalFiles);
        }
    }
}
