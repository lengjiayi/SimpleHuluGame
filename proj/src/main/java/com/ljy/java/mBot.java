package com.ljy.java;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 控制妖怪的机器人
 */
public class mBot {

    //MARK: 治疗等级
    static final int HEALLEVEL_NONEED = 0;
    static final int HEALLEVEL_REGULAR = 1;
    static final int HEALLEVEL_GROUP = 2;
    static final int HEALLEVEL_EMERGENT = 3;

    public AtomicBoolean finish = new AtomicBoolean(true);

    private battleManager bmanager;
    private Scorpion scorption;
    private Snake snake;

    /** 需要治疗的紧急程度*/
    private int heallevel = 0;
    private int count = 0;
    private int curfmt = 0;

    public mBot(Scorpion s, Snake sn, battleManager bmanager)
    {
        scorption=s;
        snake=sn;
        this.bmanager = bmanager;
    }

    /** 集体普通攻击*/
    private void GroupAttack()
    {
        bmanager.savestack.addMove(scorption.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK1);
        if(scorption.alive && scorption.avaliable.getAndSet(false))
            scorption.cmd.set(2);
        else
        {
            for(Charactor x: scorption.troops)
            {
                if(x.alive && x.avaliable.getAndSet(false)) {
                    x.cmd.set(2);
                }
            }
        }
    }

    /** 改变阵型*/
    private void  nFMT(int index)
    {
        if (scorption.alive && scorption.avaliable.getAndSet(false)) {
            if(scorption.checkFmtValid(index)) {
                bmanager.savestack.addMove(scorption.IdNo, 0, 0, SaveStack.SAVETYPE_CFMT, index);
                scorption.changeFMT(index);
            }
            else
                scorption.avaliable.set(true);
        }
    }

    /** 集体放大招*/
    private void ZXC()
    {
        if(scorption.alive && scorption.zxcavaliable && scorption.avaliable.getAndSet(false)) {
            bmanager.savestack.addMove(scorption.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK3);
            scorption.cmd.set(4);
        }
    }

    /** 蛇精移动到需要救治的妖精处，并判断伤势等级*/
    private void heal()
    {
        heallevel=HEALLEVEL_NONEED;
        iPoint vloc;
        if(scorption.alive && scorption.HP<scorption.maxHP) {
            //蝎子精优先接受治疗
            vloc = virtualField.rpTovp(scorption.PositionX.get()+Configs.B_SIZE/2, scorption.PositionY.get()+Configs.B_SIZE/2);
            if (virtualField.cmap[vloc.y][vloc.x + 1] == null)
            {
                heallevel=HEALLEVEL_REGULAR;
                if(scorption.maxHP-scorption.HP>=80 && snake.zxcavaliable)
                    heallevel=HEALLEVEL_EMERGENT;
                snake.moveto(virtualField.vpTorp(vloc.x + 1, vloc.y));
                bmanager.savestack.addMove(snake.IdNo, vloc.x + 1, vloc.y, SaveStack.SAVETYPE_MOVE);
                snake.cmd.set(1);
                return;
            }
        }
        for(Charactor x : scorption.troops)
        {
            //小妖精接受治疗，不过蛇精的治疗大招不会浪费在小妖精身上
            vloc = virtualField.rpTovp(x.PositionX.get()+Configs.B_SIZE/2, x.PositionY.get()+Configs.B_SIZE/2);
            if(x.alive && x.HP<x.maxHP && virtualField.cmap[vloc.y][vloc.x + 1]==null) {
                heallevel=HEALLEVEL_REGULAR;
                snake.moveto(virtualField.vpTorp(vloc.x + 1, vloc.y));
                bmanager.savestack.addMove(snake.IdNo, vloc.x + 1, vloc.y, SaveStack.SAVETYPE_MOVE);
                snake.cmd.set(1);
                return;
            }
        }
        snake.moveto(virtualField.vpTorp(Configs.B_WNUM-1, 0));
        bmanager.savestack.addMove(snake.IdNo, Configs.B_WNUM-1, 0, SaveStack.SAVETYPE_MOVE);
        snake.cmd.set(1);
        return;
    }

    /** 判断蝎子精是否应该发大招*/
    private boolean letsZXC()
    {
        if(!scorption.zxcavaliable)
            return false;
        iPoint vloc = virtualField.rpTovp(scorption.PositionX.get()+Configs.B_SIZE/2, scorption.PositionY.get()+Configs.B_SIZE/2);
        for(int i=0;i<virtualField.width;i++)
            if(virtualField.cmap[vloc.y][i]!=null && virtualField.cmap[vloc.y][i].alive  && !virtualField.cmap[vloc.y][i].monster)
                return true;
        return false;
    }

    /** 阻塞到所有妖怪都处于空闲状态*/
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
            for(Charactor x : scorption.troops)
            {
                if(!x.avaliable.get() && x.alive)
                {
                    ready=false;
                    break;
                }
            }
            if(scorption.alive && !scorption.avaliable.get())
                ready=false;
            if(snake.alive && !snake.avaliable.get())
                ready=false;
            if(ready)
                break;
        }
    }

    /** 机器人做出下一步行为*/
    public void nextMove()
    {
        finish.set(false);
        count++;
        count=count%100;
        if(count>2 && letsZXC())           //如果当前满足条件则放大招。为了降低难度，前两个回合不会放大招。
            ZXC();
        else
            GroupAttack();                  //普通攻击
        waitforReady();

        if(snake.alive) {       //蛇精每个回合负责在最后治疗伤员
            heal();
            waitforReady();
            if(heallevel!=HEALLEVEL_NONEED) {
                snake.avaliable.set(false);
                if(heallevel==HEALLEVEL_REGULAR)
                {
                    bmanager.savestack.addMove(snake.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK1);
                    snake.cmd.set(2);
                }
                else
                {
                    bmanager.savestack.addMove(snake.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK3);
                    snake.cmd.set(4);
                }
            }
            waitforReady();
            snake.moveto(virtualField.vpTorp(Configs.B_WNUM-1, 0));
            bmanager.savestack.addMove(snake.IdNo, Configs.B_WNUM-1, 0, SaveStack.SAVETYPE_MOVE);
            snake.cmd.set(1);
            waitforReady();
        }

        if(true)        //蝎子精胆子很小，如果伤害过多就会变阵退到后面
//        if(count%3==0 || scorption.HP<=70)        //蝎子精胆子很小，如果伤害过多就会变阵退到后面
            if(scorption.HP<=70 && scorption.curFMT!=2) {
                nFMT(2);
            }else if(scorption.HP>70)
                nFMT(++curfmt);
        waitforReady();

        finish.set(true);

    }
}
