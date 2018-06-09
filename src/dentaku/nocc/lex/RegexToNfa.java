package dentaku.nocc.lex;

import dentaku.nocc.lex.nfa.*;
import dentaku.nocc.lex.regex.*;

public final class RegexToNfa {
    private RegexToNfa() { }

    /**
     * 正規表現を NFA に変換
     */
    public static NfaRepr convert(RegexNode regexNode) {
        NfaRepr result = regexNode.accept(new RegexToNfaVisitor());
        result.getFinalState().setIsFinal(true);
        return result;
    }

    private static class RegexToNfaVisitor extends RegexNodeVisitor<NfaRepr> {
        @Override
        public NfaRepr visitNode(RegexNode node) {
            throw new IllegalStateException("到達不可能");
        }

        @Override
        public NfaRepr visitEpsilonNode(EpsilonRegexNode node) {
            NfaState start = new NfaState();
            NfaState end = new NfaState();
            start.addOutgoingEdge(end, null);
            return new NfaRepr(start, end);
        }

        @Override
        public NfaRepr visitCharNode(CharRegexNode node) {
            NfaState start = new NfaState();
            NfaState end = new NfaState();
            start.addOutgoingEdge(end, node.getCharRange());
            return new NfaRepr(start, end);
        }

        @Override
        public NfaRepr visitConcatNode(ConcatRegexNode node) {
            // left と right を NFA に変換
            NfaRepr left = node.getLeft().accept(this);
            NfaRepr right = node.getRight().accept(this);
            // ε で接続
            left.getFinalState().addOutgoingEdge(right.getStartState(), null);
            return new NfaRepr(left.getStartState(), right.getFinalState());
        }

        @Override
        public NfaRepr visitSelectNode(SelectRegexNode node) {
            // left と right を NFA に変換
            NfaRepr left = node.getLeft().accept(this);
            NfaRepr right = node.getRight().accept(this);

            // start から left と right に ε で接続
            NfaState start = new NfaState();
            start.addOutgoingEdge(left.getStartState(), null);
            start.addOutgoingEdge(right.getStartState(), null);

            // left と right から end に ε で接続
            NfaState end = new NfaState();
            left.getFinalState().addOutgoingEdge(end, null);
            right.getFinalState().addOutgoingEdge(end, null);

            return new NfaRepr(start, end);
        }

        @Override
        public NfaRepr visitRepeatNode(RepeatRegexNode node) {
            NfaRepr r = node.getNode().accept(this);
            NfaState repeatState = new NfaState();
            NfaState finalState = new NfaState();
            // repeatState -> r
            repeatState.addOutgoingEdge(r.getStartState(), null);
            // r -> repeatState
            r.getFinalState().addOutgoingEdge(repeatState, null);
            // repeatState -> finalState
            repeatState.addOutgoingEdge(finalState, null);
            return new NfaRepr(repeatState, finalState);
        }
    }
}
