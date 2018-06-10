package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;

import java.util.*;

/**
 * 非決定性有限オートマトンの状態
 */
public class NfaState {
    private final Set<NfaEdge> m_outgoingEdges = new HashSet<>();
    private boolean m_isFinal;

    public boolean isFinal() { return m_isFinal; }

    public void setIsFinal(boolean isFinal) { m_isFinal = isFinal; }

    public void addOutgoingEdge(NfaState to, CharRange label) {
        m_outgoingEdges.add(new NfaEdge(this, to, label));
    }

    public void addOutgoingEpsilonEdge(NfaState to) {
        addOutgoingEdge(to, null);
    }

    public Set<NfaEdge> getOutgoingEdges() {
        return Collections.unmodifiableSet(m_outgoingEdges);
    }
}
