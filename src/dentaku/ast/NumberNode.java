package dentaku.ast;

/**
 * 数値を表す
 */
public class NumberNode extends DentakuAstNode {
    private final double m_value;

    public NumberNode(double value) {
        m_value = value;
    }

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
