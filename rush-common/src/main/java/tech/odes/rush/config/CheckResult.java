package tech.odes.rush.config;

public class CheckResult {

    private static final CheckResult SUCCESS = new CheckResult(true, "");

    private boolean success;

    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Deprecated
    public CheckResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static CheckResult success() {
        return SUCCESS;
    }

    public static CheckResult error(String msg) {
        return new CheckResult(false, msg);
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                '}';
    }
}
