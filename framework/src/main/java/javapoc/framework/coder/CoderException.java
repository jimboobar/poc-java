package javapoc.framework.coder;

public class CoderException extends RuntimeException {

    public CoderException(String message) {
        super(message);
    }

    public CoderException(Throwable t) {
        super(t);
    }
}
