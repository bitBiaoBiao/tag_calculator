package com.mip.roaring.tagcalc.velocity.utils;

import java.util.Stack;

/**
 * @Author ZhengWenbiao
 * @Date 2019/6/1 13:51
 **/
public class Infix2SuffixCalculator {

    /**
     * 中缀转后缀表达式，适配 '|'和'&'操作符,为同一优先级
     * ((1|2)&3)|(4&5)&6
     *
     * @param input
     * @return
     */
    public static final String infixToSuffix(String input) {

        /**
         * 在输入 input 前后添加操作符，保证先处理 符号 而不是数字
         * 例如："19|18"  ---》  "1918,|,"
         * 加了括号19,18就会分隔开 "19|18"  ---》  "19,18,|,,"
         */
        char start = input.charAt(0);
        if (start >= 48 && start <= 57) {
            StringBuilder sb = new StringBuilder(input);
            sb.insert(0, '(');
            sb.append(')');
            input = sb.toString();
        }
        char[] chars = input.toCharArray();
        //操作符栈
        Stack<Character> inputChars = new Stack<>();
        //结果后缀表达式串
        String suffix = "";
        for (Character c : chars) {
            char temp;
            /**
             * 对中缀表达式的每一个字符并进行判断 加入','字符的目的是为了分隔出每个需要计算的数字
             * 思想挺简单，因为数字之间总是由 逻辑符号 隔开，所以只需在inputChars入栈时加入 ',' 分隔符即可
             */
            switch (c) {
                case '(':                                       // 如果是左括号直接压入堆栈
                    inputChars.push(c);
                    inputChars.push(',');
                    break;
                case '&':                                       // 碰到'|' '&'，将栈中的所有运算符全部弹出去
                case '|':                                       // 直至碰到左括号为止，输出到队列中去
                    while (!inputChars.isEmpty()) {
                        temp = inputChars.pop();
                        if (temp == '(') {
                            inputChars.push('(');
                            inputChars.push(',');
                            break;
                        }
                        suffix += temp;
                    }
                    inputChars.push(',');                  //第一次进入，直接入栈
                    inputChars.push(c);
                    inputChars.push(',');
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
        String[] chars = exp.split(",");
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
        if (s.matches(REGEX_TIMESCALE) &&
                (countNumber(s, '(') == countNumber(s, ')')) &&
                (countNumber(s, '(') != -1))
            return s;
        return null;
    }

    public static int countNumber(String string, char s) {
        if (string == null || string.isEmpty())
            return -1;
        int count = 0;
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (c == s)
                count++;
        }
        return count;
    }

//    public static void main(String[] args) {
//        String s = "((1|2)&3)|(4&5)&6";
//        System.out.println(calculatorSuffix(infixToSuffix(s)));
//    }
}
