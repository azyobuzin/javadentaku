package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;
import dentaku.nocc.lex.nfa.NfaState;
import dentaku.nocc.util.ImmutablePair;

import java.util.*;
import java.util.stream.*;

public final class DfaSimplifier {
    private DfaSimplifier() { }

    public static DfaState simplify(DfaState startState) {
        Set<DfaState> stateSet = new HashSet<>();
        stateSet.add(startState);

        Map<EdgeLabelSet, Set<DfaState>> stateLabelMap = new HashMap<>();

        {
            // 深さ優先ですべての状態を探索
            Deque<DfaState> stack = new ArrayDeque<>();
            stack.addFirst(startState);

            DfaState state;
            while ((state = stack.pollFirst()) != null) {
                Set<DfaEdge> edges = state.getOutgoingEdges();

                // 辺のラベルリストで分割
                stateLabelMap
                    .computeIfAbsent(
                        new EdgeLabelSet(edges.stream().map(DfaEdge::getLabel)),
                        k -> new HashSet<>())
                    .add(state);

                for (DfaEdge edge : edges) {
                    DfaState destState = edge.getTo();
                    if (stateSet.add(destState))
                        stack.addFirst(destState);
                }
            }
        }

        List<ImmutablePair<EdgeLabelSet, Set<DfaState>>> sets =
            stateLabelMap.entrySet().stream()
                .flatMap(entry -> {
                    EdgeLabelSet labelSet = entry.getKey();
                    // 受理状態となる NFA によって分割（受理する NFA 状態が違えば、違う DFA 状態にする）
                    return entry.getValue().stream()
                        .collect(Collectors.groupingBy(
                            dfaState -> dfaState.getIncludedNfaStates().stream()
                                .filter(NfaState::isFinal)
                                .collect(Collectors.toSet()),
                            Collectors.toSet()
                        ))
                        .values().stream()
                        .map(set -> new ImmutablePair<>(labelSet, set));
                })
                .collect(Collectors.toList());

        // 状態から、状態が所属している集合を引けるようにする
        Map<DfaState, Set<DfaState>> stateSetMap = sets.stream()
            .flatMap(pair -> pair.item2.stream().map(state -> new ImmutablePair<>(state, pair.item2)))
            .collect(Collectors.toMap(pair -> pair.item1, pair -> pair.item2));

        for (int setIndex = 0; setIndex < sets.size(); setIndex++) {
            ImmutablePair<EdgeLabelSet, Set<DfaState>> pair = sets.get(setIndex);
            EdgeLabelSet labelSet = pair.item1;
            Set<DfaState> currentSet = pair.item2;

            while (currentSet.size() > 1) {
                Optional<Collection<List<DfaState>>> op =
                    labelSet.charStream() // ラベル 1 文字ずつについて
                        .mapToObj(c -> currentSet.stream().collect(
                            // 遷移先状態集合でグルーピング
                            Collectors.groupingBy(state ->
                                state.getOutgoingEdges().stream()
                                    .map(edge -> stateSetMap.get(edge.getTo()))
                                    .collect(Collectors.toSet()))
                        ))
                        .filter(map -> map.size() > 1) // 全部同じ遷移先ではない
                        .map(Map::values)
                        .findAny();

                if (op.isPresent()) {
                    // 分割を行う
                    for (List<DfaState> groupStates : op.get()) {
                        Set<DfaState> newSet = new HashSet<>();

                        for (DfaState state : groupStates) {
                            newSet.add(state);
                            currentSet.remove(state);
                            stateSetMap.put(state, newSet); // 状態集合逆引きも更新
                        }

                        sets.add(new ImmutablePair<>(labelSet, newSet));
                    }
                } else {
                    // これ以上分割できないなら次の集合へ
                    break;
                }
            }
        }

        // 集合に対応する状態を作成
        Map<Set<DfaState>, DfaState> newStateMap =
            sets.stream().map(pair -> pair.item2)
                .filter(set -> set.size() > 0)
                .collect(Collectors.toMap(
                    set -> set,
                    set -> {
                        DfaState newState = new DfaState();
                        set.stream().flatMap(s -> s.getIncludedNfaStates().stream())
                            .forEach(newState::includeNfaState);
                        return newState;
                    }));

        // 辺をつなぐ
        for (Map.Entry<Set<DfaState>, DfaState> entry : newStateMap.entrySet()) {
            final Set<DfaState> set = entry.getKey();
            final DfaState newState = entry.getValue();

            set.stream().flatMap(state -> state.getOutgoingEdges().stream())
                .map(edge -> new ImmutablePair<>(newStateMap.get(stateSetMap.get(edge.getTo())), edge.getLabel()))
                .collect(Collectors.groupingBy( // 遷移先でグルーピング
                    pair -> pair.item1,
                    Collectors.collectingAndThen(
                        Collectors.mapping(pair -> pair.item2, Collectors.toList()),
                        charRanges -> new EdgeLabelSet(charRanges.stream())
                    )
                ))
                .entrySet().stream()
                // CharRange ごとに分解
                .flatMap(e -> Arrays.stream(e.getValue().getCharRanges())
                    .map(range -> new ImmutablePair<>(e.getKey(), range)))
                // 辺を追加
                .forEach(pair -> newState.addOutgoingEdge(pair.item1, pair.item2));
        }

        return newStateMap.get(stateSetMap.get(startState));
    }

    private static final class EdgeLabelSet {
        private final CharRange[] m_charRanges;

        public EdgeLabelSet(Stream<CharRange> charRangeStream) {
            // 開始順にソート
            List<CharRange> charRanges = charRangeStream
                .sorted(Comparator.comparingInt(CharRange::getStart))
                .collect(Collectors.toList());

            switch (charRanges.size()) {
                case 0:
                    m_charRanges = new CharRange[0];
                    return;
                case 1:
                    m_charRanges = new CharRange[]{charRanges.get(0)};
                    return;
            }

            for (int i = 1; i < charRanges.size(); ) {
                CharRange prev = charRanges.get(i - 1);
                CharRange current = charRanges.get(i);

                if (current.getStart() <= prev.getEnd() + 1) {
                    // マージ可能
                    char end = current.getEnd() >= prev.getEnd() ? current.getEnd() : prev.getEnd();
                    charRanges.remove(i);
                    charRanges.set(i - 1, new CharRange(prev.getStart(), end));
                } else {
                    // マージしない場合は次のインデックスへ
                    i++;
                }
            }

            m_charRanges = charRanges.toArray(new CharRange[0]);
        }

        public CharRange[] getCharRanges() { return m_charRanges; }

        public IntStream charStream() {
            return Arrays.stream(m_charRanges)
                .flatMapToInt(range -> IntStream.rangeClosed(range.getStart(), range.getEnd()));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EdgeLabelSet)) return false;
            EdgeLabelSet that = (EdgeLabelSet) o;
            return Arrays.equals(m_charRanges, that.m_charRanges);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(m_charRanges);
        }
    }
}
