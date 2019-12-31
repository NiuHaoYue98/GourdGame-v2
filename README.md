# Hulu Game

----------
2019 java课程大作业

## 游戏说明
### 基本设置
* 地图大小 15*15
* 生物种类 

|名称|生命值（HP）|攻击力（AP）|防御力（DP）|特殊技能|
|:-:|:-:|:-:|:-:|:-:|
|喽啰|20|4|2|-|
|蝎子|20|6|2|对方全部成员HP-2|
|蛇精|20|10|4|己方全部成员HP+2|
|爷爷|20|10|4|己方全部成员HP+2|
|红娃|20|1|7|-|
|橙娃|20|2|6|-|
|黄娃|20|3|5|-|
|绿娃|20|4|4|-|
|青娃|20|5|3|-|
|蓝娃|20|6|2|-|
|紫娃|20|7|1|-|

* 阵型种类 **鹤翼** **雁行** **衡轭** **长蛇** **鱼鳞** **方円** **偃月** **锋矢**
* 游戏规则
	* 游戏开始后双方成员在地图上随机移动。
	* 当生物发现有对方成员在自己周围9宫格内时，立即锁定对方成员并完成一次攻击。
	* 在攻击的过程中爷爷、蛇精、蝎子精以一定概率释放自身的特殊技能。
	* 生物死亡后会留下实体，占据地图中的一个位置，此后其他生物无法再进入该位置。
	* 当某一方生物全部死亡后游戏结束。

### 操作说明
* 进入初始界面后，可以选择开始新的游戏和回放历史游戏记录，或单击帮助查看游戏规则说明和开发信息。
* 完成一次完整的战斗或历史回放后，需点击重新开始，才可以重新开启新的战斗或者新的历史回放。
* 【关于新游戏】
	* 进入界面后双方均默认以长蛇阵型排列，玩家可以通过点击**葫芦娃变阵**或**妖精**变阵来随机改变游戏双方的各自的阵型，可选择的阵型范围如上所示。
	* 调整好双方阵型后单击**开始游戏**按钮，游戏即可自动启动，双方开始战斗，直到某一方全部死亡即为游戏结束。
* 【关于历史回放】
	* 玩家点击选择菜单栏中**文件**，点击**历史记录**选择需要读取的记录。记录文件为txt格式，选好游戏记录后回放将直接开始。
* 【关于帮助】
	* 玩家点击选择菜单栏中**帮助**，点击**游戏规则**可以查看游戏规则说明，点击**关于葫芦世界**可以查看相关开发信息。
	
