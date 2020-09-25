import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.net.*;

public class Server extends JFrame implements Runnable, ActionListener {
    // Central控件
    private JPanel pnCentral = new JPanel();
    private JTextArea taMagCentral = new JTextArea("消息记录" + "\n", 20, 20);
    private JScrollPane spMagCentral = new JScrollPane(
            taMagCentral,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    );;

    // South 控件
    private JPanel pnSouth = new JPanel();
    private JTextField tfMagSouth = new JTextField(25);
    private JButton btSend = new JButton("发送");

    // East 控件
    private JPanel pnEast = new JPanel();
    private JTextArea taMag = new JTextArea("客户端: " + "\n", 15, 8);
    private JComboBox<String> cbClient = new JComboBox<String>();
    private JButton btOffLine = new JButton("下线");

    // 套接字
    private Socket s;
    private ServerSocket ss;
    private Socket sInfo;
    private ServerSocket ssInfo;
    private HashMap<String, String>  ClientsInfoMap = new HashMap<String, String>();
    // 流

    // 格式化时间
    private SimpleDateFormat sdf = new SimpleDateFormat();

    String offLineAccount = null;


    // 存放客户端列表
    private ArrayList clientsChatArray = new ArrayList();
    private ArrayList clientsInfoArray = new ArrayList();

    public Server() throws Exception{
        this.setTitle("Server");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.yellow);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setSize(600,400);


        // Central 控件
        pnCentral.add(spMagCentral);
        this.add(pnCentral, BorderLayout.CENTER);

        // South 控件
        pnSouth.add(tfMagSouth);
        pnSouth.add(btSend);
        this.add(pnSouth, BorderLayout.SOUTH);

        // East 控件
        pnEast.setLayout(new BorderLayout());
        pnEast.add(taMag, BorderLayout.CENTER);
        pnEast.add(cbClient, BorderLayout.NORTH);
        pnEast.add(btOffLine, BorderLayout.SOUTH);
        this.add(pnEast, BorderLayout.EAST);

        // 字体
        taMag.setFont(new Font("Dialog", 1, 18));
        taMagCentral.setFont(new Font("Dialog", 1, 18));
        tfMagSouth.setFont(new Font("Dialog", 1, 18));
        btOffLine.setFont(new Font("Dialog", 1, 18));
        btSend.setFont(new Font("Dialog", 1, 18));
        cbClient.setFont(new Font("Dialog", 1, 18));

        // 监听
        btSend.addActionListener(this);
        btOffLine.addActionListener(this);
        cbClient.addActionListener(this);


