package com.framework.common.utils;

public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     * @return
     */
    public static int cpus(){
        return Runtime.getRuntime().availableProcessors();
    }
}
