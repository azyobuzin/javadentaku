package dentaku.cc;

import dentaku.ast.DentakuAstNode;

class DentakuParserPlayground {
    public static void main(String[] args) throws ParseException {
        DentakuParser parser = new DentakuParser(System.in);
        parser.disable_tracing();
        DentakuAstNode ast = parser.start();
        System.out.println(ast);
    }
}
