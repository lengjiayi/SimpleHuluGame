import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
public class myFrame extends JFrame{        //进入游戏前的界面，模仿一些程序的加载界面，设计为一个可拖动，边缘不规则的图案
    private JFrame nextFrame=null;
    private startbackground background;
    private Point pressPoint=null;
    private Toolkit kit;
    private Dimension screenSize;
    private JLabel close;
    private JLabel start;
    private Font mfont=new Font("华文楷体",Font.BOLD,60);

    private Point pressedpoint;
    public myFrame(String filename)
    {
        init(filename);
    }

    public myFrame(String filename, JFrame nf)
    {
        nextFrame=nf;
        init(filename);
    }


    private void init(String filename)
    {
        kit=Toolkit.getDefaultToolkit();
        screenSize=kit.getScreenSize();
        int width=screenSize.height/3*2;
        int height=screenSize.height/3*2;
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(width,height);
        setLocation(screenSize.width/2-width/2,screenSize.height/2-height/2);
        background=new startbackground(filename,width,height);
        setContentPane(background);
        setBackground(new Color(0,0,0,0));
        background.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                bedragged(e);
            }
        });

        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                pressedpoint=e.getPoint();      //记录鼠标按下的位置，用于实现拖动效果
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);      //鼠标进入主区域时隐藏关闭窗口按键
                close.setVisible(false);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                close.setVisible(true);
            }
        });

        close=new JLabel();     //关闭窗口按键
        ImageIcon clicon=new ImageIcon(getClass().getResource("close.PNG"));
        int rate=10;
        clicon.setImage(clicon.getImage().getScaledInstance(width/rate,width/rate,Image.SCALE_SMOOTH));
        close.setBounds(width-width/rate,0,width/rate,width/rate);
        close.setIcon(clicon);
        close.setBackground(new Color(0,0,0,0));
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(nextFrame!=null) {
                    nextFrame.dispose();
                    System.exit(0);
                }
                dispose();
            }
        });
        background.add(close);

        start=new JLabel("开始");     //开始游戏按键
        start.setForeground(new Color(255,255-80,255-120));
        start.setFont(mfont);
        start.setBackground(new Color(0,0,0,0));
        start.setBounds(width/2-width/rate,height/2-width/rate/2,130,60);
        start.setVisible(true);
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                dispose();
                Thread newframe=new Thread(new newFrame());
                newframe.start();
            }
        });
        background.add(start);

        setVisible(true);
    }

    protected void bedragged(MouseEvent e)      //实现拖动效果
    {
        if(pressedpoint==null)
            return;
        Point toloc=e.getPoint();
        Point nowloc=getLocation();
        setLocation(nowloc.x+toloc.x-pressedpoint.x,nowloc.y+toloc.y-pressedpoint.y);
    }

    class newFrame implements Runnable    //游戏主界面
    {
        BattleField battle;
        public newFrame()
        {
            JFrame frame = new JFrame("BattleField");
            frame.setTitle("葫芦娃兄弟阵法");
            frame.setResizable(false);
            battle=new BattleField();
            battle.father=frame;
            frame.addMouseListener(battle.ms);
            frame.setContentPane(battle.battlefield);
            battle.battlefield.repaint();

            Toolkit kit=Toolkit.getDefaultToolkit();
            Dimension screenSize=kit.getScreenSize();
            int width=screenSize.width;
            int height=screenSize.height;

            frame.setUndecorated(true);
            frame.setLocation(0,0);
            frame.setSize(width,height);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

        @Override
        public void run() {
            battle.getMouseInfo();
        }
    }
}

class startbackground extends JPanel {                //以特定图片作为背景的Panel
    private Image image;
    private int width,height;
    public startbackground(String filename,int width, int height)
    {
        this.setLayout(null);
        this.width=width;
        this.height=height;
        this.setSize(width,height);
        image=(Image)new ImageIcon(this.getClass().getResource(filename)).getImage();
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image,0,0,width,height,this);
    }
}