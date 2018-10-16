import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class MySaver {
    private String saveto = null;
    public JButton savebtn;
    private Document docs;
    private Element root;
    private Element round;
    private int countmove;
    public static void main(String[] args) {
        JFrame frame = new JFrame("test");
        frame.setResizable(false);

        frame.setLayout(null);
        frame.setBounds(100, 100, 200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        MySaver ms = new MySaver(frame);
        ms.savebtn.setBounds(120, 120, 50, 50);
        ms.savebtn.setText("保存");
        frame.getContentPane().add(ms.savebtn);
    }

    public MySaver(JFrame frame) {
        init();
        countmove=0;
        savebtn = new JButton();
        savebtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                FileDialog fd = new FileDialog(frame, "保存", FileDialog.SAVE);
                fd.setVisible(true);
                saveto = fd.getDirectory()+fd.getFile();
                System.out.println(saveto);
                save2file();
            }
        });
    }

    public void init() {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            docs = db.newDocument();
            root = docs.createElement("battle");

            String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
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

    public void addMove(int chatno, Point dst, int attack)
    {
        countmove=countmove%3;
        if(countmove==0) {
            if(round!=null)
                root.appendChild(round);
            round = docs.createElement("round");
        }Element move=docs.createElement("move");
        if(attack>0)
            move.setAttribute("type","attack");
        else
            move.setAttribute("type","walk");
        move.setAttribute("Chatno",Integer.toString(chatno));
        move.setAttribute("Y",Integer.toString(dst.y));
        move.setAttribute("X",Integer.toString(dst.x));
        Text m=docs.createTextNode(Integer.toString(attack));
        move.appendChild(m);
        round.appendChild(move);
        countmove++;
    }

    public void save2file(){
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


/*
    public void read() {
        if (readfrom == null)
            return;
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document docs = db.parse(readfrom);
            Node node=docs.getDocumentElement();
            System.out.println(node.getNodeName());
            NodeList child=node.getChildNodes();
            System.out.println(child.getLength());
            for(int i=0;i<child.getLength();i++)
            {
                System.out.println(child.item(i).getNodeName()+":"+child.item(i).getTextContent());
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
*/
