package dentaku.nocc.nfa;

public class NfaEdge<T> {
    private final NfaState<T> m_from;
    private final NfaState<T> m_to;
    private final char[] m_labels;

    protected NfaEdge(NfaState<T> from, NfaState<T> to, char[] labels) {
        if (from == null) throw new IllegalArgumentException("from が null です。");
        if (to == null) throw new IllegalArgumentException("to が null です。");
        if (labels != null && labels.length == 0)
            throw new IllegalArgumentException("ラベルがありません。");

        m_from = from;
        m_to = to;
        m_labels = labels;
    }

    public static <T> NfaEdge<T> create(NfaState<T> from, NfaState<T> to, char... labels) {
        return new NfaEdge<>(from, to, labels != null && labels.length == 0 ? null : labels);
    }

    public static <T> NfaEdge<T> createEpsilon(NfaState<T> from, NfaState<T> to) {
        return new NfaEdge<>(from, to, null);
    }

    public boolean isEpsilon() { return m_labels == null; }

    public char[] getLabels() { return m_labels; }

    // TODO: ラベルの範囲とかうまくできるやつ作りたい
}
