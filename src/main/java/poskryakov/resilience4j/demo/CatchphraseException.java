package poskryakov.resilience4j.demo;

public class CatchphraseException extends RuntimeException {

    public CatchphraseException(String message) {
        super(message);
    }
}
