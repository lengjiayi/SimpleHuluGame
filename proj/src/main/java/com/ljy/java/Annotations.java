package com.ljy.java;

import javax.imageio.IIOException;
import java.io.*;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface IsCharactor{
    String name();
    String group();
    int HP();
    int MP();
    int REGcost();
    int AOEcost();
    int ZXCcost();
    boolean remote();
    boolean heal();
}

class HintGenerater{
    static HintGenerater generater;
    static Class[] clazzlist = {Grandpa.class, Scorpion.class, Snake.class};
    static String helpstring = "";
    static{
        generater = new HintGenerater();
    }
    private HintGenerater(){ }
    public static boolean GenerateHintDoc(){
        if(generater==null)
            return false;
        CucurbitBoys[] brothers = CucurbitBoys.values();
        for(CucurbitBoys boy:brothers)
        {
            helpstring += boy.getName()+"(人类):";
            helpstring += "\n\t属性: " + "攻击型、" + (boy.IsRemote()?"远程型":"近战型");
            helpstring += "\n\tHP: "+boy.getHP();
            helpstring += "\tMP: "+100;
            helpstring += "\n\t普通攻击: "+boy.getregCost();
            helpstring += "\n\t群伤攻击: "+boy.getMpCost();
            helpstring += "\n\t必杀攻击: "+boy.getZXCCost();
            helpstring += "\n\n";
        }
        for(Class clazz:clazzlist)
        {
            if(clazz.isAnnotationPresent(IsCharactor.class))
            {
                IsCharactor annotation = (IsCharactor) clazz.getAnnotation(IsCharactor.class);
                helpstring += annotation.name()+"("+annotation.group()+"):";
                helpstring += "\n\t属性: " + (annotation.heal()?"治疗型、":"攻击型、") + (annotation.remote()?"远程型":"近战型");
                helpstring += "\n\tHP: "+annotation.HP();
                helpstring += "\tMP: "+annotation.MP();
                helpstring += "\n\t普通攻击: "+annotation.REGcost();
                helpstring += "\n\t群伤攻击: "+annotation.AOEcost();
                helpstring += "\n\t必杀攻击: "+annotation.ZXCcost();
                helpstring += "\n\n";
            }
        }
        File file = new File("hints.txt");
        try{
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(helpstring);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        generater = null;
        return true;
    }
}