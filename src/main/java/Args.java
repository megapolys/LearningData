public enum Args {

    LEARN_LEN("-learn_len"),
    RESULT_LEN("-result_len");

    String name;

    Args(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
