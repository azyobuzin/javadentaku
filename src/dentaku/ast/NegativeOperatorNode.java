package dentaku.ast;

/**
 * マイナス単項演算子を表す
 */
public class NegativeOperatorNode extends DentakuAstNode {
    private final DentakuAstNode m_node;

    public NegativeOperatorNode(DentakuAstNode node) {
        m_node = node;
    }

    public DentakuAstNode getNode() { return m_node; }

    @Override
    public <T> T accept(DentakuAstNodeVisitor<T> visitor) {
        return visitor.visitNegativeOperatorNode(this);
    }

    @Override
    public String toString() {
        return String.format("Negative(%s)", getNode());
    }
}
