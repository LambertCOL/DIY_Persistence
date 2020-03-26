package cn.lamb.io;

import java.io.InputStream;

/**
 * @Description TODO
 * @Date 2020/3/25 15:11
 * @Creator Lambert
 */
public class Resources {

    public static InputStream getResourcesAsStream(String path){
        InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return inputStream;
    }
}
