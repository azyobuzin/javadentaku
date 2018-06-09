package dentaku.nocc.lex.nfa;

import dentaku.nocc.lex.CharRange;
import dentaku.nocc.util.DotUtil;

import java.io.*;
import java.util.*;

public final class NfaPrinter {
    private NfaPrinter() { }

    /**
     * NFA を Graphviz の DOT 言語で出力する
     */
    public static void writeDotTo(NfaState startState, Writer output) {
        PrintWriter pw = new PrintWriter(output);
        pw.println("digraph {");
        pw.println("    rankdir = LR");

        // 到達可能な状態を幅優先で探索して番号をつけておく
        int index = 0;
        Map<NfaState, Integer> stateMap = new HashMap<>();
        List<NfaEdge> edges = new ArrayList<>();
        Queue<NfaState> queue = new ArrayDeque<>();
        queue.add(startState);

        NfaState state;
        while ((state = queue.poll()) != null) {
            if (!stateMap.containsKey(state)) {
                // 状態の書式を出力
                pw.format("    %s [label=%s", indexToId(index), DotUtil.toDotString(Integer.toString(index)));
                if (state.isFinal()) pw.write(", peripheries=2");
                pw.println("]");

                stateMap.put(state, index++);

                for (NfaEdge edge : state.getOutgoingEdges()) {
                    edges.add(edge);

                    NfaState to = edge.getTo();
                    if (!stateMap.containsKey(to))
                        queue.add(to);
                }
            }
        }

        // すべての辺を出力
        for (NfaEdge edge : edges) {
            CharRange charRange = edge.getLabel();
            String label = charRange != null ? charRange.toString() : "&epsilon;";
            pw.format(
                "    %s -> %s [label=%s]",
                indexToId(stateMap.get(edge.getFrom())),
                indexToId(stateMap.get(edge.getTo())),
                DotUtil.toDotString(label)
            );
            pw.println();
        }

        pw.println("}");
    }

    private static String indexToId(int index) {
        return String.format("S_%d", index);
    }
}
