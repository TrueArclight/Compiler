package translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LexicalAnalyzer {
    private static Map<String, Integer> symbolMap;
    private ArrayList<String> source;
    private String file;

    static final class RESERVE {
        static final String PROGRAM = "программа";
        static final String VAR = "переменные";
        static final String REAL = "вещественные";
        static final String WHOLE = "целые";
        static final String INPUT = "ввод";
        static final String OUTPUT = "вывод";
        static final String IF = "если";
        static final String RAV = "равен";
        static final String THEN = "то";
        static final String SIN = "sin";
        static final String COS = "cos";
        static final String BEGIN = "начало";
        static final String END = "конец";
        static final String SEMICOLON = ";";
        static final String COLON = ":";
        static final String PRODUCT = "*";
        static final String DIV = "/";
        static final String SLBKT = "[";
        static final String SRBKT = "]";
        static final String COMMA = ",";
        static final String EQUAL = "=";
        static final String LBKT = "(";
        static final String RBKT = ")";
        static final String DOT = ".";
    }

    private static final String[] SYMBOL_SEARCH_ORDER = {
            RESERVE.PROGRAM, RESERVE.VAR, RESERVE.REAL, RESERVE.WHOLE,
            RESERVE.INPUT, RESERVE.OUTPUT, RESERVE.IF, RESERVE.RAV, RESERVE.THEN, RESERVE.SIN, RESERVE.COS, RESERVE.BEGIN,
            RESERVE.END, RESERVE.SEMICOLON, RESERVE.COLON, RESERVE.PRODUCT, RESERVE.DIV,  RESERVE.SLBKT,RESERVE.SRBKT,
            RESERVE.COMMA, RESERVE.EQUAL, RESERVE.LBKT, RESERVE.RBKT, RESERVE.DOT,
    };

    private void initStatic() {
        symbolMap = new HashMap<>(SYMBOL_SEARCH_ORDER.length);
        symbolMap.put(RESERVE.PROGRAM, DB.PROGRAM);
        symbolMap.put(RESERVE.VAR, DB.VAR);
        symbolMap.put(RESERVE.REAL, DB.REAL);
        symbolMap.put(RESERVE.WHOLE, DB.WHOLE);
        symbolMap.put(RESERVE.INPUT, DB.INPUT);
        symbolMap.put(RESERVE.OUTPUT, DB.OUTPUT);
        symbolMap.put(RESERVE.IF, DB.IF);
        symbolMap.put(RESERVE.RAV, DB.RAV);
        symbolMap.put(RESERVE.THEN, DB.THEN);
        symbolMap.put(RESERVE.SIN, DB.SIN);
        symbolMap.put(RESERVE.COS, DB.COS);
        symbolMap.put(RESERVE.BEGIN, DB.BEGIN);
        symbolMap.put(RESERVE.END, DB.END);
        symbolMap.put(RESERVE.SEMICOLON, DB.SEMICOLON);
        symbolMap.put(RESERVE.COLON, DB.COLON);
        symbolMap.put(RESERVE.PRODUCT, DB.PRODUCT);
        symbolMap.put(RESERVE.DIV, DB.DIV);
        symbolMap.put(RESERVE.SLBKT, DB.SLBKT);
        symbolMap.put(RESERVE.SRBKT, DB.SRBKT);
        symbolMap.put(RESERVE.COMMA, DB.COMMA);
        symbolMap.put(RESERVE.EQUAL, DB.EQUAL);
        symbolMap.put(RESERVE.LBKT, DB.LBKT);
        symbolMap.put(RESERVE.RBKT, DB.RBKT);
        symbolMap.put(RESERVE.DOT, DB.DOT);
    }

    public LexicalAnalyzer(String Source) throws IOException {
        this.source = Utils.readFile(Source);
        this.file = Source;
        initStatic();
    }

    private String nextword(String check, ArrayList<Integer> forfile, ArrayList<String> table) {
        int pos;
        for (String constant : SYMBOL_SEARCH_ORDER) {
            if (check.equals(constant)) {
                forfile.add(symbolMap.get(constant));
                return "";
            }
            if (check.startsWith(constant)) {
                forfile.add(symbolMap.get(constant));
                return check.substring(constant.length());
            }
        }
        if (Character.isDigit(check.charAt(0))) {
            for (int i = 0; (pos = ++i) < check.length(); ) {
                if (!Character.isDigit(check.charAt(i))) {
                    break;
                }
            }
            forfile.add(DB.CONST);
            forfile.add(Integer.parseInt(check.substring(0, pos)));
            return check.substring(pos);
        }
        if (Character.isLetter(check.charAt(0))) {
            for (int i = 0; (pos = ++i) < check.length(); ) {
                if (!Character.isLetter(check.charAt(i))) {
                    break;
                }
            }
            forfile.add(DB.ID);
            String name = check.substring(0, pos);
            if (!table.contains(name)) {
                table.add(name);
            }
            forfile.add(table.indexOf(name));
            return check.substring(pos);
        }
        throw new IllegalStateException("Ошибка лексического анализа: \nнеизвестный символ " + "'" + check.charAt(0) + "'");
    }
    public String analyze() throws IOException {
        ArrayList<Integer> forfile = new ArrayList<>();
        ArrayList<String> tableforfile = new ArrayList<>();

        for (String stroka : source) {
            String[] space = stroka.split("\\s+");
            for (String slovo : space) {
                while (slovo != null && !slovo.isEmpty()) {
                    slovo = nextword(slovo, forfile, tableforfile);
                }
            }
        }

        String xi = Utils.joinInt(" ", forfile);
        String mi = Utils.joinStr(" ", tableforfile);

        Utils.writeFile(file + ".lex", xi, mi);

        return file + ".lex";
    }
    public String getSource() {
        return Utils.joinStr("\n", source);
    }
}

