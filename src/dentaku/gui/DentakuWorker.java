package dentaku.gui;

import dentaku.ast.DentakuAstNode;
import dentaku.ast.DentakuEvaluator;
import dentaku.cc.*;

import java.io.*;
import java.util.function.Consumer;

public class DentakuWorker implements AutoCloseable {
    private final Object m_lockObj = new Object();
    private WorkerThread m_workerThread;
    private PipedWriter m_pipedWriter;
    private Consumer<DentakuWorkerResult> m_resultConsumer;

    public DentakuWorker() {
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
        try {
            m_pipedWriter.write(c);
        } catch (Throwable e) {
            // もし書き込みに失敗したら、リセットをかける
            m_workerThread.interrupt();
            m_workerThread = null;
            // エラー通知
            handleResult(DentakuWorkerResult.unexpectedError());
            reset();
        }
    }

    /**
     * 状態をクリアして、最初から読み取る
     */
    public void reset() {
        synchronized (m_lockObj) {
            close();

            // 新しいワーカースレッドを作成する
            m_pipedWriter = new PipedWriter();

            PipedReader pipedReader;
            try { pipedReader = new PipedReader(m_pipedWriter); } catch (IOException e) {
                // ここで IOException が発生するのは明らかなバグなので回復不可能
                throw new IOError(e);
            }

            m_workerThread = new WorkerThread(pipedReader);
        }

        m_workerThread.start();
    }

    @Override
    public void close() {
        synchronized (m_lockObj) {
            // スレッドに割り込んで終了させる
            if (m_workerThread != null) {
                m_workerThread.interrupt();
                m_workerThread = null;
            }

            if (m_pipedWriter != null) {
                try {
                    m_pipedWriter.close();
                } catch (Throwable e) {
                    // クローズに失敗しても問題はないので、何もしない
                    e.printStackTrace();
                }
                m_pipedWriter = null;
            }
        }
    }

    private void handleResult(DentakuWorkerResult result) {
        Consumer<DentakuWorkerResult> consumer = m_resultConsumer;
        if (consumer != null) consumer.accept(result);
    }

    private final class WorkerThread extends Thread {
        private final Reader m_reader;

        public WorkerThread(Reader reader) {
            super("dentaku.gui.DentakuWorker-" + threadNum());
            m_reader = reader;
        }

        @Override
        public void run() {
            System.out.println("Started: " + Thread.currentThread().getName());

            try {
                DentakuParser parser = new DentakuParser(m_reader);

                while (true) {
                    // 構文解析を実行
                    DentakuAstNode ast = parser.start();

                    // 結果を出力しておく（デバッグ用）
                    System.out.println(ast);

                    // 式を評価
                    double evaluatedValue = DentakuEvaluator.evaluate(ast);

                    // 結果を作成
                    DentakuWorkerResult result = DentakuWorkerResult.success(evaluatedValue);

                    // 割り込みが発生していない（まだ終了させられてない）なら結果を返す
                    if (Thread.interrupted()) return;
                    handleResult(result);
                }
            } catch (Throwable e) {
                // スレッド割り込みのせいなら例外が発生するのが正常なので、そのまま終了
                if (Thread.interrupted()) return;

                e.printStackTrace();

                DentakuWorkerResult result;
                if (e instanceof TokenMgrError) {
                    result = DentakuWorkerResult.lexicalError();
                } else if (e instanceof ParseException) {
                    result = DentakuWorkerResult.syntacticError();
                } else {
                    result = DentakuWorkerResult.unexpectedError();
                }
                // TODO: 巨大すぎる数字はどうなる？

                if (!Thread.interrupted()) {
                    handleResult(result);

                    // Parser, PipedReader/Writer のどこかがおかしいので、完全にリセットする
                    reset();
                }
            }
        }
    }

    private static int m_threadNum;

    private static synchronized int threadNum() { return m_threadNum++; }
}

