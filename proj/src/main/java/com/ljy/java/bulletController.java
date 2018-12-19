package com.ljy.java;

public class bulletController{
    static final int ATTACK_REG =0;
    static final int ATTACK_AOE_1 =1;
    static final int ATTACK_AOE_2 =2;
    static final int ATTACK_AOE_3 =3;
    static final int ATTACK_ZXC =4;

    public static void start(Charactor chat, int type)
    {
//        if(battle == null || chat == null)
//            return;
        Bullet outlook;
        int chatx=chat.PositionX.get();
        int chaty=chat.PositionY.get();
        iPoint cur = virtualField.rpTovp(chatx+Configs.B_SIZE/2, chaty+Configs.B_SIZE/2);
        int injuery = 0;
        Charactor target = null;
        switch (type)
        {
            case ATTACK_REG:
                outlook = ViewBundle.Attack1.get(chat.IdNo);
                injuery = chat.regularcost;
                cur.x += chat.monster?-1:1; break;
            case ATTACK_ZXC:
                outlook = ViewBundle.Attack3.get(chat.IdNo);
                injuery = chat.zxccost;
                cur.x += chat.monster?-1:1; break;
            case ATTACK_AOE_1 :
                outlook = ViewBundle.Attack2_1.get(chat.IdNo);
                injuery = chat.mpcost;
                cur.x += chat.monster?-1:1; break;
            case ATTACK_AOE_2 :
                outlook = ViewBundle.Attack2_2.get(chat.IdNo);
                injuery = chat.mpcost;
                cur.y += 1; break;
            case ATTACK_AOE_3 :
                outlook = ViewBundle.Attack2_3.get(chat.IdNo);
                injuery = chat.mpcost;
                cur.y -= 1; break;
            default:
                System.out.println("attack type error");
                return;
        }
        iPoint rcur = virtualField.vpTorp(cur.x, cur.y);
        outlook.PositionX.set(rcur.x);
        outlook.PositionY.set(rcur.y);
        outlook.visuable.set(true);

        //非远程攻击
        if(!chat.remoteattack || type == ATTACK_AOE_1 || type == ATTACK_AOE_2 || type == ATTACK_AOE_3)
        {
            target = virtualField.cmap[cur.y][cur.x];
            if(target != null)
                target.injured(injuery);
            try {
                if (type == ATTACK_AOE_1 || type == ATTACK_AOE_2 || type == ATTACK_AOE_3)
                {
                    if (chat.automode)
                        Thread.sleep(20);
                    else
                        Thread.sleep(100);
                }
                else
                {
                    if (chat.automode)
                        Thread.sleep(50);
                    else
                        Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outlook.visuable.set(false);
            return;
        }
        int aimx = cur.x;
        int duration;
        double deltaloc;
        if(!chat.monster)
        {
            while (true) {
                if (aimx < virtualField.width &&
                        (virtualField.cmap[cur.y][aimx] == null ||
                                !virtualField.cmap[cur.y][aimx].alive ||
                                (virtualField.cmap[cur.y][aimx].monster == chat.heal)))
                    aimx++;
                else
                    break;
            }
            aimx = Math.min(aimx, virtualField.width-1);
            rcur.x -= Configs.B_SIZE/2;
            duration = 2+(aimx - cur.x + 1) * 3;     //攻击飞行时间和距离成正比
            if(chat.automode)
                duration /= 4;
            deltaloc=(aimx - cur.x + 0.5)*Configs.B_SIZE/(double)duration;
        }
        else
        {
            while (true) {
                if (aimx > 0 &&
                        (virtualField.cmap[cur.y][aimx] == null ||
                                !virtualField.cmap[cur.y][aimx].alive ||
                                (virtualField.cmap[cur.y][aimx].monster != chat.heal)))
                    aimx--;
                else
                    break;
            }
            aimx = Math.max(0,aimx);
            rcur.x += Configs.B_SIZE/2;
            duration = 2+(cur.x - aimx +1) * 5;
            if(chat.automode)
                duration /= 4;
            deltaloc = -(cur.x - aimx)*Configs.B_SIZE/(double)duration;
        }
        target=virtualField.cmap[cur.y][aimx];

        for(int i=0;i<duration;i++)
        {
//            Platform.runLater(()-> {
//                outlook.relocate(rcur.x, rcur.y);
//                outlook.setVisible(false);
//                outlook.setVisible(true);
//            });
            outlook.PositionX.set(rcur.x);
            outlook.PositionY.set(rcur.y);
            rcur.x += deltaloc;
            try {
                Thread.sleep(1000/36);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

/*
        TranslateTransition move = new TranslateTransition(Duration.seconds(duration * 0.1));
        move.setToX(virtualField.vxTorx(aimx));
        FadeTransition fade = new FadeTransition(Duration.seconds(0.1));
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        SequentialTransition sequnce = new SequentialTransition(outlook, move, fade);
        Platform.runLater(()-> {
            sequnce.play();
        });
*/
//        Platform.runLater(()-> {
//            System.out.println();
//            outlook.setVisible(false);
//        });
        outlook.visuable.set(false);
        if(target != null)
            target.injured(injuery);
    }
}
