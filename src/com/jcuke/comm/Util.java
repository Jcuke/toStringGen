package com.jcuke.comm;

/**
 * Created by cuke on 2018/6/16.
 */
public class Util {

    /**
     * 这个代码无法植入到用户的环境中，只能让用户调用公共库来做这个转换
     * @param arr
     * @return
     */
    public static final String array2String(Object[] arr){
        if(arr == null || arr.length == 0){
            return "";
        }
        String printValue = "";
        for (Object s : arr) {
            printValue += "," + "\"" +s +"\"";
        }
        if(printValue.startsWith(",")){
            printValue = printValue.substring(1);
        }
        printValue = "["+ printValue +"]";
        return null;
    }
}
