package com.ljy.java;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CInfoBar extends Pane {
    Rectangle bg;
    Text name;
    ImageView HP;
    ImageView MP;
    processBar HPBar;
    processBar MPBar;
    Lock lock = new ReentrantLock();
    Lock checkchange = new ReentrantLock();
    Long lastchange = 0L;
    boolean hide = true;
    Lock checkhide = new ReentrantLock();
    public CInfoBar()
    {
        bg = new Rectangle(Configs.CIBAR_WIDTH, Configs.CIBAR_HEIGHT+20);
        LinearGradient gradient = new LinearGradient(0,0,0,1.0,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.color(138/256.0,140/256.0,136/256.0,0.5)),
                new Stop(0.3,Color.color(0.0,0.0,0.0,0.6)),
                new Stop(1,Color.color(138/256.0,144/256.0,136/256.0,0.1)));
//                new Stop(0, Color.color(38/256.0,140/256.0,136/256.0,0.5)),
//                new Stop(0.3,Color.color(38/256.0,144/256.0,136/256.0,0.6)),
//                new Stop(1,Color.color(38/256.0,144/256.0,136/256.0,0.1)));
        bg.setArcWidth(20);
        bg.setArcHeight(20);
        bg.setFill(gradient);
        getChildren().add(bg);
        setMaxSize(Configs.CIBAR_WIDTH, Configs.CIBAR_HEIGHT);

        name = new Text("default");
        name.setFont(Font.font(23));
//        name.setFill(Color.color(255/256.0,215/256.0,0.0,1.0));
        name.setFill(Color.color(245/256.0,225/256.0,10/256.0,1.0));
        name.setTextAlignment(TextAlignment.CENTER);
        name.setLayoutX(Configs.CIBAR_WIDTH/3.0);
        name.setLayoutY(Configs.CIBAR_HEIGHT/3.0);
        getChildren().add(name);

        HP = new ImageView(Configs.SysIcons.get(Configs.INDEX_HP));
        HP.setFitWidth(Configs.CIBAR_HEIGHT/4.0);
        HP.setFitHeight(Configs.CIBAR_HEIGHT/4.0);
        HP.setX(Configs.CIBAR_HEIGHT/6.0);
        HP.setY(Configs.CIBAR_HEIGHT/3.0);
        getChildren().add(HP);

        MP = new ImageView(Configs.SysIcons.get(Configs.INDEX_MP));
        MP.setFitWidth(Configs.CIBAR_HEIGHT/4.0);
        MP.setFitHeight(Configs.CIBAR_HEIGHT/4.0);
        MP.setX(Configs.CIBAR_HEIGHT/6.0);
        MP.setY(Configs.CIBAR_HEIGHT/3.0*2.0);
        getChildren().add(MP);

        HPBar = new processBar(Configs.CIBAR_WIDTH*2/3, Configs.CIBAR_HEIGHT/7, Color.RED);
        HPBar.setLayoutX(Configs.CIBAR_HEIGHT*5/12.0 + 10);
        HPBar.setLayoutY(Configs.CIBAR_HEIGHT/3.0 + Configs.CIBAR_HEIGHT/14.0);
        MPBar = new processBar(Configs.CIBAR_WIDTH*2/3, Configs.CIBAR_HEIGHT/7, Color.BLUE);
        MPBar.setLayoutX(Configs.CIBAR_HEIGHT*5/12.0 + 10);
        MPBar.setLayoutY(Configs.CIBAR_HEIGHT/3.0*2.0 + Configs.CIBAR_HEIGHT/14.0);

        getChildren().add(HPBar);
        getChildren().add(MPBar);

        //Hide after 1s with out new valid action
        new Thread(()->{
            while(true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Long cur = System.currentTimeMillis();
                checkchange.lock();
                if(cur - lastchange > 1000)
                    Hide();
                checkchange.unlock();
            }
        }).start();
    }

    public void reSet(Charactor chat)
    {
        if(chat==null)
            return;
        checkchange.lock();
        lastchange=System.currentTimeMillis();
        checkchange.unlock();

        checkhide.lock();
        if(hide) {
            checkhide.unlock();
            ShowUp();
        }
        else
            checkhide.unlock();

        Platform.runLater(()->{
            name.setText(chat.name);
            HPBar.set(chat.HP, chat.maxHP,false);
            MPBar.set(chat.MP, chat.maxMP,false);
        });
    }

    public void Hide() {
        double dy = Configs.CIBAR_HEIGHT/36.0;
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
            double cury = getLayoutY();
            for (int i = 0; i < 36; i++) {
                cury += dy;
                double y = cury;
                Platform.runLater(()-> {
                    relocate(getLayoutX(), y);
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
        double dy = Configs.CIBAR_HEIGHT/36.0;
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
            double cury = getLayoutY();
            for (int i = 0; i < 36; i++) {
                cury -= dy;
                double y = cury;
                Platform.runLater(()-> {
                    relocate(getLayoutX(), y);
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

class processBar extends Pane {
    Rectangle border;
    Rectangle fill;
    int width, height;
    double curwidth;
    Thread t;
    public processBar(int width, int height, Color color)
    {
        this.width = width;
        this.curwidth = width;
        this.height = height;

        border = new Rectangle(0,0,width, height);
        border.setStroke(Color.gray(100/256.0));
        border.setStrokeWidth(3);
        border.setArcWidth(height/2.0);
        border.setArcHeight(height/2.0);
        border.setFill(null);

        fill = new Rectangle(0,0,width, height);
        fill.setStroke(null);
        fill.setArcWidth(height/2.0);
        fill.setArcHeight(height/2.0);
        fill.setFill(color);

        getChildren().add(fill);
        getChildren().add(border);
    }

    public void set(int value, int maxValue, boolean Animate)
    {
        double nextwidth = value*width/(double)maxValue;
        double dw = (nextwidth - curwidth)/18;
        if(Animate) {
            t=new Thread(() -> {
                for (int i = 0; i < 18; i++) {
                    curwidth += dw;
                    fill.setX(0);
                    fill.setWidth(curwidth);
                    try {
                        Thread.sleep(1000 / 36);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                fill.setX(0);
                fill.setWidth(nextwidth);
                curwidth = nextwidth;
            });
            t.start();
        }
        else
        {
            if(t!=null) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            fill.setX(0);
            fill.setWidth(nextwidth);
            curwidth = nextwidth;
        }
    }
}