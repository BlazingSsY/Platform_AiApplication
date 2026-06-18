package com.starmol.logicreview.utils;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public class LambdaFieldNameUtil {
    public static <T> String resolveFieldName(SFunction<T, ?> function) {
        String methodName = LambdaUtils.extract(function).getImplMethodName(); // getScore
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return methodName;
    }
}
