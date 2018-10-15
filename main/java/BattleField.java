import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BattleField {      //游戏主界面

    public JPanel battlefield;
    private Font mfont=new Font("华文楷体",Font.BOLD,40);
    AnimationHandler animationHandler;      //动画批处理器
    /* 角色 */
    private Scorpion scorption;
    private Grandpa grandpa;
    private Snake snake;
    private Brotherhood brotherhood;

    static int oneRound=3;                  //每回合步数，包括移动和攻击，用完则对手攻击，并进入下一回合
    private Bots bot;                       //机器人控制器
    public int myMove;                      //当前步数
    private JLabel MoveCount;               //用于显示剩余步数
    private boolean Bsorted=false;

    public JFrame father;
    private TransparentPanel hlmask;        //用于高亮显示鼠标经过区域
    private SelectionBar toolbar;           //技能选择框
    private InfoBar infobar;                //信息显示框，显示鼠标经过区域角色的状态信息

    public JTextArea debug;                 //输出调试信息的JTextArea

    public static int selectionsize;
    static{
        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension screenSize=kit.getScreenSize();
        int width=screenSize.width;
        selectionsize=(int)(width/17*1.5f);
    }

    MouseListener ms;

    public int curX=0,curY=0;
    private Charactors curChats;

    public static void main(String[] args) {
        myFrame preframe=new myFrame("prebackground.PNG");
        preframe.setVisible(true);
    }
    private void createUIComponents() throws Exception {
        // TODO: place custom component creation code here
        battlefield = new BackGroundPanel();

        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension screenSize=kit.getScreenSize();
        int width=screenSize.width;
        int height=screenSize.height;

        battlefield.setBounds(0, 0, width, height);
        battlefield.setLayout(null);
/*
 *不再使用的两个按钮，用于葫芦娃的排序和妖怪的变阵
        actionButton button1 = new actionButton(1100, 500, 90, 80, "monsterhead.PNG", "monsterhead2.PNG");
        button1.addMouseListener(new MouseListener() {          //负责蝎子精变换阵型的按钮
            @Override
            public void mouseClicked(MouseEvent e) {
                if (animationHandler.avaliable.get() == false)
                    return;
                scorption.changeFMT(-1);
//                scorption.StandStill();
                animationHandler.cmd.set(1);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        battlefield.add(button1);
        actionButton button2 = new actionButton(1000, 500, 90, 80, "humanhead.PNG", "humanhead2.PNG");
        button2.addMouseListener(new MouseListener() {          //负责葫芦娃排队的按钮
            @Override
            public void mouseClicked(MouseEvent e) {
                if (animationHandler.avaliable.get() == false)
                    return;
                if (Bsorted)
                    brotherhood.randomize();
                else
                    brotherhood.BubbleSort();
                Bsorted = !Bsorted;
//                scorption.StandStill();
                animationHandler.cmd.set(1);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        battlefield.add(button2);
*/
        animationHandler = new AnimationHandler();
        /*创建角色*/
        Thread tmp;
        scorption = new Scorpion((BackGroundPanel) battlefield,this);               //创建人类和妖怪
        animationHandler.addChat(scorption);
        animationHandler.addChat(scorption.troops);
        tmp=new Thread(scorption);
        tmp.start();

        grandpa = new Grandpa((BackGroundPanel) battlefield,this);
        animationHandler.addChat(grandpa);
        grandpa.StandStill();
        tmp=new Thread(grandpa);
        tmp.start();

        snake = new Snake((BackGroundPanel) battlefield,this);
        animationHandler.addChat(snake);
        snake.StandStill();
        tmp=new Thread(snake);
        tmp.start();

        brotherhood = new Brotherhood((BackGroundPanel) battlefield,this);
        animationHandler.addChat(brotherhood.cbs);
//        brotherhood.StandStill();

        /*加入动画处理器*/
        Thread anim = new Thread(animationHandler);
        anim.start();
        animationHandler.cmd.set(1);

        /*加入其他控件*/

        JLabel close=new JLabel();
        ImageIcon clicon=new ImageIcon(getClass().getResource("close.PNG"));
        int rate=25;
        clicon.setImage(clicon.getImage().getScaledInstance(width/rate,width/rate,Image.SCALE_SMOOTH));
        close.setBounds(width-width/rate,0,width/rate,width/rate);
        close.setIcon(clicon);
        close.setBackground(new Color(0,0,0,0));
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                father.dispose();
                System.exit(0);
            }
        });
        battlefield.add(close);

        hlmask = new TransparentPanel();
        hlmask.setBounds(((BackGroundPanel) battlefield).xstart, ((BackGroundPanel) battlefield).ystart, ((BackGroundPanel) battlefield).block.x, ((BackGroundPanel) battlefield).block.y);
        hlmask.setTransparency(.3f);
        battlefield.add(hlmask);

        infobar=new InfoBar(width/3,height/5,this);
        infobar.setLocation(width/3,height/5*4);
        battlefield.add(infobar);

        toolbar = new SelectionBar(this, (BackGroundPanel) battlefield, infobar);
        toolbar.setBounds(((BackGroundPanel)battlefield).deltax/3, ((BackGroundPanel) battlefield).deltay/2*3,
                ((BackGroundPanel) battlefield).deltax/3*10, ((BackGroundPanel) battlefield).deltax*13);
        battlefield.add(toolbar);

        bot=new Bots(scorption, snake, animationHandler, this);
        System.out.println("bot loaded");

        MoveCount =new JLabel();
        MoveCount.setForeground(Color.BLACK);
        BackGroundPanel tp=(BackGroundPanel) battlefield;
        MoveCount.setLocation(tp.xstart+tp.block.x*10,tp.ystart+ tp.block.y);
        MoveCount.setSize(width-MoveCount.getLocation().x, height/10);
        MoveCount.setFont(new Font("华文楷体",Font.BOLD,40));
        battlefield.add(MoveCount);
        myMove=oneRound;
        updateCount();


        debug=new JTextArea();
        debug.setLocation(tp.xstart+tp.block.x*12,tp.ystart+ tp.block.y+height/10);
        debug.setSize(width-debug.getLocation().x-tp.block.x/2, height-debug.getLocation().y);
        debug.setFont(new Font("华文楷体",Font.BOLD,16));
        debug.setForeground(Color.BLACK);
        debug.setOpaque(false);
        debug.append("start\n");
        JScrollPane JSP=new JScrollPane(debug);
        JSP.setLocation(debug.getLocation());
        JSP.setSize(debug.getSize());
        JSP.setOpaque(false);
        JSP.getViewport().setOpaque(false);
        battlefield.add(JSP);

        ms=new MouseListener() {        //用于监听处理鼠标事件
            @Override
            public void mouseClicked(MouseEvent e) {        //点击时可能是选择人物或是人物移动
                Point curpit=e.getPoint();
                Point tmploc=father.getLocation();
                BackGroundPanel tmp=(BackGroundPanel)battlefield;
                curpit.x=curpit.x-tmploc.x-tmp.xstart;
                curpit.y=curpit.y-tmploc.y-tmp.ystart;
                int virtuleX=(int)curpit.x/tmp.block.x;
                int virtuleY=(int)curpit.y/tmp.block.y;
                if(virtuleX<0 || virtuleX>=virtualField.width || virtuleY<0 || virtuleY>=virtualField.height) {
                    curChats=null;
                    toolbar.reloadBars(null);
                    return;
                }
                Charactors newchats=virtualField.field[virtuleY][virtuleX];
                toolbar.reloadBars(newchats);
                if(curChats!=null)
                {
                    if(newchats==null)      //如果已经选择人物，且当前点击区域为空则该人物移动到此处
                    {
                        curChats.moveto(virtuleX,virtuleY);
                        animationHandler.cmd.set(1);
                        myMove--;
                        updateCount();
                    }
                }
                if(newchats==null || !newchats.monster)   //是否允许控制妖怪
                   curChats=newchats;       //取消选择或重新选择
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };

    }

    public void updateCount(){      //更新显示剩余步数
        MoveCount.setText("剩余次数："+myMove);
        MoveCount.repaint();
    }

    public void getMouseInfo()      //开始执行这一线程，不断获取鼠标信息，并判断是否是轮到妖怪攻击
    {
        System.out.println("start get Minfo");
        while (true)
        {
            try{
                Thread.sleep(50);

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            PointerInfo pointerInfo=MouseInfo.getPointerInfo();
            Point p=pointerInfo.getLocation();
            highlight(p.getX(),p.getY());
            infobar.reloadBars();
            if(myMove==0)
            {
                while(true)     //在妖怪攻击前首先要等待人类完成剩余的动作
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean hready=true;
                    for(Charactors x : brotherhood.cbs)
                    {
                        if(!x.avaliable.get() && x.alive)
                        {
                            hready=false;
                            break;
                        }
                    }
                    if(grandpa.alive && !grandpa.avaliable.get())
                        hready=false;
                    if(!animationHandler.avaliable.get())
                        hready=false;
                    if(hready)
                        break;
                }
                debug.append("human ready\n");
                bot.nextMove();
                myMove=oneRound;
                updateCount();
            }
        }
    }

    private void highlight(double x, double y)          //高亮显示当前鼠标所在区域
    {
        Point tmploc=father.getLocation();
        BackGroundPanel tmp=(BackGroundPanel)battlefield;
        x=x-tmploc.x-tmp.xstart;
        y=y-tmploc.y-tmp.ystart;
        int virtuleX=(int)x/tmp.block.x;
        int virtuleY=(int)y/tmp.block.y;
        if(virtuleX<0 || virtuleX>=virtualField.width || virtuleY<0 || virtuleY>=virtualField.height)
            return;
        String name="empty";
        if(virtualField.field[virtuleY][virtuleX]!=null)
            name=virtualField.field[virtuleY][virtuleX].basename+" HP:"+virtualField.field[virtuleY][virtuleX].HP;
        curX=virtuleX;
        curY=virtuleY;
        hlmask.setBounds(tmp.xstart+virtuleX*tmp.block.x, tmp.ystart+virtuleY*tmp.block.y,tmp.block.x,tmp.block.y);
    }

    public void checkEND(Charactors diechat)        //判读游戏是否结束，以一方全部被消灭结束
    {
        int result=animationHandler.checkend(diechat);
        if(result!=0)
            END(result);
    }

    private boolean END(int winner) {           //游戏结束，弹出结束信息
        int width=battlefield.getWidth(),height=battlefield.getHeight();
//        new gameEnd(width/4,height/4,width/2,height/2,father);
        new gameEnd(0,0,width,height,winner,father);
        return true;
    }
}

