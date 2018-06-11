package dentaku.ast;

/**
 * 抽象構文木のノードの基底クラス
 */
public abstract class DentakuAstNode {
    public abstract <T> T accept(DentakuAstNodeVisitor<T> visitor);
}
