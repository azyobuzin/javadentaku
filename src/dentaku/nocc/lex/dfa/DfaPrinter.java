package dentaku.nocc.lex.dfa;

import dentaku.nocc.util.DotUtil;

import java.io.*;
import java.util.*;

public final class DfaPrinter {
    private DfaPrinter() { }

    public static void writeDotTo(DfaState startState, Writer output, DfaStateLabelProvider stateLabelProvider) {
        PrintWriter pw = new PrintWriter(output);
        pw.println("digraph {");
        pw.println("    rankdir = LR");

        // 到達可能な状態を幅優先で探索して番号をつけておく
        int index = 0;
        Map<DfaState, Integer> stateMap = new HashMap<>();
        List<DfaEdge> edges = new ArrayList<>();
        Queue<DfaState> queue = new ArrayDeque<>();
        queue.add(startState);

        DfaState state;
        while ((state = queue.poll()) != null) {
            if (!stateMap.containsKey(state)) {
                // 状態の書式を出力
                String label = stateLabelProvider != null ? stateLabelProvider.getLabel(state) : "";
                pw.format("    %s [label=%s", indexToId(index), DotUtil.toDotString(label));
                if (state.isFinal()) pw.write(", peripheries=2");
                pw.println("]");

                stateMap.put(state, index++);

                for (DfaEdge edge : state.getOutgoingEdges()) {
                    edges.add(edge);

                    DfaState to = edge.getTo();
                    if (!stateMap.containsKey(to))
                        queue.add(to);
                }
            }
        }

        // すべての辺を出力
        for (DfaEdge edge : edges) {
            pw.format(
                "    %s -> %s [label=%s]",
                indexToId(stateMap.get(edge.getFrom())),
                indexToId(stateMap.get(edge.getTo())),
                DotUtil.toDotString(edge.getLabel().toString())
            );
            pw.println();
        }

        pw.println("}");
    }

    private static String indexToId(int index) {
        return String.format("S_%d", index);
    }
}
