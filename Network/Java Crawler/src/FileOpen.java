import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class FileOpen {
    ArrayList<String> wordList = null;
    ArrayList<String> websitList = null;

    public FileOpen() {
        wordList = new ArrayList<String>();
        websitList = new ArrayList<String>();
    }
    public ArrayList<String> getWordList(File choosenWordLib) {
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(choosenWordLib));
            while(true) {
                String str=br.readLine();
                if(str==null)	break;
                wordList.add(str);
            }
            br.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件不存在");
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件读取失败");
            e1.printStackTrace();
        }
        return wordList;
    }

    public ArrayList<String> getWebsitList(File choosenWordLib) {
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(choosenWordLib));
            while(true) {
                String str=br.readLine();
                if(str==null)	break;
                websitList.add(str);
            }
            br.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件不存在");
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件读取失败");
            e1.printStackTrace();
        }
        return websitList;
    }



}
