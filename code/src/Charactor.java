package sample;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Charactor implements Runnable {
    //MARK: UIRelative
    /** 角色在真实画布上的位置*/
    public AtomicInteger PositionX=new AtomicInteger(0), PositionY=new AtomicInteger(0);
    /** 当前是否正在执行操作*/
    public AtomicBoolean avaliable=new AtomicBoolean(true);
    /** 用于接收命令*/
    public AtomicInteger cmd=new AtomicInteger(0);
    /** 是否使用移动中贴图，用于实现移动动画*/
    public AtomicBoolean mov1=new AtomicBoolean(false);
    /** 角色移动的目的地*/
    protected int DstX, DstY;

    //MARK: Properties
    /** 在Configs和ViewBundle中的下标*/
    public int IdNo;
    /** 角色移动速度*/
    protected int speed=3;
    /** 角色资源名（用于获取图片等资源）*/
    public String basename;
    /** 角色姓名*/
    public String name;
    /** 当前是否存活*/
    public boolean alive=true;
    /** 是否为妖怪阵营*/
    public boolean monster=false;
    /** s是否具有远程攻击属性*/
    public boolean remoteattack=false;
    /** 是否为治疗系角色*/
    public boolean heal=false;

    //MARK: battle relative
    /** 战斗属性*/
    public int maxHP=100;
    public int maxMP=100;
    public int HP=100;
    public int MP=100;
    public int regularcost=10;
    public int mpcost=20;
    public int zxccost=50;
    /** 当前AOE攻击是否可用*/
    public boolean aoeavaliable=true;
    /** 当前ZXC攻击是否可用*/
    public boolean zxcavaliable=true;

    /**
     * 角色基类
     * @param sx 初始位置横坐标
     * @param sy 初始位置纵坐标
     * @param nx 进场动画的目的地横坐标
     * @param ny 进场动画的目的地纵坐标
     */
    public Charactor(int sx, int sy, int nx, int ny)
    {
        iPoint s=virtualField.vpTorp(sx,sy);
        iPoint t=virtualField.vpTorp(nx,ny);
        PositionX.set(s.x);
        PositionY.set(s.y);
        DstX=t.x;
        DstY=t.y;
    }

    /** 初始化一个角色的属性值*/
    protected void setProps(String basename, String name, int maxHP, int maxMP, int regularcost, int mpcost, int zxccost)
    {
        this.basename = basename;
        this.name = name;
        this.maxHP = this.HP = maxHP;
        this.maxMP = this.MP = maxMP;
        this.regularcost = regularcost;
        this.mpcost = mpcost;
        this.zxccost = zxccost;
    }

    /** 设定目的地到特定位置*/
    public void moveto(iPoint dst) { moveto(dst.x, dst.y); }
    /** 设定目的地到特定位置*/
    public void moveto(int x,int y)      //移动到新位置
    {
        DstX=x;
        DstY=y;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cmdHandler();       //命令处理函数
        }
    }

    /** 在循环中等待新命令*/
    protected void cmdHandler()
    {
        switch (cmd.get())
        {
            case 0: break;                  //当前无任务
            case 1: WalkToDst(); cmd.set(0);break;       //1 号命令为移动到目的地
            case 2: Attack1(); cmd.set(0); break;
            case 3: Attack2(); cmd.set(0); break;
            case 4: Attack3(); cmd.set(0); break;
            default: break;
        }
    }

    /** 用于产生逐帧移动动画的位置信息*/
    protected void WalkToDst()
    {
        avaliable.set(false);
        virtualField.set(this, PositionX.get(), PositionY.get(),true);
        double dx,dy;
        double realspeed=(double)speed/36;
        dx=(DstX-PositionX.get());
        dy=(DstY-PositionY.get());
        double distance=Math.sqrt(dx*dx+dy*dy);
        int TimeConsume=(int)distance/speed;    //移动动画所需时间=距离/速度
        dx/=TimeConsume;
        dy/=TimeConsume;
        double curx=PositionX.get();
        double cury=PositionY.get();

        virtualField.set(this, DstX, DstY,false);
        for(int i=0;i<TimeConsume;i++) {
            curx+=dx;
            cury+=dy;
            PositionX.set((int)curx);
            PositionY.set((int)cury);
//            System.out.println(name +PositionX.get() +","+PositionY.get());
            try {
                Thread.sleep(1000 / 36);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(i%5==0)
                mov1.set(!mov1.get());
        }
        PositionX.set((int)DstX);
        PositionY.set((int)DstY);
        mov1.set(false);
        avaliable.set(true);
    }

    protected void Attack1()
    {
        bulletController.start(this, bulletController.ATTACK_REG);
        avaliable.set(true);
    }
    protected void Attack2()
    {
        MP -= mpcost;
        if(MP<mpcost)           //MP不足时不能再使用对群攻击
            aoeavaliable=false;
        bulletController.start(this, bulletController.ATTACK_AOE_1);
        bulletController.start(this, bulletController.ATTACK_AOE_2);
        bulletController.start(this, bulletController.ATTACK_AOE_3);
        avaliable.set(true);
    }
    protected void Attack3()
    {
        zxcavaliable = false;       //大招只能使用一次
        bulletController.start(this, bulletController.ATTACK_ZXC);
        avaliable.set(true);
    }
    public void injured(int cost)       //受伤并进行死亡判定
    {
//        System.out.println(name + cost);
        if(cost<0 && HP-cost>maxHP)     //恢复血量不会超过最大值
            HP = maxHP;
        else
            HP -= cost;
        if(HP <= 0)
            DIE();
    }
    protected void DIE()        //死亡
    {
        this.alive=false;
    }
}

/** 葫芦娃兄弟们属性的枚举类型*/
enum CucurbitBoys{
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