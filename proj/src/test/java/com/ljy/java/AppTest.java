package com.ljy.java;

import static org.junit.Assert.assertTrue;

import javafx.embed.swing.JFXPanel;
import org.junit.Test;

import java.util.Random;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * 简单的逻辑测试和资源测试
     */
    JFXPanel jfxPanel = new JFXPanel();

    @Test
    public void testConfigs()
    {
        try{
            Configs.initialize();
        }catch (IllegalArgumentException e){
            assertTrue("贴图加载失败，请重新下载贴图",false);
        }catch (Exception e){
            assertTrue("未知加载错误",false);
        }
        assertTrue("横向布局参数错误",Configs.LEFT_MARGIN + Configs.B_SIZE*Configs.B_WNUM + Configs.RIGHT_MARGIN == Configs.WIN_WIDTH);
        assertTrue("纵向布局参数错误",Configs.TOP_MARAGIN + Configs.B_SIZE*Configs.B_HNUM + Configs.BOTTOM_MARAGIN == Configs.WIN_HEIGHT);
        assertTrue( true );
    }

    @Test
    public void testVpToRp()
    {
        Configs.initialize();
        Random random = new Random();
        //MARK:100次随机坐标转换测试, 测试坐标应该落在同一个格子中，即返回的虚拟坐标为(3,2)
        iPoint result;
        for(int i=0;i<100;i++)
        {
            result = virtualField.rpTovp(Configs.LEFT_MARGIN + 3 * Configs.B_SIZE + random.nextInt(Configs.B_SIZE-1),
                    Configs.TOP_MARAGIN + 2 * Configs.B_SIZE + random.nextInt(Configs.B_SIZE-1));
            assertTrue( "实际坐标到虚拟坐标转换错误",result.x == 3 && result.y == 2);
        }
        result = virtualField.vpTorp(0,0);
        assertTrue( "虚拟坐标到实坐标转换错误",result.x == Configs.LEFT_MARGIN && result.y == Configs.TOP_MARAGIN);
        assertTrue(true);
    }
}