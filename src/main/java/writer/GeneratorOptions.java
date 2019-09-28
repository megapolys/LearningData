package writer;

import model.Headers;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class GeneratorOptions {

    @Value.Default
    public int getLearnLength() {
        return 200;
    }

    @Value.Default
    public int getResultLength() {
        return 10;
    }

    public abstract List<Headers.Header> getHeaders();

    @Value.Check
    public void check() {
        if (getHeaders().isEmpty()) {
            throw new RuntimeException("headers must be not empty");
        }
    }

    @Value.Default
    public int getStep() {
        return 1;
    }

    public abstract int getSeparatePoint();

    public String getValueSeparator() {
        return " ";
    }

    public String getLearnResSeparator() {
        return ";";
    }

    public String getRowSeparator() {
        return ",";
    }

    public String getDataSetSeparator() {
        return "\n";
    }




}
