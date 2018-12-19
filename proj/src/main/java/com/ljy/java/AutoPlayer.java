package com.ljy.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;


public class AutoPlayer implements Runnable {
    private String readfrom = null;
    private Document docs;
    private Element root;
    private Element round;
    private int countmove;
    private battleManager bmanager;
    private Scorpion scorpion;

    protected ArrayList<Charactor> creatures = new ArrayList<>();

    public AutoPlayer(String file, battleManager bmanager) {
        this.bmanager = bmanager;
        countmove=0;
        readfrom = file;
    }

    public void add(Charactor chat)
    {
        if(chat instanceof Scorpion)
            scorpion = (Scorpion) chat;
        creatures.add(chat);
    }

    public void init() {        //打开保存游戏信息的xml文件
        if (readfrom == null)
            return;
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            docs = db.parse(readfrom);
            root=docs.getDocumentElement();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("init ready");
    }

    public void waitforReady()      //等待当前动作结束
    {
        while(true)
        {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean ready=true;
            for(Charactor x : creatures)
            {
                if(!x.avaliable.get() && x.alive)
                {
                    ready=false;
                    break;
                }
            }
            if(ready)
                break;
        }
    }

    @Override
    public void run() {
        init();
        if(docs==null)
            return;
        waitforReady();

        for(Charactor x:creatures)
            x.automode = true;

        NodeList rounds=root.getChildNodes();
        NodeList battle=rounds.item(1).getChildNodes();
        for(int j=0;j<battle.getLength();j++) {          //复现每一回合所有的操作
            Node thismove=battle.item(j);

            int Chatno=Integer.parseInt(thismove.getAttributes().getNamedItem("ChatId").getNodeValue());        //被操作的人物
            int type = Integer.parseInt(thismove.getFirstChild().getNodeValue());
            int fmt = Integer.parseInt(thismove.getAttributes().getNamedItem("fmt").getNodeValue());
            iPoint dst = new iPoint(0,0);          //移动的目的地
            dst.x = Integer.parseInt(thismove.getAttributes().getNamedItem("X").getNodeValue());
            dst.y = Integer.parseInt(thismove.getAttributes().getNamedItem("Y").getNodeValue());

            Charactor tmpchat=null;
            for(Charactor x:creatures)         //从人物列表中找到本次操作的人物
            {
                if(x.IdNo==Chatno)
                    tmpchat=x;
            }

            switch(type)
            {
                case SaveStack.SAVETYPE_MOVE:
                    tmpchat.moveto(virtualField.vpTorp(dst.x,dst.y));
                    tmpchat.cmd.set(1);
                    break;
                case SaveStack.SAVETYPE_ATTACK1:
                    tmpchat.cmd.set(2);
                    break;
                case SaveStack.SAVETYPE_ATTACK2:
                    tmpchat.cmd.set(3);
                    break;
                case SaveStack.SAVETYPE_ATTACK3:
                    tmpchat.cmd.set(4);
                    break;
                case SaveStack.SAVETYPE_GROUPATTACH:
                    for(Charactor x: scorpion.troops)
                    {
                        if(x.alive && x.avaliable.getAndSet(false)) {
                            x.cmd.set(2);
                        }
                    }
                    if(scorpion.alive && scorpion.avaliable.getAndSet(false))
                        scorpion.cmd.set(2);
                    break;
                case SaveStack.SAVETYPE_CFMT:
                    ((Scorpion)tmpchat).changeFMT(fmt);
                    break;
                default:
                    System.out.println("load type error!");
            }

            waitforReady();
            virtualField.cmaplock.lock();
            for(Charactor x:creatures)
                if(x.alive)
                    virtualField.cmap[virtualField.ryTovy(x.PositionY.get())][virtualField.rxTovx(x.PositionX.get())] = x;
            virtualField.cmaplock.unlock();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Charactor x:creatures)
            x.automode = false;

        bmanager.autoplaying = false;
        bmanager.bind.set(false);
    }
}
