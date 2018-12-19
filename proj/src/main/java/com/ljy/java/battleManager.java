package com.ljy.java;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/** 负责战斗人员及战斗逻辑的管理*/
public class battleManager{
    battleWindow view;
    OutLookManager outLookManager = null;
    SaveStack savestack;

    /** 所有人物的队列*/
    ArrayList<Charactor> creatures = new ArrayList<>();
    /** 当前选中的角色*/
    Charactor ChatSelected = null;
    /** 机器人玩家*/
    Bot bot = null;
    /** 玩家剩余步数*/
    protected int stepRemain = 3;
    /** 当前玩家是否可以执行操作*/
    protected AtomicBoolean bind = new AtomicBoolean(true);
    /** 游戏是否已经结束*/
    protected boolean End = false;
    protected String loadfile;
    boolean autoplaying = false;
    AutoPlayer autoPlayer = null;

    static final int ACTION_CLICKED = 0;
    static final int ACTION_MOVEABOVE = 1;

    public battleManager(battleWindow view, String loadfile)
    {
        this.view = view;
        this.loadfile = loadfile;
        if(loadfile!=null)
            autoplaying = true;
        outLookManager = new OutLookManager(this);

        if(autoplaying)
            autoPlayer = new AutoPlayer(loadfile, this);

        try {
            addCreatures();
        } catch (Exception e) {
            System.out.println("add charactors fail");
            e.printStackTrace();
        }
        addBullets();
        Thread t = new Thread(outLookManager);
        savestack = new SaveStack();
        t.start();
        t = new Thread(()->{
            for(Charactor x:creatures) {
                x.cmd.set(1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            bind.set(false);
            if(autoplaying)
                new Thread(autoPlayer).start();
        });
        t.start();
    }

    /**
     * 创建一个角色，并将其加入outlookManager中管理UI，启动其线程
     * @param chat 新的游戏角色对象
     */
    protected void addCharactor(Charactor chat)
    {
        outLookManager.addCharactor(chat);
        creatures.add(chat);
        if(autoPlayer!=null)
            autoPlayer.add(chat);
        Thread tmpthread=new Thread(chat);
        tmpthread.start();
        chat.cmd.set(0);
    }

    /** 添加所有的人物*/
    public void addCreatures() throws Exception {
        addCharactor(new Grandpa(-1,3,0, virtualField.height-1));
        Thread tmpthread;
        for(int i=0;i<7;i++) {
            addCharactor(new CucurbitBoy(-1, 3, 1, i));
        }
        Snake snake = new Snake(10,3,9,0);
        addCharactor(snake);
        Scorpion scorpion = new Scorpion(10,3,9,4);
        scorpion.changeFMT(0);
        addCharactor(scorpion);
        for(Roro x:scorpion.troops)
            addCharactor(x);
        bot = new Bot(scorpion, snake, this);
    }

    /** 添加所有人物的攻击效果*/
    public void addBullets()
    {
        for(Bullet x:ViewBundle.Attack1)
            outLookManager.addBullet(x);
        for(Bullet x:ViewBundle.Attack2_1)
            outLookManager.addBullet(x);
        for(Bullet x:ViewBundle.Attack2_2)
            outLookManager.addBullet(x);
        for(Bullet x:ViewBundle.Attack2_3)
            outLookManager.addBullet(x);
        for(Bullet x:ViewBundle.Attack3)
            outLookManager.addBullet(x);
    }

    public Charactor newAction(iPoint loc, int type)
    {
        //重放时不能控制角色
        if(type == ACTION_CLICKED && autoplaying)
            return null;
        for(Charactor x: creatures)
        {
            if(x.alive && x.avaliable.get())
            {
                iPoint vp = virtualField.rpTovp(x.PositionX.get()+Configs.B_SIZE/2, x.PositionY.get()+Configs.B_SIZE/2);
                if(loc.x==vp.x && loc.y==vp.y)
                {
                    if(type == ACTION_CLICKED)
                        ChatSelected = x;
                    if(type == ACTION_MOVEABOVE || (!bind.get() && !x.monster))
                        return x;
                 }
            }
        }
        if(type == ACTION_CLICKED) {
            if(ChatSelected!=null && ChatSelected.monster)
                ChatSelected = null;
            //TODO: move curChat to this location
            if(!bind.get() && ChatSelected != null)
            {
                savestack.addMove(ChatSelected.IdNo, loc.x, loc.y, SaveStack.SAVETYPE_MOVE);
                ChatSelected.moveto(virtualField.vpTorp(loc.x, loc.y));
                ChatSelected.cmd.set(1);
                ChatSelected = null;
                stepDecrease();
            }
        }
        return null;
    }

    /** 玩家步数减少1，并且判断是否轮到敌人进攻*/
    public void stepDecrease()
    {
        if(End)
            return;
        stepRemain--;
        view.hint.set(stepRemain);
        if(stepRemain==0)
        {
            bind.set(true);
            new Thread(()->{
                while(true)
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean ready=true;
                    for(Charactor x : creatures)
                    {
                        if(x.alive && !x.avaliable.get())
                        {
                            ready=false;
                            break;
                        }
                    }
                    if(ready)
                        break;
                }

                bot.nextMove();

                while(true)
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(bot.finish.get())
                        break;
                }

                virtualField.cmaplock.lock();
                for(Charactor x:creatures)
                    if(x.alive)
                        virtualField.cmap[virtualField.ryTovy(x.PositionY.get())][virtualField.rxTovx(x.PositionX.get())] = x;
                virtualField.cmaplock.unlock();
                bind.set(false);
            }).start();
            stepRemain = 3;
            view.hint.set(stepRemain);
        }
    }

    public void GameEnd(boolean monster)
    {
//        savestack.saveToFile("tmp.xml");
        End = true;
        Platform.runLater(()-> {
            view.endMask.whoWin(monster);
            view.endMask.setVisible(true);
            FadeTransition appear = new FadeTransition(Duration.seconds(2));
            appear.setFromValue(0.0);
            appear.setToValue(1.0);
/*
            FadeTransition fade = new FadeTransition(Duration.seconds(1));
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
*/
            SequentialTransition sequence = new SequentialTransition(view.endMask, appear);
            sequence.play();
        });
    }
}
