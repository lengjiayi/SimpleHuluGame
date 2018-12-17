public class virtualField {          //战场类
    public static int width=15;     //战场大小
    public static int height=15;
    public static char field[][]=new char[height][width];
    public static void clear()      //清空之前的残留信息
    {
        for(int i=0;i<height;i++)
            for (int j = 0; j < width; j++)
                field[i][j]='_';
    }
    public static void print()      //输出当前战场信息
    {
        for(int i=0;i<40;i++)       //分割线
            System.out.print('*');
        System.out.println();
        for(int i=0;i<height;i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(field[i][j]);
                System.out.print(' ');
            }
            System.out.println();
        }
    }
}
