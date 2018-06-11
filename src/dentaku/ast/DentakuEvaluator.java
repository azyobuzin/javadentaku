package dentaku.ast;

public final class DentakuEvaluator {
    private DentakuEvaluator() {}

    /**
     * AST を解析して計算を行う
     */
    public static double evaluate(DentakuAstNode node) {
        return node.accept(new EvaluationVisitor());
    }

    private static final class EvaluationVisitor extends DentakuAstNodeVisitor<Double> {
        @Override
        public Double visitNode(DentakuAstNode node) {
            throw new IllegalStateException("到達不可能");
        }

        @Override
        public Double visitNumberNode(NumberNode node) {
            return node.getValue();
        }

        @Override
        public Double visitBinaryOperatorNode(BinaryOperatorNode node) {
            double left = node.getLeft().accept(this);
            double right = node.getRight().accept(this);
            return node.getOperator().apply(left, right);
        }

        @Override
        public Double visitNegativeOperatorNode(NegativeOperatorNode node) {
            return -node.getNode().accept(this);
        }
    }
}
