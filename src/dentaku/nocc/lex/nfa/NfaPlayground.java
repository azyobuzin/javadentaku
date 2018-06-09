package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.regex.*;

import java.io.*;

class NfaPlayground {
    public static void main(String[] args) throws IOException {
        // 小数の正規表現 ([0-9]+(\.[0-9]+)?|\.[0-9]+)
        RegexNode integerRegex = RegexFactory.repeat1(RegexFactory.charRange('0', '9'));
        RegexNode dotRegex = RegexFactory.charcter('.');
        RegexNode decimalRegex = RegexFactory.choice(
            RegexFactory.sequence(
                integerRegex,
                RegexFactory.maybe(
                    RegexFactory.sequence(
                        dotRegex,
                        integerRegex
                    )
                )
            ),
            RegexFactory.sequence(
                dotRegex,
                integerRegex
            )
        );

        NfaRepr nfa = RegexToNfa.convert(decimalRegex);

        try (FileWriter output = new FileWriter("DecimalNfa.dot")) {
            NfaPrinter.writeDotTo(nfa.getStartState(), output);
        }
    }
}
