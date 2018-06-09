package dentaku.nocc.lex;

import dentaku.nocc.lex.nfa.*;
import dentaku.nocc.lex.regex.*;

public final class RegexToNfa {
    private RegexToNfa() { }

    /**
     * 正規表現を NFA に変換
     */
    public static <T> NfaRepr<T> convert(RegexNode regexNode) {
        NfaRepr<T> result = regexNode.accept(new RegexToNfaVisitor<>());
        result.getFinalState().setIsFinal(true);
        return result;
    }

    private static class RegexToNfaVisitor<T> extends RegexNodeVisitor<NfaRepr<T>> {
        @Override
        public NfaRepr<T> visitNode(RegexNode node) {
            throw new IllegalStateException("到達不可能");
        }

        @Override
        public NfaRepr<T> visitEpsilonNode(EpsilonRegexNode node) {
            NfaState<T> start = new NfaState<>();
            NfaState<T> end = new NfaState<>();
            start.addOutgoingEdge(end, null);
            return new NfaRepr<>(start, end);
        }

        @Override
        public NfaRepr<T> visitCharNode(CharRegexNode node) {
            NfaState<T> start = new NfaState<>();
            NfaState<T> end = new NfaState<>();
            start.addOutgoingEdge(end, node.getCharRange());
            return new NfaRepr<>(start, end);
        }

        @Override
        public NfaRepr<T> visitConcatNode(ConcatRegexNode node) {
            // left と right を NFA に変換
            NfaRepr<T> left = node.getLeft().accept(this);
            NfaRepr<T> right = node.getRight().accept(this);
            // ε で接続
            left.getFinalState().addOutgoingEdge(right.getStartState(), null);
            return new NfaRepr<>(left.getStartState(), right.getFinalState());
        }

        @Override
        public NfaRepr<T> visitSelectNode(SelectRegexNode node) {
            // left と right を NFA に変換
            NfaRepr<T> left = node.getLeft().accept(this);
            NfaRepr<T> right = node.getRight().accept(this);

            // start から left と right に ε で接続
            NfaState<T> start = new NfaState<>();
            start.addOutgoingEdge(left.getStartState(), null);
            start.addOutgoingEdge(right.getStartState(), null);

            // left と right から end に ε で接続
            NfaState<T> end = new NfaState<>();
            left.getFinalState().addOutgoingEdge(end, null);
            right.getFinalState().addOutgoingEdge(end, null);

            return new NfaRepr<>(start, end);
        }

        @Override
        public NfaRepr<T> visitRepeatNode(RepeatRegexNode node) {
            NfaRepr<T> r = node.getNode().accept(this);
            NfaState<T> repeatState = new NfaState<>();
            NfaState<T> finalState = new NfaState<>();
            // repeatState -> r
            repeatState.addOutgoingEdge(r.getStartState(), null);
            // r -> repeatState
            r.getFinalState().addOutgoingEdge(repeatState, null);
            // repeatState -> finalState
            repeatState.addOutgoingEdge(finalState, null);
            return new NfaRepr<>(repeatState, finalState);
        }
    }
}
