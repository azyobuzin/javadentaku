package dentaku.nocc.lex.nfa;

/**
 * 開始状態と受理状態のセット
 */
public class NfaRepr<T> {
    private final NfaState<T> m_startState;
    private final NfaState<T> m_finalState;

    public NfaRepr(NfaState<T> startState, NfaState<T> finalState) {
        if (startState == null) throw new IllegalStateException("startState が null");
        if (finalState == null) throw new IllegalStateException("finalState が null");

        m_startState = startState;
        m_finalState = finalState;
    }

    public NfaState<T> getStartState() { return m_startState; }

    public NfaState<T> getFinalState() { return m_finalState; }
}
