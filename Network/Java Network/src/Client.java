import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Client extends JFrame implements ActionListener, Runnable {
    /* 控件 */
    private JTextArea taMag = new JTextArea("聊天记录: " + "\n");
    private JTextField tfMsg = new JTextField();
    private JScrollPane spMag = null;
    private JComboBox<String> cbCliList = new JComboBox<String>();
    // 套接字
    private Socket s = null;
    private Socket sInfo = null;

    // 用户信息
    private String[] userInfo = null;
    private String nickName = null;
    private Boolean toSomeone = false;
    // 格式化时间
    private SimpleDateFormat sdf = new SimpleDateFormat();
    // 客户端列表
    String[] clientArray = null;

    public Client(String nickName, String account) {

        /* 界面初始化 */
        this.setTitle("客户端");
        this.setSize(650, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(taMag, BorderLayout.CENTER);
        taMag.setLineWrap(true);
        taMag.setFont(new Font("Dialog", 1, 20));
        this.add(tfMsg, BorderLayout.SOUTH);
        tfMsg.setBackground(Color.lightGray);
        tfMsg.setFont(new Font("Dialog", 1, 18));
        this.add(cbCliList, BorderLayout.NORTH);

         // 滚动条
        spMag = new JScrollPane(
                taMag,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.add(spMag);
        this.setVisible(true);

        this.nickName = nickName;

        /* 添加监听 */
        tfMsg.addActionListener(this);
        cbCliList.addActionListener(this);

        try{
            // 通过 ip 地址 和 端口 连接服务器
            final long l = System.currentTimeMillis();
            final int x = (int)( l % 100 );
            String address = "127.0.0." + Integer.toString(x);
            s = new Socket(address, 9999);
            sInfo = new Socket(address, 9998);
            JOptionPane.showMessageDialog(this,"连接成功");
            // 连接成功后，客户端向服务器发送自己的客户端信息
            OutputStream osInfo = sInfo.getOutputStream();
            PrintStream psInfo = new PrintStream(osInfo);
            psInfo.println(nickName + "<" + account + ">");

            this.setTitle("客户端: " + nickName);
            // 接收客户端信息进程
            new InfoThread(sInfo).start();
            // 发送线程
            new Thread(this).start();
        } catch (Exception e) { }
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    OutputStream osInfo = sInfo.getOutputStream();
                    PrintStream psInfo = new PrintStream(osInfo);
                    psInfo.println("//Delete");
                    s.close();
                    sInfo.close();
                    e.getWindow().dispose();
                } catch (Exception eee) {}
            }
        });
    }

    // 接收服务器发来的消息
    public void run() {
        try{
            while(true){
                // 读取 服务器 发来的消息, 加入消息记录
                InputStream is = s.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
                String str = bf.readLine();
                String str2 = bf.readLine();
                if(str.equals("///offline") || str2.equals("///offline")){
                    OutputStream osInfo = sInfo.getOutputStream();
                    PrintStream psInfo = new PrintStream(osInfo);
                    psInfo.println("//Delete");
                    s.close();
                    sInfo.close();
                    return;
                }
                if(str.contains("###")){
                    str = str.substring(3);
                }
                if(str2.contains("###")){
                    str2 = str2.substring(3);
                }
                taMag.append(str + "\n");
                taMag.append(str2 + "\n");
            }
        } catch (Exception e) { }
    }

    // 监听输入框
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == tfMsg) {
            try {
                // 时间格式
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                // 向服务器发送的流
                OutputStream os = s.getOutputStream();
                PrintStream ps = new PrintStream(os);
                if(!toSomeone) {
                    ps.println(nickName + "  " + sdf.format(date) + " : ");
                    ps.println(tfMsg.getText());
                } else {
                    String information = nickName + " to "+ userInfo[0] +"<"+userInfo[1]+">" +" "+ sdf.format(date) + " : ";
                    ps.println("###" + information);
                    String chat = tfMsg.getText();
                    ps.println("###" + chat);
                    taMag.append(information + "\n");
                    taMag.append(chat + "\n");

                }
                tfMsg.setText("");
            } catch (Exception ee) { }
        }
        else if(e.getSource() == cbCliList) {
            try {
                String item = (String) cbCliList.getSelectedItem();
                if("全体成员".equals(item)) {
                    toSomeone = false;
                } else {
                    userInfo = item.split("<|>");
                    toSomeone = true;
                }
            } catch (Exception ee) {}
        }
    }


    class InfoThread extends Thread{
        private Socket sInfo = null;
        InputStream isInfo = null;
        BufferedReader bfInfo = null;
        public InfoThread(Socket s){
            try {
                this.sInfo = s;
                isInfo = this.sInfo.getInputStream();
                bfInfo = new BufferedReader(new InputStreamReader(isInfo));
            } catch (Exception e){}
        }
        public void run(){
            try {
                cbCliList.addItem("全体成员");
                while(true) {
                    String str = bfInfo.readLine();
                    String[] strArray = str.split("#", 0);
                    updateCbCliList(strArray);
                }
            }catch (Exception e){}
        }

        public void updateCbCliList(String[] strArray){
            Boolean flag = false;
            for(int i = 1; i < strArray.length; i ++) {
                String addString = strArray[i];
                for(int j = 0; j < cbCliList.getItemCount(); j ++) {
                    if(cbCliList.getItemAt(j).equals(addString)) {
                        flag = true;
                    }
                }
                if(flag == false) {
                    cbCliList.addItem(addString);
                }
            }
        }

    }
}
