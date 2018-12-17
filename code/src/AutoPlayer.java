package sample;

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

    protected ArrayList<Charactor> creatures = new ArrayList<>();

    public AutoPlayer(String file, battleManager bmanager) {
        this.bmanager = bmanager;
        countmove=0;
        readfrom = file;
    }

    public void add(Charactor chat)
    {
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
                Thread.sleep(200);
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
        NodeList rounds=root.getChildNodes();
        for(int i=1;i<rounds.getLength();i++)       //复现所有的回合
        {
            NodeList moves=rounds.item(i).getChildNodes();
            for(int j=0;j<moves.getLength();j++) {          //复现每一回合所有的操作
                Node thismove=moves.item(j);
                int Chatno=Integer.parseInt(thismove.getAttributes().getNamedItem("ChatId").getNodeValue());        //被操作的人物
                String type=thismove.getAttributes().getNamedItem("type").getNodeValue();           //操作类型
                int value=0;
                if(type.equals("attack"))
                    value=Integer.parseInt(thismove.getFirstChild().getNodeValue());
                iPoint dst=new iPoint(0,0);          //移动的目的地
                dst.x=Integer.parseInt(thismove.getAttributes().getNamedItem("X").getNodeValue());
                dst.y=Integer.parseInt(thismove.getAttributes().getNamedItem("Y").getNodeValue());

                Charactor tmpchat=null;
                for(Charactor x:creatures)         //从人物列表中找到本次操作的人物
                {
                    if(x.IdNo==Chatno)
                        tmpchat=x;
                }

                if(type.equals("walk"))     //执行操作
                {
                    tmpchat.moveto(virtualField.vpTorp(dst.x,dst.y));
                    tmpchat.cmd.set(1);
                }
                else
                {
                    System.out.println("attack"+(value+1));
                    tmpchat.cmd.set(value+1);
                }
                waitforReady();
                bmanager.stepDecrease();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //TODO: battle.bot.nextMove();      //一个回合结束，妖怪做出对应的动作
            waitforReady();
        }
        bmanager.autoplaying = false;
        bmanager.bind.set(false);
    }
}
