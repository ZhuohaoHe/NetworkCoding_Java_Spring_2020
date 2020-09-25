import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.ExportUtils;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFrame extends JFrame implements ActionListener{
    // North 控件
    private JPanel pnNorth = new JPanel();
    private JLabel lbInputHtml = new JLabel("输入网址: ");
    private JTextField tfHtml = new JTextField(40);
    private JButton btRunSpider = new JButton("开始爬虫");

    // Central 控件
    private JPanel pnCentralHtml = new JPanel();
    private JPanel pnetpCentralText = new JPanel();
    private JTextArea taHtml = new JTextArea();
    private JTextArea taText = new JTextArea();
    private JScrollPane spHtml = new JScrollPane(
            taHtml,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    );
    private JScrollPane spText = new JScrollPane(
            taText,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    );
    private JTabbedPane tpCenter = new JTabbedPane();

    // South 控件
    private JPanel pnSouth = new JPanel();
    private JButton btSelectWebsitFile = new JButton("选取网址库");
    private JTextArea taWebsitList = new JTextArea(2, 80);
    private JScrollPane spWebsitList = new JScrollPane(
            taWebsitList,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    );
    private JButton btRunAllSpider = new JButton("爬取网址库");

    // East 控件
    private JPanel pnEast = new JPanel();
    private JButton btSelectWordFile = new JButton("选取词库");
    private JButton btUseIt = new JButton("应用");
    private JTextArea taWordList = new JTextArea(20,8);
    private JScrollPane spWordList = new JScrollPane(
            taWordList,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    );


    // 爬虫
    private Spider spider = new Spider();
    // 文件处理
    private FileOpen fileOpen = new FileOpen();

    // 敏感词
    private ArrayList<String> wordList = null;
    // 网址
    private ArrayList<String> websitList = null;
    // 敏感词出现次数
    private HashMap<String, Integer> wordCounter = null;

    public MainFrame() {
        super("Spider");
        // 控件字体
        lbInputHtml.setFont(new Font("Dialog", 1, 18));
        tfHtml.setFont(new Font("Dialog", 1, 18));
        btRunSpider.setFont(new Font("Dialog", 1, 18));
        taText.setFont(new Font("Dialog", 1, 18));
        taHtml.setFont(new Font("Dialog", 1, 18));
        btSelectWebsitFile.setFont(new Font("Dialog", 1, 18));
        btSelectWordFile.setFont(new Font("Dialog", 1, 18));
        btUseIt.setFont(new Font("Dialog", 1, 18));
        taWordList.setFont(new Font("Dialog", 1, 18));
        taWebsitList.setFont(new Font("Dialog", 1, 18));
        btRunAllSpider.setFont(new Font("Dialog", 1, 18));

        // North 面板
        pnNorth.add(lbInputHtml);
        pnNorth.add(tfHtml);
        pnNorth.add(btRunSpider);
        pnNorth.setLayout(new FlowLayout());
        this.add(pnNorth, BorderLayout.NORTH);

        // Central 面板
        pnCentralHtml.setLayout(new BorderLayout());
        pnCentralHtml.add(spHtml, BorderLayout.CENTER);
        tpCenter.add(pnCentralHtml, "HTML");
        pnetpCentralText.setLayout(new BorderLayout());
        pnetpCentralText.add(spText, BorderLayout.CENTER);
        tpCenter.add(pnetpCentralText, "Text");
        this.add(tpCenter);

        // East 面板
        pnEast.setLayout(new BorderLayout());
        pnEast.add(btSelectWordFile, BorderLayout.NORTH);
        pnEast.add(spWordList, BorderLayout.CENTER);
        pnEast.add(btUseIt, BorderLayout.SOUTH);
        this.add(pnEast, BorderLayout.EAST);

        // South 面板
        pnSouth.setLayout(new FlowLayout());
        pnSouth.add(btSelectWebsitFile);
        pnSouth.add(spWebsitList);
        pnSouth.add(btRunAllSpider);
        this.add(pnSouth, BorderLayout.SOUTH);

        wordList = new ArrayList<String>();
        websitList = new ArrayList<String>();
        wordCounter = new HashMap<String, Integer>();

        this.setLocation(400, 200);
        this.setSize(600, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 监听
        btRunSpider.addActionListener(this);
        btSelectWordFile.addActionListener(this);
        btSelectWebsitFile.addActionListener(this);
        btUseIt.addActionListener(this);
        btRunAllSpider.addActionListener(this);
    }

    public void HighLightWord() {
            Highlighter hg=taText.getHighlighter();
            hg.removeAllHighlights();
            String text=taText.getText();
            DefaultHighlighter.DefaultHighlightPainter painter=new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);	//设置高亮显示颜色为黄色
            for(String str : wordList) {
                int index = 0;
                int count = 0;
                while((index = text.indexOf(str, index)) >= 0) {
                    try {
                        count ++;
                        hg.addHighlight(index, index+str.length(), painter);
                        index += str.length();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                wordCounter.put(str, count);
            }
    }

    public void drewPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for(int i = 0; i < wordCounter.size(); i ++) {
            dataset.setValue(wordList.get(i), wordCounter.get(wordList.get(i)));
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "统计",
                dataset,
                true,
                true, false);
        Font font = new Font("宋体", Font.BOLD, 20);
        chart.setTitle(new TextTitle("统计", font));
        // 设置图片标题的字体
        chart.getLegend().setItemFont(font);
        // 得到图块,准备设置标签的字体
        PiePlot plot = (PiePlot) chart.getPlot();
        // 设置标签字体
        plot.setLabelFont(font);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({1}占{2})"));
        ChartFrame cfPieChart = new ChartFrame("敏感词统计", chart);
        cfPieChart.pack();
        cfPieChart.setVisible(true);
        try {
            ChartUtils.saveChartAsJPEG(new File("PieChart.jpg"), chart, 600, 400);
        }catch (Exception e) {}

    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btRunSpider) {
            String websit = tfHtml.getText();
            String html = spider.getHTML(websit);
            String text = spider.getText(html);
            taHtml.setText("");
            taHtml.append(html);
            taText.setText("");
            taText.append(text);
        } else if(e.getSource() == btSelectWordFile) {
            // 清空List
            wordList.clear();
            taWordList.setText("");
            // 获取文件
            JFileChooser fcWordFile = new JFileChooser();
            int ok = fcWordFile.showOpenDialog(this);
            if(ok != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File choosenWordLib = fcWordFile.getSelectedFile();
            wordList = fileOpen.getWordList(choosenWordLib);
            StringBuffer str1 = new StringBuffer();
            for(int i = 0; i < wordList.size(); i ++) {
                str1.append(wordList.get(i) + "\n");
            }
            taWordList.append(str1.toString());
        } else if(e.getSource() == btSelectWebsitFile) {
            // 清空List
            websitList.clear();
            taWebsitList.setText("");

            // 获取文件
            JFileChooser fcWebsiteFile = new JFileChooser();
            int ok = fcWebsiteFile.showOpenDialog(this);
            if (ok != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File choosenWebsiteLib = fcWebsiteFile.getSelectedFile();
            websitList = fileOpen.getWebsitList(choosenWebsiteLib);
            StringBuffer str2 = new StringBuffer();
            for(int i = 0; i < websitList.size(); i ++) {
                str2.append(websitList.get(i) + "\n");
            }
            taWebsitList.append(str2.toString());
        } else if(e.getSource() == btRunAllSpider) {
            String html = spider.getHTML(websitList);
            String text = spider.getText(html);
            taHtml.setText("");
            taHtml.append(html);
            taText.setText("");
            taText.append(text);
        } else if(e.getSource() == btUseIt) {
            // 应用
            HighLightWord();
            drewPieChart();
        }
    }

    public static void main(String[] args) {
            new MainFrame();
    }
}
