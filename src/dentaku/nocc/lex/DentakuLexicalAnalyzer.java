package dentaku.nocc.lex;

import dentaku.nocc.lex.regex.RegexFactory;
import dentaku.nocc.lex.regex.RegexNode;

/**
 * 電卓を実現する字句解析器のためのヘルパークラス
 */
public final class DentakuLexicalAnalyzer {
    private DentakuLexicalAnalyzer() { }

    /**
     * 整数および小数の正規表現
     */
    public static RegexNode decimalRegexNode() {
        // ([0-9]+(\.[0-9]*)?|\.[0-9]+)
        RegexNode decimalSymbolRegex = RegexFactory.charRange('0', '9');
        RegexNode integerRegex = RegexFactory.repeat1(decimalSymbolRegex);
        RegexNode dotRegex = RegexFactory.charcter('.');
        return RegexFactory.choice(
            RegexFactory.sequence(
                integerRegex,
                RegexFactory.maybe(
                    RegexFactory.sequence(
                        dotRegex,
                        RegexFactory.repeat0(decimalSymbolRegex)
                    )
                )
            ),
            RegexFactory.sequence(
                dotRegex,
                integerRegex
            )
        );
    }

    /**
     * 「+」の正規表現
     */
    public static RegexNode plusRegexNode() {
        return RegexFactory.charcter('+');
    }

    /**
     * 「-」の正規表現
     */
    public static RegexNode minusRegexNode() {
        return RegexFactory.charcter('-');
    }

    /**
     * 「*」の正規表現
     */
    public static RegexNode timesRegexNode() {
        return RegexFactory.charcter('*');
    }

    /**
     * 「/」の正規表現
     */
    public static RegexNode divisionRegexNode() {
        return RegexFactory.charcter('/');
    }

    /**
     * 「(」の正規表現
     */
    public static RegexNode openParenthesesRegexNode() {
        return RegexFactory.charcter('(');
    }

    /**
     * 「)」の正規表現
     */
    public static RegexNode closeParenthesesRegexNode() {
        return RegexFactory.charcter(')');
    }

    /**
     * 「=」の正規表現
     */
    public static RegexNode equalRegexNode() {
        return RegexFactory.charcter('=');
    }

    /**
     * 1 文字以上の空白の正規表現
     */
    public static RegexNode whitespaceRegexNode() {
        // [ \r\n\t]+
        return RegexFactory.repeat1(RegexFactory.choice(
            RegexFactory.charcter(' '),
            RegexFactory.charcter('\r'),
            RegexFactory.charcter('\n'),
            RegexFactory.charcter('\t')
        ));
    }

    public static LexicalAnalyzer.Builder<DentakuToken> createLexicalAnalyzerBuilder() {
        return new LexicalAnalyzer.Builder<DentakuToken>()
            .addToken(decimalRegexNode(), DentakuLexicalAnalyzer::parseNumber)
            .addToken(plusRegexNode(), x -> new DentakuToken(DentakuToken.KIND_PLUS))
            .addToken(minusRegexNode(), x -> new DentakuToken(DentakuToken.KIND_MINUS))
            .addToken(timesRegexNode(), x -> new DentakuToken(DentakuToken.KIND_TIMES))
            .addToken(divisionRegexNode(), x -> new DentakuToken(DentakuToken.KIND_DIV))
            .addToken(openParenthesesRegexNode(), x -> new DentakuToken(DentakuToken.KIND_OPEN))
            .addToken(closeParenthesesRegexNode(), x -> new DentakuToken(DentakuToken.KIND_CLOSE))
            .addToken(equalRegexNode(), x -> new DentakuToken(DentakuToken.KIND_EQUAL))
            .addToken(whitespaceRegexNode(), x -> new DentakuToken(DentakuToken.KIND_SPACE));
    }

    private static DentakuToken parseNumber(String s) throws LexicalException {
        double value;
        try {
            value = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new LexicalException("double への変換に失敗", e);
        }
        return new DentakuToken.NumberToken(value);
    }
}
