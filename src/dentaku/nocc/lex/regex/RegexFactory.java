package dentaku.nocc.lex.regex;

import dentaku.nocc.lex.CharRange;

/**
 * {@link RegexNode} を書きやすくするためのヘルパークラス
 */
public final class RegexFactory {
    private RegexFactory() { }

    /**
     * 1 文字にマッチ
     */
    public static RegexNode charcter(char c) {
        return new CharRegexNode(new CharRange(c, c));
    }

    /**
     * {@code start} から {@code end} までのどれか 1 文字にマッチ
     */
    public static RegexNode charRange(char start, char end) {
        return new CharRegexNode(new CharRange(start, end));
    }

    /**
     * すべてを連結
     */
    public static RegexNode sequence(RegexNode... nodes) {
        if (nodes == null || nodes.length == 0)
            throw new IllegalArgumentException("nodes が 1 件もありません。");

        RegexNode node = nodes[0];
        for (int i = 1; i < nodes.length; i++)
            node = new ConcatRegexNode(node, nodes[i]);
        return node;
    }

    /**
     * {@code nodes} のいずれかにマッチ
     */
    public static RegexNode choice(RegexNode... nodes) {
        if (nodes == null || nodes.length == 0)
            throw new IllegalArgumentException("nodes が 1 件もありません。");

        RegexNode node = nodes[0];
        for (int i = 1; i < nodes.length; i++)
            node = new SelectRegexNode(node, nodes[i]);
        return node;
    }

    /**
     * 0 回以上の繰り返し
     */
    public static RegexNode repeat0(RegexNode node) {
        return new RepeatRegexNode(node);
    }

    /**
     * 1 回以上の繰り返し
     */
    public static RegexNode repeat1(RegexNode node) {
        return new ConcatRegexNode(
            node,
            new RepeatRegexNode(node)
        );
    }

    /**
     * {@code node} が出現してもしなくても良い
     */
    public static RegexNode maybe(RegexNode node) {
        return new SelectRegexNode(
            node,
            new EpsilonRegexNode()
        );
    }
}