class BackGroundPanel extends JPanel{                //以特定图片作为背景的Panel
    private Image image=(Image)new ImageIcon(this.getClass().getResource("background.jpg")).getImage();
    int deltax,deltay;      //用于布局
    int xstart,ystart;      //用于布局
    Point block;              //用于布局
    public BackGroundPanel()
    {
        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension screenSize=kit.getScreenSize();
        int width=screenSize.width;
        int height=screenSize.height;

        deltax=deltay=height/17;
        xstart=4*deltax;
        ystart=deltay/2;
        block=new Point();
        block.x=deltax*15/10;
        block.y=block.x;
    }
    protected void paintComponent(Graphics g)
    {
        g.drawImage(image,0,0,this.getWidth(),this.getHeight(),this);
    }
}

class actionButton extends JButton      //不再使用
{                 //使用图片形状的按钮，按键按下时会改变成另一幅图片
    private Image img1;
    private Image img2;
    public actionButton(int x, int y, int width, int height, String img1name, String img2name)          //构造时需要指定大小，位置和图片文件名称
    {
        this.setBounds(x,y,width,height);
        ImageIcon imgicon1=new ImageIcon(this.getClass().getResource(img1name));
        ImageIcon imgicon2=new ImageIcon(this.getClass().getResource(img2name));
        imgicon1.setImage(imgicon1.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));        //缩放图片到适当大小
        img1=imgicon1.getImage();
        imgicon2.setImage(imgicon2.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        img2=imgicon2.getImage();
        this.setBorder(null);
        this.setIcon(imgicon1);
        this.setMargin(new Insets(0,0,0,0));
        this.setContentAreaFilled(false);
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                setIcon(imgicon2);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setIcon(imgicon1);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}

class TransparentPanel extends JPanel   //一个半透明的方块，用于高亮当前鼠标经过区域
{
    protected float transparency;
    public void setTransparency(float trans)
    {
        transparency=trans;
    }
    @Override
    protected void paintComponent(Graphics g)
    {
//        super.paintComponent(g);
        Graphics2D graphics2D=(Graphics2D)g.create();
        graphics2D.setBackground(Color.GRAY);
        graphics2D.setComposite(AlphaComposite.SrcOver.derive(transparency));
        graphics2D.fillRect(0,0,getWidth(),getHeight());
        graphics2D.dispose();
    }
}