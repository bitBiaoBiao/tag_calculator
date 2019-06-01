package com.mip.roaring.tagcalc.velocity.utils;

import java.util.Stack;

/**
 * @Author ZhengWenbiao
 * @Date 2019/6/1 13:51
 **/
public class Infix2SuffixCalculator {

    /**
     * 中缀转后缀表达式，适配 '|'和'&'操作符,为同一优先级
     *
     * @param input
     * @return
     */
    public static final String infixToSuffix(String input) {

        char[] chars = input.toCharArray();
        //操作符栈
        Stack<Character> inputChars = new Stack<>();
        //结果后缀表达式串
        String suffix = "";
        for (Character c : chars) {
            char temp;
            //对中缀表达式的每一个字符并进行判断
            switch (c) {
                case '(':                                       // 如果是左括号直接压入堆栈
                    inputChars.push(c);
                    break;
                case '&':                                       // 碰到'|' '&'，将栈中的所有运算符全部弹出去
                case '|':                                       // 直至碰到左括号为止，输出到队列中去
                    while (!inputChars.isEmpty()) {
                        temp = inputChars.pop();
                        if (temp == '(') {
                            inputChars.push('(');
                            break;
                        }
                        suffix += temp;
                    }
                    inputChars.push(c);                          //第一次进入，直接入栈
                    break;
                case ')':
                    while (!inputChars.isEmpty()) {
                        temp = inputChars.pop();
                        if (temp == '(')
                            break;
                        suffix += temp;
                    }
                    break;
                default:                                         //都是数字
                    suffix += c;
                    break;
            }
        }
        while (!inputChars.isEmpty())
            suffix += inputChars.pop();
        return suffix;
    }

    /**
     * 计算后缀表达式
     *
     * @param exp
     */
    public static final Integer calculatorSuffix(String exp) {
        String[] chars = exp.split("");
        Stack<Integer> stack = new Stack<>();
        for (String c : chars) {
            switch (c) {
                case "&":
                case "|":
                    Integer second = stack.pop();
                    Integer first = stack.pop();
                    stack.push(calculator(first, second, c));
                    break;
                default:
                    stack.push(Integer.parseInt(c));
                    break;
            }
        }
        return stack.pop();
    }

    public static final Integer calculator(Integer first, Integer second, String operator) {
        if (operator.equals("|"))
            return first | second;
        if (operator.equals("&"))
            return first & second;
        return -1;
    }

    public static String regexNumber(String s) {
        s = s.replaceAll(" ", "");
        String REGEX_TIMESCALE = "^[|()&1234567890]+$";
        if (s.matches(REGEX_TIMESCALE))
            return s;
        return null;
    }

//    public static void main(String[] args) {
//        String s = "((1|2)&3)|(4&5)&6";
//        System.out.println(calculatorSuffix(infixToSuffix(s)));
//    }
}
