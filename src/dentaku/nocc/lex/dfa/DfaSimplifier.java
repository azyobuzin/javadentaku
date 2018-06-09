package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;

import java.util.*;

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

        List<Set<DfaState>> sets = new ArrayList<>();
        sets.addAll(stateMap.values());
        sets.addAll(finalStateMap.values());

        for (int setIndex = 0; setIndex < sets.size(); setIndex++) {
            Set<DfaState> set = sets.get(setIndex);

            while (true) {
                Optional<DfaEdge> outsideEdge = set.stream()
                    .flatMap(state -> Arrays.stream(state.getOutgoingEdges()))
                    .filter(edge -> !set.contains(edge.getTo()))
                    .findAny();

                if (outsideEdge.isPresent()) {
                    // 別の集合に分割
                    DfaState sourceState = outsideEdge.get().getFrom();
                    set.remove(sourceState);
                    sets.add(Set.of(sourceState));
                } else {
                    break;
                }
            }
        }

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
