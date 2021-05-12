package translator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import translator.Syntax.*;

public class CodeGenerator {

    private static final char SPACE = ' ';
    private static final char LINE = '\n';

    static final class PASCAL {
        static final String PROGRAM = "program";
        static final String VAR = "var";
        static final String BEGIN = "begin";
        static final String REAL = "double";
        static final String WHOLE = "integer";
        static final String INPUT = "read";
        static final String OUTPUT = "write";
        static final String SIN = "Sin";
        static final String COS = "Cos";
        static final String IF = "if";
        static final String RAVEN = "=";
        static final String THEN = "then";
        static final String END = "end";
        static final String DOT = ".";
        static final String SEMICOLON = ";";
        static final String COLON = ":";
        static final String PRODUCT = "*";
        static final String DIV = "/";
        static final String COMMA = ",";
        static final String EQUAL = ":=";
        static final String LBKT = "(";
        static final String RBKT = ")";
    }

    private ArrayList<String> vars;
    private StringBuilder sb;

    public CodeGenerator() {
        sb = new StringBuilder();
    }

    public String generate(String source) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(source));
        Program program = (Program) is.readObject();
        vars = (ArrayList<String>) is.readObject();
        is.close();
        generateProgram(program);
        String result = source.replace(".syn", ".pas");
        Utils.writeFile(result, sb.toString());
        return result;
    }

    private void generateProgram(Program program) {
        sb.append(PASCAL.PROGRAM).append(SPACE)
                .append(vars.get(program.getName().getVarId()))
                .append(PASCAL.SEMICOLON)
                .append(LINE);

        generateVars(program.getVars());
        generateOpList(program.getOperations());
        sb.append(PASCAL.END).append(PASCAL.DOT);
    }

    private void generateVars(ArrayList<NewVars> newVars) {
        sb.append(PASCAL.VAR).append(SPACE);
        for (NewVars nv : newVars) {
            ArrayList<String> vl = new ArrayList<>();
            for (Id id : nv.getVars()) vl.add(vars.get(id.getVarId()));
            sb.append(Utils.joinStr(PASCAL.COMMA, vl)).append(PASCAL.COLON);
            switch (nv.getType()) {
                case DB.WHOLE:
                    sb.append(SPACE);
                    sb.append(PASCAL.WHOLE).append(PASCAL.SEMICOLON);
                    sb.append(LINE);
                    break;
                case DB.REAL:
                    sb.append(SPACE);
                    sb.append(PASCAL.REAL).append(PASCAL.SEMICOLON);
                    sb.append(LINE);
                    break;
            }
        }
        sb.append(PASCAL.BEGIN).append(LINE);
    }

    private void generateOpList(ArrayList<Operator> operations) {
        for (Operator op : operations) {
            if (op instanceof Assignment) {
                generateAssignment(op);
                sb.append(LINE);
            }
            else if (op instanceof IO) {
                generateIO(op);
                sb.append(LINE);
            }
            else if (op instanceof IF) {
                generateIF(op);
            }
            else if (op instanceof END)
                break;

        }
    }

    private void generateIF(Operator op) {
        IF usl = (IF) op;
        ArrayList<Mult> mults = usl.getMultList();
        ArrayList<Operator> opers = usl.getOpList();
        for(int i = 0; i<mults.size();i++) {
            sb.append(PASCAL.IF)
                    .append(SPACE)
                    .append(vars.get(usl.getVar().getVarId()))
                    .append(PASCAL.RAVEN);
            generateMultiplier(mults.get(i));
            sb.append(SPACE).append(PASCAL.THEN).append(SPACE);
            generateAssignment(opers.get(i));
            sb.append(LINE);
        }
    }

    private void generateAssignment(Operator op) {
        Assignment statement = (Assignment) op;
        sb.append(vars.get(statement.getVar().getVarId()));
        sb.append(PASCAL.EQUAL);
        generateExpression(statement.getExpr());
        sb.append(PASCAL.SEMICOLON);
    }

    private void generateIO(Operator op) {
        IO io = (IO) op;
        switch (io.getOp()) {
            case DB.INPUT:
                sb.append(PASCAL.INPUT);
                sb.append(PASCAL.LBKT);
                sb.append(vars.get(io.getVar().getVarId()));
                sb.append(PASCAL.COMMA).append(SPACE);
                sb.append(vars.get(io.getVar2().getVarId()));
                sb.append(PASCAL.RBKT);
                sb.append(PASCAL.SEMICOLON);
                break;
            case DB.OUTPUT:
                sb.append(PASCAL.OUTPUT);
                sb.append(PASCAL.LBKT);
                sb.append(vars.get(io.getVar().getVarId()));
                sb.append(PASCAL.RBKT);
                sb.append(PASCAL.SEMICOLON);
                break;
        }
    }


    private void generateExpression(Expr expr) {
        generateSum(expr.getLeft());
        if (expr.getRight() == null) return;
        switch (expr.getOp()) {
            case DB.PRODUCT:
                sb.append(PASCAL.PRODUCT);
                break;
            case DB.DIV:
                sb.append(PASCAL.DIV);
                break;
        }
        generateExpression(expr.getRight());
    }

    private void generateSum(Sum sum) {
        generateMultiplier(sum.getLeft());
        if (sum.getRight() == null) return;
        switch (sum.getOp()) {
            case DB.PRODUCT:
                sb.append(PASCAL.PRODUCT);
                break;
            case DB.DIV:
                sb.append(PASCAL.DIV);
                break;
        }
        generateSum(sum.getRight());
    }

    private void generateMultiplier(Mult expr) {
        if (expr instanceof Id)
            sb.append(vars.get(((Id) expr).getVarId()));
        else if (expr instanceof Const)
            sb.append(((Const) expr).getValue());
        else if (expr instanceof Expr) {
            sb.append(PASCAL.LBKT);
            generateExpression((Expr) expr);
            sb.append(PASCAL.RBKT);
        } else if (expr instanceof Sin) {
            sb.append(PASCAL.SIN);
            sb.append(PASCAL.LBKT);
            sb.append(vars.get(((Sin) expr).getVar().getVarId()));
            sb.append(PASCAL.RBKT);
        } else if (expr instanceof Cos) {
            sb.append(PASCAL.COS);
            sb.append(PASCAL.LBKT);
            sb.append(vars.get(((Cos) expr).getVar().getVarId()));
            sb.append(PASCAL.RBKT);
        }

    }

    public String getOutput() {
        return sb.toString();
    }
}
