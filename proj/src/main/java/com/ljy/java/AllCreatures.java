package com.ljy.java;

import jdk.nashorn.internal.runtime.regexp.joni.Config;

class CucurbitBoy extends Charactor
{
    static protected int nextid=0;     //每个葫芦娃都是唯一的，nextid用来记录下一个未出生的葫芦娃排行
    private CucurbitBoys prop;            //葫芦娃属性
    public CucurbitBoy(int sx, int sy, int nx, int ny) throws Exception
    {
        super(sx, sy, nx, ny);
        IdNo = Configs.INDEX_BROTHERS + nextid;
        if(nextid>=7)
            throw new Exception("葫芦娃只能有七个！");
        this.prop=CucurbitBoys.values()[CucurbitBoy.nextid];
        setProps("brother"+(prop.ordinal()+1),prop.getName(),prop.getHP(),100,prop.getregCost(),prop.getMpCost(),prop.getZXCCost());
        remoteattack = prop.IsRemote();
        nextid++;
    }
}

class Grandpa extends Charactor
{
    public Grandpa(int sx, int sy, int nx, int ny)
    {
        super(sx, sy, nx, ny);
        IdNo = Configs.INDEX_GRANDPA;
        setProps("grandpa","爷爷",80,100,-30,-30,-100);
        remoteattack=true;
        heal=true;
    }
}

class Snake extends Charactor
{
    public Snake(int sx, int sy, int nx, int ny)
    {
        super(sx, sy, nx, ny);
        IdNo = Configs.INDEX_SNAKE;
        setProps("snake","蛇精",70,100,-30,-50,-100);
        monster = true;
        heal = true;
    }
}

class Roro extends Charactor
{
    int innerIndex=0;
    public Roro(int sx, int sy, int nx, int ny, int innerId)
    {
        super(sx, sy, nx, ny);
        this.innerIndex = innerId;
        IdNo = Configs.INDEX_RORO+innerId;
        setProps("Roro","小喽啰",60,100,10,0,30);
        monster = true;
        remoteattack = true;
        aoeavaliable = false;
    }
}

class Scorpion extends Charactor
{
    public static final int troopScale = 13;
    public int curFMT=-1;
    private formations[] learnedFormations=new formations[]{new Fengshi(), new Yanyue(), new Yulin()};       //蝎子精学过的阵型
    public Roro[] troops=new Roro[13];                  //每个蝎子精带有13个小喽啰
    public Scorpion(int sx, int sy, int nx, int ny)
    {
        super(sx, sy, nx, ny);
        IdNo = Configs.INDEX_SCORPTION;
        setProps("scorption","蝎子精",120,100,20,40,80);
        monster = true;
        remoteattack = true;
        for(int i=0;i<troops.length;i++)
            troops[i]=new Roro(sx, sy, nx, ny, i);
    }

    public boolean checkFmtValid(int index)
    {
        index=index%learnedFormations.length;
        if(index<0)
            index = learnedFormations.length-1;
        int vx=virtualField.width-learnedFormations[index].rightDistance-2;
        int vy=(virtualField.height-1)/2;
        if(virtualField.cmap[vy][vx] != null && !virtualField.cmap[vy][vx].monster)
            return false;
        for(int i=0;i<troops.length;i++) {
            int Rorovx=vx+learnedFormations[index].RelativePosition[i][0];
            int Rorovy=vy+learnedFormations[index].RelativePosition[i][1];
            if(troops[i].alive)
            {
                if(virtualField.cmap[Rorovy][Rorovx] != null && !virtualField.cmap[Rorovy][Rorovx].monster)
                    return false;
            }
        }
        return true;
    }

    /** 蝎子精改变阵型，并且要求喽罗们按照新阵型站队*/
    public void changeFMT(int index)
    {
        if(!alive)        //如果蝎子精战死则所有喽啰群龙无首，不再移动
            return;
        avaliable.set(false);
        curFMT=index%learnedFormations.length;
        if(curFMT<0)
            curFMT = learnedFormations.length-1;
        int vx=virtualField.width-learnedFormations[curFMT].rightDistance-2;
        int vy=(virtualField.height-1)/2;
        iPoint realPst=virtualField.vpTorp(vx,vy);
        moveto(realPst.x, realPst.y);
        cmd.set(1);
        for(int i=0;i<troops.length;i++) {
            int Rorovx=vx+learnedFormations[curFMT].RelativePosition[i][0];
            int Rorovy=vy+learnedFormations[curFMT].RelativePosition[i][1];
            if(troops[i].alive)
            {
                troops[i].moveto(virtualField.vpTorp(Rorovx, Rorovy));
                troops[i].cmd.set(1);
            }
        }
    }
    @Override
    protected void Attack1() {
        for(Charactor x : troops)
        {
            if(x.alive && x.avaliable.getAndSet(false))
                x.cmd.set(2);
        }
        super.Attack1();
    }

    @Override
    protected void Attack2()
    {
        MP -= mpcost;
        if(MP<mpcost)           //MP不足时不能再使用对群攻击
        {
            aoeavaliable=false;
            avaliable.set(true);
        }
        else
        {
            bulletController.start(this, bulletController.ATTACK_AOE_1);
            bulletController.start(this, bulletController.ATTACK_AOE_2);
            bulletController.start(this, bulletController.ATTACK_AOE_3);
        }
        if(checkFmtValid(curFMT+1))
            changeFMT(++curFMT);
        avaliable.set(true);
    }

    @Override
    protected void Attack3() {
        for(Charactor x : troops)
        {
            if(x.alive && x.avaliable.getAndSet(false))
                x.cmd.set(4);
        }
        super.Attack3();
    }
}