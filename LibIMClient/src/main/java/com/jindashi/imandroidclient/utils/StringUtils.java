/*
 * Copyright (C) 2012 www.amsoft.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jindashi.imandroidclient.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XJ
 */
public class StringUtils {

    /**
     * 描述：将null转化为“”.
     *
     * @param str 指定的字符串
     * @return 字符串的String类型
     */
    public static String parseEmpty(String str) {
        if (str == null || "null".equals(str.trim())) {
            str = "";
        }
        return str.trim();
    }

    /**
     * 描述：判断一个字符串是否为null或空值.
     *
     * @param str 指定的字符串
     * @return true or false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equals("null") || str.trim().length() == 0;
    }

    /**
     * 获取字符串中文字符的长度（每个中文算2个字符）.
     *
     * @param str 指定的字符串
     * @return 中文字符的长度
     */
    public static int chineseLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        if (!isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                /* 获取一个字符 */
                String temp = str.substring(i, i + 1);
                /* 判断是否为中文字符 */
                if (temp.matches(chinese)) {
                    valueLength += 2;
                }
            }
        }
        return valueLength;
    }

    /**
     * 描述：获取字符串的长度.
     *
     * @param str 指定的字符串
     * @return 字符串的长度（中文字符计2个）
     */
    public static int strLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                    //中文字符长度为2
                    valueLength += 2;
                } else {
                    //其他字符长度为1
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    /**
     * 描述：获取指定长度的字符所在位置.
     *
     * @param str  指定的字符串
     * @param maxL 要取到的长度（字符长度，中文字符计2个）
     * @return 字符的所在位置
     */
    public static int subStringLength(String str, int maxL) {
        int currentIndex = 0;
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < str.length(); i++) {
            //获取一个字符
            String temp = str.substring(i, i + 1);
            //判断是否为中文字符
            if (temp.matches(chinese)) {
                //中文字符长度为2
                valueLength += 2;
            } else {
                //其他字符长度为1
                valueLength += 1;
            }
            if (valueLength >= maxL) {
                currentIndex = i;
                break;
            }
        }
        return currentIndex;
    }

    /**
     * 描述：手机号格式验证.
     *
     * @param str 指定的手机号码字符串
     * @return 是否为手机号码格式:是为true，否则false
     */
    public static Boolean isMobileNo(String str) {
        Boolean isMobileNo = false;
        try {
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Matcher m = p.matcher(str);
            isMobileNo = m.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMobileNo;
    }

    /**
     * 描述：是否只是字母和数字.
     *
     * @param str 指定的字符串
     * @return 是否只是字母和数字:是为true，否则false
     */
    public static Boolean isNumberLetter(String str) {
        Boolean isNoLetter = false;
        String expr = "^[A-Za-z0-9]+$";
        if (str.matches(expr)) {
            isNoLetter = true;
        }
        return isNoLetter;
    }

    /**
     * 描述：是否只是数字.
     *
     * @param str 指定的字符串
     * @return 是否只是数字:是为true，否则false
     */
    public static Boolean isNumber(String str) {
        Boolean isNumber = false;
        String expr = "^[0-9]+$";
        if (str.matches(expr)) {
            isNumber = true;
        }
        return isNumber;
    }


    /**
     * 交易密码校验规则：密码必须包括数字、字符、符号
     *
     * @param str
     * @return 0:校验通过;
     * 1:纯数字;
     * 2:纯字符;
     * 3:纯字母;
     * 4:纯字母大写;
     * 5:纯字母小写;
     * 6：密码只有(数字、字母、字符)中的两种组合
     * <p>
     * a.长度大于等于6位小于等于20位
     * b.密码不能为纯数字
     * c.密码不能为纯大写字母
     * d.密码不能为纯小写字母
     * e.密码不能为纯符号
     * f.密码不能包含空格
     */
    public static int passwordCheck(String str) {

        String expr_correct = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!\\s+$)(?![0-9.*&%\\!@#$,~-]+$)(?![a-zA-Z.*&%\\!@#$,~-]+$)[\\w.*&%\\!@#$,~-]{6,20}$";//正确的校验
        String expr_isNumber = "^[0-9]+$";//纯数字
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？' ']";//纯特殊符号
        String expr_isLetter = "^[A-Za-z]+$";//纯字母
        String expr_isLetter_dx = "^[A-Z]+$";//纯字母大写
        String expr_isLetter_xx = "^[a-z]+$";//纯字母小写

        if (str.matches(expr_isNumber)) {//纯数字
            return 1;
        } else if (str.matches(regEx)) {//纯特殊符号
            return 2;
        } else if (str.matches(expr_isLetter)) {//纯字母
            return 3;
        } else if (str.matches(expr_isLetter_dx)) {//纯字母大写
            return 4;
        } else if (str.matches(expr_isLetter_xx)) {//纯字母小写
            return 5;
        } else if (str.matches(expr_correct)) {//校验通过
            return 0;
        }
        return 6;
    }


    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    /**
     * 描述：是否是中文.
     *
     * @param str 指定的字符串
     * @return 是否是中文:是为true，否则false
     */
    public static Boolean isChinese(String str) {
        Boolean isChinese = true;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                } else {
                    isChinese = false;
                }
            }
        }
        return isChinese;
    }

    /**
     * 描述：是否包含中文.
     *
     * @param str 指定的字符串
     * @return 是否包含中文:是为true，否则false
     */
    public static Boolean isContainChinese(String str) {
        Boolean isChinese = false;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                    isChinese = true;
                } else {

                }
            }
        }
        return isChinese;
    }

    /**
     * 描述：从输入流中获得String.
     *
     * @param is 输入流
     * @return 获得的String
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            //最后一个\n删除
            if (sb.indexOf("\n") != -1 && sb.lastIndexOf("\n") == sb.length() - 1) {
                sb.delete(sb.lastIndexOf("\n"), sb.lastIndexOf("\n") + 1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 描述：标准化日期时间类型的数据，不足两位的补0.
     *
     * @param dateTime 预格式的时间字符串，如:2012-3-2 12:2:20
     * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
     */
    public static String dateTimeFormat(String dateTime) {
        StringBuilder sb = new StringBuilder();
        try {
            if (isEmpty(dateTime)) {
                return null;
            }
            String[] dateAndTime = dateTime.split(" ");
            if (dateAndTime.length > 0) {
                for (String str : dateAndTime) {
                    if (str.contains("-")) {
                        String[] date = str.split("-");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append("-");
                            }
                        }
                    } else if (str.contains(":")) {
                        sb.append(" ");
                        String[] date = str.split(":");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append(":");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    /**
     * 描述：不足2个字符的在前面补“0”.
     *
     * @param str 指定的字符串
     * @return 至少2个字符的字符串
     */
    public static String strFormat2(String str) {
        try {
            if (str.length() <= 1) {
                str = "0" + str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 描述：截取字符串到指定字节长度.
     *
     * @param str    the str
     * @param length 指定字节长度
     * @return 截取后的字符串
     */
    public static String cutString(String str, int length) {
        return cutString(str, length, "");
    }

    /**
     * 描述：截取字符串到指定字节长度.
     *
     * @param str    文本
     * @param length 字节长度
     * @param dot    省略符号
     * @return 截取后的字符串
     */
    public static String cutString(String str, int length, String dot) {
        int strBLen = strlen(str, "GBK");
        if (strBLen <= length) {
            return str;
        }
        int temp = 0;
        StringBuffer stringBuffer = new StringBuffer(length);
        char[] ch = str.toCharArray();
        for (char c : ch) {
            stringBuffer.append(c);
            if (c > 256) {
                temp += 2;
            } else {
                temp += 1;
            }
            if (temp >= length) {
                if (dot != null) {
                    stringBuffer.append(dot);
                }
                break;
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 描述：截取字符串从第一个指定字符.
     *
     * @param str1   原文本
     * @param str2   指定字符
     * @param offset 偏移的索引
     * @return 截取后的字符串
     */
    public static String cutStringFromChar(String str1, String str2, int offset) {
        if (isEmpty(str1)) {
            return "";
        }
        int start = str1.indexOf(str2);
        if (start != -1) {
            if (str1.length() > start + offset) {
                return str1.substring(start + offset);
            }
        }
        return "";
    }

    /**
     * 描述：获取字节长度.
     *
     * @param str     文本
     * @param charset 字符集（GBK）
     * @return the int
     */
    public static int strlen(String str, String charset) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int length = 0;
        try {
            length = str.getBytes(charset).length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    /**
     * 获取大小的描述.
     *
     * @param size 字节个数
     * @return 大小的描述
     */
    public static String getSizeDesc(long size) {
        String suffix = "B";
        if (size >= 1024) {
            suffix = "K";
            size = size >> 10;
            if (size >= 1024) {
                suffix = "M";
                //size /= 1024;
                size = size >> 10;
                if (size >= 1024) {
                    suffix = "G";
                    size = size >> 10;
                    //size /= 1024;
                }
            }
        }
        return size + suffix;
    }

    /**
     * 描述：ip地址转换为10进制数.
     *
     * @param ip the ip
     * @return the long
     */
    public static long ip2int(String ip) {
        ip = ip.replace(".", ",");
        String[] items = ip.split(",");
        return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        System.out.println(dateTimeFormat("2012-3-2 12:2:20"));
    }

    public static boolean isMobileNO(Context context, String mobiles) {
        if (mobiles.indexOf("1") != 0) {
            Toast.makeText(context, "电话号码第一位必须为1", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mobiles.length() != 11) {
            Toast.makeText(context, "您输入的手机号码位数不对", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 身份证号码验证
    public static boolean isIDNumber(String id_number) {
        if (id_number.length() == 18) {
            return true;
        }
        return false;
    }

    /**
     * 替换成***
     */
    public static String replaceStar(String text, int start, int end) {
        String stars = "";
        int count = end - start;
        for (int i = 0; i < count; i++) {
            stars += "*";
        }
        if (text != null && !"".equals(text)) {
            String sub = text.substring(start, end);
            text = text.replace(sub, stars);
            return text;
        }
        return "";
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 字符串转double
     *
     * @return
     */
    public static double strToDouble(String str) {
        return Double.parseDouble(str);
    }

    /**
     * 字符串转double
     *
     * @return
     */
    public static float strToFloat(String str) {
        return Float.valueOf(str);
    }


    /**
     * 截取小数点后多少位
     *
     * @return
     */
    public static String subDotNum(String text, int num) {
        int dotIndex = text.indexOf(".");
        return text.substring(0, dotIndex + num + 1);
    }

    /**
     * 截取小数点后多少位
     *
     * @return
     */
    public static String subDotNumNoCludeDot(String text, int num) {
        int dotIndex = text.indexOf(".");
        return text.substring(0, dotIndex + num);
    }


    /**
     * 关键字高亮
     */
    public static void heighShow(String text, TextView textView, int start, int end, int restart, int reend) {
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new BackgroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new BackgroundColorSpan(Color.RED), restart, reend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
    }

    // 2014-04-06
    public static String getShortDate(String date) {
        return date.substring(date.indexOf("-") + 1);
    }


    /**
     * 判断密码强度 0 为弱 1为中等强度 2 为强
     *
     * @param passwordStr
     * @return \d+匹配最小长度为1的、由数字组成的字符串。 [a-zA-Z]+匹配最小长度为1的、由字母组成的字符串。
     * [-`=\\\[\];',./~!@#$%^&*()_+|{}:"<>?]+匹配最小长度为1的、由特殊字符组成的字符串。
     * \d+[a-zA-Z]+[-`=\\\[\];',./~!@#$%^&*()_+|{}:
     * "<>?]+匹配型如“数字＋字母＋特殊字符”类型的字符串。
     * (\d+[-`=\\\[\];',./~!@#$%^&*()_+|{}:
     * "<>?]+[a-zA-Z]+)匹配型如“数字＋特殊字符＋字母”类型的字符串。
     * [a-zA-Z]+\d+[-`=\\\[\];',./~!@#$%^&
     * *()_+|{}:"<>?]+匹配型如“字母＋数字＋特殊字符”类型的字符串。 *
     * [a-zA-Z]+[-`=\\\[\];',./~!@#$%^&
     * *()_+|{}:"<>?]+\d+匹配型如“字母＋特殊字符＋数字”类型的字符串。
     * [-`=\\\[\];',./~!@#$%^&*
     * ()_+|{}:"<>?]+\d+[a-zA-Z]+匹配型如“特殊字符＋数字＋字母”类型的字符串。
     * [-`=\\\[\];',./~!@#$%^&
     * *()_+|{}:"<>?]+[a-zA-Z]+\d+匹配型如“特殊字符＋字母＋数字”类型的字符串。 字符类
     * [-\da-zA-Z`=\\\[\];',./~!@#$%^&*()_+|{}:"<>?]*：\d表示任意数字；
     */
    public static int checkPasswordStrength(String passwordStr) {
        String number = "\\d+"; // 纯数字
        String letter = "[a-zA-Z]+";// 纯字母
        String character = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}‘；：”“’。，、？]+";

        int length = passwordStr.length();
        int currSocre = 0;
        if (length < 6) {
            currSocre = currSocre - 25;
            return 0;
        } else if (length >= 6 && length < 16) {
            currSocre = currSocre + 25;
        } else {
            currSocre = currSocre + 50;
        }
        // 纯数字或者纯字母
        if (passwordStr.matches(number) || passwordStr.matches(letter)
                || passwordStr.matches(character)) {
            currSocre = currSocre - 25;
        } else {
            currSocre = currSocre + 25;
        }

        if (currSocre <= 25)
            return 0;
        else if (currSocre > 25 && currSocre <= 75)
            return 1;
        else
            return 2;
    }


    /**
     * 设置一个EditText可以输入的小数的位数
     *
     * @param editText editText对象
     * @param digit    保留多少位小数
     */
    public static void setPricePoint(final EditText editText, final int digit) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > digit) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + digit + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }

                //当第一个字符为"."时，将s设置为"0."
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    //指定输入框光标位置
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * 设置一个EditText可以输入的小数的整数位数
     *
     * @param editText editText对象
     * @param digit    保留多少位小数
     */
    public static void setPricePoint_Int(final EditText editText, final int digit) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (editText.hasFocus()) {
                    if (!s.toString().contains(".")) {
                        if (s.length() > digit) {
                            s = s.toString().subSequence(0, digit);
                            editText.setText(s);
                            editText.setSelection(s.length());
                        }
                    } else {
                        int i = s.toString().indexOf(".");
                        if (i > digit) {
                            s = s.toString().substring(0, digit) + s.toString().substring(i, s.toString().length());
                            editText.setText(s);
                            editText.setSelection(s.length());
                        }

                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

    }

    /**
     * 设置一个EditText可以输入的小数的位数
     *
     * @param editText editText对象
     * @param digit    保留多少位小数
     */
    public static void setPricePoint_decimal(final EditText editText, final int digit) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > digit) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + digit + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }

                //当第一个字符为"."时，将s设置为"0."
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    //指定输入框光标位置
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * 获取隐藏手机号中间四位的字符串
     *
     * @return
     */
    public static String getMobileByHide(String mobile) {
        if (StringUtils.isEmpty(mobile) || mobile.length() < 11)
            return mobile;
        return mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
    }


}
