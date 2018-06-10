package dentaku.nocc.lex;

import java.io.IOException;
import java.io.Reader;

/**
 * {@link Reader} を {@link CharReader} としてラップ
 */
public class StreamCharReader implements CharReader {
    private final Reader m_reader;

    public StreamCharReader(Reader reader) {
        m_reader = reader;
    }

    @Override
    public int read() throws IOException {
        return m_reader.read();
    }
}
