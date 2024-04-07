package org.racingarena.client.object;

import java.util.Random;

public class Calculation {
    private Random random;
    private int minInt;
    private int maxInt;
    private int num1;
    private int num2;
    private double answer;
    private char[] operators;
    private char operator;

    public Calculation() {
        random = new Random();
        minInt = -10000;
        maxInt = 10000;
        operators = new char[]{'+', '-', 'x', '/', '%'};
    }

    public void newCalculation() {
        num1 = random.nextInt(maxInt - minInt + 1) + minInt;
        num2 = random.nextInt(maxInt - minInt + 1) + minInt;
        operator = operators[random.nextInt(operators.length)];
        if ((operator == '%' || operator == '/') && num2 == 0) newCalculation();
        else calculateAnswer();
    }

    private void  calculateAnswer() {
        switch (operator) {
            case '+':
                answer =num1 + num2;
            case '-':
                answer = num1 - num2;
            case '*':
                answer = num1 * num2;
            case '/':
                answer = (double) num1 / num2;
            case '%':
                answer = num1 % num2;
            default:
                answer = -1;
        };
    }

    public boolean checkAnswer(double answer){
        return answer == this.answer;
    }

    public String getCalculation(){
        return num1 + " " + operator + " " + num2 + " = ";
    }
}
