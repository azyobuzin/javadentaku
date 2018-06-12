package dentaku.ast;

import dentaku.util.DotUtil;

import java.io.PrintWriter;
import java.io.Writer;

public final class DentakuAstPrinter {
    private DentakuAstPrinter() {}

    public static void writeDotTo(DentakuAstNode node, Writer output) {
        PrintWriter pw = new PrintWriter(output);
        pw.println("digraph {");
        pw.println("    edge [dir=none]");
        node.accept(new PrintVisitor(pw));
        pw.println("}");
    }

    private static final class PrintVisitor extends DentakuAstNodeVisitor<String> {
        private final PrintWriter m_writer;
        private int m_nodeIndex;

        public PrintVisitor(PrintWriter writer) {
            m_writer = writer;
        }

        @Override
        public String visitNode(DentakuAstNode node) {
            throw new IllegalStateException("到達不可能");
        }

        @Override
        public String visitNumberNode(NumberNode node) {
            return createNode(node.getText());
        }

        @Override
        public String visitBinaryOperatorNode(BinaryOperatorNode node) {
            String opId = createNode(node.getOperator().getOperatorSign());
            String leftId = node.getLeft().accept(this);
            String rightId = node.getRight().accept(this);

            m_writer.format("    %s -> {%s; %s}", opId, leftId, rightId);
            m_writer.println();

            return opId;
        }

        @Override
        public String visitNegativeOperatorNode(NegativeOperatorNode node) {
            String opId = createNode("-");
            String operandId = node.getNode().accept(this);

            m_writer.format("    %s -> %s", opId, operandId);
            m_writer.println();

            return opId;
        }

        /**
         * ラベルを書き出し、 dot 上での ID を返す
         */
        private String createNode(String label) {
            String id = String.format("N_%d", m_nodeIndex++);
            m_writer.format("    %s [label=%s]", id, DotUtil.toDotString(label));
            m_writer.println();
            return id;
        }
    }
}
