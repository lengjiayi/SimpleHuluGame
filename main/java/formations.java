public class formations //阵型类
{
    public int troopsize;       //阵型需要的喽啰人数
    public int[][] RelativePosition;  //(x,y) pairs         //阵法
    public int topDistance,bottomDistance,leftDistance,rightDistance;       //布阵所需空间
}
class Fengshi extends formations        //锋失阵
{
    public Fengshi(){
        troopsize=13;
        RelativePosition=new int[][]{{1,0},{1,1},{1,-1},{2,0},{2,1},{2,-1},{2,2},{2,-2},
                {3,0},{3,3},{3,-3},{4,0},{5,0}};
        topDistance=3; bottomDistance=3; leftDistance=0; rightDistance=5;
    }
}

class Yanyue extends formations         //偃月阵
{
    public Yanyue(){
        troopsize=13;
        RelativePosition=new int[][]{{0,1},{0,-1},{1,0},{1,1},{1,-1},
                {1,2},{1,-2},{2,2},{2,-2},{2,3},{2,-3},{3,3},{3,-3}};
        topDistance=3; bottomDistance=3; leftDistance=0; rightDistance=3;
    }
}

class Yulin extends formations
{
    public Yulin(){
        troopsize=13;
        RelativePosition=new int[][]{{-4,0},{-3,1},{-3,-1},{-2,0},{-2,2},{-2,-2},
                {-1,0},{-1,1},{-1,-1},{0,1},{0,-1},{0,2},{0,-2}};
        topDistance=2; bottomDistance=2; leftDistance=4; rightDistance=0;
    }
}