package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.nfa.NfaState;

import java.util.*;

final class NfaStateSet {
    private final NfaState[] m_states;

    public NfaStateSet(NfaState[] states) {
        m_states = states;
    }

    public NfaStateSet(List<NfaState> states) {
        m_states = states.toArray(new NfaState[0]);
    }

    public NfaStateSet(Set<NfaState> states) {
        m_states = states.toArray(new NfaState[0]);
    }

    public NfaState[] getStates() { return m_states; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof NfaStateSet)) return false;
        NfaStateSet stateSet = (NfaStateSet) o;
        return Arrays.equals(m_states, stateSet.m_states);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(m_states);
    }
}
