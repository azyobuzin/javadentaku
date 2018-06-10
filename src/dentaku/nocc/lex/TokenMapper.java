package dentaku.nocc.lex;

/**
 * 受理された文字列からトークン型に変換する関数型インターフェイス
 *
 * @param <T> トークン型
 */
@FunctionalInterface
public interface TokenMapper<T> {
    /**
     * @throws LexicalException 入力文字列を処理できなかった場合にスローされる
     */
    T map(String s) throws LexicalException;
}
