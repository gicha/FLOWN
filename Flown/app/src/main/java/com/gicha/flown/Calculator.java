package com.gicha.flown;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by MSI1 on 02.06.2016.
 */
public class Calculator {  //этот класс обеспечивает работу нашего клевого калькулятора))

    public static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {
        MAIN_MATH_OPERATIONS = new HashMap<String, Integer>();
        MAIN_MATH_OPERATIONS.put("*", 1);
        MAIN_MATH_OPERATIONS.put("/", 1);
        MAIN_MATH_OPERATIONS.put("+", 2);
        MAIN_MATH_OPERATIONS.put("-", 2);
    }

    public static String sortingStation(String expression, Map<String, Integer> operations, String leftBracket,
                                        String rightBracket) {
        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");
        List<String> out = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();

        expression = expression.replace(" ", "");

        Set<String> operationSymbols = new HashSet<String>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        int index = 0;
        boolean findNext = true;
        while (findNext) {
            int nextOperationIndex = expression.length();
            String nextOperation = "";
            for (String operation : operationSymbols) {
                int i = expression.indexOf(operation, index);
                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }
            }
            if (nextOperationIndex == expression.length()) {
                findNext = false;
            } else {
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }
                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                } else if (nextOperation.equals(rightBracket)) {
                    while (!stack.peek().equals(leftBracket)) {
                        out.add(stack.pop());
                        if (stack.empty()) {
                            throw new IllegalArgumentException("Unmatched brackets");
                        }
                    }
                    stack.pop();
                } else {
                    while (!stack.empty() && !stack.peek().equals(leftBracket) &&
                            (operations.get(nextOperation) >= operations.get(stack.peek()))) {
                        out.add(stack.pop());
                    }
                    stack.push(nextOperation);
                }
                index = nextOperationIndex + nextOperation.length();
            }
        }
        if (index != expression.length()) {
            out.add(expression.substring(index));
        }
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        StringBuffer result = new StringBuffer();
        if (!out.isEmpty())
            result.append(out.remove(0));
        while (!out.isEmpty())
            result.append(" ").append(out.remove(0));

        return result.toString();
    }

    public static String sortingStation(String expression, Map<String, Integer> operations) {
        return sortingStation(expression, operations, "(", ")");
    }

    public static String calculateExpression(String expression) {
        try {
            String rpn = sortingStation(expression, MAIN_MATH_OPERATIONS);
            StringTokenizer tokenizer = new StringTokenizer(rpn, " ");
            Stack<BigDecimal> stack = new Stack<BigDecimal>();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();

                if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {
                    stack.push(new BigDecimal(token));
                } else {
                    BigDecimal operand2 = stack.pop();
                    BigDecimal operand1 = stack.empty() ? BigDecimal.ZERO : stack.pop();
                    if (token.equals("*")) {
                        stack.push(operand1.multiply(operand2));
                    } else if (token.equals("/")) {
                        stack.push(operand1.divide(operand2));
                    } else if (token.equals("+")) {
                        stack.push(operand1.add(operand2));
                    } else if (token.equals("-")) {
                        stack.push(operand1.subtract(operand2));
                    }
                }
            }
            if (stack.size() != 1)
                throw new IllegalArgumentException("Expression syntax error.");
            return stack.pop() + "";
        } catch (Exception e) {
            Log.e("Calc", e + "");
        }
        return "Неправильный ввод";
    }

    public static void startCalc(String s) throws Exception {
        Log.e("Инфиксная нотация", s);
        String rpn = sortingStation(s, MAIN_MATH_OPERATIONS);
        Log.e("польская нотация", rpn);
        Log.e("Результат", calculateExpression(s) + "");
        String x = calculateExpression(s);
        if (x.equals("Неправильный ввод")) {
            Core.getInstance().addMyMessage("Что-то пошло не так\nНо вы там держитесь, добра вам и хорошего настроения");
            return;
        }
        Core.getInstance().addMyMessage("На, держи!\n" + x);

    }

}
