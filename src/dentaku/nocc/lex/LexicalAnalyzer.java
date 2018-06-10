package dentaku.nocc.lex;

import dentaku.nocc.lex.dfa.DfaState;
import dentaku.nocc.lex.nfa.NfaState;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * 字句解析器
 * @param <T> トークン型
 */
public class LexicalAnalyzer<T> {
    private final DfaState m_startState;
    private final Map<NfaState, FinalStateInfo<T>> m_finalStates;
    private final BufferedCharReader m_reader;
    private DfaState m_currentState;
    private DfaState m_lastFinalState;

    protected LexicalAnalyzer(DfaState startState, Map<NfaState, FinalStateInfo<T>> finalStates, CharReader reader) {
        m_startState = startState;
        m_finalStates = finalStates;
        m_reader = new BufferedCharReader(reader);
    }

    // TODO

    protected static class FinalStateInfo<T> {
        /**
         * 優先度。値が小さいほど優先される
         */
        public final int priority;

        /**
         * 受理された文字列からトークン型に変換する関数
         */
        public final Function<String, T> mapper;

        public FinalStateInfo(int priority, Function<String, T> mapper) {
            this.priority = priority;
            this.mapper = mapper;
        }
    }

    /**
     * 字句解析器用のリーダー
     */
    private static final class BufferedCharReader {
        private final CharReader m_reader;
        private final StringBuilder m_buffer = new StringBuilder();
        private int m_checkpoint;

        public BufferedCharReader(CharReader innerReader) {
            m_reader = innerReader;
        }

        /**
         * 1 文字読み取る
         */
        public int read() throws IOException {
            int value = m_reader.read();
            if (value >= 0)
                m_buffer.append((char)value);
            return value;
        }

        /**
         * 「ここまで正常に読み取れた」という印を設定しておく
         */
        public void setCheckpoint() {
            m_checkpoint = m_buffer.length();
        }

        /**
         * バッファーの最初からチェックポイントまでを取り出し、バッファーを削除する
         */
        public String takeString() {
            String result = m_buffer.substring(0, m_checkpoint);
            m_buffer.delete(0, m_checkpoint);
            return result;
        }
    }

    public static class Builder {
        // TODO
    }
}
