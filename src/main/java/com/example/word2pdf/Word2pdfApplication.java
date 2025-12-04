package com.example.word2pdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

@SpringBootApplication
public class Word2pdfApplication {

    private static final Logger log = LoggerFactory.getLogger(Word2pdfApplication.class);

    public static void main(String[] args) {
        log.info("应用启动中: Word2PDF Service");
        registerWord2412();
        SpringApplication.run(Word2pdfApplication.class, args);
        log.info("应用启动完成");
    }

    /**
     * aspose-words:jdk17:24.12 版本注册
     */
    public static void registerWord2412() {
        try {
            log.info("正在注册 Aspose Words 24.12 ...");

            Class<?> zzodClass = Class.forName("com.aspose.words.zzod");
            Constructor<?> constructors = zzodClass.getDeclaredConstructors()[0];
            constructors.setAccessible(true);
            Object instance = constructors.newInstance(null, null);

            Field zzWws = zzodClass.getDeclaredField("zzWws");
            zzWws.setAccessible(true);
            zzWws.set(instance, 1);

            Field zzVZC = zzodClass.getDeclaredField("zzVZC");
            zzVZC.setAccessible(true);
            zzVZC.set(instance, 1);

            Class<?> zz83Class = Class.forName("com.aspose.words.zz83");
            constructors.setAccessible(true);
            constructors.newInstance(null, null);

            Field zzZY4 = zz83Class.getDeclaredField("zzZY4");
            zzZY4.setAccessible(true);
            ArrayList<Object> zzwPValue = new ArrayList<>();
            zzwPValue.add(instance);
            zzZY4.set(null, zzwPValue);

            Class<?> zzXuRClass = Class.forName("com.aspose.words.zzXuR");
            Field zzWE8 = zzXuRClass.getDeclaredField("zzWE8");
            zzWE8.setAccessible(true);
            zzWE8.set(null, 128);

            Field zzZKj = zzXuRClass.getDeclaredField("zzZKj");
            zzZKj.setAccessible(true);
            zzZKj.set(null, false);

            log.info("Aspose Words 24.12 注册成功");

        } catch (Exception e) {
            log.error("Aspose Words 注册失败", e);
        }
    }

}
