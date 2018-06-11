package dentaku.gui;

/**
 * 計算が行われた結果
 */
public class DentakuWorkerResult {
    private final boolean m_succeeded;
    private final String m_message;

    public DentakuWorkerResult(boolean succeeded, String message) {
        m_succeeded = succeeded;
        m_message = message;
    }

    /**
     * 処理が成功したかどうか
     */
    public boolean succeeded() { return m_succeeded; }

    /**
     * 結果を表す文字列
     */
    public String getMessage() { return m_message; }

    @Override
    public String toString() { return getMessage(); }

    /**
     * 成功したときの {@link DentakuWorkerResult} を作成
     */
    public static DentakuWorkerResult success(double result) {
        return new DentakuWorkerResult(true, String.format("%f", result));
    }

    public static DentakuWorkerResult lexicalError() {
        return new DentakuWorkerResult(false, "字句解析失敗");
    }

    public static DentakuWorkerResult syntacticError() {
        return new DentakuWorkerResult(false, "構文解析失敗");
    }

    public static DentakuWorkerResult unexpectedError() {
        return new DentakuWorkerResult(false, "予期しないエラー");
    }
}
