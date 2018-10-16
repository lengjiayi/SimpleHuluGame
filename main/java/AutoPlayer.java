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

public class AutoPlayer implements Runnable{
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

    public void init() {
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

    public void addChat(Charactors newchat)     //添加角色
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
        for(int i=1;i<rounds.getLength();i++)
        {
            NodeList moves=rounds.item(i).getChildNodes();
            for(int j=0;j<moves.getLength();j++) {
                Node thismove=moves.item(j);
                int Chatno=Integer.parseInt(thismove.getAttributes().getNamedItem("Chatno").getNodeValue());
                String type=thismove.getAttributes().getNamedItem("type").getNodeValue();
                int value=0;
                if(type.equals("attack"))
                    value=Integer.parseInt(thismove.getFirstChild().getNodeValue());
                Point dst=new Point();
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
                for(Charactors x:chats)
                {
                    if(x.charno==Chatno)
                        tmpchat=x;
                }

                if(type.equals("walk"))
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
            battle.bot.nextMove();
            waitforReady();
        }
    }
}
