package dentaku.nocc.lex.dfa;

import dentaku.nocc.lex.CharRange;

import java.util.*;

class TransitionMap {
    private final Map<Character, Set<DfaState>> m_map = new HashMap<>();

    public Set<DfaState> get(char label) {
        return m_map.computeIfAbsent(label, k -> new HashSet<>());
    }

    public void put(char label, DfaState state) {
        get(label).add(state);
    }

    public void put(CharRange label, DfaState state) {
        for (char c = label.getStart(); c <= label.getEnd(); c++)
            put(c, state);
    }
}
