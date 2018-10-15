public class Bots{      /* 控制所有妖精的行为 */
    private Scorpion scorption;
    private Snake snake;
    private  AnimationHandler anim;
    private BattleField battle;
    private int heallevel=0;
    private int count=0;
    public Bots(Scorpion s, Snake sn, AnimationHandler a, BattleField bf)
    {
        scorption=s;
        snake=sn;
        anim=a;
        battle=bf;
    }
    private void GroupAttack()  //群体攻击
    {
        for(Charactors x: scorption.troops)
        {
            if(x.alive && x.avaliable.get()) {
                x.avaliable.set(false);
                x.cmd.set(2);
            }
        }
        if(scorption.alive && scorption.avaliable.get())
            scorption.cmd.set(2);
    }

    private void  nFMT(int index)       //改变阵型
    {
        battle.debug.append("changeFMT\n");
        if (scorption.alive && anim.avaliable.get()) {
            scorption.changeFMT(index);
        }
    }

    private void ZXC()          //集体放大招
    {
        if(scorption.alive && scorption.zxcavaliable && scorption.avaliable.get()) {
            scorption.avaliable.set(false);
            scorption.cmd.set(4);
        }
    }

    private void heal()    //蛇精移动到需要救治的妖精处，并判断伤势等级
    {
        heallevel=0;
        battle.debug.append(snake.typename+" searching injury...\n");
        if(scorption.alive && scorption.HP<scorption.maxHP) {       //蝎子精优先接受治疗
            battle.debug.append(scorption.typename + " is injured");
            if (virtualField.field[scorption.positionY][scorption.positionX + 1] == null)
            {
                heallevel=1;
                if(scorption.maxHP-scorption.HP>=90 && snake.zxcavaliable)
                    heallevel=2;
                snake.moveto(scorption.positionX + 1, scorption.positionY);
                battle.debug.append("reach"+scorption.typename);
                return;
            }
        }
        for(Charactors x : scorption.troops)
        {       //小妖精接受治疗，不过蛇精的治疗大招不会浪费在小妖精身上
            if(x.alive && x.HP<x.maxHP && virtualField.field[x.positionY][x.positionX+1]==null) {
                heallevel=1;
                snake.moveto(x.positionX+1, x.positionY);
                battle.debug.append(x.typename+" at("+(x.positionX)+":"+x.positionY+") is injured\n");
                return;
            }
        }
    }

    public void waitforReady()      //等待当前动作结束
    {
        while(true)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean ready=true;
            for(Charactors x : scorption.troops)
            {
                if(!x.avaliable.get() && x.alive)
                {
                    ready=false;
                    break;
                }
            }
            if(scorption.alive && !scorption.avaliable.get())
                ready=false;
            if(snake.alive && !snake.avaliable.get())
                ready=false;
            if(!anim.avaliable.get())
                ready=false;
            if(ready)
                break;
        }
    }

    private boolean letsZXC()       //判断蝎子精是否应该发大招
    {
        if(!scorption.zxcavaliable)
            return false;
        for(int i=0;i<virtualField.width;i++)
            if(virtualField.field[scorption.positionY][i]!=null && !virtualField.field[scorption.positionY][i].monster)
                return true;
        return false;
    }

    public void nextMove()
    {
        battle.debug.append("Monster's Turn\n");
        count++;
        count=count%100;
        if(count>2 && letsZXC())           //如果当前满足条件则放大招。为了降低难度，前两个回合不会放大招。
            ZXC();
        else
            GroupAttack();                  //普通攻击
        waitforReady();
        battle.debug.append("M-attack ready\n");

        if(count%3==0 || scorption.HP<=70)      //蝎子精胆子很小，如果伤害过多就会变阵退到后面
            if(scorption.HP<=70 && scorption.curFMT!=2) {
                battle.debug.append("M-fatal damage\n");
                nFMT(2);
            }else if(scorption.HP>70)
                nFMT(-1);

        anim.cmd.set(1);
        battle.debug.append("M-nFMT\n");

        waitforReady();
        battle.debug.append("M-Move ready\n");

        if(snake.alive) {       //蛇精每个回合负责在最后治疗伤员
            heal();
            anim.cmd.set(1);
            waitforReady();
            if(heallevel!=0) {
                snake.avaliable.set(false);
                if(heallevel==1)
                    snake.cmd.set(2);
                else
                    snake.cmd.set(4);
            }
        }
        battle.debug.append("M-Heal ready\n");
    }
}
