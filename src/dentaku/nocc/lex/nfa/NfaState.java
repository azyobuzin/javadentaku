package dentaku.nocc.lex.nfa;

import java.util.ArrayList;
import java.util.List;

/**
 * 非決定性有限オートマトンの状態
 *
 * @param <T> 受理状態に紐づくデータの型
 */
public class NfaState<T> {
    private final List<NfaState> m_outgoingEdges = new ArrayList<>();
    private T m_tag;

    public T getTag() { return m_tag; }

    public void setTag(T value) { m_tag = value; }

    public void addOutgoingEdge(NfaState<T> to) {
        m_outgoingEdges.add(to);
    }

    public NfaState<T>[] getOutgoingEdges() {
        return (NfaState<T>[]) m_outgoingEdges.toArray();
    }
}
