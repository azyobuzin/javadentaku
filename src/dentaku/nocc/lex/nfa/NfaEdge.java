package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;

/**
 * 非決定性有限オートマトンの辺
 * @param <T>
 */
public class NfaEdge<T> {
    private final NfaState<T> m_from;
    private final NfaState<T> m_to;
    private final CharRange m_label;

    protected NfaEdge(NfaState<T> from, NfaState<T> to, CharRange label) {
        if (from == null) throw new IllegalArgumentException("from が null");
        if (to == null) throw new IllegalArgumentException("to が null");

        m_from = from;
        m_to = to;
        m_label = label;
    }

    public static <T> NfaEdge<T> create(NfaState<T> from, NfaState<T> to, CharRange label) {
        return new NfaEdge<>(from, to, label);
    }

    public static <T> NfaEdge<T> createEpsilon(NfaState<T> from, NfaState<T> to) {
        return new NfaEdge<>(from, to, null);
    }

    public boolean isEpsilon() { return m_label == null; }

    public CharRange getLabel() { return m_label; }
}