import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame implements ActionListener {
    /* 控件 */
    private JLabel lbAccount = new JLabel("账户: ");
    private JTextField tfAccount = new JTextField(15);
    private JLabel lbNickname = new JLabel("昵称: ");
    private JTextField tfNickname = new JTextField(15);
    private JLabel lbPassword1 = new JLabel("密码:       ");
    private JPasswordField pfPassword1 = new JPasswordField(20);
    private JLabel lbPassword2 = new JLabel(" 重复密码: ");
    private JPasswordField pfPassword2 = new JPasswordField(20);
    private JButton btRegister = new JButton("注册");
    private JButton btCancel = new JButton("返回");
    public RegisterFrame() {
        /* 界面初始化 */
        super("注册");
        this.setLayout(new FlowLayout());
        lbAccount.setFont(new Font("Dialog", 1, 20));
        tfAccount.setFont(new Font("Dialog", 1, 20));
        lbNickname.setFont(new Font("Dialog", 1, 20));
        tfNickname.setFont(new Font("Dialog", 1, 20));
        lbPassword1.setFont(new Font("Dialog", 1, 20));
        lbPassword2.setFont(new Font("Dialog", 1, 20));
        btRegister.setFont(new Font("Dialog", 1, 20));
        btCancel.setFont(new Font("Dialog", 1, 20));
        this.add(lbAccount);
        this.add(tfAccount);
        this.add(lbNickname);
        this.add(tfNickname);
        this.add(lbPassword1);
        this.add(pfPassword1);
        this.add(lbPassword2);
        this.add(pfPassword2);
        this.add(btRegister);
        this.add(btCancel);
        this.setSize(400,320);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        /* 添加监听 */
        btRegister.addActionListener(this);
        btCancel.addActionListener(this);
    }
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == btRegister) {
            String password1 = new String(pfPassword1.getPassword());
            String password2 = new String(pfPassword2.getPassword());
            if(!password1.equals(password2)) {
                JOptionPane.showMessageDialog(this, "两次密码不相同");
                return;
            }
            String account = tfAccount.getText();
            FileOpen.getInfoByAccount(account);
            if(account.equals(Conf.account)) {
                JOptionPane.showMessageDialog(this,"用户已经注册");
                return;
            }
            String nickname = tfNickname.getText();
            FileOpen.updateInfo(account, nickname, password1);
            JOptionPane.showMessageDialog(this,"注册成功");
            this.dispose();
            new LoginFrame();
        } else {
            this.dispose();
            new LoginFrame();
        }
    }
}
