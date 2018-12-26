package com.ljy.java;

import java.util.concurrent.atomic.AtomicBoolean;

public class hBot {

    //MARK: 治疗等级
    static final int HEALLEVEL_NONEED = 0;
    static final int HEALLEVEL_REGULAR = 1;
    static final int HEALLEVEL_GROUP = 2;
    static final int HEALLEVEL_EMERGENT = 3;
    //MARK: 远程攻击等级
    static final int REMOTEATTACKLEVEL_NONEED =0;
    static final int REMOTEATTACKLEVEL_NORMAL =1;
    static final int REMOTEATTACKLEVEL_AOE =2;
    static final int REMOTEATTACKLEVEL_FATAL =3;

    public AtomicBoolean finish = new AtomicBoolean(true);

    private battleManager bmanager;
    private CucurbitBoy[] brothers;
    private Grandpa grandpa;
    private int heallevel;

    public hBot(CucurbitBoy[] brothers, Grandpa grandpa, battleManager bmanager)
    {
        this.brothers = brothers;
        this.grandpa = grandpa;
        this.bmanager = bmanager;
    }

    /** 所有葫芦娃攻击*/
    protected void closeAttack()
    {
        iPoint vloc;
        for(Charactor x : brothers)
            if(x.alive)
            {
                vloc = virtualField.rpTovp(x.PositionX.get() + Configs.B_SIZE / 2, x.PositionY.get() + Configs.B_SIZE / 2);
                if(!x.remoteattack && vloc.x+1<virtualField.width && virtualField.cmap[vloc.y][vloc.x+1]!=null && virtualField.cmap[vloc.y][vloc.x+1].monster)
                {
                    if(x.zxcavaliable){
                        bmanager.savestack.addMove(x.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK3);
                        x.cmd.set(4);
                    }
                    else if(x.aoeavaliable){
                        bmanager.savestack.addMove(x.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK2);
                        x.cmd.set(3);
                    }
                    else{
                        bmanager.savestack.addMove(x.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK1);
                        x.cmd.set(2);
                    }
                }
            }
    }

    protected int analyseAttackLevel(Charactor bro)
    {
        iPoint vloc;
        vloc = virtualField.rpTovp(bro.PositionX.get() + Configs.B_SIZE / 2, bro.PositionY.get() + Configs.B_SIZE / 2);
        int targetx = vloc.x+1;
        for(;targetx<virtualField.width;targetx++)
        {
            Charactor aim = virtualField.cmap[vloc.y][targetx];
            if(aim!=null && aim.monster)
            {
                //优先对蝎子精使用必杀技
                if(aim instanceof Scorpion && bro.zxcavaliable)
                    return REMOTEATTACKLEVEL_FATAL;
                //敌人就在面前，可以使用近程攻击
                if(targetx == vloc.x+1 && bro.aoeavaliable)
                {
                    return REMOTEATTACKLEVEL_AOE;
                }
                return REMOTEATTACKLEVEL_NORMAL;
            }
        }
        //没有敌人
        return REMOTEATTACKLEVEL_NONEED;
    }
    protected void remoteAttack()
    {
        for(int i=1;i<=2;i++)
        {
            if(!brothers[i].alive)
                continue;
            switch (analyseAttackLevel(brothers[i]))
            {
                case REMOTEATTACKLEVEL_NONEED:
                    break;
                case REMOTEATTACKLEVEL_NORMAL:
                    brothers[i].cmd.set(2);
                    break;
                case REMOTEATTACKLEVEL_AOE:
                    brothers[i].cmd.set(3);
                    break;
                case REMOTEATTACKLEVEL_FATAL:
                    brothers[i].cmd.set(4);
                    break;
            }
        }
    }


