import javax.swing.*;
import java.util.Random;

public class Brotherhood{            //葫芦兄弟类
    private final int CB_NUM=7;     //世界上有七个葫芦娃兄弟
    public CucurbitBoy cbs[]=new CucurbitBoy[CB_NUM];
    private int positionX=1, positionY=1;
    Brotherhood(BackGroundPanel father, BattleField bt) throws Exception {
        //创造世界上的葫芦娃
        for(int i=0;i<CB_NUM;i++)
            cbs[i]=new CucurbitBoy(father, bt);
//        randomize();                  //最初葫芦娃们随机排列
        swap(3,0);
        swap(2,6);
        swap(1,2);
        for(int i=0;i<CB_NUM;i++) {
            if(!cbs[i].alive)
                continue;
            cbs[i].setInnerPosition("兄弟们", i);
            cbs[i].resort("兄弟们",positionX, positionY);
        }
        for(int i=0;i<CB_NUM;i++) {
            Thread tmp = new Thread(cbs[i]);
            tmp.start();
        }
    }

    public void StandStill()        //葫芦娃们更新自己的位置
    {
        for(int i=0;i<CB_NUM;i++)
            cbs[i].StandStill();
    }

    public void countoff()
    {
        //用于调试输出葫芦娃顺序
        for(CucurbitBoy x:cbs)
            System.out.printf("%d ",x.getID());
        System.out.printf("\n");
    }

    //交换两个葫芦娃的位置
    private void swap(int cb1, int cb2)
    {
        if(cb1==cb2)
            return;
//        System.out.println(cbs[cb1].tellName()+":"+cb1+"->"+cb2);
//        System.out.println(cbs[cb2].tellName()+":"+cb2+"->"+cb1);
        if(!cbs[cb1].alive || !cbs[cb2].alive)
            return;
        CucurbitBoy tmp=cbs[cb1];
        cbs[cb1]=cbs[cb2];
        cbs[cb2]=tmp;
        return;
    }

    //葫芦娃随机站队
    protected void randomize()
    {
        Random random=new Random();
        int ctime=random.nextInt(21);
        for(int i=0;i<ctime;i++)
        {
            swap(random.nextInt(7),random.nextInt(7));
        }
        for(int i=0;i<CB_NUM;i++) {
            if(!cbs[i].alive)
                continue;
            cbs[i].setInnerPosition("兄弟们", i);
            cbs[i].resort("兄弟们",positionX, positionY);
        }
    }

    //对葫芦娃使用冒泡排序并报出自己的名字
    protected void BubbleSort()
    {
        for(int i=0;i<CB_NUM-1;i++)
        {
            for(int j=0;j<CB_NUM-i-1;j++)
                if(cbs[j].getID()>cbs[j+1].getID())
                    swap(j,j+1);
        }
        for(int i=0;i<CB_NUM;i++) {             //在排队后通知每个葫芦娃他在队列中的相对位置，并更新位置信息
            if(!cbs[i].alive)
                continue;
            cbs[i].setInnerPosition("兄弟们", i);
            cbs[i].resort("兄弟们",positionX, positionY);
        }
//        for(CucurbitBoy x:cbs)
//            System.out.print(x.typename+"! ");
//        System.out.print("\n");
    }

    //对葫芦娃使用二分法排序并报出自己的颜色
    protected void BiSort()
    {
        for(int i=1;i<CB_NUM;i++)
        {
            int left=0;
            int right=i-1;
            int mid=0;
            while(left<=right)
            {
                mid=(left+right)/2;
                if(cbs[mid].getID()<cbs[i].getID())
                    left=mid+1;
                else
                    right=mid-1;
            }
            int index=left;
            if(index!=i)
            {
                for(int j=i-1;j>=index;j--)
                    swap(j,j+1);
            }
        }
        for(int i=0;i<CB_NUM;i++) {
            cbs[i].setInnerPosition("兄弟们", i);
            cbs[i].resort("兄弟们",positionX, positionY);
        }
//        for(CucurbitBoy x:cbs)
//            System.out.print(x.tellColor()+"! ");
//        System.out.print("\n");
    }
}

