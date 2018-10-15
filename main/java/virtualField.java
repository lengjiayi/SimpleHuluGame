import java.awt.*;

public class virtualField {          //虚拟战场类
    public static int width=10;     //战场大小
    public static int height=10;
    public static Charactors field[][]=new Charactors[height][width];

    public static void clear()      //清空之前的残留信息
    {
        for(int i=0;i<10;i++)
            for (int j = 0; j < 10; j++)
                field[i][j]=null;
    }
    public static void print()      //调试输出
    {
        for(int i=0;i<10;i++) {
            for (int j = 0; j < 10; j++)
                System.out.print(field[i][j]==null?"0":"1");
            System.out.println();
        }
    }
}
