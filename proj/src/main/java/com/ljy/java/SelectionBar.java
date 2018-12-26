package com.ljy.java;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** 技能栏，用于玩家使用鼠标选择技能*/
public class SelectionBar extends Pane{
    Text curName;
    Label regularAttack;
    Label AOEAttack;
    Label ZXCAttack;
    Charactor curChat=null;
    battleManager bmanager = null;

    Lock lock = new ReentrantLock();
    boolean hide = true;
    Lock checkhide = new ReentrantLock();

    public SelectionBar()
    {
        ImageView bg = new ImageView(Configs.SysIcons.get(Configs.INDEX_FLOATBAR));
        bg.setFitWidth(Configs.SBAR_WIDTH);
        bg.setFitHeight(Configs.SBAR_HEIGHT);
        getChildren().add(bg);

        curName = new Text("空");
        curName.setFont(Font.font("楷体", FontWeight.BOLD, 35));
//        curName.setFill(new Color(255/256.0,215/256.0,0.0,1.0));
        curName.setFill(Color.BLACK);
        curName.setTextAlignment(TextAlignment.CENTER);
        curName.setY(Configs.SBAR_HEIGHT/5);
        curName.setX(Configs.SBAR_WIDTH/4);

        ImageView regImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_B_ATTACK1));
        regImg.setFitWidth(Configs.SBAR_WIDTH*4/5);
        regImg.setFitHeight(Configs.SBAR_HEIGHT/5);
        regularAttack = new Label("",regImg);
        regularAttack.setLayoutY(Configs.SBAR_HEIGHT/5*2 - Configs.SBAR_HEIGHT/10);
        regularAttack.setLayoutX(Configs.SBAR_WIDTH/10);
        regularAttack.setOnMouseEntered((MouseEvent e)->{ regImg.setImage(Configs.SysIcons.get(Configs.INDEX_B_ATTACK12)); });
        regularAttack.setOnMouseExited((MouseEvent e)->{ regImg.setImage(Configs.SysIcons.get(Configs.INDEX_B_ATTACK1)); });
        regularAttack.setOnMouseClicked((MouseEvent)->{
            if(curChat!=null && curChat.avaliable.getAndSet(false) && (bmanager==null || !bmanager.bind.get())) {
                if(bmanager!=null)
                    bmanager.stepDecrease();
                bmanager.savestack.addMove(curChat.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK1);
                curChat.cmd.set(2);
                reSet(null);
            }
        });

        ImageView aoeImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_B_ATTACK2));
        aoeImg.setFitWidth(Configs.SBAR_WIDTH*4/5);
        aoeImg.setFitHeight(Configs.SBAR_HEIGHT/5);
        AOEAttack = new Label("",aoeImg);
        AOEAttack.setLayoutY(Configs.SBAR_HEIGHT/5*3 - Configs.SBAR_HEIGHT/10);
        AOEAttack.setLayoutX(Configs.SBAR_WIDTH/10);
        AOEAttack.setOnMouseEntered((MouseEvent e)->{ aoeImg.setImage(Configs.SysIcons.get(Configs.INDEX_B_ATTACK22)); });
        AOEAttack.setOnMouseExited((MouseEvent e)->{ aoeImg.setImage(Configs.SysIcons.get(Configs.INDEX_B_ATTACK2)); });
        AOEAttack.setOnMouseClicked((MouseEvent)->{
            if(curChat!=null && curChat.avaliable.getAndSet(false) && (bmanager==null || !bmanager.bind.get())) {
                if(bmanager!=null)
                    bmanager.stepDecrease();
                bmanager.savestack.addMove(curChat.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK2);
                curChat.cmd.set(3);
                reSet(null);
            }
        });

        ImageView zxcImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_B_ATTACK3));
        zxcImg.setFitWidth(Configs.SBAR_WIDTH*4/5);
        zxcImg.setFitHeight(Configs.SBAR_HEIGHT/5);
        ZXCAttack = new Label("",zxcImg);
        ZXCAttack.setLayoutY(Configs.SBAR_HEIGHT/5*4 - Configs.SBAR_HEIGHT/10);
        ZXCAttack.setLayoutX(Configs.SBAR_WIDTH/10);
        ZXCAttack.setOnMouseEntered((MouseEvent e)->{ zxcImg.setImage(Configs.SysIcons.get(Configs.INDEX_B_ATTACK32)); });
        ZXCAttack.setOnMouseExited((MouseEvent e)->{ zxcImg.setImage(Configs.SysIcons.get(Configs.INDEX_B_ATTACK3)); });
        ZXCAttack.setOnMouseClicked((MouseEvent)->{
            if(curChat!=null && curChat.avaliable.getAndSet(false) && (bmanager==null || !bmanager.bind.get())) {
                if(bmanager!=null)
                    bmanager.stepDecrease();
                bmanager.savestack.addMove(curChat.IdNo, 0, 0, SaveStack.SAVETYPE_ATTACK3);
                curChat.cmd.set(4);
                reSet(null);
            }
        });

        regularAttack.setVisible(false);
        AOEAttack.setVisible(false);
        ZXCAttack.setVisible(false);
        getChildren().add(curName);
        getChildren().add(regularAttack);
        getChildren().add(AOEAttack);
        getChildren().add(ZXCAttack);
    }


    /**
     * 更新技能栏显示的信息
     * @param chat 当前选中的角色
     */
    public void reSet(Charactor chat)
    {
        curChat = chat;
        regularAttack.setVisible(false);
        AOEAttack.setVisible(false);
        ZXCAttack.setVisible(false);
        if(curChat == null)
        {
            checkhide.lock();
            if(!hide) {
                hide = false;
                checkhide.unlock();
                Hide();
            }
            else
                checkhide.unlock();
            curName.setText("空");
        }
        else
        {
            checkhide.lock();
            if(hide) {
                checkhide.unlock();
                ShowUp();
            }
            else
                checkhide.unlock();
            curName.setText(curChat.name);
            regularAttack.setVisible(true);
            if(curChat.aoeavaliable)
                AOEAttack.setVisible(true);
            if(curChat.zxcavaliable)
                ZXCAttack.setVisible(true);
        }
    }

    public void Hide() {
        double dx = (Configs.SBAR_WIDTH + 25)/36.0;
        new Thread(()->{
            lock.lock();
            checkhide.lock();
            if(hide) {
                checkhide.unlock();
                lock.unlock();
                return;
            }
            hide = true;
            checkhide.unlock();
            double curx = getLayoutX();
            for (int i = 0; i < 36; i++) {
                curx -= dx;
                double x = curx;
                Platform.runLater(()-> {
                    relocate(x, getLayoutY());
                });
                //                setLayoutY(cury);
                try {
                    Thread.sleep(700 / 36);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lock.unlock();
        }).start();
    }

    public void ShowUp()
    {
        double dx = (Configs.SBAR_WIDTH + 25)/36.0;
        new Thread(()->{
            lock.lock();
            checkhide.lock();
            if(!hide) {
                checkhide.unlock();
                lock.unlock();
                return;
            }
            hide = false;
            checkhide.unlock();
            double curx = getLayoutX();
            for (int i = 0; i < 36; i++) {
                curx += dx;
                double x = curx;
                Platform.runLater(()-> {
                    relocate(x, getLayoutY());
                });
                //                setLayoutY(cury);
                try {
                    Thread.sleep(700 / 36);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lock.unlock();
        }).start();
    }

}
