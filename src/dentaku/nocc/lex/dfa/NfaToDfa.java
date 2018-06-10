package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;
import dentaku.nocc.lex.nfa.NfaEdge;
import dentaku.nocc.lex.nfa.NfaState;
import dentaku.nocc.util.ImmutablePair;

import java.util.*;
import java.util.stream.*;

public final class NfaToDfa {
    private NfaToDfa() { }

    /**
     * NFA から DFA に変換する
     *
     * @param nfaStartState 初期状態
     * @return 初期状態
     */
    public static DfaState convert(NfaState nfaStartState) {
        Map<Set<NfaState>, Set<IntermediateEdge>> knownDfaStates = new HashMap<>();

        Set<NfaState> firstStateSet = findStatesReachableByEpsilonTransition(nfaStartState);
        knownDfaStates.put(firstStateSet, new HashSet<>());

        {
            Deque<Set<NfaState>> stack = new ArrayDeque<>();
            stack.addFirst(firstStateSet);

            Set<NfaState> stateSet;
            while ((stateSet = stack.pollFirst()) != null) {
                // 遷移先探索
                Map<Character, Set<NfaState>> transitionMap = createTransitionMap(stateSet);

                Set<IntermediateEdge> edges = knownDfaStates.get(stateSet);

                for (Map.Entry<Character, Set<NfaState>> entry : transitionMap.entrySet()) {
                    Set<NfaState> destStateSet = entry.getValue();

                    // まだ探索していない集合ならスタックに追加
                    if (!knownDfaStates.containsKey(destStateSet)) {
                        knownDfaStates.put(destStateSet, new HashSet<>());
                        stack.addFirst(destStateSet);
                    }

                    // 辺として登録
                    edges.add(new IntermediateEdge(destStateSet, entry.getKey()));
                }
            }
        }

        // DfaState クラスに変換していく
        Map<Set<NfaState>, DfaState> dfaStateMap = new HashMap<>();
        for (Set<NfaState> stateSet : knownDfaStates.keySet()) {
            DfaState dfaState = new DfaState();
            for (NfaState nfaState : stateSet)
                dfaState.includeNfaState(nfaState);
            dfaStateMap.put(stateSet, dfaState);
        }

        // 辺の接続
        for (Map.Entry<Set<NfaState>, Set<IntermediateEdge>> entry : knownDfaStates.entrySet()) {
            DfaState sourceState = dfaStateMap.get(entry.getKey());
            for (ImmutablePair<Set<NfaState>, CharRange> edgeData : createEdges(entry.getValue())) {
                sourceState.addOutgoingEdge(
                    dfaStateMap.get(edgeData.item1),
                    edgeData.item2
                );
            }
        }

        return dfaStateMap.get(firstStateSet);
    }

    /**
     * {@code state} および {@code state} からε遷移で到達できる状態を探索
     */
    private static Set<NfaState> findStatesReachableByEpsilonTransition(NfaState state) {
        Set<NfaState> result = new HashSet<>();
        Deque<NfaState> stack = new ArrayDeque<>();

        result.add(state);
        stack.addFirst(state);

        while ((state = stack.pollFirst()) != null) {
            for (NfaEdge edge : state.getOutgoingEdges()) {
                if (edge.isEpsilon()) {
                    NfaState to = edge.getTo();

                    if (result.add(to))
                        stack.addFirst(to);
                }
            }
        }

        return result;
    }

    /**
     * {@code sourceStateSet} から次に遷移可能な状態の集合を探す
     */
    private static Map<Character, Set<NfaState>> createTransitionMap(Set<NfaState> sourceStateSet) {
        return sourceStateSet.stream()
            .flatMap(state -> state.getOutgoingEdges().stream())
            .filter(edge -> !edge.isEpsilon())
            .flatMap(edge -> {
                CharRange charRange = edge.getLabel();
                return findStatesReachableByEpsilonTransition(edge.getTo()).stream()
                    .flatMap(reachableState ->
                        IntStream.rangeClosed(charRange.getStart(), charRange.getEnd())
                            .mapToObj(c -> new ImmutablePair<>((char) c, reachableState)));
            })
            .collect(Collectors.groupingBy(
                p -> p.item1,
                Collectors.mapping(p -> p.item2, Collectors.toSet())
            ));
    }

    /**
     * {@code intermediateEdges} をうまく {@link CharRange} にまとめる
     */
    private static List<ImmutablePair<Set<NfaState>, CharRange>> createEdges(Set<IntermediateEdge> intermediateEdges) {
        Set<Map.Entry<Set<NfaState>, List<Character>>> destGrouping =
            intermediateEdges.stream()
                .collect(Collectors.groupingBy(
                    IntermediateEdge::getTo,
                    Collectors.mapping(IntermediateEdge::getLabel, Collectors.toList())))
                .entrySet();

        List<ImmutablePair<Set<NfaState>, CharRange>> result = new ArrayList<>();

        for (Map.Entry<Set<NfaState>, List<Character>> entry : destGrouping) {
            Set<NfaState> dest = entry.getKey();
            List<Character> charList = entry.getValue();
            if (charList.size() == 0) continue;

            Collections.sort(charList);

            int startIndex = 0;
            for (int i = 1; i < charList.size(); i++) {
                if (charList.get(i - 1) + 1 != charList.get(i)) {
                    // 連続になっていないのでここで区切り
                    result.add(new ImmutablePair<>(
                        dest,
                        new CharRange(charList.get(startIndex), charList.get(i - 1))
                    ));
                    startIndex = i;
                }
            }

            // 残りの部分
            result.add(new ImmutablePair<>(
                dest,
                new CharRange(charList.get(startIndex), charList.get(charList.size() - 1))
            ));
        }

        return result;
    }
}
