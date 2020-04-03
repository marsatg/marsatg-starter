package org.marsatg.boot;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        java.lang.reflect.Method mx = Main.class.getDeclaredMethod("ss", int.class);
        Object invoke = mx.invoke(new Main(), new Integer(1));
        System.out.println(invoke);
        System.out.println();
       /* BigDecimal bigDecimal = new BigDecimal(323.5);
        Double parse = JSON.parseObject(bigDecimal.toString(), double.class);
        System.out.println();*/
    }


    public void ss(int s) {
        System.out.println(s);
    }


}