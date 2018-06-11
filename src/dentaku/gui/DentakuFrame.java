package dentaku.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DentakuFrame extends JFrame {
    // 定数
    private static final int INITIAL_WIDTH = 350;
    private static final int INITIAL_HEIGHT = 250;
    private static final int DISPLAY_LABEL_PADDING = 10;
    private static final String INITIAL_DISPLAY_LABEL_TEXT = "0";
    private static final char[] BUTTON_LABELS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '(', ')', '+', '-', '*', '/', '='};
    private static final int BUTTONS_PANEL_ROWS = 3;
    private static final int BUTTONS_PANEL_COLS = 6;
    private static final String CLEAR_BUTTON_LABEL = "AC";

    private final DentakuWorker m_worker;
    private final JLabel m_displayLabel;

    // 入力された文字列
    private String m_inputText = "";

    public DentakuFrame(DentakuWorker worker) {
        setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        Container pane = getContentPane();

        // 表示部の作成
        m_displayLabel = new JLabel();
        m_displayLabel.setBorder(new EmptyBorder(DISPLAY_LABEL_PADDING, DISPLAY_LABEL_PADDING, DISPLAY_LABEL_PADDING, DISPLAY_LABEL_PADDING));
        m_displayLabel.setHorizontalAlignment(SwingConstants.TRAILING); // 右寄せ
        m_displayLabel.setText(INITIAL_DISPLAY_LABEL_TEXT);
        pane.add(m_displayLabel, BorderLayout.NORTH);

        // ボタンを配置するパネル
        JPanel buttonsPanel = new JPanel(new GridLayout(BUTTONS_PANEL_ROWS, BUTTONS_PANEL_COLS));
        pane.add(buttonsPanel, BorderLayout.CENTER);

        // ボタン作成
        for (char c : BUTTON_LABELS) {
            JButton button = new JButton();
            button.setText(String.valueOf(c));
            // クリックされたら、ボタンの文字を引数にして onClickButton を呼び出す
            button.addActionListener(e -> onClickButton(c));
            buttonsPanel.add(button);
        }

        // クリアボタン
        JButton clearButton = new JButton();
        clearButton.setText(CLEAR_BUTTON_LABEL);
        clearButton.addActionListener(this::onClickClearButton);
        buttonsPanel.add(clearButton);

        // ワーカーに結果ハンドラを登録
        m_worker = worker;
        m_worker.setResultConsumer(this::onReceiveWorkerResult);
    }

    /**
     * 表示部に文字列を表示させる
     */
    private void setDisplayText(String text) {
        m_displayLabel.setText(text);
    }

    /**
     * 入力された文字列をクリア
     */
    private void resetInput() {
        // 入力状態を空文字にする
        m_inputText = "";
    }

    /**
     * 文字ボタンが押されたときのイベントハンドラ
     */
    private void onClickButton(char c) {
        System.out.println("Input: " + c);
        // 表示を更新
        m_inputText += c;
        setDisplayText(m_inputText);
        // ワーカーに入力を投げる
        m_worker.input(c);
    }

    /**
     * クリアボタンが押されたときのイベントハンドラ
     */
    private void onClickClearButton(ActionEvent e) {
        System.out.println("Reset");
        // ワーカーの状態をリセット
        m_worker.reset();
        // 入力状態をリセット
        resetInput();
        // 表示部には初期状態で表示する文字列を表示させる
        setDisplayText(INITIAL_DISPLAY_LABEL_TEXT);
    }

    /**
     * ワーカーが結果を返したときのイベントハンドラ
     */
    private void onReceiveWorkerResult(DentakuWorkerResult result) {
        // 入力状態をリセット
        resetInput();
        // 結果を表示部に表示
        setDisplayText(result.getMessage());
    }
}
