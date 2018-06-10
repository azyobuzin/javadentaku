package dentaku.nocc.lex;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LexicalException extends Exception {
    public LexicalException(String message) {
        super(message);
    }

    public LexicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public static LexicalException create(Stream<CharRange> acceptableChars, int actualInput) {
        String message = String.format(
            "入力: %s, 受理できる文字: %s",
            actualInput >= 0 ? CharRange.escapeChar((char) actualInput) : "EOF",
            acceptableChars.map(CharRange::toString).collect(Collectors.joining(","))
        );
        return new LexicalException(message);
    }
}
