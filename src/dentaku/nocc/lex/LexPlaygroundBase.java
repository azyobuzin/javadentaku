package dentaku.nocc.lex;

import dentaku.nocc.lex.dfa.*;
import dentaku.nocc.lex.nfa.*;

import java.io.FileWriter;
import java.io.IOException;

abstract class LexPlaygroundBase {
    protected static void saveNfa(NfaState startState, String fileName) throws IOException {
        try (FileWriter output = new FileWriter(fileName)) {
            NfaPrinter.writeDotTo(startState, output);
        }
    }

    protected static void saveDfa(DfaState startState, String fileName, DfaStateLabelProvider stateLabelProvider) throws IOException {
        try (FileWriter output = new FileWriter(fileName)) {
            DfaPrinter.writeDotTo(startState, output, stateLabelProvider);
        }
    }
}
