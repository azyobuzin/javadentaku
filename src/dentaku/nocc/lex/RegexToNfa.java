package dentaku.nocc.lex;

import dentaku.nocc.lex.nfa.*;
import dentaku.nocc.lex.regex.*;

public class RegexToNfa {
    // TODO

    private static class RegexToNfaVisitor<T> extends RegexNodeVisitor<NfaRepr<T>> {
        private final T m_tag;

        public RegexToNfaVisitor(T tag) {
            m_tag = tag;
        }

        @Override
        public NfaRepr<T> visitNode(RegexNode node) {
            throw new IllegalStateException("到達不可能");
        }

        @Override
        public NfaRepr<T> visitEpsilonNode(EpsilonRegexNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NfaRepr<T> visitCharNode(CharRegexNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NfaRepr<T> visitConcatNode(ConcatRegexNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NfaRepr<T> visitSelectNode(SelectRegexNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NfaRepr<T> visitRepeatNode(RepeatRegexNode node) {
            throw new UnsupportedOperationException();
        }
    }
}
