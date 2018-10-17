## 设计思路

### 数据类型

&#160;&#160;&#160;&#160;本游戏逻辑上主要的类型有**myFrame, Charactors, BattleField, virtualField , AnimationHandler , Bots, AutoPlayer 和 MySaver**。其他的数据类型都为在显示上的优化，具体实现见对应文件中注释。<br>

- **MyFrame：**继承JFrame，为进入游戏界面，使用了一个重写paintComponent()的JPanel作为背景，通过监听鼠标事件实现了界面的拖动。

- **Charactors**：Charactors类为所有人物类型的父类，包含了人物共有的属性数据成员和共有的操作函数，包括攻击和移动。Charactors类实现了Runnable接口，因此每一个人物都作为一个线程独立运作，在run()函数中循环接收外部指令并执行。部分人物根据需要重写了Charactors提供的方法。

- **BattleField：**BattleField控制一场战斗的行为，主要实现了游戏界面的绘制、更新和两方阵营的交替进攻控制。实现了Runnable接口在循环中不断接收鼠标行为，并根据鼠标行为更新界面。

- **virtualField：**virtualField为虚拟战场类，没有实现的对象，所有的成员都为静态成员因此可以被所有其他类对象访问。负责建立一个战场上位置和人物的索引关系。

- **AnimationHandler：**AnimationHandler为一个动画批处理类，实现了Runnable接口在后台等待接收指令，主要作用为实现所有人物移动动画时在virtualField中对应索引的同步。执行移动动画时被调用，负责向所有需要执行动画的人物发出指令并更新在virtualField上的索引。

- **Bots：**Bots包含敌人的运行逻辑，包括自动攻击、移动和回血，提供了nextMove()方法，在每回合结束后调用，控制所有敌人做出行动。

- **MySaver和AutoPlayer：**这两个类负责保存和读取游戏，保存时采用xml文件，每一回合的格式如下：

  ```xml
  <round>
      <move Chatno="6" X="2" Y="4" type="walk">-1</move>
      <move Chatno="6" X="-1" Y="-1" type="attack">3</move>
      <move Chatno="6" X="-1" Y="-1" type="attack">1</move>
  </round>
  ```
