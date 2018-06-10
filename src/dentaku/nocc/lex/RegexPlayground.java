package dentaku.nocc.lex;

import dentaku.nocc.lex.dfa.*;
import dentaku.nocc.lex.nfa.*;
import dentaku.nocc.lex.regex.*;

import java.io.*;

class RegexPlayground {
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
        saveNfa(nfa.getStartState(), "DecimalNfa.dot");

        DfaState dfa = NfaToDfa.convert(nfa.getStartState());

        DfaStateLabelProvider stateLabelProvider = new DefaultDfaStateLabelProvider(nfa.getStartState());
        saveDfa(dfa, "DecimalDfa.dot", stateLabelProvider);

        DfaState dfaSimplified = DfaSimplifier.simplify(dfa);
        saveDfa(dfaSimplified, "DecimalDfaSimplified.dot", stateLabelProvider);
    }

    private static void saveNfa(NfaState startState, String fileName) throws IOException {
        try (FileWriter output = new FileWriter(fileName)) {
            NfaPrinter.writeDotTo(startState, output);
        }
    }

    private static void saveDfa(DfaState startState, String fileName, DfaStateLabelProvider stateLabelProvider) throws IOException {
        try (FileWriter output = new FileWriter(fileName)) {
            DfaPrinter.writeDotTo(startState, output, stateLabelProvider);
        }
    }
}
