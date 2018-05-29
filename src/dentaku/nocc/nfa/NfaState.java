package dentaku.nocc.nfa;

import java.util.ArrayList;
import java.util.List;

public class NfaState<T> {
    private final List<NfaState> m_outgoingEdges = new ArrayList<>();
    private final boolean m_isFinal;
    private final T m_tag;

    protected NfaState(boolean isFinal, T tag) {
        m_isFinal = isFinal;
        m_tag = tag;
    }

    public static <T> NfaState<T> createState() {
        return new NfaState<>(false, null);
    }

    public static <T> NfaState<T> createFinalState(T tag) {
        return new NfaState<>(true, tag);
    }

    public boolean isFinal() { return m_isFinal; }

    public T getTag() { return m_tag; }

    public void addOutgoingEdge(NfaState<T> to) {
        m_outgoingEdges.add(to);
    }

    public NfaState<T>[] getOutgoingEdges() {
        return (NfaState<T>[]) m_outgoingEdges.toArray();
    }
}
