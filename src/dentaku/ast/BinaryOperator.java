package dentaku.ast;

/**
 * 二項演算子の種類
 */
public enum BinaryOperator {
    ADDITION {
        @Override
        public String getOperatorSign() { return "+"; }

        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    SUBTRACTION {
        @Override
        public String getOperatorSign() { return "-"; }

        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    MULTIPLICATION {
        @Override
        public String getOperatorSign() { return "*"; }

        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVISION {
        @Override
        public String getOperatorSign() { return "/"; }

        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    /**
     * 演算子の記号を返す
     */
    public abstract String getOperatorSign();

    /**
     * {@code double} 型の値 {@code x} と {@code y} が与えられたときに、その二項演算子を適用した結果を返す
     */
    public abstract double apply(double x, double y);
}
