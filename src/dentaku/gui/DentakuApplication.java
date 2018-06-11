package dentaku.gui;

import javax.swing.*;

public class DentakuApplication {
    private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";

    public static void main(String[] args) {
        try {
            // ルック&フィールを設定する
            UIManager.setLookAndFeel(PREFERRED_LOOK_AND_FEEL);
        } catch (Throwable ignored) {
            // 設定に失敗した場合でも、続行可能
        }

        // ワーカーとウィンドウを作成
        DentakuWorker worker = new DentakuWorker();
        DentakuFrame frame = new DentakuFrame(worker);

        // ウィンドウが閉じられたらアプリを終了する
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ウィンドウ表示
        frame.setVisible(true);
    }
}