### 界面展示
![](https://i.imgur.com/mvUsFsD.gif)

## 实现说明
### 继承
* **GUIThread.java**刷新界面的相关显示操作封装进了**ShowMap**中
* 最主要的继承是生物类中的继承，**Creature**包中的类继承关系如下：
![](https://i.imgur.com/2fg5PzY.png)


### 多线程
* 在世界类**World**中建立了一个线程池，**World.startBattle() **函数控制线程池启动。线程池中有17个生物线程和1个GUI界面刷新线程。
* **Creature **和** myScene **均实现了** Runnable **接口，可以作为线程使用。生物线程和GUI刷新线程轮流工作。这里是通过在** Map **类中使用了**wait()**方法实现的。
* 控制生物线程是否执行的是生物自身的控制符** actionFlag **，当** actionFlag **为** true**时，生物可以完整执行一次移动和攻击，执行结束后该标志自动置为** False **，执行** notifyAll() **,同时进入** WaitForAction **状态中。
* 控制界面刷新的是** Map **地图中的** creatureActionNum **，生物每进行一次动作它会加1，当它到达17后刷新显示界面，并重置生物线程** actionFlag **控制符，** creatureActionNum **清零，而后执行** notifyAll() **,界面刷新线程进入** waitForPrint() **状态。
* 实现中最重要的也是最容易写错的地方在于** notifyAll() **和**wait()**不是线程的方法，而是类的方法。多线程想要通过这种方法控制顺序，所有线程中都必须引用** Map **这一共享变量，** notifyAll() **和**wait()**通知的也是共享变量的改变。
* 必须留一条用来吐槽，这点老师上课很是强调过，然而必须经过很长很长很长很长时间DeBug的过程才能清楚地认识这一点。
* 核心代码如下：

```java
public class Map {
    public int creatureActionNum = 0;                      //生物动作计数
	……

    //多线程控制函数：生物动作和画面更新交替进行
    public synchronized void resetCreatureActionNum(){
        this.creatureActionNum = 0;
        notifyAll();
    }
    public synchronized void addCreatureActionNum(){
        this.creatureActionNum += 1;
        notifyAll();
    }
    public synchronized void waitForAction(Creature creature) throws InterruptedException{
        while(!creature.getActionFlag()){ wait();}
    }
    public synchronized void waitForPrint() throws InterruptedException{
        System.out.println("开始等待页面更新……"+this.getCreatureActionNum());
        while(this.getCreatureActionNum() != 17){ wait();}
    }
}

public class Creature implements Runnable {
    public boolean actionFlag = false;      //动作进行标志:true表示可以进行
    Map map;                            	//引用类型，引用公共变量map
	……

    public Creature(String name,Map map) {
        this.image = ImageLoader.getImage(name);
        this.map = map;
    }

    public void run(){
        try {
            while(true) {
                this.action();
                this.waitForAction();
            }
        }catch (InterruptedException e){
    }

    public void action(){
        //完成功能
        if(this.moveRound) {
            this.move();
            this.setMoveRound(false);
        }
        else {
            this.attack();
            this.usingSkill();
            this.setMoveRound(true);
        }
        //更新控制信息
        //自身控制信息
        this.setActionFlag(false);
        //地图打印控制信息
        map.addCreatureActionNum();
        System.out.println(this.name + "动作完毕！" + "this.map.creatureActinoNum = " + this.map.creatureActionNum);
    }
    public void waitForAction() throws InterruptedException{
        System.out.println(this.name + "等待动作……");
        map.waitForAction(this);
    }
}

public class myScene implements Runnable{
    private Map map;						//引用类型，引用公共变量map
    private Creature[] creatures;
	……

    public myScene(Map map, Creature[]creatures, Canvas mycanvans, World world, boolean is_reloaded, FileReader read, ShowMap showMap, Record record){
        ……
        this.map = map;             //引用地图
        this.creatures = creatures; //引用全部生物
    }

    @Override
    public void run() {
        //战斗
        if (this.is_reloaded == false) {this.battle();}
        //回放
        else {this.replyBattle();}
    }

    //【战斗】
    private void battle(){
        map.setStart();
        while (!Thread.interrupted()) {
            this.check(); //进行检查判断是否是应该结束
            System.out.println("at GUIThread.run 完成存活检验，可以继续战斗!" + this.is_finished);
            if (!this.is_finished) {
                try {
                    this.waitForPrint();
                    this.printMap();
                    //TimeUnit.SECONDS.sleep(1);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("GUI线程终止！");
                }
            }
            else {
                map.showMap();
                record.recordActions();
                this.showResult();
                System.out.println("游戏结束!");
                this.world.closeAll();
                this.record.endRecord(this.winnerOfBattle);
                Thread.interrupted();
                return;
            }
        }
    }

    public void printMap(){
        //刷新
        //this.showBattleground();
        map.showMap();
        System.out.println("at GUIThread.run 战斗进行中，完成战场地图绘制！");
        //保存
        record.recordActions();
        System.out.println("at GUIThread.run 战斗进行中，完成战场信息保存！");

        //完成打印功能
        //map.printMap();
        //更新控制信息
        //重置每个生物的执行控制符号
        for (Creature creature : this.creatures) {
            creature.setActionFlag(true);
        }
        //重置自身的控制符号（就是计数）
        map.resetCreatureActionNum();
        System.out.println("页面刷新");
    }

    //等待更新画面函数
    public void waitForPrint() throws InterruptedException{
        System.out.println("开始等待页面更新……"+this.map.getCreatureActionNum());
        map.waitForPrint();
    }

public class World {
    ExecutorService exec = Executors.newCachedThreadPool();
	……
    public void startBattle() throws Exception{
        this.map.setStart();
        myScene = new myScene(this.map,this.creatures,this.mycanvas,this,false,read,this.showMap,record);
        for (Creature creature:creatures){ exec.execute(creature);}
        exec.execute(myScene);
    }
}

```

### 设计原则
* **SRP单一职责原则**
在完成时尽可能地将模块抽象封装。最典型的是将地图绘制、生物显示这些操作封装到`ShowMap`中。这些操作原先都是直接写在`myScene`或者`Creature`中，两部分中代码有很多重复。进行这种抽象后减少了`canvas`参数层层传递，同时使各个类之间高内聚、低耦合。`Record`类的设计也是基于这种想法。
* **ISP接口隔离原则**
具有特殊技能的`Leaders`，`Scorpion`两个类均实现了`Finghting`接口。
* **CARP合成/聚合复用**
`Map`中的地图时`Position`的聚合，`Record`记录是`Map`和`Action`的聚合。

### 游戏记录及回放
* 记录游戏信息主要可以分为两部分：初始的阵型地图、游戏过程中的生物行为。两次刷新界面的时间间隔内的生物行为看成一组，在回放的时候同样在一次刷新间隔内完成。
* **Reply**包内的**Record.java**和**Action.java**分别记录了信息的结构和生物动作。
* 记录的过程中，写入的文件同样也是共享变量，因此写入时也加了锁。文件名为当前时间戳。
* 历史记录的回放没有调用生物多线程，生物行为顺序完成，然后周期结束后界面统一刷新。
* 在实现回放的过程中，我不仅一次遇到了诸如“它应该死了，可是它还活着”和“它应该活着，可是为什么是一个墓碑在动”的问题。于是我一直翻来覆去看我的多线程有没有哪里写错了，直到最后发现，不能在`Creature.usingSkill()`函数中确定随机释放技能。不然在回放的时候，技能的使用还是随机的，随机的……不要什么都怪多线程，万一就是因为我的顺序结构没学好呢？

### 其他
一些其他的基本的操作和实现可以简略说明：
#### 异常处理
在使用** Thread.sleep() **时必须用** try-catch **捕捉线程的异常；使用线程池控制多线程启动时也要在函数中抛出可能存在的异常** public void startBattle() throws Exception**；文件操作时也使用了** try-catch **捕捉异常。
#### 集合框架
主要使用的集合框架为** ArrayList<> **，在记录生物战斗信息时使用：
```java
    ArrayList<Action> actions;                  //战斗动作序列 Reply.Record
```
大作业之前对于葫芦娃使用的是** List<Creature> kids = new ArrayList<Creature>(); **属于领导类中创建的成员变量，原本的设计时领导者死亡之后其他生物就都死了，好处就是打乱重新排序只需要一步：
```java
 	Collections.shuffle(kids);
```
不过后来规则变成一方全部死亡游戏才会结束，葫芦娃和喽啰就不再挂在** Leader **之下了，而是在世界中单独创建。
#### 输入输出
使用** Writer write = new FileWriter(saveRes,false) **保存文件。全部的写文件操作被封装在了** Reply.Record **中，这部分分为创建问阿金、记录初始化地图，记录生物动作序列、关闭文件这四个部分。战斗回放中读取保存的文件。
#### 注解
`@Override`注解最常见，在子类中重写某种方法时就会用到，此外我还对两个线程类添加了` @Author`和`@Description`说明。
#### 自动构建 Maven
使用Maven自动构建代码，代码结构更加清晰。代码放入`src/main/java中`，图片资源等文件在`src/main/resources`中。
#### 单元测试
为地图显示模块添加了单元测试。


## The End
十分感谢两位老师这学期的讲解和指导！写大作业的时候才真的体会到“纸上得来终觉浅，绝知此事要躬行”的道理。多线程这种东西非得自己设计实现一次才算是完全明白。
另外这真的可以算得上是大项目了，我原先写代码总是边想边写，也都过来了。这次我也是这么开始的，然而最后发现还是需要先设计好，比如先定好整体的函数和变量，再逐一增加功能，不然真的一个头两个大，极度混乱。
这次写完几个类之后有意识地进行了功能抽象和封装。不得不说这还是个挺有成就感的过程，虽然我不是快刀，但也解开了一部分原来的乱麻。
java就是一门痛并快乐着的课啊，写大作业的时候每天我都在后悔为什么要选这门课，然而写完了还是有点点成就感的，毕竟也算是打开了多线程和图形界面的大门。
**再次感谢余萍老师和曹春老师本学期的讲解和指导！**