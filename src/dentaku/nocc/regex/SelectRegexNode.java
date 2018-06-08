package dentaku.nocc.regex;

/**
 * 2つの正規表現のうちのどちらかを選択する正規表現ノード
 */
public class SelectRegexNode extends RegexNode {
    private final RegexNode m_left;
    private final RegexNode m_right;

    public SelectRegexNode(RegexNode left, RegexNode right) {
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
        return visitor.visitSelectNode(this);
    }

    @Override
    public String toString() {
        return "(" + m_left + "|" + m_right + ")";
    }
}
