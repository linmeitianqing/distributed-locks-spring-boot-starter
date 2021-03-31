package com.bianjf.utils;

public class StringUtil {
    /**
     * 判断是否为非空
     * @param cs 字符序列
     * @return true:空 false:非空
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为空
     * @param cs 字符序列
     * @return true:非空 false:空
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 判断是否为非空
     * @param cs 字符序列
     * @return true:空 false:非空
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 是否为空
     * @param cs 字符序列
     * @return true:非空 false:空
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
}
