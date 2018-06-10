package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.nfa.NfaState;

import java.util.*;

/**
 * 辺の中間表現（NFA 状態の集合同士を結ぶ）
 */
final class IntermediateEdge {
    private final Set<NfaState> m_to;
    private final char m_label;

    public IntermediateEdge(Set<NfaState> to, char label) {
        if (to == null) throw new IllegalArgumentException("to が null");

        m_to = to;
        m_label = label;
    }

    public Set<NfaState> getTo() { return m_to; }

    public char getLabel() { return m_label; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntermediateEdge)) return false;
        IntermediateEdge that = (IntermediateEdge) o;
        return m_label == that.m_label &&
            Objects.equals(m_to, that.m_to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_to, m_label);
    }
}
