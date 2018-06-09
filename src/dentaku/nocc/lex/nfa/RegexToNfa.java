package dentaku.nocc.lex.nfa;

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
            start.addOutgoingEpsilonEdge(end);
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
            left.getFinalState().addOutgoingEpsilonEdge(right.getStartState());
            return new NfaRepr(left.getStartState(), right.getFinalState());
        }

        @Override
        public NfaRepr visitSelectNode(SelectRegexNode node) {
            // left と right を NFA に変換
            NfaRepr left = node.getLeft().accept(this);
            NfaRepr right = node.getRight().accept(this);

            // start から left と right に ε で接続
            NfaState start = new NfaState();
            start.addOutgoingEpsilonEdge(left.getStartState());
            start.addOutgoingEpsilonEdge(right.getStartState());

            // left と right から end に ε で接続
            NfaState end = new NfaState();
            left.getFinalState().addOutgoingEpsilonEdge(end);
            right.getFinalState().addOutgoingEpsilonEdge(end);

            return new NfaRepr(start, end);
        }

        @Override
        public NfaRepr visitRepeatNode(RepeatRegexNode node) {
            NfaRepr r = node.getNode().accept(this);
            NfaState repeatState = new NfaState();
            NfaState finalState = new NfaState();
            // repeatState -> r
            repeatState.addOutgoingEpsilonEdge(r.getStartState());
            // r -> repeatState
            r.getFinalState().addOutgoingEpsilonEdge(repeatState);
            // repeatState -> finalState
            repeatState.addOutgoingEpsilonEdge(finalState);
            return new NfaRepr(repeatState, finalState);
        }
    }
}
