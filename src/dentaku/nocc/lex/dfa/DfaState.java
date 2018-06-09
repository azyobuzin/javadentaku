package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;
import dentaku.nocc.lex.nfa.NfaState;

import java.util.*;

/**
 * 決定性有限オートマトンの状態
 */
public class DfaState {
    private final List<DfaEdge> m_outgoingEdges = new ArrayList<>();
    private final Set<NfaState> m_includedNfaStates = new HashSet<>();

    public void addOutgoingEdge(DfaState to, CharRange label) {
        m_outgoingEdges.add(new DfaEdge(this, to, label));
    }

    /**
     * 内包している NFA の状態を登録
     */
    public void includeNfaState(NfaState nfaState) {
        m_includedNfaStates.add(nfaState);
    }

    public DfaEdge[] getOutgoingEdges() {
        return m_outgoingEdges.toArray(new DfaEdge[0]);
    }

    public NfaState[] getIncludedNfaStates() {
        return m_includedNfaStates.toArray(new NfaState[0]);
    }
}
