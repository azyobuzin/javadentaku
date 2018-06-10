package dentaku.nocc.lex;

public class DentakuToken {
    public static final int KIND_SPACE = 1;
    public static final int KIND_NUM = 2;
    public static final int KIND_PLUS = 3;
    public static final int KIND_MINUS = 4;
    public static final int KIND_TIMES = 5;
    public static final int KIND_DIV = 6;
    public static final int KIND_OPEN = 7;
    public static final int KIND_CLOSE = 8;
    public static final int KIND_EQUAL = 9;

    private final int m_kind;

    public DentakuToken(int kind) {
        m_kind = kind;
    }

    public int getKind() { return m_kind; }

    @Override
    public String toString() {
        switch (getKind()) {
            case KIND_SPACE: return "SPACE";
            case KIND_PLUS: return "PLUS";
            case KIND_MINUS: return "MINUS";
            case KIND_TIMES: return "TIMES";
            case KIND_DIV: return "DIV";
            case KIND_OPEN: return "OPEN";
            case KIND_CLOSE: return "CLOSE";
        }
        return Integer.toString(getKind());
    }

    public static class NumberToken extends DentakuToken {
        private final double m_value;

        public NumberToken(double value) {
            super(KIND_NUM);
            m_value = value;
        }

        public double getValue() { return m_value; }

        @Override
        public String toString() {
            return String.format("NUM(%f)", getValue());
        }
    }
}
