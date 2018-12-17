package sample;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** 管理所有人物和攻击的UI对象，按照index寻址*/
public final class ViewBundle {

    //MARK: 每个人物及他们攻击效果的UI对象，使用相同下标
    public static ArrayList<Bullet> Attack1 = new ArrayList<>();
    public static ArrayList<Bullet> Attack2_1 = new ArrayList<>();
    public static ArrayList<Bullet> Attack2_2 = new ArrayList<>();
    public static ArrayList<Bullet> Attack2_3 = new ArrayList<>();
    public static ArrayList<Bullet> Attack3 = new ArrayList<>();

    //MARK: 人物的下标
    public static final int INDEX_BROTHERS = 0;
    public static final int INDEX_GRANDPA = 7;
    public static final int INDEX_SCORPTION = 8;
    public static final int INDEX_SNAKE = 9;
    public static final int INDEX_ROROS = 10;

    public static void initialize()
    {
        ImageView tmpview = null;
        Label tmp;
        int index = 0;
        for(String x:Configs.names)
        {
            Attack1.add(new Bullet(Configs.Attack1Icons.get(index)));

            Attack2_1.add(new Bullet(Configs.Attack2Icons.get(index)));
            Attack2_2.add(new Bullet(Configs.Attack2Icons.get(index)));
            Attack2_3.add(new Bullet(Configs.Attack2Icons.get(index)));

            Attack3.add(new Bullet(Configs.Attack3Icons.get(index)));

            index++;
        }
        index--;
        for(int i=0;i<Scorpion.troopScale-1;i++)
        {
            Attack1.add(new Bullet(Configs.Attack1Icons.get(index)));

            Attack2_1.add(new Bullet(Configs.Attack2Icons.get(index)));
            Attack2_2.add(new Bullet(Configs.Attack2Icons.get(index)));
            Attack2_3.add(new Bullet(Configs.Attack2Icons.get(index)));

            Attack3.add(new Bullet(Configs.Attack3Icons.get(index)));
        }
    }
}

class Bullet{
    AtomicBoolean visuable = new AtomicBoolean(false);
    AtomicInteger PositionX = new AtomicInteger(0);
    AtomicInteger PositionY = new AtomicInteger(0);
    Image icon;
    public Bullet(Image img)
    {
        icon = img;
    }
}