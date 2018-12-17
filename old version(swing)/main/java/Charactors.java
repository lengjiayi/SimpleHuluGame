import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Charactors implements Runnable{       //角色类
    public String typename;        //每个角色有自己的名字
    public String basename;
    public int charno;
    ImageIcon baseimg;                  //基本贴图
    ImageIcon movingimg;               //运动时贴图
    ImageIcon regularattack;           //常规攻击贴图
    ImageIcon AOE;                      //AOE攻击贴图
    ImageIcon ZXC;                      //ZXC攻击贴图
    ImageIcon RIP;                      //死亡后贴图，默认都是Rest In Peace墓碑
    boolean aoeavaliable=true;       //aoe攻击是否可用
    boolean zxcavaliable=true;       //大招是否可用

    protected BackGroundPanel world;         //每个角色所在的容器
    protected BattleField battle;
    protected JLabel testLabel=new JLabel("T");     //每个角色在界面中表现为一个具有特殊图片的JLabel
    public boolean animate=false;   //是否有移动动画需要执行


    public int positionX,positionY;         //每个角色有自己的位置坐标
    public int nextX=0, nextY=0;             //目的地坐标
    public double tmpX=0,tmpY=0;             //执行动画时具有的中间坐标
    public char testview;                    //在命令行中输出代表的字符
    public boolean alive=true;              //角色是否存活
    public boolean monster=true;                 //是否为敌人
    public boolean remoteattack=false;     //是否具有远程攻击属性

    public boolean heal=false;              //是否为辅助类
    public int maxHP=100;
    public int maxMP=100;
    public int HP=100;
    public int MP=100;
    public int regularcost=10;
    public int mpcost=20;
    public int zxccost=50;


    public void load()
    {
        baseimg=new ImageIcon(this.getClass().getResource(basename+".PNG"));
        baseimg.setImage(baseimg.getImage().getScaledInstance(world.block.x, world.block.y, Image.SCALE_SMOOTH));
        movingimg=new ImageIcon(this.getClass().getResource(basename+"mov1.PNG"));
        movingimg.setImage(movingimg.getImage().getScaledInstance(world.block.x, world.block.y, Image.SCALE_SMOOTH));
        RIP=new ImageIcon(this.getClass().getResource("RIP.PNG"));
        RIP.setImage(RIP.getImage().getScaledInstance(world.block.x, world.block.y, Image.SCALE_SMOOTH));
        loadtmpimg();
    }
    public void moveto(int x,int y)          //移动到新位置
    {
        nextX=x;
        nextY=y;
        animate=true;
    }
    public int realX(int x)     //将在虚拟战场上的坐标转换为在窗口中的坐标
    { return world.xstart+x*world.block.x; }
    public int realY(int y)
    { return world.ystart+y*world.block.y; }
    public void StandStill()                 //更新自己在战场上的位置信息
    {
        if(alive) {         //只有活着的角色能够在战场上移动
//            virtualField.field[positionY][positionX] = testview;
            testLabel.setBounds(world.xstart+positionX*world.block.x,world.ystart+positionY*world.block.y,testLabel.getWidth(),testLabel.getHeight());
        }
    }
    public void loadtmpimg()    //加载非常规贴图
    {
        int size=world.block.x;
        if(this.getClass().getResource(basename+"a1.PNG")==null)
        {
            ImageIcon tmp = new ImageIcon(this.getClass().getResource("tmpskill.PNG"));
            regularattack=new ImageIcon();
            regularattack.setImage(tmp.getImage().getScaledInstance(size,size, Image.SCALE_SMOOTH));
            AOE=new ImageIcon();
            ZXC=new ImageIcon();
            AOE.setImage(tmp.getImage().getScaledInstance(size,size, Image.SCALE_SMOOTH));
            ZXC.setImage(tmp.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
            return;
        }
        regularattack=new ImageIcon(this.getClass().getResource(basename+"a1.PNG"));
        regularattack.setImage(regularattack.getImage().getScaledInstance(size,size, Image.SCALE_SMOOTH));
        AOE=new ImageIcon(this.getClass().getResource(basename+"a2.PNG"));
        AOE.setImage(AOE.getImage().getScaledInstance(size,size, Image.SCALE_SMOOTH));
        ZXC=new ImageIcon(this.getClass().getResource(basename+"a3.PNG"));
        ZXC.setImage(ZXC.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    public AtomicInteger cmd=new AtomicInteger(0);      //用于接收命令
    public AtomicBoolean avaliable=new AtomicBoolean(true);     //标记当前角色是否出于执行其他命令的状态

    public void run()
    {
//        System.out.println(basename);
        while (true)
        {
            try {
                Thread.sleep(50);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            cmdHandler();       //命令处理函数
//            if(!alive)
//                break;
        }
    }

    protected void cmdHandler()
    {
        switch (cmd.get())
        {
            case 0: break;
            case 1: movement(); cmd.set(0);break;       //1 号命令为执行移动动画
            case 2: Attack1(); cmd.set(0);break;        //2~4号命令为进行不同的攻击
            case 3: Attack2(); cmd.set(0);break;
            case 4: Attack3(); cmd.set(0);break;
        }
    }


    protected void movement() {
        avaliable.set(false);
        if (monster)            //妖怪可以无视物理规则移动，而人类则遇到敌人会被挡住
        {
            walktodst();
            avaliable.set(true);
            return;
        }
        int dstX = nextX;
        nextX = positionX;
        if (nextY > positionY) {
            for (int i = positionY; i < nextY; i++) {
                if (virtualField.field[i+1][positionX] != null && virtualField.field[i+1][positionX].monster != monster) {
                    nextY = i;
                    break;
                }
            }
        }
        else {
            for (int i = positionY; i > nextY; i--) {
                if (virtualField.field[i-1][positionX] != null && virtualField.field[i-1][positionX].monster != monster) {
                    nextY = i;
                    break;
                }
            }
        }
        walktodst();
        nextX=dstX;
        if (nextX > positionX) {
            for (int i = positionX; i < nextX; i++) {
                if (virtualField.field[positionY][i + 1] != null && virtualField.field[positionY][i + 1].monster != monster) {
                    nextX = i;
                    break;
                }
            }
        }
        else {
            for (int i = positionX; i > nextX; i--) {
                if (virtualField.field[positionY][i - 1] != null && virtualField.field[positionY][i - 1].monster != monster) {
                    nextX = i;
                    break;
                }
            }
        }
        walktodst();
        avaliable.set(true);
    }

    protected void walktodst()          //移动到指定位置的逐帧动画
    {
        tmpX=realX(positionX);
        tmpY=realY(positionY);
//            System.out.printf("%s:%d, %d to %d, %d\n",x.typename,x.positionX,x.positionY,x.nextX,x.nextY);
        if(animate)
            testLabel.setIcon(movingimg);
        boolean change=false;
        int duration=Math.abs(nextX-positionX)+Math.abs(nextY-positionY);
        duration=duration*10;
        for(int i=0;i<duration;i++)     //人物走动时间和曼哈顿距离成正比
        {
            tmpX+=(double)(realX(nextX)-realX(positionX))/duration;
            tmpY+=(double)(realY(nextY)-realY(positionY))/duration;
            testLabel.setLocation((int)tmpX,(int)tmpY);
            if(i%6==0)
            {
                change=!change;
                if (!change) {      //通过不断变换人物贴图实现逐帧动画
                    testLabel.setIcon(baseimg);
                    testLabel.repaint();
                } else {
                    testLabel.setIcon(movingimg);
                    testLabel.repaint();
                }
            }
            try {
                Thread.sleep(1000 / 36);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        testLabel.setIcon(baseimg);
        positionX=nextX;
        positionY=nextY;
        testLabel.setLocation(realX((int)positionX),realY((int)positionY));
    }

    protected void Attack1(){   //常规攻击
        if(monster && positionX==0) {       //如果攻击超出边界则没有效果
            avaliable.set(true);
            return;
        }if(!monster && positionX==virtualField.width-1) {
            avaliable.set(true);
            return;
        }
        Charactors target=null;     //攻击目标
        if(monster)
            target=virtualField.field[positionY][positionX-1];      //妖怪从右向左攻击
        else
            target=virtualField.field[positionY][positionX+1];      //人类从左向右攻击
        JLabel tmp=new JLabel();
        tmp.setSize(world.block.x,world.block.y);
        tmp.setIcon(regularattack);
        tmp.setOpaque(false);
        tmp.setVisible(true);
        world.add(tmp,0);
        world.repaint();
        if(remoteattack==false) {   //如果为非远程攻击，则直接攻击攻击方向的下一个区域
            if(!monster)
                tmp.setLocation(realX((int)positionX+1),realY(positionY));
            else
                tmp.setLocation(realX((int)positionX-1),realY(positionY));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else        //如果是远程攻击
        {
            int aim,duration;
            float curloc,deltaloc;
            if(!monster) {
                aim = positionX + 1;            //首先判断攻击目标，如果攻击路线上没有攻击目标则攻击会一直飞出战场
                while (true) {
                    if (aim < virtualField.width &&
                            (virtualField.field[positionY][aim] == null ||
                                    !virtualField.field[positionY][aim].alive ||
                                    (virtualField.field[positionY][aim].monster == heal)))
                        aim++;
                    else
                        break;
                }
                if (aim == virtualField.width)
                    aim--;
                duration = 2+(aim - positionX) * 5;     //攻击飞行时间和距离成正比
                curloc=realX(positionX+1)-world.block.x/2;
                deltaloc=(realX(aim)-curloc)/duration;
            }
            else
            {
                aim = positionX - 1;
                while (true) {
                    if (aim >= 0 &&
                            (virtualField.field[positionY][aim] == null ||
                                    !virtualField.field[positionY][aim].alive ||
                                    virtualField.field[positionY][aim].monster))
                        aim--;
                    else
                        break;
                }
                if (aim <0)
                    aim=0;
                duration = 2+(positionX - aim) * 5;
                curloc=realX(positionX-1)+world.block.x/2;
                deltaloc=(realX(aim)-curloc)/duration;
            }
            target=virtualField.field[positionY][aim];
            for(int i=0;i<duration;i++)
            {
                tmp.setLocation((int)curloc,realY(positionY));
                tmp.repaint();
                curloc+=deltaloc;
                try {
                    Thread.sleep(1000/36);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(target!=null && ((target.monster!=monster && !heal)||(target.monster==monster && heal)))     //攻击作用在角色上，这里直接禁止了队友伤害和为敌方加血这种不明智的行为
            target.injured(regularcost);
        world.remove(tmp);
        world.repaint();
        avaliable.set(true);
    }
    protected void Attack2(){           //AOE攻击，使用相应的MP值对周围三个取阈造成伤害
        if(MP<mpcost) {
            avaliable.set(true);
            return;
        }JLabel tmp1=new JLabel();
        tmp1.setSize(world.block.x,world.block.y);
        tmp1.setIcon(AOE);
        tmp1.setOpaque(false);
        if(!monster)
            tmp1.setLocation(realX((int)positionX+1),realY(positionY));
        else
            tmp1.setLocation(realX((int)positionX-1),realY(positionY));
        JLabel tmp2=new JLabel();
        tmp2.setSize(world.block.x,world.block.y);
        tmp2.setIcon(AOE);
        tmp2.setOpaque(false);
        tmp2.setLocation(realX((int)positionX),realY(positionY+1));
        JLabel tmp3=new JLabel();
        tmp3.setSize(world.block.x,world.block.y);
        tmp3.setIcon(AOE);
        tmp3.setOpaque(false);
        tmp3.setLocation(realX((int)positionX),realY(positionY-1));
        world.add(tmp1,0);
        world.add(tmp2,0);
        world.add(tmp3,0);
        world.repaint();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(positionY!=0 && virtualField.field[positionY-1][positionX]!=null)
            virtualField.field[positionY-1][positionX].injured(mpcost);
        if(positionY!=virtualField.height-1 && virtualField.field[positionY+1][positionX]!=null)
            virtualField.field[positionY+1][positionX].injured(mpcost);
        Charactors lasttarget=null;
        if(monster && positionX>0)
            lasttarget=virtualField.field[positionY][positionX-1];
        else if(!monster && positionX<virtualField.width-1)
            lasttarget=virtualField.field[positionY][positionX+1];
        if(lasttarget!=null)
            lasttarget.injured(mpcost);
        world.remove(tmp1);
        world.remove(tmp2);
        world.remove(tmp3);
        world.repaint();
        MP-=mpcost;
        if(MP<mpcost)
            aoeavaliable=false;
        avaliable.set(true);
    }
    protected void Attack3() {      //大招攻击，和普通攻击类似，每个角色一次游戏只能使用一次
        if (monster && positionX == 0) {
            avaliable.set(true);
            return;
        }
        if (!monster && positionX == virtualField.width - 1) {
            avaliable.set(true);
            return;
        }
        Charactors target = null;
        if (monster)
            target = virtualField.field[positionY][positionX - 1];
        else
            target = virtualField.field[positionY][positionX + 1];
        JLabel tmp = new JLabel();
        tmp.setSize(world.block.x, world.block.y);
        tmp.setIcon(ZXC);
        tmp.setOpaque(false);
        tmp.setVisible(true);
        world.add(tmp,0);
        world.repaint();
        if (remoteattack == false) {
            if(!monster)
                tmp.setLocation(realX((int) positionX + 1), realY(positionY));
            else
                tmp.setLocation(realX((int) positionX - 1), realY(positionY));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            int aim, duration;
            float curloc, deltaloc;
            if (!monster) {
                aim = positionX + 1;
                while (true) {
                    if (aim < virtualField.width &&
                            (virtualField.field[positionY][aim] == null ||
                                    !virtualField.field[positionY][aim].alive ||
                                    (virtualField.field[positionY][aim].monster==heal)))
                        aim++;
                    else
                        break;
                }
                if (aim == virtualField.width)
                    aim--;
                duration = 2 + (aim - positionX) * 5;
                curloc = realX(positionX + 1) - world.block.x / 2;
                deltaloc = (realX(aim) - curloc) / duration;
            } else {
                aim = positionX - 1;
                while (true) {
                    if (aim >= 0 &&
                            (virtualField.field[positionY][aim] == null ||
                                    !virtualField.field[positionY][aim].alive ||
                                    virtualField.field[positionY][aim].monster))
                        aim--;
                    else
                        break;
                }
                if (aim < 0)
                    aim = 0;
                duration = 2 + (positionX - aim) * 5;
                curloc = realX(positionX - 1) + world.block.x / 2;
                deltaloc = (realX(aim) - curloc) / duration;
            }
            target = virtualField.field[positionY][aim];
            for (int i = 0; i < duration; i++) {
                tmp.setLocation((int) curloc, realY(positionY));
                tmp.repaint();
                curloc += deltaloc;
                try {
                    Thread.sleep(1000 / 36);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (target != null && ((target.monster!=monster && !heal)||(target.monster==monster && heal)))
            target.injured(zxccost);
        world.remove(tmp);
        world.repaint();
        this.zxcavaliable = false;
        avaliable.set(true);
    }
    public void injured(int cost)       //受伤并进行死亡判定
    {
        if(cost<0 && HP-cost>maxHP)
            HP=maxHP;
        else
            HP-=cost;
        if(HP<=0)
            DIE();
    }
    protected void DIE()        //死亡判定
    {
        this.alive=false;
        this.testLabel.setIcon(RIP);
        this.testLabel.repaint();
        virtualField.field[positionY][positionX]=null;
        battle.checkEND(this);      //判断游戏是否结束
    }
}

class Rank{         //为所有角色分等级，默认高级角色可以指挥低级角色，并且对立阵营角色在本方心中为等级最低
    static public String[] MonsterClass=new String[]{"小喽啰","蝎子精","蛇精","青蛇精"};       //妖怪阵营等级（由低到高）
    static public String[] HumanClass=new String[]{"百姓","老七","老六","老五","老四","老三","老二","老大","兄弟们","爷爷"};     ////人类阵营等级（由低到高
    static public int find(String str, boolean human)       //查询某一角色的等级，如果非同一阵营则返回最低等级-1
    {
        int index=0;
        if(human) {
            for (; index < HumanClass.length; index++)
                if (str.equals(HumanClass[index]))
                    return index;
            return -1;
        }
        else
        {
            for (; index < MonsterClass.length; index++)
                if (str.equals(MonsterClass[index]))
                    return index;
            return -1;
        }
    }

}

enum CucurbitBoys{          //葫芦娃兄弟们的枚举类型
    RedBro, OrangeBro, YellowBro, GreenBro, BlueBro, IndigoBro, VioletBro;
    static protected String[] names={"老大","老二","老三","老四","老五","老六","老七"};
    static protected String[] colors={"红色","橙色","黄色","绿色","青色","蓝色","紫色"};
    static protected String[] imgname={"brother1.PNG","brother2.PNG","brother3.PNG","brother4.PNG","brother5.PNG","brother6.PNG","brother7.PNG"};
    static protected boolean[] isremote={false,true,true,false,false,false,false};     //规定葫芦娃的远程攻击属性
    static protected int[] HP={120,90,80,60,100,150,60};        //每种葫芦娃的属性值保存在这四个数组中
    static protected int[] regularcost={10,10,5,20,20,30,10};
    static protected int[] MPcost={20,40,20,40,30,20,20};
    static protected int[] ZXCcost={90,70,80,100,90,60,50};
    public String getName(){ return names[ordinal()]; }
    public String getColor(){ return colors[ordinal()]; }
    public boolean IsRemote(){ return isremote[ordinal()]; }
    public int getHP(){ return HP[ordinal()]; }
    public int getregCost(){ return regularcost[ordinal()]; }
    public int getMpCost(){ return MPcost[ordinal()]; }
    public int getZXCCost(){ return ZXCcost[ordinal()]; }
}

class Grandpa extends Charactors        //老爷爷，可以为葫芦娃远程加血
{
    Grandpa(BackGroundPanel father, BattleField bt)
    {               //老爷爷初始时在战场左下角观战
        world=father;
        charno=7;
        battle=bt;
        basename="grandpa";
        monster=false;
        load();
        testLabel.setSize(world.block.x,world.block.y);
        testLabel.setIcon(baseimg);
        testLabel.setOpaque(false);
        world.add(testLabel);           //将老爷爷加入战场界面中
        nextX=positionX=0;
        nextY=positionY=virtualField.height-1;
        maxHP=HP=80;
        regularcost=-30;
        mpcost=-30;
        zxccost=-100;
        remoteattack=true;
        heal=true;
        typename="爷爷";
        testview='Y';
    }
}

class CucurbitBoy extends Charactors        //葫芦娃类
{
    //按照排行顺序存放葫芦娃的姓名和颜色
    static protected int nextid=0;     //每个葫芦娃都是唯一的，nextid用来记录下一个未出生的葫芦娃排行
    private CucurbitBoys id;            //每个葫芦娃拥有自己的id，描述他在兄弟中的排行
    private int innerPosition;        //葫芦娃在队列内部的位置
    CucurbitBoy(BackGroundPanel father, BattleField bt) throws Exception {
        world=father;
        battle=bt;
        this.id=CucurbitBoys.values()[CucurbitBoy.nextid];
        charno=nextid;
        remoteattack=id.IsRemote();
        maxHP=id.getHP();
        HP=maxHP;
        regularcost=id.getregCost();
        mpcost=id.getMpCost();
        zxccost=id.getZXCCost();


        typename=id.getName();
        basename="brother"+(id.ordinal()+1);
        this.monster=false;
        load();
        testLabel.setSize(world.block.x,world.block.y);
//        System.out.println(father.deltax);
        testLabel.setIcon(baseimg);
        testLabel.setOpaque(false);
        world.add(testLabel);           //将葫芦娃加入界面中
        //每个葫芦娃只能出生一次，并且最多有七个
        if(CucurbitBoy.nextid>=7)
        {
            throw new Exception("葫芦娃只有七个！");
        }
        //为葫芦娃分配排行
        testview=(char)('1'+this.getID());

        CucurbitBoy.nextid++;
    }
    //返回自己的名字
    public String tellName(){ return id.getName(); }
    //返回自己的颜色
    public String tellColor(){ return id.getColor(); }
    //返回自己的排行
    public int getID(){ return id.ordinal(); }
    public void setInnerPosition(String type, int newpst)       //设定该葫芦娃在兄弟队伍中的相对位置
    {
        if(Rank.find(typename,true)>Rank.find(type,true))       //如果等级比自己低则无权指挥自己
            return;
        innerPosition=newpst;
    }
    public void resort(String type, int mx, int my)             //按照兄弟们整体的位置重新站队
    {
        if(Rank.find(typename,true)>Rank.find(type,true))
            return;
        moveto(mx, my+innerPosition);
    }

}

class Roro extends Charactors           //小喽啰类
{
    public int number;                  //小喽啰的编号
    private formations curFMT=null;     //小喽啰们当前应该站的阵型
    public static int nextid=0;
    public Roro(BackGroundPanel father, BattleField bt){
        world=father;
        battle=bt;
        basename="Roro";
        charno=nextid+9;
        nextid++;
        load();
        testLabel.setSize(world.block.x,world.block.y);
        testLabel.setIcon(baseimg);
        testLabel.setOpaque(false);
        world.add(testLabel);           //将小喽啰加入界面中
        typename="小喽啰";
        testview='R';
        nextX=positionX=virtualField.width-1;
        nextY=positionY=0;
        maxHP=HP=60;
        regularcost=10;
        aoeavaliable=false;
        zxccost=30;
        remoteattack=true;
    }
    public void resort(String type, int mx, int my, formations FMT)         //在高级妖怪要求换阵型时，每个小喽啰重新找到自己应该站的位置
    {
        if(Rank.find(typename,false)>Rank.find(type,false))
            return;
        curFMT=FMT;
//        System.out.println(curFMT.rightDistance);
        int npX=mx+curFMT.RelativePosition[number][0];          //这两项是在当前阵法中这个小喽啰正确的位置坐标
        int npY=my+curFMT.RelativePosition[number][1];
        moveto(npX, npY);
    }
}

class Scorpion extends Charactors                       //蝎子精
{
    public int curFMT=-1;
    private formations[] learnedFormations=new formations[]{new Fengshi(), new Yanyue(), new Yulin()};       //蝎子精学过的阵型
    public Roro[] troops=new Roro[13];                  //每个蝎子精带有13个小喽啰
    public Scorpion(BackGroundPanel father, BattleField bt)
    {
        world=father;
        battle=bt;
        basename="scorption";
        charno=9;
        load();
        testLabel.setSize(world.block.x,world.block.y);
        testLabel.setIcon(baseimg);
        testLabel.setOpaque(false);
        world.add(testLabel);       //将蝎子精加入战场界面中
        typename="蝎子精";
        testview='X';
        nextX=positionX=virtualField.width-1;
        nextY=positionY=0;
        maxHP=HP=120;
        regularcost=20;
        mpcost=40;
        zxccost=80;
        remoteattack=true;
        for(int i=0;i<troops.length;i++) {
            troops[i]=new Roro(world,battle);
            troops[i].number = i;                       //蝎子精为小喽啰编号
            Thread tmp=new Thread(troops[i]);
            tmp.start();
        }
        changeFMT(-1);                                      //开始列阵
    }

    @Override
    public void StandStill()                            //蝎子精不仅要更新自己的位置，并且要告诉喽罗们更新他们的位置
    {
        super.StandStill();
        if(alive==false)                                //如果蝎子精死了，则喽罗们会一哄而散
            return;
        for(int i=0;i<troops.length;i++)
            troops[i].StandStill();
    }

    public void changeFMT(int index)                             //蝎子精改变阵型，并且要求喽罗们按照新阵型站队
    {
        if(alive==false)        //如果蝎子精战死则所有喽啰群龙无首，不再移动
            return;
        if(index<0 || index>=learnedFormations.length)
            curFMT=(curFMT+1)%learnedFormations.length;
        else
            curFMT=index;
        moveto(virtualField.width-learnedFormations[curFMT].rightDistance-2,(virtualField.height-1)/2);
        for(int i=0;i<troops.length;i++) {
            if(troops[i].alive)
                troops[i].resort(typename,nextX,nextY,learnedFormations[curFMT]);
        }
    }

    @Override
    protected void Attack3() {      //改写大招，当蝎子精使用大招时，所有小妖精都一起发动大招
        for(int i=0;i<troops.length;i++)
            if(troops[i].alive && troops[i].zxcavaliable)
            {
                troops[i].cmd.set(4);
            }
        super.Attack3();
    }
}

class Snake extends Charactors          //蛇精，可以为妖怪加血
{
    public Snake(BackGroundPanel father, BattleField bt)
    {                                     //蛇精初始时在战场右上角观战
        world=father;
        battle=bt;
        basename="snake";
        charno=8;
        load();
        testLabel.setSize(world.block.x,world.block.y);
        testLabel.setIcon(baseimg);
        testLabel.setOpaque(false);
        world.add(testLabel);
        nextX=positionX=virtualField.width-1;
        nextY=positionY=0;
        maxHP=HP=70;
        regularcost=-30;
        mpcost=-50;
        zxccost=-100;
        heal=true;
        typename="蛇精";
        testview='S';
    }
}