package com.ljy.java;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import javax.swing.text.Position;
import java.util.ArrayList;

/** 模拟FPS动画，按照36帧每秒刷新界面*/
public class OutLookManager implements Runnable{

    /** 需要管理的角色*/
    protected ArrayList<Charactor> creatures=new ArrayList<>();
    protected ArrayList<Bullet> bullets = new ArrayList<>();

    /** 添加一个新角色进入管理队列*/
    protected void addCharactor(Charactor chat)
    {
        creatures.add(chat);
    }
    /** 添加一个攻击效果进入管理队列*/
    protected void addBullet(Bullet bullet)
    {
        bullets.add(bullet);
    }

    protected battleManager battle = null;
    public OutLookManager(battleManager battle){ this.battle = battle; }

    @Override
    public void run() {
        boolean monsterExist = false;           //用于判断是否当前还有妖怪存活
        boolean humanExist = false;             //用于判断是否当前还有人类存活
        boolean IsEnd = false;                  //游戏是否结束
        while (true) {
            monsterExist = false;
            humanExist = false;
            try {
                Thread.sleep(1000 / 36);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GraphicsContext gc = battle.view.canvas.getGraphicsContext2D();
            Platform.runLater(()->{
                gc.clearRect(0,0,Configs.WIN_WIDTH, Configs.WIN_HEIGHT);
            });

            /*
            //MARK: Debug Map
            Platform.runLater(()->{
            for(int j=0;j<virtualField.height;j++)
                for(int i=0;i<virtualField.width;i++)
                {
                    virtualField.cmaplock.lock();
                    Charactor chat = virtualField.cmap[j][i];
                    virtualField.cmaplock.unlock();
                    if(chat==null || !chat.alive)
                    {
                        gc.setFill(Color.WHITE);
                        gc.fillRect(i*10,j*10,10,10);
                    }
                    else if(chat.monster)
                    {
                        gc.setFill(Color.RED);
                        gc.fillRect(i*10,j*10,10,10);
                    }
                    else
                    {
                        gc.setFill(Color.BLUE);
                        gc.fillRect(i*10,j*10,10,10);
                    }
                }
            });
            */

            for (Charactor x : creatures) {
                Block block = Configs.SPEC_MID_SIZE(x.PositionX.get(), x.PositionY.get());
                if (x.alive) {
                    if (x.monster)
                        monsterExist = true;
                    else
                        humanExist = true;
                    if(x.visible.get())
                    {
                        Platform.runLater(() -> {
                        if (x.mov1.get())
                            gc.drawImage(Configs.movingIcons.get(Math.min(Configs.movingIcons.size() - 1, x.IdNo)), block.bx, block.y, block.twidth, block.twidth);
                        else
                            gc.drawImage(Configs.normalIcons.get(Math.min(Configs.normalIcons.size() - 1, x.IdNo)), block.bx, block.y, block.twidth, block.twidth);
                        });
                    }
                } else {
                    Platform.runLater(()->{
                        gc.drawImage(Configs.SysIcons.get(Configs.INDEX_RIP),block.bx, block.y,block.twidth, block.twidth);
                    });
                }
            }
            for(Bullet x:bullets)
            {
                if(x.visuable.get())
                {
                    try {
                        Block block = Configs.SPEC_MID_SIZE(x.PositionX.get(), x.PositionY.get());
                        Platform.runLater(() -> {
                            gc.drawImage(x.icon, block.bx, block.y, block.twidth, block.twidth);
//                        gc.drawImage(x.icon,x.PositionX.get(), x.PositionY.get(),Configs.B_SIZE, Configs.B_SIZE);
                        });
                    }catch (Exception e)
                    {
                        iPoint vloc = virtualField.rpTovp(x.PositionX.get()+1, x.PositionY.get()+1);
                        System.out.println("bullet "+x.PositionX.get()+","+ x.PositionY.get());
                        System.out.println(vloc.x + "," + vloc.y);
                        e.printStackTrace();
                        throw e;
                    }
                }
            }

            if (!IsEnd && (!monsterExist || !humanExist)) {
                //TODO: Game End
                System.out.println("Game End");
                IsEnd = true;
                battle.GameEnd(monsterExist);
            }
        }
    }
}


/*
            for(Bullet x:ViewBundle.Attack1)
            {
                if(x.visuable.get())
                {
                    Platform.runLater(()->{
                        x.setVisible(false);
                        x.setVisible(true);
                        x.relocate(x.PositionX.get(), x.PositionY.get());
                    });
                }
                else
                {
                    Platform.runLater(()->{
                        x.setVisible(true);
                        x.setVisible(false);
                    });
                }
            }

            for(Bullet x:ViewBundle.Attack2_1)
            {
                if(x.visuable.get())
                {
                    Platform.runLater(()->{
                        x.setVisible(false);
                        x.setVisible(true);
                        x.relocate(x.PositionX.get(), x.PositionY.get());
                    });
                }
                else
                {
                    Platform.runLater(()->{
                        x.setVisible(true);
                        x.setVisible(false);
                    });
                }
            }

            for(Bullet x:ViewBundle.Attack2_2)
            {
                if(x.visuable.get())
                {
                    Platform.runLater(()->{
                        x.setVisible(false);
                        x.setVisible(true);
                        x.relocate(x.PositionX.get(), x.PositionY.get());
                    });
                }
                else
                {
                    Platform.runLater(()->{
                        x.setVisible(true);
                        x.setVisible(false);
                    });
                }
            }

            for(Bullet x:ViewBundle.Attack2_3)
            {
                if(x.visuable.get())
                {
                    Platform.runLater(()->{
                        x.setVisible(false);
                        x.setVisible(true);
                        x.relocate(x.PositionX.get(), x.PositionY.get());
                    });
                }
                else
                {
                    Platform.runLater(()->{
                        x.setVisible(true);
                        x.setVisible(false);
                    });
                }
            }

            for(Bullet x:ViewBundle.Attack3)
            {
                if(x.visuable.get())
                {
                    Platform.runLater(()->{
                        x.setVisible(false);
                        x.setVisible(true);
                        x.relocate(x.PositionX.get(), x.PositionY.get());
                    });
                }
                else
                {
                    Platform.runLater(()->{
                        x.setVisible(true);
                        x.setVisible(false);
                    });
                }
            }

*/