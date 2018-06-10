package dentaku.nocc.lex;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 電卓の字句解析器をテスト
 */
class DentakuLexicalAnalyzerPlayground {
    public static void main(String[] args) throws LexicalException, IOException {
        // 標準入力から読み取る
        LexicalAnalyzer<DentakuToken> lexer = DentakuLexicalAnalyzer.createLexicalAnalyzerBuilder()
            .build(new StreamCharReader(new InputStreamReader(System.in)));

        while (true) {
            DentakuToken token = lexer.eat();
            System.out.println(token);
        }

        // EOF の処理はしていないので、最後は必ず例外で終了
    }
}
