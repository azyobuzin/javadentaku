package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;

import java.io.*;
import java.util.*;

public final class NfaPrinter {
    private NfaPrinter() { }

    /**
     * NFA を Graphviz の DOT 言語で出力する
     */
    public static <T> void writeDotTo(NfaState<T> startState, Writer output) {
        PrintWriter pw = new PrintWriter(output);
        pw.println("digraph {");
        pw.println("rankdir = LR");
        pw.println("node [shape = circle]");

        // 到達可能な状態を幅優先で探索して番号をつけておく
        int index = 0;
        Map<NfaState<T>, Integer> stateMap = new HashMap<>();
        List<NfaEdge<T>> edges = new ArrayList<>();
        Queue<NfaState<T>> queue = new ArrayDeque<>();
        queue.add(startState);

        NfaState<T> state;
        while ((state = queue.poll()) != null) {
            if (!stateMap.containsKey(state)) {
                // 状態の書式を出力
                pw.format("    %s [label=%s", indexToId(index), toDotString(Integer.toString(index)));
                if (state.isFinal()) pw.write(", shape=doublecircle");
                pw.println("]");

                stateMap.put(state, index++);

                for (NfaEdge<T> edge : state.getOutgoingEdges()) {
                    edges.add(edge);
                    queue.add(edge.getTo());
                }
            }
        }

        // すべての辺を出力
        for (NfaEdge<T> edge : edges) {
            CharRange charRange = edge.getLabel();
            String label = charRange != null ? charRange.toString() : "&epsilon;";
            pw.format(
                "    %s -> %s [label=%s]",
                indexToId(stateMap.get(edge.getFrom())),
                indexToId(stateMap.get(edge.getTo())),
                toDotString(label)
            );
            pw.println();
        }

        pw.println("}");
    }

    private static String indexToId(int index) {
        return String.format("S_%d", index);
    }

    /**
     * DOT 言語向けのエスケープ処理
     */
    private static String toDotString(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }
}
