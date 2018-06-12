package dentaku.nocc.lex;

import dentaku.nocc.lex.dfa.*;
import dentaku.nocc.lex.nfa.*;
import dentaku.nocc.lex.regex.RegexNode;

import java.io.*;

/**
 * 小数の正規表現について状態遷移図を出力
 */
class RegexPlayground extends LexPlaygroundBase {
    public static void main(String[] args) throws IOException {
        // 小数の正規表現
        RegexNode decimalRegex = DentakuLexicalAnalyzer.decimalRegexNode();

        // 正規表現 -> NFA
        NfaRepr nfa = RegexToNfa.convert(decimalRegex);
        saveNfa(nfa.getStartState(), "DecimalNfa.dot");

        // NFA -> DFA
        DfaState dfa = NfaToDfa.convert(nfa.getStartState());

        DfaStateLabelProvider stateLabelProvider = new NfaNumberDfaStateLabelProvider(nfa.getStartState());
        saveDfa(dfa, "DecimalDfa.dot", stateLabelProvider);

        // DFA の最小化
        DfaState dfaSimplified = DfaSimplifier.simplify(dfa);
        saveDfa(dfaSimplified, "DecimalDfaSimplified.dot", stateLabelProvider);
        saveDfa(dfaSimplified, "DecimalDfaSimplifiedSequential.dot", new SequentialDfaStateLabelProvider(dfaSimplified));
    }
}
