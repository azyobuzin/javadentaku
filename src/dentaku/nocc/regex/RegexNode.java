package dentaku.nocc.regex;

public abstract class RegexNode {
    public abstract <T> T accept(RegexNodeVisitor<T> visitor);
}