        // 打开端口 接收连接
        ss = new ServerSocket(9999);
        ssInfo = new ServerSocket(9998);
        // 接收客户连接
        new Thread(this).start();
    }

    public void run() {
        try{
            while(true){
                // 持续接收
                sInfo = ssInfo.accept();
                s = ss.accept();
                // 客户端信息进程
                InfoThread it = new InfoThread(sInfo);
                clientsInfoArray.add(it);
                it.start();
                // 聊天消息线程
                ChatThread ct = new ChatThread(s);
                clientsChatArray.add(ct);
                ct.start();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cbClient) {
            String[] offLineInfo = ((String) cbClient.getSelectedItem()).split("<|>");
            offLineAccount = offLineInfo[1];
        } else if(e.getSource() == btSend) {
            try {
                // 时间格式
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String message = tfMagSouth.getText();
                String str = "管理员" + " " + sdf.format(date) + " " + ":";
                for(int i = 0; i < clientsChatArray.size(); i ++) {
                    ChatThread ct = (ChatThread)clientsChatArray.get(i);
                    ct.ps.println(str);
                    taMagCentral.append(str + "\n");
                    ct.ps.println(message);
                    taMagCentral.append(message + "\n");
                    tfMagSouth.setText("");
                }
            } catch (Exception ee) { }
        } else if (e.getSource() == btOffLine) {
            SendMessageToOffline(offLineAccount);
        }
    }

    public void SendMessageToOffline(String account) {
        InetAddress ipAddress = null;
        for(int i = 0; i < clientsInfoArray.size(); i ++) {
            InfoThread it = (InfoThread) (clientsInfoArray.get(i));
            if(account.equals(it.getAccount())){
                ipAddress = it.getIpAddress();
                break;
            }
        }
        for(int i = 0; i < clientsChatArray.size(); i ++) {
            ChatThread ct = (ChatThread)(clientsChatArray.get(i));
            if(ipAddress.equals(ct.getIpAddress())){
                ct.ps.println("///offline");
                return;
            }
        }
    }

    // 聊天线程
    class ChatThread extends Thread {
        private Socket s = null;
        private BufferedReader br = null;
        private PrintStream ps = null;
        public ChatThread(Socket s) throws Exception{
            this.s = s;
            // 获取传输的字符串
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ps = new PrintStream(s.getOutputStream());
        }

        public void run() {
            try{
                while(true){
                    // 循环接收客户端发来的字符串
                    String str = br.readLine();
                    String str2 = br.readLine();
                    if(!str.contains("###")) {
                        // 发送给所有客户端
                        sendMessage(str);
                        sendMessage(str2);
                        taMagCentral.append(str + "\n" + str2 + "\n");

                    } else {
                        // 发送给特定客户端
                        String[] info = str.split("<|>");
                        String account = info[1];
                        InetAddress address = findIpAddress(account);
                        sendMessageToSomeone(str, address);
                        sendMessageToSomeone(str2, address);
                        taMagCentral.append(str + "\n" + str2 + "\n");
                    }
                }
            } catch (Exception e) {

            }
        }

        // 根据账户寻找相应ip地址
        public InetAddress findIpAddress(String account) {
            for(int i = 0; i < clientsInfoArray.size(); i ++) {
                InfoThread it = (InfoThread) (clientsInfoArray.get(i));
                if(account.equals(it.getAccount())){
                    return it.getIpAddress();
                }
            }
            return null;
        }

        public InetAddress getIpAddress() {
            return s.getLocalAddress();
        }

        // 将聊天消息发送给所有客户端
        public void sendMessage(String msg){
            for(int i = 0; i < clientsChatArray.size(); i ++) {
                 ChatThread ct = (ChatThread)clientsChatArray.get(i);
                ct.ps.println(msg);
            }
        }

        public void sendMessageToSomeone(String msg, InetAddress address) {
            for(int i = 0; i < clientsChatArray.size(); i ++) {
                ChatThread ct = (ChatThread)(clientsChatArray.get(i));
                if(address.equals(ct.getIpAddress())){
                    ct.ps.println(msg);
                    System.out.println(msg);
                    return;
                }
            }
        }
    }

    // 客户端信息线程
    class InfoThread extends Thread {
        private Socket sInfo = null;
        private BufferedReader br = null;
        private PrintStream ps = null;
        private String account = null;
        private String nickName = null;

        public InfoThread(Socket s) throws Exception{
            this.sInfo = s;
            // 获取传输的字符串
            this.br = new BufferedReader(new InputStreamReader(sInfo.getInputStream()));
            this.ps = new PrintStream(sInfo.getOutputStream());
        }

        public void run() {
            try {
                while (true){
                    // 循环接收客户端发来的消息
                    String str = br.readLine();
                    if(str.contains("//Delete")) {
                        clientsInfoArray.remove(this);
                    } else {
                        String[] info = str.split("<|>");
                        nickName = info[0];
                        account = info[1];
                    }
                    updateTaMag();
                    sendInfoMessage(taMag.getText());
                }
            } catch (Exception e) { }
        }

        // 向各个客户端发送客户端信息
        public void sendInfoMessage(String msg){
            for(int i = 0; i < clientsInfoArray.size(); i ++) {
                InfoThread it = (InfoThread) clientsInfoArray.get(i);
                it.ps.println(msg);
            }
        }

        public void updateTaMag(){
            taMag.setText("");
            taMag.append("客户端: \n");
            for(int i = 0; i < clientsInfoArray.size(); i ++) {
                InfoThread it = (InfoThread)clientsInfoArray.get(i);
                taMag.append("#" + it.nickName + "<" + it.account + ">" + "\n");
                updateCbClient(it.nickName + "<" + it.account +">");
            }
        }

        public void updateCbClient(String addString){
            for(int i = 0; i < cbClient.getItemCount(); i ++) {
                if(cbClient.getItemAt(i).equals(addString)) {
                    return;
                }
            }
            cbClient.addItem(addString);
        }

        // 获得客户端的账户
        public String getAccount(){
            return account;
        }

        // 获得客户端的昵称
        public String getNickName(){
            return nickName;
        }

        // 获取IP地址
        public InetAddress getIpAddress(){
            return sInfo.getLocalAddress();
        }

    }

    public static void main(String[] args) throws Exception{
        Server s = new Server();
    }
}