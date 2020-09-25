import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame implements ActionListener {
    /* 控件 */
    private JLabel lbAccount = new JLabel("账户: ");
    private JTextField tfAccount = new JTextField(15);
    private JLabel lbPassword = new JLabel("密码: ");
    private JPasswordField pfPassword = new JPasswordField(15);
    private JButton btLogin = new JButton("登陆");
    private JButton btRegister = new JButton("注册");
    private JButton btCancel = new JButton("取消");

    public LoginFrame() {
        /* 界面初始化 */
        super("登陆");
        this.setLayout(new FlowLayout());
        lbAccount.setFont(new Font("Dialog", 1, 20));
        lbPassword.setFont(new Font("Dialog", 1, 20));
        btLogin.setFont(new Font("Dialog", 1, 20));
        btRegister.setFont(new Font("Dialog", 1, 20));
        btCancel.setFont(new Font("Dialog", 1, 20));
        tfAccount.setFont(new Font("Dialog", 1, 17));
        pfPassword.setFont(new Font("Dialog", 1, 17));

        this.setSize(280,400);
        this.add(lbAccount);
        this.add(tfAccount);
        this.add(lbPassword);
        this.add(pfPassword);
        this.add(btLogin);
        this.add(btRegister);
        this.add(btCancel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setVisible(true);
        /* 添加监听 */
        btLogin.addActionListener(this);
        btRegister.addActionListener(this);
        btCancel.addActionListener(this);
    }

    // 监听控件
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btLogin) {
            // 登陆
            String account = tfAccount.getText();
            String password = new String(pfPassword.getPassword());
            FileOpen.getInfoByAccount(account);
            if(Conf.account == null || !Conf.password.equals(password)){
                JOptionPane.showMessageDialog(this, "账号或密码不正确.");
                return;
            }
            String nickName = Conf.nickname;
            JOptionPane.showMessageDialog(this, "登陆成功");
            this.dispose();
            new Client(nickName, account);
        } else if(e.getSource() == btRegister) {
            // 注册
            this.dispose();
            new RegisterFrame();
        } else {
            // 退出
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        LoginFrame lf = new LoginFrame();
    }

}
