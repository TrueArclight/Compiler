package translator;

import java.io.Serializable;
import java.util.ArrayList;

public class Syntax {

    interface Operator {
    }

    interface Mult {
    }

    static class Program implements Serializable {
        private Id name;
        private ArrayList<NewVars> vars;
        private ArrayList<Operator> operations;

        Program(Id name, ArrayList<NewVars> vars, ArrayList<Operator> operations) {
            this.name = name;
            this.vars = vars;
            this.operations = operations;
        }

        public Id getName() {
            return name;
        }

        public ArrayList<NewVars> getVars() {
            return vars;
        }

        public ArrayList<Operator> getOperations() {
            return operations;
        }
    }

    static class NewVars implements Serializable {
        private int type;
        private ArrayList<Id> vars;

        NewVars(ArrayList<Id> vars, int type) {
            this.vars = vars;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public ArrayList<Id> getVars() {
            return vars;
        }
    }

    static class Assignment implements Operator, Serializable {
        private Id var;
        private Expr expr;

        Assignment(Id target, Expr expr) {
            this.var = target;
            this.expr = expr;
        }

        public Id getVar() {
            return var;
        }

        public Expr getExpr() {
            return expr;
        }
    }
    static class Expr implements Mult, Serializable {
        private Sum left;
        private Expr right;
        private int op;

        Expr(Sum left, Expr right, int operation) {
            this.left = left;
            this.right = right;
            this.op = operation;
        }

        public Sum getLeft() {
            return left;
        }

        public Expr getRight() {
            return right;
        }

        public int getOp() {
            return op;
        }
    }

    static class Sum implements Serializable {
        private Mult left;
        private Sum right;
        private int op;

        Sum(Mult left, Sum right, int operation) {
            this.left = left;
            this.right = right;
            this.op = operation;
        }

        public Mult getLeft() {
            return left;
        }

        public Sum getRight() {
            return right;
        }

        public int getOp() {
            return op;
        }
    }
    static class Id implements Mult, Serializable {
        private int varId;

        public int getVarId() {
            return varId;
        }

        Id(int varId) {
            this.varId = varId;
        }
    }
    static class Const implements Mult, Serializable {
        private int value;

        Const(Integer value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    static class IO implements Operator, Serializable {
        private int op;
        private Id var;
        private Id var2;

        IO(int op, Id var) {
            this.op = op;
            this.var = var;
        }
        IO(int op, Id var,Id var2) {
            this.op = op;
            this.var = var;
            this.var2 = var2;
        }

        public int getOp() {
            return op;
        }

        public Id getVar() {
            return var;
        }

        public Id getVar2() {
            return var2;
        }
    }
    static class Sin implements Mult, Serializable {
        private int op;
        private Id var;

        Sin(int op, Id var) {
            this.op = op;
            this.var = var;
        }

        public int getOp() {
            return op;
        }

        public Id getVar() {
            return var;
        }
    }
    static class Cos implements Mult, Serializable {
        private int op;
        private Id var;

        Cos(int op, Id var) {
            this.op = op;
            this.var = var;
        }

        public int getOp() {
            return op;
        }

        public Id getVar() {
            return var;
        }
    }
    static class IF implements Operator, Serializable{
        private Id var;
        private ArrayList<Mult> multList;
        private ArrayList<Operator> opList;

        public IF(Id var, ArrayList<Mult> multList, ArrayList<Operator> opList) {
            this.var = var;
            this.multList = multList;
            this.opList = opList;
        }

        public Id getVar() {
            return var;
        }
        public ArrayList<Mult> getMultList() {
            return multList;
        }

        public ArrayList<Operator> getOpList() {
            return opList;
        }
    }
    static  class END implements Operator,Serializable{
        private int op;

        public int getOp() {
            return op;
        }

        public  END(int op){
            this.op = op;
        }
    }
}
