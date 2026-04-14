package M6FGR.epic_api.exception;

public class ClassLoadingException extends RuntimeException {
    private final String message;
    public ClassLoadingException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
