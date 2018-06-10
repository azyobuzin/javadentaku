package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;
import dentaku.nocc.util.ImmutablePair;

import java.util.*;
import java.util.stream.*;

public final class DfaSimplifier {
    private DfaSimplifier() { }

    public static DfaState simplify(DfaState startState) {
        Set<DfaState> stateSet = new HashSet<>();
        stateSet.add(startState);

        Map<EdgeLabelSet, Set<DfaState>> stateMap = new HashMap<>();
        Map<EdgeLabelSet, Set<DfaState>> finalStateMap = new HashMap<>();

        {
            Deque<DfaState> stack = new ArrayDeque<>();
            stack.addFirst(startState);

            DfaState state;
            while ((state = stack.pollFirst()) != null) {
                DfaEdge[] edges = state.getOutgoingEdges();

                // 受理状態と他の状態を分けて、さらに辺のラベルリストで分ける
                (state.isFinal() ? finalStateMap : stateMap)
                    .computeIfAbsent(new EdgeLabelSet(edges), k -> new HashSet<>())
                    .add(state);

                for (DfaEdge edge : edges) {
                    DfaState destState = edge.getTo();
                    if (stateSet.add(destState))
                        stack.addFirst(destState);
                }
            }
        }

        // 最初の分割をまとめる
        List<ImmutablePair<EdgeLabelSet, Set<DfaState>>> sets =
            Stream.concat(stateMap.entrySet().stream(), finalStateMap.entrySet().stream())
                .map(entry -> new ImmutablePair<>(entry.getKey(), entry.getValue()))
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
                                Arrays.stream(state.getOutgoingEdges())
                                    .map(edge -> stateSetMap.get(edge.getTo()))
                                    .collect(Collectors.toSet()))
                        ))
                        .filter(map -> map.size() > 1) // 全部同じ遷移先ではない
                        .map(Map::values)
                        .findAny();

                if (op.isPresent()) {
                    // 分割を行う
                    for (List<DfaState> destinationGroup : op.get()) {
                        Set<DfaState> newSet = new HashSet<>();

                        for (DfaState state : destinationGroup) {
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

        Map<DfaState, DfaState> mergedStateMap = new HashMap<>();


        // TODO: 再合成
        throw new UnsupportedOperationException();
    }

    private static final class EdgeLabelSet {
        private final CharRange[] m_charRanges;

        public EdgeLabelSet(DfaEdge[] edges) {
            if (edges.length == 0) {
                m_charRanges = new CharRange[0];
                return;
            }
            if (edges.length == 1) {
                m_charRanges = new CharRange[]{edges[0].getLabel()};
                return;
            }

            List<CharRange> charRanges = new ArrayList<>();
            for (DfaEdge edge : edges)
                charRanges.add(edge.getLabel());

            // 開始順にソート
            charRanges.sort(Comparator.comparingInt(CharRange::getStart));

            ListIterator<CharRange> iter = charRanges.listIterator();
            CharRange prev = iter.next();
            while (iter.hasNext()) {
                CharRange current = iter.next();
                if (current.getStart() <= prev.getEnd() + 1) {
                    // マージ可能
                    char end = current.getEnd() >= prev.getEnd() ? current.getEnd() : prev.getEnd();
                    current = new CharRange(prev.getStart(), end);

                    iter.remove();
                    iter.set(current);
                }
                prev = current;
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
