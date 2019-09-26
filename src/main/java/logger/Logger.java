package logger;

public class Logger {

    private Class clazz;

    public Logger(Class clazz) {
        this.clazz = clazz;
    }

    public void log(String msg) {
        System.out.println(msg);
    }

    public void logErr(String msg, Exception e) {
        System.out.println(msg);
        e.printStackTrace();
    }
}
