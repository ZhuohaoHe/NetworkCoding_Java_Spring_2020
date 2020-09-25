import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Spider {

    public String getHTML(String website) {
        StringBuffer text = new StringBuffer();
        String str = null;
        try {
            // 连接网页
            URL url = new URL(website);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            // 获取数据流
            BufferedReader br = new BufferedReader((new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)));
            System.out.println("开始爬取: ");
            while(true) {
                str = br.readLine();
                if(str != null) {
                    text.append(str + "\n");
                } else {
                    break;
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println(website + "爬取失败");
        }
        return text.toString();
    }

    public String getHTML(ArrayList<String> websitList) {
        StringBuffer text = new StringBuffer();
        String str = null;
        String website = null;
        Boolean flag = false;
        for(int i = 0; i < websitList.size(); i ++) {
            website = websitList.get(i);
            try {
                // 连接网页
                URL url = new URL(website);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                // 获取数据流
                BufferedReader br = new BufferedReader((new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)));
                System.out.println("开始爬取: ");
                while(true) {
                    str = br.readLine();
                    if(str != null) {
                        text.append(str + "\n");
                    } else {
                        break;
                    }
                }
                br.close();
                text.append("************************************************" +
                        "************************************************" +
                        "************************************************" + "\n");
            } catch (Exception e) {
                flag = true;
                System.out.println(website + "爬取失败");
            }
        }
        return text.toString();
    }

    public String getText(String HTML){
        Document doc = Jsoup.parse(HTML);
        StringBuffer text = new StringBuffer(doc.text());
        System.out.println(text.length());
        int start = 0;
        int end = start;
        StringBuffer outStr = new StringBuffer();
        while(true) {
            end = text.indexOf(" ", start + 1);
            if(end == -1) {
                break;
            }
            outStr.append(text.substring(start, end));
            outStr.append("\n");
            start = end;
        }
        return outStr.toString();
    }

}
