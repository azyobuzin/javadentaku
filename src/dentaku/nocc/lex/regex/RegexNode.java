package dentaku.nocc.lex.regex;

import dentaku.nocc.lex.CharRange;

/**
 * 正規表現ノードの基底クラス
 */
public abstract class RegexNode {
    public abstract <T> T accept(RegexNodeVisitor<T> visitor);
}
