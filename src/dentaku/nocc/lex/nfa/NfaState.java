package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;

import java.util.ArrayList;
import java.util.List;

/**
 * 非決定性有限オートマトンの状態
 *
 * @param <T> 受理状態に紐づくデータの型
 */
public class NfaState<T> {
    private final List<NfaEdge<T>> m_outgoingEdges = new ArrayList<>();
    private boolean m_isFinal;
    private T m_tag;

    public boolean isFinal() { return m_isFinal; }

    public void setIsFinal(boolean isFinal) { m_isFinal = isFinal; }

    public T getTag() { return m_tag; }

    public void setTag(T value) { m_tag = value; }

    public void addOutgoingEdge(NfaState<T> to, CharRange label) {
        m_outgoingEdges.add(new NfaEdge<>(this, to, label));
    }

    public NfaEdge<T>[] getOutgoingEdges() {
        return (NfaEdge<T>[]) m_outgoingEdges.toArray(new NfaEdge[m_outgoingEdges.size()]);
    }
}