    /** 葫芦娃移动到合适位置准备攻击*/
    protected void prepareAttack() {
        int index = 0;
        iPoint[] target = new iPoint[virtualField.height];
        iPoint vloc;
        for (int j = 0; j < virtualField.height; j++) {
            for (int i = 0; i < virtualField.width; i++) {
                if (virtualField.cmap[j][i] != null && virtualField.cmap[j][i].alive && virtualField.cmap[j][i].monster && i - 1 > 0) {
                    target[index] = new iPoint(i - 1, j);
                    index++;
                    break;
                }
            }
        }
        index = 0;
        int bindex = 0;
        //为近程攻击的葫芦娃分配敌人
        for (int i = 0; i < virtualField.height; i++) {
            if (target[i] != null) {
                if (virtualField.cmap[target[i].y][target[i].x] == null) {
                    for (; bindex < 7; bindex++) {
                        if (brothers[bindex].alive && !brothers[bindex].remoteattack) {
                            vloc = virtualField.rpTovp(brothers[bindex].PositionX.get() + Configs.B_SIZE / 2, brothers[bindex].PositionY.get() + Configs.B_SIZE / 2);
                            if (vloc.x + 1 >= virtualField.width || virtualField.cmap[vloc.y][vloc.x + 1] == null || !virtualField.cmap[vloc.y][vloc.x + 1].monster) {
                                bmanager.savestack.addMove(brothers[bindex].IdNo, target[i].x, target[i].y, SaveStack.SAVETYPE_MOVE);
                                brothers[bindex].moveto(virtualField.vpTorp(target[i]));
                                brothers[bindex].cmd.set(1);
                                index++;
                                bindex++;
                                break;
                            }
                        }
                    }
                    if (bindex >= 7)
                        break;
                }
            }
        }
        waitforReady();
        for(int i=0;i<7;i++)
        {
            vloc = virtualField.rpTovp(brothers[i].PositionX.get() + Configs.B_SIZE / 2, brothers[i].PositionY.get() + Configs.B_SIZE / 2);
            virtualField.cmap[vloc.y][vloc.x] = brothers[i];
        }

        if(brothers[1].alive)
            bindex=1;
        else if(brothers[2].alive)
            bindex = 2;
        else
            return;
        //为远程攻击的葫芦娃分配敌人
        for (int i = 0; i < virtualField.height; i++) {
            if (target[i] != null) {
                for(int j=target[i].x;j>0;j--)
                {
                    if(virtualField.cmap[target[i].y][j]==null)
                    {
                        bmanager.savestack.addMove(brothers[bindex].IdNo, j, target[i].y, SaveStack.SAVETYPE_MOVE);
                        virtualField.cmap[target[i].y][j]=brothers[bindex];
                        brothers[bindex].moveto(virtualField.vpTorp(j,target[i].y));
                        brothers[bindex].cmd.set(1);
                        bindex++;
                        if(bindex > 2 || !brothers[2].alive)
                            return;
                        break;
                    }
                }
            }
        }
    }

    /** 阻塞到所有人类都处于空闲状态*/
    public void waitforReady()
    {
        while(true)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean ready=true;
            for(Charactor x : brothers)
            {
                if(!x.avaliable.get() && x.alive)
                {
                    ready=false;
                    break;
                }
            }
            if(grandpa.alive && !grandpa.avaliable.get())
                ready=false;
            if(ready)
                break;
        }
    }

    /** 爷爷移动到需要救治的妖精处，并判断伤势等级*/
    private void heal()
    {
        heallevel=HEALLEVEL_NONEED;
        iPoint vloc;
        //优先治疗伤势过重的兄弟
        Charactor maxinjuered=null;
        for(Charactor x:brothers)
        {
            if(x.alive && x.HP<x.maxHP)
            {
                vloc = virtualField.rpTovp(x.PositionX.get()+Configs.B_SIZE/2, x.PositionY.get()+Configs.B_SIZE/2);
                if (virtualField.cmap[vloc.y][vloc.x - 1] == null)
                {
                    if(maxinjuered == null || (x.maxHP - x.HP) > (maxinjuered.maxHP-maxinjuered.HP))
                    {
                        maxinjuered = x;
                    }
                }
            }
        }
        if(maxinjuered != null)
        {
            vloc = virtualField.rpTovp(maxinjuered.PositionX.get()+Configs.B_SIZE/2, maxinjuered.PositionY.get()+Configs.B_SIZE/2);
            heallevel = (maxinjuered.maxHP - maxinjuered.HP > 70)?HEALLEVEL_EMERGENT:HEALLEVEL_REGULAR;
            if(!grandpa.zxcavaliable)
                heallevel = HEALLEVEL_REGULAR;
            bmanager.savestack.addMove(grandpa.IdNo, vloc.x-1, vloc.y, SaveStack.SAVETYPE_MOVE);
            grandpa.moveto(virtualField.vpTorp(vloc.x-1, vloc.y));
            grandpa.cmd.set(1);
            return;
        }
        bmanager.savestack.addMove(grandpa.IdNo, 0, virtualField.height-1, SaveStack.SAVETYPE_MOVE);
        grandpa.moveto(virtualField.vpTorp(0, virtualField.height-1));
        grandpa.cmd.set(1);
        return;
    }

    /** 机器人做出下一步行为*/
    public void nextMove()
    {
        finish.set(false);

        prepareAttack();
        waitforReady();
        closeAttack();
        waitforReady();
        remoteAttack();
        waitforReady();
        heal();
        waitforReady();
        if(heallevel!=HEALLEVEL_NONEED)
        {
            switch (heallevel)
            {
                case HEALLEVEL_REGULAR:
                    grandpa.cmd.set(2);
                    break;
                case HEALLEVEL_EMERGENT:
                    grandpa.cmd.set(4);
                    break;
            }
        }
        waitforReady();
        finish.set(true);
    }
}
