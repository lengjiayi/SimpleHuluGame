import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

class gameEnd extends JDialog {     //游戏结束的提示信息
    private Font mfont=new Font("华文楷体",Font.BOLD,100);
    private int width,height;
    public gameEnd(int x, int y, int width, int height, int winner, JFrame frame)
    {
        super(frame,true);
        this.width=width;
        this.height=height;
        this.setBounds(x,y,width,height);
        this.setLayout(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.setUndecorated(true);      //设置背景半透明，覆盖整个游戏区域
        this.getRootPane().setOpaque(false);
        this.getContentPane().setBackground(new Color(0,0,0,0));
        this.setBackground(new Color(0,0,0,100));

        ImageIcon endimg = new ImageIcon(this.getClass().getResource("END.PNG"));
        endimg.setImage(endimg.getImage().getScaledInstance(width/2,width/4, Image.SCALE_SMOOTH));
        JLabel text=new JLabel();
        text.setIcon(endimg);
        text.setBounds(width/4,height/2-width/8,width/2,width/4);
        this.add(text);

        JLabel close=new JLabel();
        ImageIcon clicon=new ImageIcon(getClass().getResource("dclose.PNG"));       //加入关闭窗口的图标
        int rate=20;
        clicon.setImage(clicon.getImage().getScaledInstance(width/rate,width/rate,Image.SCALE_SMOOTH));
        close.setBounds(width/4*3,height/2-width/8+width/rate,width/rate,width/rate);
        close.setIcon(clicon);
        close.setBackground(new Color(0,0,0,0));
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
            }
        });
        this.add(close);

        String winnername=(winner==1)?"humanhead.PNG":"monsterhead.PNG";        //胜利阵营的图标
        ImageIcon wingroup = new ImageIcon(this.getClass().getResource(winnername));
        wingroup.setImage(wingroup.getImage().getScaledInstance(height/4,height/4, Image.SCALE_SMOOTH));
        JLabel win=new JLabel();
        win.setIcon(wingroup);
        win.setBounds(width/2-height/8,height/4*3,height/4,height/4);
        this.add(win);


        this.setVisible(true);
    }
}