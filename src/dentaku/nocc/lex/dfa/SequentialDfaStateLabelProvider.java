package dentaku.nocc.lex.dfa;

import java.util.*;

/**
 * DFA の状態に幅優先で連番を振って、それをラベル名に使う
 */
public class SequentialDfaStateLabelProvider implements DfaStateLabelProvider {
    private final Map<DfaState, String> m_stateLabelMap = new HashMap<>();

    public SequentialDfaStateLabelProvider(DfaState startState) {
        int index = 0;
        Queue<DfaState> queue = new ArrayDeque<>();
        queue.add(startState);

        DfaState state;
        while ((state = queue.poll()) != null) {
            if (!m_stateLabelMap.containsKey(state)) {
                m_stateLabelMap.put(state, Integer.toString(index++));

                for (DfaEdge edge : state.getOutgoingEdges()) {
                    DfaState to = edge.getTo();
                    if (!m_stateLabelMap.containsKey(to))
                        queue.add(to);
                }
            }
        }
    }

    @Override
    public String getLabel(DfaState state) {
        return m_stateLabelMap.get(state);
    }
}
