package com.person.zb.buddy.study.util;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.dynamic.DynamicType;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Desc:
 * @Author: ZhouBin
 * @Date: 2022/5/10
 */
@Slf4j
public class DemoUtil {


    /**
     * 将字节码输出、并返回代理对象
     */
    public static <T> T writeAndNew(DynamicType.Loaded<T> loaded) {
        String fileName = "D:\\下载\\" + loaded.getLoaded().getSimpleName() + ".class";
        File file = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(loaded.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return loaded.getLoaded().newInstance();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static <T> void write(DynamicType.Loaded<T> loaded) {
        String fileName = "D:\\下载\\" + loaded.getLoaded().getSimpleName() + ".class";
        File file = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(loaded.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
