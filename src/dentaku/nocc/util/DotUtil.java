package dentaku.nocc.util;

public final class DotUtil {
    private DotUtil() { }

    /**
     * DOT 言語向けのエスケープ処理
     */
    public static String toDotString(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }
}
