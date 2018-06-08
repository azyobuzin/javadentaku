package dentaku.nocc.regex;

/**
 * 1文字を受理する正規表現ノード
 */
public class CharRegexNode extends RegexNode {
    private final char[] m_chars;

    public CharRegexNode(char... chars) {
        if (chars == null || chars.length == 0)
            throw new IllegalArgumentException("1文字以上指定してください。");

        m_chars = chars;
    }

    public char[] getChars() {
        return m_chars;
    }

    @Override
    public <T> T accept(RegexNodeVisitor<T> visitor) {
        return visitor.visitCharNode(this);
    }

    @Override
    public String toString() {
        if (m_chars.length == 1) {
            // 1文字ならそれを表示
            return Character.toString(m_chars[0]);
        } else {
            // 複数の文字から選択するなら [abc] の形式で
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (char c : m_chars)
                sb.append(c);
            sb.append(']');
            return sb.toString();
        }
    }
}
