package com.jcuke.tostringGen;

/**
 * Created by cuke on 2018/6/16.
 */
public class Util {

    /**
     * ��������޷�ֲ�뵽�û��Ļ����У�ֻ�����û����ù������������ת��
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
