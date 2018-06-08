package dentaku.nocc;

public class CharRange {
    private final char m_start;
    private final char m_end;

    public CharRange(char start, char end) {
        m_start = start;
        m_end = end;
    }

    public CharRange(char c) {
        this(c, c);
    }

    public char getStart() { return m_start; }

    public char getEnd() { return m_end; }

    public CharRange getIntersection(CharRange other) {
        char maxStart = m_start >= other.m_start ? m_start : other.m_start;
        char minEnd = m_end <= other.m_end ? m_end : other.m_end;
        return maxStart <= minEnd ? new CharRange(maxStart, minEnd) : null;
    }

    @Override
    public String toString() {
        return m_start == m_end
                ? String.valueOf(m_start)
                : String.format("[%c-%c]", m_start, m_end);
    }
}
