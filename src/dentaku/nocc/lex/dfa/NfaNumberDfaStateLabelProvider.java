package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.nfa.NfaEdge;
import dentaku.nocc.lex.nfa.NfaState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NFA の状態に幅優先で連番を振って、それをラベル名に使う
 */
public class NfaNumberDfaStateLabelProvider implements DfaStateLabelProvider {
    private final Map<NfaState, Integer> m_stateMap = new HashMap<>();

    /**
     * @param startNfaState NFA の初期状態
     */
    public NfaNumberDfaStateLabelProvider(NfaState startNfaState) {
        int index = 0;
        Queue<NfaState> queue = new ArrayDeque<>();
        queue.add(startNfaState);

        NfaState state;
        while ((state = queue.poll()) != null) {
            if (!m_stateMap.containsKey(state)) {
                m_stateMap.put(state, index++);

                for (NfaEdge edge : state.getOutgoingEdges()) {
                    NfaState to = edge.getTo();
                    if (!m_stateMap.containsKey(to))
                        queue.add(to);
                }
            }
        }
    }

    @Override
    public String getLabel(DfaState state) {
        return state.getIncludedNfaStates().stream()
            .mapToInt(m_stateMap::get)
            .sorted()
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(","));
    }
}
