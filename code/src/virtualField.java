package sample;

class dPoint{
    dPoint(double x, double y){ this.x = x; this.y = y; }
    double x;
    double y;
}

class iPoint{
    iPoint(int x, int y){ this.x = x; this.y = y; }
    iPoint(double x, double y){ this.x = (int)x; this.y = (int)y; }
    int x=0;
    int y=0;
}

public class virtualField {
    public static int width=10;     //战场大小
    public static int height=10;
    public static Charactor[][] cmap;

    public static void initial()
    {
        cmap = new Charactor[height][width];
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++)
                cmap[i][j]=null;
    }

    /** 将虚拟横坐标转化为画布上的横坐标*/
    public static int vxTorx(int vx)
    { return Configs.LEFT_MARGIN+vx*Configs.B_SIZE; }

    /** 将虚拟纵坐标转化为画布上的纵坐标*/
    public static int vyTory(int vy)
    { return Configs.TOP_MARAGIN + vy*Configs.B_SIZE; }

    /** 画布上的横坐标转化为将虚拟横坐标*/
    public static int rxTovx(double rx)
    { return (int)rx/Configs.B_SIZE; }

    /** 将画布上的纵坐标转化为虚拟纵坐标*/
    public static int ryTovy(double ry)
    { return (int)ry/Configs.B_SIZE; }

    /** 将虚拟坐标转化为画布上的坐标*/
    public static iPoint vpTorp(int vx, int vy)
    {
        return new iPoint(Configs.LEFT_MARGIN+vx*Configs.B_SIZE, Configs.TOP_MARAGIN + vy*Configs.B_SIZE);
    }
    /** 将画布上的坐标坐标转化为虚拟坐标*/
    public static iPoint rpTovp(double rx, double ry)
    {
        rx -= Configs.LEFT_MARGIN;
        ry -= Configs.TOP_MARAGIN;
        return new iPoint(rx/Configs.B_SIZE, ry/Configs.B_SIZE);
    }

    /**
     * 将战场的某一个方格设置为某个角色或清空
     * @param chat 角色
     * @param x 虚拟横坐标
     * @param y 虚拟纵坐标
     * @param clean 是否清空此方格
     */
    public static synchronized void set(Charactor chat, double x, double y, boolean clean)
    {
        iPoint vloc = rpTovp(x+Configs.B_SIZE/2,y+Configs.B_SIZE/2);
        if(vloc.x<0 || vloc.y<0 || vloc.x>=width || vloc.y>=height)
            return;
        if(clean)
            cmap[vloc.y][vloc.x] = null;
        else
            cmap[vloc.y][vloc.x] = chat;
    }
}
