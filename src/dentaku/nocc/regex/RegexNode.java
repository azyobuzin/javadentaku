package dentaku.nocc.regex;

/**
 * 正規表現ノードの基底クラス
 */
public abstract class RegexNode {
    public abstract <T> T accept(RegexNodeVisitor<T> visitor);
}
