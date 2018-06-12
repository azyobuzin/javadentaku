package dentaku.ast;

/**
 * 数値を表す
 */
public class NumberNode extends DentakuAstNode {
    private final String m_text;
    private final double m_value;

    public NumberNode(String text) {
        m_text = text;
        m_value = Double.parseDouble(text);
    }

    public String getText() { return m_text; }

    public double getValue() { return m_value; }

    @Override
    public <T> T accept(DentakuAstNodeVisitor<T> visitor) {
        return visitor.visitNumberNode(this);
    }

    @Override
    public String toString() {
        return String.format("Number(%f)", getValue());
    }
}
