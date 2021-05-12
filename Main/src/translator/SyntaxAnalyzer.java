package translator;

import translator.Syntax.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SyntaxAnalyzer {

    private ArrayList<String> vars;
    private ArrayList<Integer> lexems;
    private int pos, size;

    public SyntaxAnalyzer() {
        lexems = new ArrayList<>();
        vars = new ArrayList<>();
    }

    public String analyze(String source) throws IOException {
        ArrayList<String> arraystr = Utils.readFile(source);

        vars.addAll(Arrays.asList(arraystr.get(1).split(" ")));
        for (String s : arraystr.get(0).split(" "))
            lexems.add(Integer.parseInt(s));
        this.size = lexems.size();
        this.pos = 0;

        Program program = checkProgram();
        String result = source.replace(".lex", ".syn");
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(result));
        os.writeObject(program);
        os.writeObject(vars);
        os.flush();
        os.close();

        return result;
    }

    private Program checkProgram() {
        Id name = checkHeader();
        check(DB.SEMICOLON, "\nПРОГРАММА");
        ArrayList<NewVars> vars = checkSectionVar();
        ArrayList<Operator> operations = checkSectionOp();
        check(DB.DOT, "\nПРОГРАММА");
        return new Program(name, vars, operations);
    }

    private Id checkHeader() {
        check(DB.PROGRAM, "\nЗАГОЛОВОК");
        return checkVarName("\nЗАГОЛОВОК");
    }

    private Id checkVarName(String source) {
        check(DB.ID, source);
        return new Id(next());
    }


    private ArrayList<NewVars> checkSectionVar() {  //
        check(DB.VAR, "\nРАЗДЕЛ ОПИСАНИЯ");
        return checkVarDeclaration();
    }

    private ArrayList<NewVars> checkVarDeclaration() {
        ArrayList<NewVars> varDefs = new ArrayList<>();
        ArrayList<Id> vars = checkVarList();
        check(DB.COLON, "\nОБЪЯВЛЕНИЕ ПЕРЕМЕННЫХ");
        int type = checkType();
        varDefs.add(new NewVars(vars, type));
        check(DB.SEMICOLON, "\nОБЪЯВЛЕНИЕ ПЕРЕМЕННЫХ");
        if (pickNext() == DB.ID)
            varDefs.addAll(checkVarDeclaration());
        return varDefs;
    }

    private ArrayList<Id> checkVarList() {
        ArrayList<Id> vars = new ArrayList<>();
        vars.add(checkVarName("\nСПИСОК ПЕРЕМЕННЫХ"));
        if (pickNext() == DB.COMMA) {
            next();
            vars.addAll(checkVarList());
        }

        return vars;
    }

    private int checkType() {
        switch (pickNext()) {
            case DB.WHOLE:
            case DB.REAL:
                return next();
            default:
                throw new IllegalStateException("Ошибка в определении переменных \nОЖИДАЕТСЯ ТИП");
        }
    }

    private ArrayList<Operator> checkSectionOp() {
        check(DB.BEGIN, "\nРАЗДЕЛ ОПЕРАТОРОВ");
        ArrayList<Operator> operations = checkOpList();
        check(DB.END, "\nРАЗДЕЛ ОПЕРАТОРОВ");
        return operations;
    }

    private ArrayList<Operator> checkOpList() {
        ArrayList<Operator> operations = new ArrayList<>();
        operations.add(checkOperator());
        if (pickNext() == DB.SEMICOLON) {
            next();
            operations.addAll(checkOpList());
        }
        if (pickNext() == DB.SRBKT) {
            next();
            operations.addAll(checkOpList());
        }
        return operations;
    }

    private Operator checkOperator() {
        switch (pickNext()) {
            case DB.INPUT:
                return checkIn();
            case DB.OUTPUT:
                return checkOut();
            case DB.ID:
                return checkAssign();
            case DB.IF:
                return checkIf();
            case DB.END:
                return checkEnd();
            default:
                throw new IllegalStateException("\nОшибка в определении списка операторов \nожидается оператор");
        }
    }
    private IO checkIn() {
        int op = next();
        if (op != DB.INPUT) throw new IllegalStateException("\nОшибка в определении операции ввода-вывода");
        Id var = checkVarName("\nIN");
        next();
        Id var2 = checkVarName("\nIN");
        return new IO(op, var, var2);
    }
    private IO checkOut() {
        int op = next();
        if (op != DB.OUTPUT) throw new IllegalStateException("\nОшибка в определении операции ввода-вывода");
        Id var = checkVarName("\nOUT");
        return new IO(op, var);
    }

    private Assignment checkAssign() {
        Id target = checkVarName("\nПРИСВАИВАНИЕ");
        check(DB.EQUAL, "\nПРИСВАИВАНИЕ");
        Expr expr = checkExpression();
        return new Assignment(target, expr);
    }
    private Expr checkExpression() {
        Sum left = checkSum();
        Expr expr = null;
        int operation = 0;

        int op = pickNext();
        if (op == DB.DIV || op == DB.PRODUCT) {
            operation = next();
            expr = checkExpression();
        }

        return new Expr(left, expr, operation);
    }

    private Sum checkSum() {
        Mult left = checkMultiplier();
        Sum sum = null;
        int operation = 0;

        int op = pickNext();
        if (op == DB.PRODUCT || op == DB.DIV) {
            operation = next();
            sum = checkSum();
        }
        return new Sum(left, sum, operation);
    }

    private Mult checkMultiplier() {
        Mult multiplier;
        switch (pickNext()) {
            case DB.LBKT:
                next();
                multiplier = checkExpression();
                check(DB.RBKT, "\nВЫРАЖЕНИЕ");
                break;
            case DB.ID:
                multiplier = checkVarName("\nВЫРАЖЕНИЕ");
                break;
            case DB.CONST:
                next();
                multiplier = new Const(next());
                break;
            case DB.COS:
                multiplier = checkCos();
                break;
            case DB.SIN:
                multiplier = checkSin();
                break;
            default:
                throw new IllegalStateException("\nОшибка в определении множителя");
        }
        return multiplier;
    }

    private Mult checkCos() {
        int op = next();
        if (op != DB.COS) throw new IllegalStateException("\nОшибка в определении операции косинуса");
        check(DB.LBKT,"Открывающая скобка");
        Id var = checkVarName("\nCos");
        check(DB.RBKT,"Закрывающая скобка");
        return new Cos(op, var);
    }

    private Mult checkSin() {
        int op = next();
        check(DB.LBKT,"Открывающая скобка");
        Id var = checkVarName("\nSinus");
        check(DB.RBKT,"Закрывающая скобка");
        return new Sin(op, var);
    }
    private Operator checkIf(){
        ArrayList<Operator> opList = new ArrayList<>();
        ArrayList<Mult> multList = new ArrayList<>();

        check(DB.IF,"\nЕСЛИ");
        Id var = checkVarName("\ni");
        check(DB.RAV,"\nРавен");
        if (pickNext() == DB.SLBKT) {
            if (next() != DB.SLBKT) throw new IllegalStateException("\nОшибка в условном операторе");
            if (pickNext() == DB.CONST)
                multList.add(checkMultiplier());
            check(DB.THEN, "\nТо");
            if (pickNext() == DB.ID)
                opList.add(checkAssign());
            if(pickNext() == DB.COMMA) {
                next();
                do {
                    if (pickNext() == DB.CONST)
                        multList.add(checkMultiplier());
                    check(DB.THEN, "\nТо");
                    if (pickNext() == DB.ID)
                        opList.add(checkAssign());
                    if(pickNext() == DB.COMMA)
                        next();
                } while (pickNext() != DB.SRBKT);
            }
            if (pickNext() == DB.SRBKT) {
                return new IF(var, multList, opList);
            }
        }
        return null;
    }
    private  Operator checkEnd(){
        int op = pickNext();
        return new END(op);
    }
    private Integer pickNext() {
        if (pos >= size) throw new IllegalStateException("This is the end of the story");
        return lexems.get(pos);
    }

    private Integer next() {
        if (pos >= size) throw new IllegalStateException("This is the end of the story");
        return lexems.get(pos++);
    }

    private void check(Integer expected, String operator) {
        if (next().equals(expected)) return;
        throw new IllegalStateException("Обнаружена ошибка во фрагменте:" + operator);
    }
}
