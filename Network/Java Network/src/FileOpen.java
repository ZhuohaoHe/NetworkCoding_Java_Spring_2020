import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

class Conf {
    public static String nickname;
    public static String account;
    public static String password;
}

public class FileOpen{
    private static String filename = "cus.inc";
    // 属性列表
    private static Properties pps;
    private static FileReader fr = null;
    static{
        try {
            pps = new Properties();
            // 对 文件 读取获得 字符流
            fr = new FileReader(filename);
            // 从 输入字符流 中 读取 属性值
            pps.load(fr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "文件操作异常");
            System.exit(0);
        } finally {
            // 文件关闭
            try {
                fr.close();
            } catch (IOException ioe) { }
        }
    }

    // 对 文件 进行更新
    private static void listInfo() {
        PrintStream ps = null;
        try{
            ps = new PrintStream(filename);
            // 将 属性列表 输出到 输出流
            pps.list(ps);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "文件操作异常");
            System.exit(0);
        } finally {
            // 关闭文件
            try{
                ps.close();
            } catch (Exception ee) {}
        }
    }

    // 从 属性列表 中根据 账户 获取 相应的密码 和 昵称
    public static void getInfoByAccount(String account) {
        String cusInfo = pps.getProperty(account);
        if(cusInfo != null) {
            String[] info = cusInfo.split("#");
            Conf.account = account;
            Conf.nickname = info[0];
            Conf.password = info[1];
        }
    }

    // 更新 属性列表 和 文件
    public static void updateInfo(String account, String nickname, String password) {
        pps.setProperty(account, nickname + "#" + password);
        // 更新文件
        listInfo();
    }
}
