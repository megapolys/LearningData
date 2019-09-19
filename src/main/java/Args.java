public enum Args {

    LEARN_LEN("-l", "100"),
    RESULT_LEN("-r", "50"),
    STEP("-s", "10"),
    INPUT_CSV("-fcsv"),
    INPUT_DIR("-dcsv"),
    OUTPUT_DIRECTORY("-out");

    private String name;
    private String defaultValue;

    Args(String name) {
        this.name = name;
    }

    Args(String name, String defaultValueult) {
        this.name = name;
        this.defaultValue = defaultValueult;
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defaultValue;
    }

    public String getValue() {
        final String getenv = System.getenv(getName());
        return getenv == null ? getDefault() : getenv;
    }
}
