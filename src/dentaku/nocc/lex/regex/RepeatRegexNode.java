package dentaku.nocc.lex.regex;

/**
 * 正規表現の0回以上の繰り返しを表す正規表現ノード
 */
public class RepeatRegexNode extends RegexNode {
    private final RegexNode m_node;

    public RepeatRegexNode(RegexNode node) {
        m_node = node;
    }

    public RegexNode getNode() {
        return m_node;
    }

    @Override
    public <T> T accept(RegexNodeVisitor<T> visitor) {
        return visitor.visitRepeatNode(this);
    }

    @Override
    public String toString() {
        return "(" + m_node + ")*";
    }
}
