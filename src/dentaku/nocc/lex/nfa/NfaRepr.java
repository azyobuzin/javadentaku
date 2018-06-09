package dentaku.nocc.lex.nfa;

/**
 * 開始状態と受理状態のセット
 */
public class NfaRepr {
    private final NfaState m_startState;
    private final NfaState m_finalState;

    public NfaRepr(NfaState startState, NfaState finalState) {
        if (startState == null) throw new IllegalStateException("startState が null");
        if (finalState == null) throw new IllegalStateException("finalState が null");

        m_startState = startState;
        m_finalState = finalState;
    }

    public NfaState getStartState() { return m_startState; }

    public NfaState getFinalState() { return m_finalState; }
}
