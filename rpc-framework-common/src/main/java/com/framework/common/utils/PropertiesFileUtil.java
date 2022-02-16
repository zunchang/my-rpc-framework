package com.framework.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class PropertiesFileUtil {
    public PropertiesFileUtil() {
    }

    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try(InputStreamReader inputStreamReader=new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)){
            properties=new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("读取属性文件时发生异常 [{}]",fileName);
        }
        return properties;
    }
}