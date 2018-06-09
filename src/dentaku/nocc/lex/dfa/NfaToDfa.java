package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.nfa.NfaEdge;
import dentaku.nocc.lex.nfa.NfaState;

import java.util.*;

public final class NfaToDfa {
    private NfaToDfa() { }

    /**
     * 今回の {@link #merge(DfaState)} で変更があったかどうか
     */
    private boolean m_changed;

    /**
     * NFA 状態の集合から、それに一致する DFA 状態を引き出すための Map
     */
    private final Map<NfaStateSet, DfaState> m_stateSetMap = new HashMap<>();

    /**
     * NFA から DFA に変換する
     *
     * @param nfaStartState 初期状態
     * @return 初期状態
     */
    public static DfaState convert(NfaState nfaStartState) {
        NfaToDfa self = new NfaToDfa();
        DfaState dfaStartState = self.copyAutomaton(nfaStartState);

        do {
            self.m_changed = false;
            self.m_stateSetMap.clear();
            dfaStartState = self.merge(dfaStartState);
        } while (self.m_changed);

        return dfaStartState;
    }

    /**
     * {@link NfaState} を {@link DfaState} に型変換する
     *
     * @param nfaStartState 初期状態
     * @return 初期状態
     */
    private DfaState copyAutomaton(NfaState nfaStartState) {
        Map<NfaState, DfaState> map = new HashMap<>();

        // 全状態のコピー
        {
            Deque<NfaState> stack = new ArrayDeque<>();
            stack.addFirst(nfaStartState);

            NfaState nfaState;
            while ((nfaState = stack.pollFirst()) != null) {
                if (!map.containsKey(nfaState)) {
                    DfaState dfaState = new DfaState();
                    dfaState.includeNfaState(nfaState);
                    map.put(nfaState, dfaState);

                    for (NfaEdge edge : nfaState.getOutgoingEdges()) {
                        NfaState to = edge.getTo();
                        if (!map.containsKey(to))
                            stack.addFirst(edge.getTo());
                    }
                }
            }
        }

        // 辺のコピー
        for (Map.Entry<NfaState, DfaState> entry : map.entrySet()) {
            NfaState nfaState = entry.getKey();
            DfaState dfaState = entry.getValue();

            for (NfaEdge edge : nfaState.getOutgoingEdges())
                dfaState.addOutgoingEdge(map.get(edge.getTo()), edge.getLabel());
        }

        return map.get(nfaStartState);
    }

    /**
     * {@link #m_stateSetMap} に {@code state} を登録する
     */
    private void registerDfaStateToMap(DfaState state) {
        NfaStateSet stateSet = new NfaStateSet(state.getIncludedNfaStates());
        DfaState oldValue = m_stateSetMap.putIfAbsent(stateSet, state);

        if (oldValue != null && oldValue != state) {
            // 相互に接続して次の merge で統合する
            oldValue.addOutgoingEdge(state, null);
            state.addOutgoingEdge(oldValue, null);
        }
    }

    /**
     * {@code state} および {@code state} からε遷移で到達できる状態を探索
     */
    private static Set<DfaState> findStatesReachableByEpsilonTransition(DfaState state) {
        Set<DfaState> result = new HashSet<>();
        Deque<DfaState> stack = new ArrayDeque<>();
        stack.addFirst(state);
        while ((state = stack.pollFirst()) != null) {
            if (result.add(state)) {
                for (DfaEdge edge : state.getOutgoingEdges()) {
                    if (edge.isEpsilon()) {
                        DfaState to = edge.getTo();
                        if (!result.contains(to))
                            stack.addFirst(to);
                    }
                }
            }
        }

        return result;
    }

    private DfaState merge(DfaState state) {
        Set<DfaState> stateSet = findStatesReachableByEpsilonTransition(state);

        TransitionMap transitionMap = new TransitionMap();

        if (stateSet.size() > 1) {
            // 2 個以上ある → ε遷移ができるのでまとめる
            state = new DfaState();
            for (DfaState dfaState : stateSet)
            {
                for (NfaState nfaState : dfaState.getIncludedNfaStates())
                    state.includeNfaState(nfaState);

                for (DfaEdge edge : dfaState.getOutgoingEdges())
                {
                    if (!edge.isEpsilon())
                        transitionMap.put(edge.getLabel(), edge.getTo());
                }
            }
            m_changed = true;
        }

        registerDfaStateToMap(state);

        // TODO: 遷移先重複チェック
        for (DfaEdge edge : state.)

        return state;
    }

    private DfaState createDfaState(NfaStateSet nfaStates) {
        DfaState newState = new DfaState();
        for (NfaState s : nfaStates.getStates())
            newState.includeNfaState(s);

        registerDfaStateToMap(newState);
    }
}
