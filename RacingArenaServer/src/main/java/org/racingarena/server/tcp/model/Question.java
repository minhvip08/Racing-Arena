package org.racingarena.server.tcp.model;

import java.time.Instant;

public class Question {
    private Character[] operators = {'+', '-', '*', '/', '%'};
    private Character operator;
    private Integer[] operands;
    private Integer result;
    private Instant timestamp;
    private String question;

    public Question() {
        this.operands = new Integer[2];
        this.timestamp = Instant.now();
        this.operator = operators[(int) (Math.random() * operators.length)];
        this.operands[0] = (int) (Math.random() * 10000);
        this.operands[1] = (int) (Math.random() * 10000);
//        Check if the second operand is 0 for division and modulo
        if (this.operator == '/' || this.operator == '%') {
            while (this.operands[1] == 0) {
                this.operands[1] = (int) (Math.random() * 10000);
            }

        }
        this.setResult();
        this.question = this.operands[0] + " " + this.operator + " " + this.operands[1];



    }

    public int getAnswer() {
        return this.result;

    }

    private void setResult() {
        switch (this.operator) {
            case '+':
                this.result = this.operands[0] + this.operands[1];
                break;
            case '-':
                this.result = this.operands[0] - this.operands[1];
                break;
            case '*':
                this.result = this.operands[0] * this.operands[1];
                break;
            case '/':
                this.result = this.operands[0] / this.operands[1];
                break;
            case '%':
                this.result = this.operands[0] % this.operands[1];
                break;
        }
    }

    public String getQuestion() {
        return this.question;
    }
}
