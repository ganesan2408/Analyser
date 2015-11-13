package com.yhh.androidutils;

import java.util.Arrays;
import java.util.List;

/**
 * Array Utils
 *
 * Created by yuanhh1 on 2015/9/4.
 */
public class ArrayUtils {
    private ArrayUtils(){}

    /**
     * 判断数组的长度是否为0
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] array){
        return array == null || array.length == 0;
    }

    /**
     * 将数组 转换为List
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(T[] array){
        if(isEmpty(array)){
            return null;
        }
        return Arrays.asList(array);
    }

    /**
     * 将List 转换为 数组。
     *
     * 无法 new T[]{}，所以此次无法采用泛型了
     *
     * @param list
     * @param <T>
     * @return
     */
    public static String[] fromList(List<String> list){
        return list == null? null : list.toArray(new String[list.size()]);
    }
}
