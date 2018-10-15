import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationHandler implements Runnable{      //动画批处理器，用于需要同步执行的动画处理
    private Charactors chats[]=new Charactors[]{};                          //批处理的角色

    public AtomicInteger cmd=new AtomicInteger(0);              //用于接收命令
    public AtomicBoolean avaliable=new AtomicBoolean(true);   //是否处理完成

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
    public void print()     //调试输出
    {
        for(int i=0;i<chats.length;i++)
            System.out.println(chats[i].typename);
    }
    public void run()
    {
        while (true)
        {
            try {
                Thread.sleep(50);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            cmdHandler();
        }
    }
    private void cmdHandler()       //命令处理函数
    {
        switch (cmd.get())
        {
            case 0: break;      // 0为闲置状态
            case 1: movement(); cmd.set(0);break;       // 1为处理移动动画
        }
    }
    private void movement()     //处理移动动画
    {
        avaliable.set(false);
        for (Charactors x : chats)
        {
            if(x.animate && x.avaliable.get())
            {
                x.avaliable.set(false);
                x.cmd.set(1);           //对所有角色发出执行移动动画的命令
            }
        }
        while(true)             //等待所有角色完成移动动画
        {
            boolean ready=true;
            for(Charactors x : chats)
            {
                if(!x.avaliable.get() && x.alive)
                {
                    ready=false;
                    break;
                }
            }
            if(ready)
                break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        virtualField.clear();       //更新场上位置信息
        for (Charactors x : chats)
        {
            x.animate=false;
            if(x.alive)
                virtualField.field[x.positionY][x.positionX]=x;
        }
        avaliable.set(true);
    }

    public int checkend(Charactors diechat)     //判断是否游戏结束，返回 1 则人类胜利， -1 则妖怪胜利
    {
        boolean human=false, monster=false;
        for(Charactors x:chats)
        {
            if(x.alive)
            {
                if(x.monster)
                    monster=true;
                else
                    human=true;
            }
        }
        if(human && monster)
            return 0;
        if(human)
            return 1;
        return -1;
    }
}
