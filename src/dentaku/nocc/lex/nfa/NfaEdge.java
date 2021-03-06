package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;

import java.util.Objects;

/**
 * 非決定性有限オートマトンの辺
 */
public class NfaEdge {
    private final NfaState m_from;
    private final NfaState m_to;
    private final CharRange m_label;

    public NfaEdge(NfaState from, NfaState to, CharRange label) {
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

    @Override
    public String toString() {
        String label = isEpsilon() ? "ε" : getLabel().toString();
        return String.format("%s -%s-> %s", getFrom(), label, getTo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NfaEdge nfaEdge = (NfaEdge) o;
        return Objects.equals(m_from, nfaEdge.m_from) &&
            Objects.equals(m_to, nfaEdge.m_to) &&
            Objects.equals(m_label, nfaEdge.m_label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_from, m_to, m_label);
    }
}
