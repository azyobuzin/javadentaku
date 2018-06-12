package dentaku.gui;

import dentaku.ast.DentakuAstNode;
import dentaku.ast.DentakuEvaluator;
import dentaku.cc.*;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class DentakuWorker implements AutoCloseable {
    private final Object m_lock = new Object();
    private WorkerThread m_workerThread;
    private BlockingReader m_reader;
    private Consumer<DentakuWorkerResult> m_resultConsumer;

    public DentakuWorker() {
        // 初期状態にする
        reset();
    }

    /**
     * 結果のハンドラを登録する
     */
    public void setResultConsumer(Consumer<DentakuWorkerResult> resultConsumer) {
        m_resultConsumer = resultConsumer;
    }

    /**
     * 1 文字の入力を受け取る
     */
    public void input(char c) {
        synchronized (m_lock) {
            m_reader.write(c);
        }
    }

    /**
     * 状態をクリアして、最初から読み取る
     */
    public void reset() {
        synchronized (m_lock) {
            close();

            // 新しいワーカースレッドを作成する
            m_reader = new BlockingReader();
            m_workerThread = new WorkerThread(m_reader);
        }

        m_workerThread.start();
    }

    @Override
    public void close() {
        synchronized (m_lock) {
            // 停止リクエストを送る
            if (m_workerThread != null) {
                m_workerThread.requestStop();
                m_workerThread = null;
            }

            // EOF を送り込んで字句解析を失敗させる
            if (m_reader != null) {
                m_reader.write(-1);
                m_reader = null;
            }
        }
    }

    private void handleResult(DentakuWorkerResult result) {
        Consumer<DentakuWorkerResult> consumer = m_resultConsumer;
        if (consumer != null) consumer.accept(result);
    }

    private static final class BlockingReader extends Reader {
        private final Object m_lock = new Object();
        private final Queue<Character> m_queue = new ArrayDeque<>();
        private boolean m_eof;
        private boolean m_closed;

        public void write(int c) {
            synchronized (m_lock) {
                // リーダーが閉じられているなら何もしない
                if (m_closed) return;

                if (c < 0) {
                    // EOF が来たのでマーク
                    m_eof = true;
                } else {
                    // 通常の文字はキューに追加
                    m_queue.add((char) c);
                }

                // 待機中のスレッドを起こす
                m_lock.notifyAll();
            }
        }

        @Override
        public int read() throws IOException {
            synchronized (m_lock) {
                while (true) {
                    // close 後なら例外を投げる
                    if (m_closed) throw new IOException("closed");

                    // EOF を迎えているなら -1 を返す
                    if (m_eof) return -1;

                    // キューから取り出せたらそれを返す
                    Character c = m_queue.poll();
                    if (c != null)
                        return (int) (char) c;

                    // 取り出せなかったら待機
                    try {
                        m_lock.wait();
                    } catch (InterruptedException ex) {
                        throw new InterruptedIOException();
                    }
                }
            }
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int c = read();
            if (c < 0) return 0; // EOF なら 0 を返す
            // 1文字セットして返す
            cbuf[off] = (char) c;
            return 1;
        }

        @Override
        public void close() {
            synchronized (m_lock) {
                // 終了状態にして待機中のスレッドで例外を起こさせる
                m_closed = true;
                m_lock.notifyAll();
            }
        }
    }

    private final class WorkerThread extends Thread {
        private final Reader m_reader;
        private boolean m_stopRequested;

        public WorkerThread(Reader reader) {
            super(createWorkerThreadName());
            m_reader = reader;
        }

        @Override
        public void run() {
            workerLog("Started");

            try {
                DentakuParser parser = new DentakuParser(m_reader);

                while (true) {
                    // 構文解析を実行
                    DentakuAstNode ast = parser.start();

                    // 結果を出力しておく（デバッグ用）
                    workerLog(ast.toString());

                    // 式を評価
                    double evaluatedValue = DentakuEvaluator.evaluate(ast);

                    // 結果を作成
                    DentakuWorkerResult result = DentakuWorkerResult.success(evaluatedValue);

                    // 停止リクエストが来ているなら終了
                    if (m_stopRequested) {
                        workerLog("Stop");
                        return;
                    }

                    handleResult(result);
                }
            } catch (Throwable e) {
                // 停止リクエストが来ているなら、例外が発生するはずなので、そのまま終了
                if (m_stopRequested) {
                    workerLog("Stop");
                    return;
                }

                e.printStackTrace();

                DentakuWorkerResult result;
                if (e instanceof TokenMgrError) {
                    result = DentakuWorkerResult.lexicalError();
                } else if (e instanceof ParseException) {
                    result = DentakuWorkerResult.syntacticError();
                } else {
                    result = DentakuWorkerResult.unexpectedError();
                }

                if (!m_stopRequested) {
                    handleResult(result);

                    // Parser, Reader のうちどこかの状態が不正かもしれないので、完全にリセットする
                    reset();
                }
            }

            workerLog("Exit");
        }

        public void requestStop() {
            m_stopRequested = true;
        }
    }

    // 内部クラスに static メンバーを定義できないのでここに書く
    private static int m_threadNum;

    /**
     * スレッドに一意な名前を付ける
     */
    private static synchronized String createWorkerThreadName() {
        return "dentaku.gui.DentakuWorker-" + m_threadNum++;
    }

    /**
     * スレッド名とメッセージを出力する
     */
    private static void workerLog(String message) {
        System.out.format("%s: %s\n", Thread.currentThread().getName(), message);
    }
}

