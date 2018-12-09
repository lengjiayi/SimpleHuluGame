## *复盘所需要的文件读写部分于20181016初步实现

# 葫芦娃兄弟

&#160; &#160; &#160; &#160;这是一个策略类游戏，游戏流程为回合制，人类和妖怪相互攻击，控制人类战斗直到一方全部被消灭结束。

## 使用方法

### 1，运行游戏

&#160; &#160; &#160; &#160;通过双击ljy.jar运行游戏，进入如下开始界面后点击“**开始游戏**”开始游戏，点击右上角的"**X**"退出游戏。*("**X**"只会在鼠标离开背景区域时才会显示）*<br>
![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/preframe.PNG)

### 2，游戏基本操作
&#160; &#160; &#160; &#160;本游戏的所有操作目前都由鼠标完成。点击进入游戏后的界面如下图：<br>
![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/initframe.PNG)
这个界面的左侧的棕色木板为技能选择栏，底部的半透明区域为人物属性框，右上角为退出游戏按钮，其下方为当前局的剩余操作次数*(含义在下面说明)*和一个透明的虚拟调试输出窗口。中间的包含人物贴图的区域即为游戏区域<br>

#### 2.1，查看人物属性
使用鼠标停在游戏区域的某一人物身上即可查看他的当前属性信息。
#### 2.2，人物移动和攻击
- 攻击：点击我方人物后左侧技能栏会出现当前选中人物姓名和1-3个按钮，由上到下依次为1v1攻击，1v3攻击和必杀技攻击，使用鼠标点击按钮即可使用对应招数，不同角色攻击效果不同。

  - 普通攻击：普通攻击可以无限使用
  - 必杀技：人类和妖怪都有必杀技，每个角色每局只可使用一次。
  - 1v3攻击：1v3攻击会消耗与攻击力成正比的MP，当MP耗光后招数不会再出现在对应人物技能栏中 

##### 技能栏和属性框效果如下图：

![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/gameframe.PNG)

![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/move.gif)

##### 攻击效果如下图：

![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/attack.gif)


- 移动：选中人物后点击游戏区域的空白区域即可令当前人物移动到该处，点击其他区域重新选择人物/取消选择。 <br>
  &#160;&#160;&#160;&#160;*(人类阵营遵循物理定律，先纵向移动再横向移动，当遇到敌人后发生碰撞。妖怪不遵循物理定律，随意移动)* <br>
#### 2.3，回合和胜负判定
- 玩家每回合可以执行三次操作，包括移动和攻击。 
- 当一方全部被消灭后游戏结束

### 3，保存和读取

&#160;&#160;&#160;&#160;经过20181016日的努力，增加了保存和读取进度功能。

- **保存：**在游戏界面右上角关闭按钮旁增加了保存按钮，点击会弹出文件选择器，并将当前进度保存在选择的文件中。
![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/reload.PNG)

- **读取：**在原先的基础上在开始界面增加了读取文件选项，点击后在弹出的文件选择器中选择之前保存的.xml文件即可开始复盘。*(testbattle.xml为我一次游戏的结果，可以作为测试读取)*
![preframe](https://github.com/lengjiayi/SimpleHuluGame/blob/master/运行截图/save.PNG)

### 4，游戏攻略

#### 4.1关于人物
&#160; &#160; &#160; &#160;每个角色都有不同的技能，其中橙色的二娃和黄色的三娃的普通攻击和必杀技为远程攻击，爷爷的攻击方式为远程为己方人物加HP，合理的利用不同人物的不同属性会获得意想不到的效果。<br>

~~(例如：将按照远程、爷爷、近战的方式排列可以由前面的近战人物抵挡伤害、爷爷为近战人物加血、远程和近战负责伤害输出)~~<b>

#### 4.2关于敌人
- 蝎子精是小妖怪的首领，如果将其击溃则小妖精不会再变换阵型
- 蝎子精很胆小的，当其受到一定伤害后会变阵退到最后方，因此最好在一个回合内将其击溃
- 蛇精每回合会为受伤的妖怪治疗，因此要尽快击溃她
### 5，附录：人物属性一览表
| 姓名 | 攻击手段 | HP | 1v1伤害 | 1v3伤害 | 必杀技伤害 |
| -----|:----:| ----:| ----:| ----:| ----:|
| 大娃 | 近战 | 120 | 10 | 20 | 90 |
| 二娃 | 远程 | 90 | 10 | 40 | 70 |
| 三娃 | 远程 | 80 | 5 | 20 | 80 |
| 四娃 | 近战 | 60 | 20 | 40 | 100 |
| 五娃 | 近战 | 100 | 20 | 30 | 90 |
| 六娃 | 近战 | 150 | 30 | 20 | 60 |
| 七娃 | 近战 | 60 | 10 | 20 | 50 |
| 爷爷 | 远程治疗 | 80 | -30 | -30 | -100 |
| 蝎子精 | 远程 | 120 | 20 | 40 | 80 |
| 小喽啰 | 远程 | 60 | 10 | 无 | 30 |
| 蛇精 | 近程治疗 | 70 | -30 | -50 | -100 |