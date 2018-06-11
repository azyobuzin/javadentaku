package dentaku.ast;

/**
 * {@link DentakuAstNode} の visitor パターン実装
 */
public abstract class DentakuAstNodeVisitor<T> {
    public T visitNode(DentakuAstNode node) { return null; }

    public T visitNumberNode(NumberNode node) { return visitNode(node); }

    public T visitBinaryOperatorNode(BinaryOperatorNode node) { return visitNode(node); }

    public T visitNegativeOperatorNode(NegativeOperatorNode node) { return visitNode(node); }
}
