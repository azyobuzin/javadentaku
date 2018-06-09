package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;

/**
 * 非決定性有限オートマトンの辺
 */
public class NfaEdge {
    private final NfaState m_from;
    private final NfaState m_to;
    private final CharRange m_label;

    protected NfaEdge(NfaState from, NfaState to, CharRange label) {
        if (from == null) throw new IllegalArgumentException("from が null");
        if (to == null) throw new IllegalArgumentException("to が null");

        m_from = from;
        m_to = to;
        m_label = label;
    }

    public NfaState getFrom() { return m_from; }

    public NfaState getTo() { return m_to; }

    public boolean isEpsilon() { return m_label == null; }

    public CharRange getLabel() { return m_label; }
}
