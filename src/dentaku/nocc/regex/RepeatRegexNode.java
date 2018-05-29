package dentaku.nocc.regex;

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
