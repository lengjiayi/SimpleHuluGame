package com.ljy.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class SaveStack {

    public static final int SAVETYPE_MOVE = 0;
    public static final int SAVETYPE_ATTACK1 = 1;
    public static final int SAVETYPE_ATTACK2 = 2;
    public static final int SAVETYPE_ATTACK3 = 3;

    private Document docs;
    private Element root;
    private Element round;
    private int countmove;

    public SaveStack()
    {
        init();
    }

    public void init() {            //创建并打开xml文件
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            docs = db.newDocument();
            root = docs.createElement("battle");

            String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());       //写入储存时间信息
            Element ctime = docs.createElement("ctime");
            Text m = docs.createTextNode(time);
            ctime.appendChild(m);
            root.appendChild(ctime);
            docs.appendChild(root);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 添加一次操作信息
     * @param chatId 角色ID
     * @param dstx 移动目的地横坐标
     * @param dsty 移动目的地纵坐标
     * @param type 操作类型
     */
    public void addMove(int chatId, int dstx, int dsty, int type)
    {
        countmove=countmove%3;
        if(countmove==0) {
            if(round!=null)
                root.appendChild(round);
            round = docs.createElement("round");
        }Element move=docs.createElement("move");
        if(type != SAVETYPE_MOVE)
            move.setAttribute("type","attack");
        else
            move.setAttribute("type","walk");
        move.setAttribute("ChatId",Integer.toString(chatId));
        move.setAttribute("Y",Integer.toString(dsty));
        move.setAttribute("X",Integer.toString(dstx));
        Text m=docs.createTextNode(Integer.toString(type));
        move.appendChild(m);
        round.appendChild(move);
        countmove++;
    }

    /** 将战斗过程写入文件*/
    public void saveToFile(String saveto){        //写入文件
        if (saveto == null)
            return;
        if(countmove!=0)
            root.appendChild(round);
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(docs);

            Properties p=new Properties();
            p.setProperty(OutputKeys.ENCODING, "UTF-8");
//            p.setProperty(OutputKeys.INDENT,"yes");
            transformer.setOutputProperties(p);
            File f = new File(saveto);
            StreamResult result = new StreamResult(new FileOutputStream(f));

            transformer.transform(domSource, result);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
