package dentaku.ast;

/**
 * 2項演算子を表す
 */
public class BinaryOperatorNode extends DentakuAstNode {
    private final DentakuAstNode m_left;
    private final DentakuAstNode m_right;
    private final BinaryOperator m_operator;

    public BinaryOperatorNode(DentakuAstNode left, DentakuAstNode right, BinaryOperator operator) {
        m_left = left;
        m_right = right;
        m_operator = operator;
    }

    public DentakuAstNode getLeft() { return m_left; }

    public DentakuAstNode getRight() { return m_right; }

    public BinaryOperator getOperator() { return m_operator; }

    @Override
    public <T> T accept(DentakuAstNodeVisitor<T> visitor) {
        return visitor.visitBinaryOperatorNode(this);
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s)", getOperator(), getLeft(), getRight());
    }
}
