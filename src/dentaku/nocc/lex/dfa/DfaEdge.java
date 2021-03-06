package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;

import java.util.Objects;

/**
 * 決定性有限オートマトンの辺
 */
public class DfaEdge {
    private final DfaState m_from;
    private final DfaState m_to;
    private final CharRange m_label;

    public DfaEdge(DfaState from, DfaState to, CharRange label) {
        if (from == null) throw new IllegalArgumentException("from が null");
        if (to == null) throw new IllegalArgumentException("to が null");
        if (label == null) throw new IllegalArgumentException("label が null");

        m_from = from;
        m_to = to;
        m_label = label;
    }

    public DfaState getFrom() { return m_from; }

    public DfaState getTo() { return m_to; }

    public CharRange getLabel() { return m_label; }

    @Override
    public String toString() {
        return String.format("%s -%s-> %s", getFrom(), getLabel(), getTo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DfaEdge dfaEdge = (DfaEdge) o;
        return Objects.equals(m_from, dfaEdge.m_from) &&
            Objects.equals(m_to, dfaEdge.m_to) &&
            Objects.equals(m_label, dfaEdge.m_label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_from, m_to, m_label);
    }
}
