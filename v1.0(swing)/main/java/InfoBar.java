import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

class InfoBar extends JPanel{       //用于实时显示当前鼠标经过角色的属性
    private BattleField father;
    private Font mfont=new Font("华文楷体",Font.BOLD,35);
    private int width,height;
    private Charactors curChats=null;
    private ProcessBar HPbar;       //显示HP的进度条
    private ProcessBar MPbar;       //显示MP的进度条
    private JLabel name;
    private JLabel hp;
    private JLabel mp;
    public InfoBar(int width, int height, BattleField father)
    {
        this.father=father;
        this.setLayout(null);
        this.height=height;
        this.width=width;
        this.setSize(width,height);
        HPbar=new ProcessBar(width/5*3,height/5,Color.RED);
        MPbar=new ProcessBar(width/5*3,height/5,Color.BLUE);
        HPbar.setLocation(width/10*3,height/10*4);
        MPbar.setLocation(width/10*3,height/10*7);
        name=new JLabel("名字");
        hp=new JLabel("HP");
        mp=new JLabel("MP");
        name.setSize(width/2,height/5*2);
        name.setLocation(width/5*2,0);
        name.setForeground(new Color(255,215,000));
        name.setFont(mfont);
        mp.setSize(width/5,height/5);
        hp.setSize(width/5,height/5);
        hp.setLocation(width/5,height/10*4);
        mp.setLocation(width/5,height/10*7);
        hp.setBackground(new Color(0,0,0,0));
        mp.setBackground(new Color(0,0,0,0));
        hp.setForeground(Color.RED);
        mp.setForeground(Color.BLUE);
        hp.setVisible(false);
        mp.setVisible(false);
        HPbar.setVisible(false);
        MPbar.setVisible(false);
        name.setVisible(false);
        this.add(hp);
        this.add(mp);
        this.add(name);
        this.add(HPbar);
        this.add(MPbar);
        this.setBackground(new Color(0,0,0,0));
    }
    public void clear()
    {
        curChats=null;
        hp.setVisible(false);
        mp.setVisible(false);
        HPbar.setVisible(false);
        MPbar.setVisible(false);
        name.setVisible(false);
        repaint();
        father.battlefield.repaint();
    }
    public void reloadBars()        //更新显示信息
    {
        curChats=virtualField.field[father.curY][father.curX];
        if(curChats==null)
        {
            clear();
            return;
        }
        HPbar.reSize(width/5*3/100*curChats.maxHP,height/5,curChats.maxHP);
        HPbar.setvalue(curChats.maxHP,curChats.HP);
        MPbar.setvalue(curChats.maxMP,curChats.MP);
        String curname="";
        switch (curChats.basename.charAt(0))
        {
            case 'b': curname=((CucurbitBoy)curChats).tellName();break;
            default: curname=curChats.typename;
        }
        name.setText(curname);

        hp.setVisible(true);
        mp.setVisible(true);
        HPbar.setVisible(true);
        MPbar.setVisible(true);
        name.setVisible(true);
        father.battlefield.repaint();
        repaint();

    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int rate=5;
        GradientPaint paint=new GradientPaint(width/2,0,new Color(54,54,54,255),width/2,height,new Color(100,100,100,5));
        g2.setPaint(paint);
        RoundRectangle2D background=new RoundRectangle2D.Float(0,0,width,height+(float)height/rate,(float)height/rate,(float)height/rate);
        g2.fill(background);
    }
}

class ProcessBar extends JLabel {       //用于绘制一个进度条
    private int width,height,max;
    private int value=0;
    private Color forecolor;
    public ProcessBar(int width, int height, Color color)
    {
        setSize(width,height);
        setLayout(null);
        setVisible(true);
        this.width=width;
        this.height=height;
        this.max=max;
        this.value=max;
        this.forecolor=color;
    }
    public void reSize(int width, int height,int max)
    {
        this.width=width;
        this.height=height;
        this.max=max;
        setSize(width,height);
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        int rate=3;
        BasicStroke basicStroke=new BasicStroke((float)height/rate,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
        g2.setStroke(basicStroke);
        Shape forground=new Rectangle2D.Float((float)height/rate/2,0,((float)width-(float)height/rate)/max*value,height);
        g2.setColor(forecolor);
        g2.fill(forground);
        Shape border=new Rectangle2D.Float(0,0,(float)width,(float)height);
        g2.setColor(new Color(200,200,255));
        g2.draw(border);
    }
    public void setvalue(int max, int value) {   this.max=max; this.value=value; }
}
