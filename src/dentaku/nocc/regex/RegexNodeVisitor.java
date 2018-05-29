package dentaku.nocc.regex;

public abstract class RegexNodeVisitor<T> {
    public T visitNode(RegexNode node) {
        return null;
    }

    public T visitEpsilonNode(EpsilonRegexNode node) {
        return visitNode(node);
    }

    public T visitCharNode(CharRegexNode node) {
        return visitNode(node);
    }

    public T visitConcatNode(ConcatRegexNode node) {
        return visitNode(node);
    }

    public T visitSelectNode(SelectRegexNode node) {
        return visitNode(node);
    }

    public T visitRepeatNode(RepeatRegexNode node) { return visitNode(node); }
}
