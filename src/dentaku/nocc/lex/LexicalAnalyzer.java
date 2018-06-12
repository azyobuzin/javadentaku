package dentaku.nocc.lex;

import dentaku.nocc.lex.dfa.*;
import dentaku.nocc.lex.nfa.*;
import dentaku.nocc.lex.regex.RegexNode;
import dentaku.util.ImmutablePair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 字句解析器
 *
 * @param <T> トークン型
 */
public class LexicalAnalyzer<T> {
    private final DfaState m_startState;
    private final Map<NfaState, FinalStateInfo<T>> m_finalStates;
    private final BufferedCharReader m_reader;
    private boolean m_readyToPeek; // 読み取り済みかどうか
    private T m_currentToken;

    protected LexicalAnalyzer(DfaState startState, Map<NfaState, FinalStateInfo<T>> finalStates, CharReader reader) {
        m_startState = startState;
        m_finalStates = finalStates;
        m_reader = new BufferedCharReader(reader);
    }

    /**
     * 次のトークンを返す
     *
     * @throws LexicalException 状態遷移に失敗
     */
    public T peek() throws LexicalException, IOException {
        // すでに読み取ってあるものがあるなら、それを返す
        if (m_readyToPeek) return m_currentToken;

        // 現在の状態
        DfaState currentState = m_startState;
        // 最後に訪れた受理状態
        DfaState lastFinalState = currentState.isFinal() ? currentState : null;

        int nextChar;
        while (true) {
            nextChar = m_reader.read();

            // EOF の処理はしないということで
            if (nextChar < 0) break;

            final int c = nextChar; // ラムダ式用に final 化
            Optional<DfaEdge> opEdge = currentState.getOutgoingEdges().stream()
                .filter(edge -> {
                    CharRange label = edge.getLabel();
                    // nextChar が CharRange の範囲内かどうか
                    return c >= label.getStart() && c <= label.getEnd();
                })
                .findAny();

            if (opEdge.isPresent()) {
                // 遷移先発見
                currentState = opEdge.get().getTo();

                if (currentState.isFinal()) {
                    // 受理状態なら記憶しておく
                    lastFinalState = currentState;
                    m_reader.setCheckpoint();

                    // これ以上遷移できないなら、ここで終わりなことが確定
                    if (currentState.getOutgoingEdges().size() == 0)
                        break;
                }
            } else {
                // 遷移先が見つからなくなったら読み取り終わり
                break;
            }
        }

        if (lastFinalState == null) {
            // 1回も受理状態を通らなかったので読み取り失敗
            ThrowLexicalException(currentState, nextChar);
        }

        // priority の値が一番小さい（一番優先される） mapper を取得
        TokenMapper<T> mapper = lastFinalState.getIncludedNfaStates().stream()
            .map(m_finalStates::get)
            .filter(Objects::nonNull)
            .min(Comparator.comparingInt(info -> info.priority))
            .get().mapper;

        // 最後の受理状態までに読み取った文字列を mapper に渡す
        m_currentToken = mapper.map(m_reader.takeString());
        m_readyToPeek = true;

        return m_currentToken;
    }

    private static void ThrowLexicalException(DfaState currentState, int inputChar) throws LexicalException {
        throw LexicalException.create(
            currentState.getOutgoingEdges().stream()
                .map(DfaEdge::getLabel)
                .sorted(Comparator.comparingInt(CharRange::getStart)),
            inputChar
        );
    }

    /**
     * 次のトークンを読み取り、位置を進める
     */
    public T eat() throws LexicalException, IOException {
        T result = peek();
        m_readyToPeek = false;
        return result;
    }

    protected static class FinalStateInfo<T> {
        /**
         * 優先度。値が小さいほど優先される
         */
        public final int priority;

        /**
         * 受理された文字列からトークン型に変換する関数
         */
        public final TokenMapper<T> mapper;

        public FinalStateInfo(int priority, TokenMapper<T> mapper) {
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
        private int m_readPosition;
        private int m_checkpoint;

        public BufferedCharReader(CharReader innerReader) {
            m_reader = innerReader;
        }

        /**
         * 1 文字読み取る
         */
        public int read() throws IOException {
            // バッファーにまだ読み取っていない文字があるときは、それを返す
            if (m_readPosition < m_buffer.length())
                return m_buffer.charAt(m_readPosition++);

            // m_reader から読みに行く
            int value = m_reader.read();
            if (value >= 0) {
                m_buffer.append((char) value);
                m_readPosition++;
            }
            return value;
        }

        /**
         * 「ここまで正常に読み取れた」という印を設定しておく
         */
        public void setCheckpoint() {
            m_checkpoint = m_buffer.length();
        }

        /**
         * バッファーの最初からチェックポイントまでを取り出し、チェックポイントより前のバッファーを削除する。
         * さらに、読み取り位置を 0 に戻す。
         */
        public String takeString() {
            String result = m_buffer.substring(0, m_checkpoint);
            m_buffer.delete(0, m_checkpoint);
            m_readPosition = 0;
            m_checkpoint = 0;
            return result;
        }
    }

    public static class Builder<T> {
        private final List<ImmutablePair<NfaRepr, FinalStateInfo<T>>> m_tokens = new ArrayList<>();
        private int m_priority;

        /**
         * トークンを登録する。同じ受理状態になる場合、最初に登録されたトークンが優先される。
         *
         * @param regexNode   正規表現
         * @param tokenMapper 受理された文字列からトークン型に変換する関数
         */
        public Builder<T> addToken(RegexNode regexNode, TokenMapper<T> tokenMapper) {
            NfaRepr nfa = RegexToNfa.convert(regexNode);
            m_tokens.add(new ImmutablePair<>(nfa, new FinalStateInfo<>(m_priority++, tokenMapper)));
            return this;
        }

        public NfaState buildNfa() {
            NfaState startState = new NfaState();

            // 開始状態からεで各 NFA に接続
            for (ImmutablePair<NfaRepr, FinalStateInfo<T>> pair : m_tokens)
                startState.addOutgoingEpsilonEdge(pair.item1.getStartState());

            return startState;
        }

        public LexicalAnalyzer<T> build(CharReader reader) {
            NfaState nfa = buildNfa();
            DfaState dfa = DfaSimplifier.simplify(NfaToDfa.convert(nfa));

            Map<NfaState, FinalStateInfo<T>> finalStates = m_tokens.stream()
                .collect(Collectors.toMap(
                    pair -> pair.item1.getFinalState(),
                    pair -> pair.item2
                ));

            return new LexicalAnalyzer<>(dfa, finalStates, reader);
        }
    }
}
