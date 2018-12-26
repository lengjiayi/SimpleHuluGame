package com.ljy.java;

import javafx.scene.image.Image;

import java.awt.*;
import java.util.ArrayList;

/** 管理所有全局属性和图片，按照index寻址*/
public final class Configs {
    //MARK: 人物相关贴图，五个数组使用相同下标
    public static ArrayList<Image> normalIcons = new ArrayList<>();
    public static ArrayList<Image> movingIcons = new ArrayList<>();
    public static ArrayList<Image> Attack1Icons = new ArrayList<>();
    public static ArrayList<Image> Attack2Icons = new ArrayList<>();
    public static ArrayList<Image> Attack3Icons = new ArrayList<>();

    //MARK: 系统相关贴图
    public static ArrayList<Image> SysIcons = new ArrayList<>();

    //MARK: 人物图片的下标
    public static final int INDEX_BROTHERS = 0;
    public static final int INDEX_GRANDPA = 7;
    public static final int INDEX_SCORPTION = 8;
    public static final int INDEX_SNAKE = 9;
    public static final int INDEX_RORO = 10;

    //MARK: 系统其他图片的下标
    public static final int INDEX_PREBACKGROUND = 0;
    public static final int INDEX_BACKGROUND = 1;
    public static final int INDEX_START = 2;
    public static final int INDEX_LOAD = 3;
    public static final int INDEX_CLOSE = 4;
    public static final int INDEX_DCLOSE = 5;
    public static final int INDEX_FLOATBAR = 6;
    public static final int INDEX_TMPSKILL = 7;
    public static final int INDEX_SAVE = 8;
    public static final int INDEX_END = 9;
    public static final int INDEX_RIP = 10;
    public static final int INDEX_B_ATTACK1 = 11;
    public static final int INDEX_B_ATTACK2 = 12;
    public static final int INDEX_B_ATTACK3 = 13;
    public static final int INDEX_HP = 14;
    public static final int INDEX_MP = 15;
    public static final int INDEX_HUMANHEAD = 16;
    public static final int INDEX_MONSTERHEAD = 17;
    public static final int INDEX_HUMANCABIN = 18;
    public static final int INDEX_MONSTERCAVE = 19;
    public static final int INDEX_PREPAREGACKGROUND = 20;
    public static final int INDEX_HUMANHEAD2 = 21;
    public static final int INDEX_MONSTERHEAD2 = 22;
    public static final int INDEX_TWOPLAYER = 23;
    public static final int INDEX_AUTOPLAY = 24;
    public static final int INDEX_TWOPLAYER2 = 25;
    public static final int INDEX_AUTOPLAY2 = 26;
    public static final int INDEX_BATTLEFIELD = 27;
    public static final int INDEX_B_ATTACK12 = 28;
    public static final int INDEX_B_ATTACK22 = 29;
    public static final int INDEX_B_ATTACK32 = 30;

    //MARK: 其他UI布局相关的数值属性
    private static double[] rate;
    private static double[] cdf;

    static {
        rate = new double[]{2.50,2.65,2.79,3.00,3.21,3.39,3.56,0};
        double baserate = 0;
        cdf = new double[9];
        cdf[0] = 0;
        for(int i=0;i<rate.length-1;i++)
        {
            baserate += rate[i];
            cdf[i+1] = baserate;
        }
        rate[7] = rate[6];
        cdf[8] = cdf[7];
    }

    /** 判断坐标(x,y)是否在row, col透视后的四边形中。*
     * @return 如果在四边形中则返回此四边形的属性Block, 否则返回null
     */
    public static Block IsInBlock(double x, double y, int row, int col)
    {
        Block block = SPEC_B_SIZE(row, col);
        if(y < block.y || y > block.y+block.height)
            return null;
        double y_rate = (y - block.y)/block.height;
        double left_bound = block.tx - (block.bx - block.tx)*y_rate;
        double right_bound = (block.tx+block.twidth) - ((block.bx+block.bwidth) - (block.tx+block.twidth))*y_rate;
        if(left_bound<x && right_bound>x)
        {
            return block;
        }
        return null;
    }

    /**
     * 获得一个透视后方格的属性
     * @param x 方格虚拟列数
     * @param y 方格虚拟行数
     * @return Block对象，描述方格的属性
     */
    private static Block SPEC_B_SIZE(int x, int y){
        Block block = new Block();
        block.twidth = SpecWidth(y);
        block.bwidth = SpecWidth(y+1);
        block.height = B_SIZE * 7.0 * rate[y] / cdf[7];
        block.tx = B_SIZE * (7-y) / 7.0 + block.twidth * x + LEFT_MARGIN;
        block.bx = B_SIZE * (7-y-1) / 7.0 + block.bwidth * x + LEFT_MARGIN;
        block.y = B_SIZE * 7.0 * cdf[y] / cdf[7] + TOP_MARAGIN;
        return block;
    }

