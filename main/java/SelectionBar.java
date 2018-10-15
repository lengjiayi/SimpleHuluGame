import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class SelectionBar extends JPanel {      //一个侧边栏（技能选择框），为以后添加各种角色的技能做准备
    private BattleField father;
    private BackGroundPanel world;
    private InfoBar relative;
    private Image image=(Image)new ImageIcon(this.getClass().getResource("floatbar.PNG")).getImage();
    private Font mfont=new Font("华文楷体",Font.BOLD,35);
    private JLabel curname;
    private JLabel regularattack;
    private JLabel AOE;
    private JLabel ZXC;
    private ImageIcon attack1;
    private ImageIcon attack2;
    private ImageIcon attack3;
    private Charactors tmpChats=null;
    public SelectionBar(BattleField battle, BackGroundPanel panel, InfoBar ibar)
    {
        father=battle;
        world=panel;
        relative=ibar;
        this.setLayout(null);
        int size=BattleField.selectionsize;

        System.out.println("size"+size);

        curname=new JLabel("空");            //当前选择角色的姓名
        curname.setBounds(world.deltax,world.deltay,size*2,size);
        curname.setForeground(new Color(255,215,000));
        curname.setFont(mfont);

        regularattack=new JLabel();        //选择普通攻击
        regularattack.setBounds(world.deltax/3*5-size/2,world.deltax*3,size,size);
        regularattack.setOpaque(false);
        regularattack.setBorder(null);

        AOE=new JLabel();               //选择群体攻击
        AOE.setBounds(world.deltax/3*5-size/2,world.deltax*6,size,size);
        AOE.setOpaque(false);
        AOE.setBorder(null);

        ZXC=new JLabel();               //选择大招
        ZXC.setBounds(world.deltax/3*5-size/2,world.deltax*9,size,size);
        ZXC.setOpaque(false);
        ZXC.setBorder(null);

        this.add(curname);
        this.add(regularattack);
        this.add(AOE);
        this.add(ZXC);

        attack1=new ImageIcon(this.getClass().getResource("attack1.PNG"));
        attack1.setImage(attack1.getImage().getScaledInstance(size,size, Image.SCALE_SMOOTH));
        attack2=new ImageIcon(this.getClass().getResource("attack2.PNG"));
        attack2.setImage(attack2.getImage().getScaledInstance(size,size, Image.SCALE_SMOOTH));
        attack3=new ImageIcon(this.getClass().getResource("attack3.PNG"));
        attack3.setImage(attack3.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));

        regularattack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {    //使用普通攻击
                if(father.myMove<=0)        //如果这一回合的步数用尽则不能操作
                    return;
                super.mouseClicked(e);
                if(tmpChats==null || tmpChats.avaliable.get()==false)
                    return;
                father.myMove--;
                father.updateCount();
                tmpChats.avaliable.set(false);
                tmpChats.cmd.set(2);
            }
        });
        AOE.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {       //使用AOE攻击
                if(father.myMove<=0)
                    return;
                super.mouseClicked(e);
                if(tmpChats==null || tmpChats.avaliable.get()==false)
                    return;
                father.myMove--;
                father.updateCount();
                tmpChats.avaliable.set(false);
                tmpChats.cmd.set(3);
                reloadBars(null);
                relative.reloadBars();
            }
        });
        ZXC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(father.myMove<=0)
                    return;
                super.mouseClicked(e);
                if(tmpChats==null || tmpChats.avaliable.get()==false)
                    return;
                father.myMove--;
                father.updateCount();
                tmpChats.avaliable.set(false);
                tmpChats.cmd.set(4);
                reloadBars(null);
                relative.reloadBars();
            }
        });
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image,0,0,this.getWidth(),this.getHeight(),this);
    }

    public void reloadBars(Charactors newchat)      //更新当前角色的技能选择框
    {
        tmpChats=newchat;
//        tmpChats=virtualField.field[father.curY][father.curX];
        regularattack.setIcon(null);
        AOE.setIcon(null);
        ZXC.setIcon(null);
        if(tmpChats==null || tmpChats.monster) {
//        if(tmpChats==null) {                  //显示妖怪的控制框
            curname.setText("空");
            return;
        }
        String newname="";
        switch (tmpChats.basename.charAt(0))
        {
            case 'b': newname=((CucurbitBoy)tmpChats).tellName();break;
            default: newname=tmpChats.typename;
        }
        curname.setText(newname);

        regularattack.setIcon(attack1);
        if(tmpChats.aoeavaliable)       //如果某一技能不可用则不显示该按钮
            AOE.setIcon(attack2);
        if(tmpChats.zxcavaliable)
            ZXC.setIcon(attack3);
        repaint();
    }
}
