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

    //MARK: 其他UI布局相关的数值属性
    public static int WIN_HEIGHT;
    public static int WIN_WIDTH;
    public static int TOP_MARAGIN;
    public static int BOTTOM_MARAGIN;
    public static int LEFT_MARGIN;
    public static int RIGHT_MARGIN;
    public static int B_NUM;
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

        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension screenSize=kit.getScreenSize();
        WIN_HEIGHT = (int)(screenSize.height * 5.0/6.0);
        SBAR_WIDTH = (int)(WIN_HEIGHT * 1/5.0);
        SBAR_HEIGHT = (int)(SBAR_WIDTH * 4);

        TOP_MARAGIN = (int)(0.05 * WIN_HEIGHT);
        BOTTOM_MARAGIN = (int)(0.1 * WIN_HEIGHT);

        B_NUM = 10;
        B_SIZE = (WIN_HEIGHT - TOP_MARAGIN - BOTTOM_MARAGIN)/B_NUM;

        LEFT_MARGIN = SBAR_WIDTH + B_SIZE + 25;
        RIGHT_MARGIN = (int)(B_SIZE *1.5);
        WIN_WIDTH = LEFT_MARGIN + 10*B_SIZE +RIGHT_MARGIN;

        CIBAR_HEIGHT = B_SIZE*2;
        CIBAR_WIDTH = B_SIZE*6;
        HINT_RADUIS = B_SIZE/8;
        HINT_PADDING = HINT_RADUIS*3;
    }
}
