package dentaku.nocc.lex;

import java.io.IOException;

/**
 * {@link #read()} を呼ぶと 1 文字を読み取るオブジェクト
 */
public interface CharReader {
    /**
     * 1 文字を読み取る。値がない場合はブロックする。EOF は -1
     */
    int read() throws IOException;
}
