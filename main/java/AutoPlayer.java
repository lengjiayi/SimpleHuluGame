import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AutoPlayer implements Runnable{        //实现复盘的自动播放程序
    private String readfrom = null;
    private Document docs;
    private Element root;
    private Element round;
    private int countmove;
    public BattleField battle;
    private Charactors[] chats={};
    public AutoPlayer() {
        countmove=0;
    }

    public void setFile(String fname){ readfrom=fname; }

    public String getFname(){ return readfrom; }

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

    public void addChat(Charactors newchat)     //添加角色，用于自动播放时操作
    {
        int oldlen=chats.length;
        Charactors tmp[]=new Charactors[oldlen+1];
        for(int i=0;i<oldlen;i++)
            tmp[i]=chats[i];
        tmp[oldlen]=newchat;
        chats=tmp;
    }
    public void addChat(Charactors[] newchats)      //添加角色
    {
        int oldlen=chats.length;
        Charactors tmp[]=new Charactors[oldlen+newchats.length];
        for(int i=0;i<oldlen;i++)
            tmp[i]=chats[i];
        for(int i=0;i<newchats.length;i++)
            tmp[i+oldlen]=newchats[i];
        chats=tmp;
    }

    public void waitforReady()      //等待当前动作结束
    {
        while(true)
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean ready=true;
            for(Charactors x : chats)
            {
                if(!x.avaliable.get() && x.alive)
                {
                    ready=false;
                    break;
                }
            }
            if(!battle.animationHandler.avaliable.get())
                ready=false;
            if(ready)
                break;
        }
    }


    @Override
    public void run() {
        System.out.println("Autoplayer start");
        init();
        if(docs==null)
            return;
        waitforReady();
        System.out.println("start read");
        NodeList rounds=root.getChildNodes();
        for(int i=1;i<rounds.getLength();i++)       //复现所有的回合
        {
            NodeList moves=rounds.item(i).getChildNodes();
            for(int j=0;j<moves.getLength();j++) {          //复现每一回合所有的操作
                Node thismove=moves.item(j);
                int Chatno=Integer.parseInt(thismove.getAttributes().getNamedItem("Chatno").getNodeValue());        //被操作的人物
                String type=thismove.getAttributes().getNamedItem("type").getNodeValue();           //操作类型
                int value=0;
                if(type.equals("attack"))
                    value=Integer.parseInt(thismove.getFirstChild().getNodeValue());
                Point dst=new Point();          //移动的目的地
                dst.x=Integer.parseInt(thismove.getAttributes().getNamedItem("X").getNodeValue());
                dst.y=Integer.parseInt(thismove.getAttributes().getNamedItem("Y").getNodeValue());
                String debugstr="\t";
                debugstr+=chats[Chatno].typename;
                debugstr = debugstr + " "+type;
                if(type.equals("walk"))
                    debugstr+=" to ("+dst.x+", "+dst.y+")";
                debugstr+="\n";
                battle.debug.append(debugstr);

                Charactors tmpchat=null;
                for(Charactors x:chats)         //从人物列表中找到本次操作的人物
                {
                    if(x.charno==Chatno)
                        tmpchat=x;
                }

                if(type.equals("walk"))     //执行操作
                {
                    tmpchat.moveto(dst.x,dst.y);
                    battle.animationHandler.cmd.set(1);
                }
                else
                {
                    tmpchat.cmd.set(value+1);
                }
                waitforReady();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            battle.debug.append("--Round--\n");
            battle.bot.nextMove();      //一个回合结束，妖怪做出对应的动作
            waitforReady();
        }
    }
}
