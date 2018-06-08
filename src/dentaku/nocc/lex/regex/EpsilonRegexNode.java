package dentaku.nocc.lex.regex;

/**
 * 何も読み取らない正規表現ノード
 */
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
