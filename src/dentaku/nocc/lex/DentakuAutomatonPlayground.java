package dentaku.nocc.lex;

import dentaku.nocc.lex.dfa.*;
import dentaku.nocc.lex.nfa.NfaState;

import java.io.IOException;

/**
 * 電卓の状態遷移図を出力
 */
class DentakuAutomatonPlayground extends LexPlaygroundBase {
    public static void main(String[] args) throws IOException {
        LexicalAnalyzer.Builder<DentakuToken> lexBuilder = DentakuLexicalAnalyzer.createLexicalAnalyzerBuilder();

        NfaState nfa = lexBuilder.buildNfa();
        saveNfa(nfa, "DentakuNfa.dot");

        DfaState dfa = DfaSimplifier.simplify(NfaToDfa.convert(nfa));
        saveDfa(dfa, "DentakuDfa.dot", new DefaultDfaStateLabelProvider(nfa));
    }
}