    /**
     * 获得一个透视后方格的属性
     * @param x 透视前纵坐标
     * @param y 透视前横坐标
     * @return Block对象，描述方格的属性
     */
    public static Block SPEC_MID_SIZE(int x, int y)
    {
        iPoint vloc = virtualField.rpTovp(x+1, y+1);
        double x_rate = (x - virtualField.vxTorx(vloc.x))/(double)B_SIZE;
        double y_rate = (y - virtualField.vyTory(vloc.y))/(double)B_SIZE;
        Block block = SPEC_B_SIZE(vloc.x, vloc.y);
        block.tx += block.twidth * x_rate;
        block.bx += block.bwidth * x_rate;
        block.y += block.height * y_rate;
        return block;
    }

    /**
     * 计算在透视变换后的方格下底宽度
     * @param row 虚拟行数，从 1 计数到 7
     * @return 透视变换后的方格上底宽度
     */
    public static double SpecWidth(int row)
    {
        return B_SIZE * (4.0 + cdf[row]/cdf[7]) / 5.0;
    }
    public static int WIN_HEIGHT;
    public static int WIN_WIDTH;
    public static int TOP_MARAGIN;
    public static int BOTTOM_MARAGIN;
    public static int LEFT_MARGIN;
    public static int RIGHT_MARGIN;
    public static int B_WNUM;
    public static int B_HNUM;
    public static int B_SIZE;
    public static int SBAR_WIDTH;
    public static int SBAR_HEIGHT;
    public static int CIBAR_WIDTH;
    public static int CIBAR_HEIGHT;
    public static int HINT_RADUIS;
    public static int HINT_PADDING;

    public final static String[] names = {"brother1","brother2","brother3","brother4",
            "brother5","brother6","brother7","grandpa",
            "scorption","snake", "Roro"};
    public static void initialize()
    {
        for(String x:names)
        {
            normalIcons.add(new Image(x+".PNG"));
            movingIcons.add(new Image(x+"mov1.PNG"));
            Attack1Icons.add(new Image(x+"a1.PNG"));
            Attack2Icons.add(new Image(x+"a2.PNG"));
            Attack3Icons.add(new Image(x+"a3.PNG"));
        }
        SysIcons.add(new Image("prebackground.PNG"));
        SysIcons.add(new Image("background.jpg"));

        SysIcons.add(new Image("start.PNG"));
        SysIcons.add(new Image("load.PNG"));
        SysIcons.add(new Image("close.PNG"));
        SysIcons.add(new Image("dclose.PNG"));

        SysIcons.add(new Image("floatbar.PNG"));
        SysIcons.add(new Image("tmpskill.PNG"));
        SysIcons.add(new Image("save.PNG"));
        SysIcons.add(new Image("END.PNG"));
        SysIcons.add(new Image("RIP.PNG"));
        SysIcons.add(new Image("attack1.PNG"));
        SysIcons.add(new Image("attack2.PNG"));
        SysIcons.add(new Image("attack3.PNG"));
        SysIcons.add(new Image("HP.PNG"));
        SysIcons.add(new Image("MP.PNG"));
        SysIcons.add(new Image("humanhead.PNG"));
        SysIcons.add(new Image("monsterhead.PNG"));
        SysIcons.add(new Image("humanhouse.PNG"));
        SysIcons.add(new Image("mcave.PNG"));
        SysIcons.add(new Image("preparebackground.PNG"));
        SysIcons.add(new Image("humanhead2.PNG"));
        SysIcons.add(new Image("monsterhead2.PNG"));
        SysIcons.add(new Image("tpmode.PNG"));
        SysIcons.add(new Image("automode.PNG"));
        SysIcons.add(new Image("tpmode2.PNG"));
        SysIcons.add(new Image("automode2.PNG"));
        SysIcons.add(new Image("backgroundSpec.PNG"));
        SysIcons.add(new Image("attack12.PNG"));
        SysIcons.add(new Image("attack22.PNG"));
        SysIcons.add(new Image("attack32.PNG"));


        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension screenSize=kit.getScreenSize();
        WIN_HEIGHT = (int)(screenSize.height * 5.0/6.0);
        SBAR_WIDTH = (int)(WIN_HEIGHT * 1/5.0);
        SBAR_HEIGHT = (int)(SBAR_WIDTH * 4);

        TOP_MARAGIN = (int)(0.05 * WIN_HEIGHT);
        BOTTOM_MARAGIN = (int)(0.05 * WIN_HEIGHT);

        B_SIZE = (WIN_HEIGHT - TOP_MARAGIN - BOTTOM_MARAGIN)/10;
        B_HNUM = 7;
        B_WNUM = 10;

        TOP_MARAGIN += B_SIZE;

        LEFT_MARGIN = SBAR_WIDTH + B_SIZE + 25;
        RIGHT_MARGIN = (int)(B_SIZE *1.5);
        WIN_WIDTH = LEFT_MARGIN + B_WNUM*B_SIZE +RIGHT_MARGIN;
        WIN_HEIGHT = TOP_MARAGIN + B_HNUM*B_SIZE +BOTTOM_MARAGIN;
//        WIN_HEIGHT -= B_SIZE;

        CIBAR_HEIGHT = B_SIZE*2;
        CIBAR_WIDTH = B_SIZE*6;
        HINT_RADUIS = B_SIZE/8;
        HINT_PADDING = HINT_RADUIS*3;
    }
}


class Block{
    double tx;
    double bx;
    double y;
    double twidth;
    double bwidth;
    double height;
    public Block(){}

}