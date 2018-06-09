package dentaku.nocc.lex.dfa;

/**
 * {@link DfaPrinter} で使う DFA の状態のラベルを生成するインターフェイス
 */
public interface DfaStateLabelProvider {
    String getLabel(DfaState state);
}
