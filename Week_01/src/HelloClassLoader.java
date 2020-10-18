import java.io.*;
import java.lang.reflect.Method;

/**
 *
 */
public class HelloClassLoader extends ClassLoader {

    // 定义使用到的常量
    private static final String CLASS_PATH = "/Hello.xlass";

    private static final String CLASS_NAME = "Hello";

    private static final String METHOD_NAME = "hello";

    private static final int OFFSET_SIZE = 0;

    // ClassLoader的main方法
    public static void main(String[] args) {
        try {
            Class<?> aClass = new HelloClassLoader().findClass(CLASS_NAME);
            Object obj = aClass.newInstance();
            Method method = aClass.getMethod(METHOD_NAME);
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 重写findClass方法
    @Override
    protected Class<?> findClass(String name) {

        try {

            File file = new File(this.getClass().getResource(CLASS_PATH).getPath());
            byte[] bytes = getBytes(file);
            return defineClass(name, bytes, OFFSET_SIZE, bytes.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 转换字节方法
    private byte[] getBytes(File file) throws IOException {

        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bytes);
        }
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (255 - bytes[i]);
        }
        return bytes;
    }

}
