package dentaku.nocc.lex;

public class CharRange {
    private final char m_start;
    private final char m_end;

    public CharRange(char start, char end) {
        if (start > end) throw new IllegalArgumentException("start > end");

        m_start = start;
        m_end = end;
    }

    public CharRange(char c) {
        this(c, c);
    }

    public char getStart() { return m_start; }

    public char getEnd() { return m_end; }

    public CharRange getIntersection(CharRange other) {
        char maxStart = getStart() >= other.getStart() ? getStart() : other.getStart();
        char minEnd = getEnd() <= other.getEnd() ? getEnd() : other.getEnd();
        return maxStart <= minEnd ? new CharRange(maxStart, minEnd) : null;
    }

    public boolean contains(char c) {
        return c >= getStart() && c <= getEnd();
    }

    @Override
    public String toString() {
        return getStart() == getEnd()
            ? escapeChar(getStart())
            : String.format("[%s-%s]", escapeChar(getStart()), escapeChar(getEnd()));
    }

    /**
     * 表示できない文字をエスケープ
     */
    static String escapeChar(char c) {
        switch (c) {
            case '\b': return "\\b";
            case '\t': return "\\t";
            case '\n': return "\\n";
            case '\f': return "\\f";
            case '\r': return "\\r";
        }

        if (c <= 0x1F || (c >= 0x7F && c <= 0xA0))
            return String.format("\\x%02X", c);

        return String.valueOf(c);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharRange) {
            CharRange other = (CharRange) obj;
            return getStart() == other.getStart() && getEnd() == other.getEnd();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getStart() << 16 | getEnd();
    }
}
