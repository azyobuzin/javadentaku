package dentaku.nocc.lex.regex;

import dentaku.nocc.lex.CharRange;

/**
 * 1文字を受理する正規表現ノード
 */
public class CharRegexNode extends RegexNode {
    private final CharRange m_charRange;

    public CharRegexNode(CharRange charRange) {
        if (charRange == null) throw new IllegalArgumentException("charRange が null");
        m_charRange = charRange;
    }

    public CharRange getCharRange() {
        return m_charRange;
    }

    @Override
    public <T> T accept(RegexNodeVisitor<T> visitor) {
        return visitor.visitCharNode(this);
    }

    @Override
    public String toString() {
        return m_charRange.toString();
    }
}
