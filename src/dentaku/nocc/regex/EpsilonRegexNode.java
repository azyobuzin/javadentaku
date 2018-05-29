package dentaku.nocc.regex;

public class EpsilonRegexNode extends RegexNode {
    @Override
    public <T> T accept(RegexNodeVisitor<T> visitor) {
        return visitor.visitEpsilonNode(this);
    }

    @Override
    public String toString() {
        return "ε";
    }
}
