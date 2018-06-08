package dentaku.nocc.lex.regex;

/**
 * 正規表現同士の連結を表す正規表現ノード
 */
public class ConcatRegexNode extends RegexNode {
    private final RegexNode m_left;
    private final RegexNode m_right;

    public ConcatRegexNode(RegexNode left, RegexNode right) {
        if (left == null) throw new IllegalArgumentException("left が null");
        if (right == null) throw new IllegalArgumentException("right が null");

        m_left = left;
        m_right = right;
    }

    public RegexNode getLeft() {
        return m_left;
    }

    public RegexNode getRight() {
        return m_right;
    }

    @Override
    public <T> T accept(RegexNodeVisitor<T> visitor) {
        return visitor.visitConcatNode(this);
    }

    @Override
    public String toString() {
        return m_left.toString() + m_right.toString();
    }
}
