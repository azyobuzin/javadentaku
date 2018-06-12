package dentaku.cc;

import dentaku.ast.DentakuAstNode;
import dentaku.ast.DentakuAstPrinter;
import dentaku.ast.DentakuEvaluator;

import java.io.FileWriter;
import java.io.IOException;

class DentakuParserPlayground {
    public static void main(String[] args) throws ParseException, IOException {
        DentakuParser parser = new DentakuParser(System.in);
        DentakuAstNode ast = parser.start();
        System.out.println(ast);

        try (FileWriter output = new FileWriter("ast.dot")) {
            DentakuAstPrinter.writeDotTo(ast, output);
        }

        double evaluated = DentakuEvaluator.evaluate(ast);
        System.out.println(evaluated);
    }
}
