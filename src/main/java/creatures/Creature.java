package creatures;

import map.*;

import reply.Action;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.Random;

/**
 * @ Author     ：NHY
 * @ Description：生物线程
 */

public abstract class Creature implements Runnable {
    //多线程交替执行控制信号
    public boolean actionFlag = false;           //动作进行标志:true表示可以进行
    Map map;                                    //引用类型，引用公共变量map
    //设置信息
    final static int N = 15;                        //地图大小
    final static int size = 50;                     //格子大小
    //基本信息
    String name;                                    //生物名称
    //位置信息
    int i = -1;
    int j = -1;
    //战斗信息
    boolean type;                                  //阵营:true表示葫芦娃一方，false表示妖精一方
    int hp = 10;                                   //总体的血量
    int tempHp = 10;                               //当前的血量，防止加血过多
    int ap;                                        //攻击力
    int dp;                                        //防御力
    boolean isAlive = true;                        //是否存活
    boolean moveRound;                             //生物本轮是攻击还是移动，true表示攻击
    //显示相关
    Image image;                                    //图标
    Image battleImage;                             //攻击图
    Image deadImage;                               //死亡图
    Image skillImage;                               //技能图

    public Creature(Map map) {
        this.map = map;
        this.deadImage =  new Image(this.getClass().getClassLoader().getResource(new String("pic/dead.png")).toString(),
                this.size,this.size,false,false);
        this.moveRound = true;
    }

    //多线程
    public void run(){
        try {
            while(true) {
                this.action();
                this.waitForAction();
            }
        }catch (InterruptedException e){
            System.out.println(this.name + "线程终止！");
        }
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

    //功能
    public void move() {
        if(this.isAlive == false)
            return;
        try {
            //访问临界区方法，移动
            synchronized(this.map) {
                //System.out.println("进入mythread.move模块");
                int []start = {this.getI(),this.getJ()};
                int end[] = this.getTargetPos(); //生成目标位置：其中也调用了map中的方法
                Action action = new Action(0,start,end);
                map.addAction(action);
                map.moveCreature(this, end[0], end[1]);
                //System.out.println("at CreatureThread.move,生物移动完成！");
            }
        }
        catch (Exception e) {
            System.err.println("生物线程移动错误！\n at CreatureThread.move");
        }
    }
    private void attack(){
        if(this.isAlive == false) return;
        synchronized (this.map) {
            if (this.getAlive() == false)
                return;
            Creature enemy = this.findEnemy();
            if (enemy == null)
                return;
            if (this.getAlive() && enemy.getAlive()) {
                int[] start = {this.getI(),this.getJ()};
                int []end = {enemy.getI(),enemy.getJ()};
                Action action = new Action(1,start,end);
                map.addAction(action);
                this.attackEnemy(enemy);
            }
        }//保存动作
    }
    private void usingSkill() {
        if(this.isAlive == false) return;
        synchronized (map) {
            if (this.getAlive() == false) return;
            //1/10的概率释放技能
            Random rand = new Random();
            if(rand.nextInt(10) >= 1)
                return;
            this.usingSkill(map,map.getShowMapCanvas());
            int start[] = { this.getI() , this.getJ() };
            Action action = new Action(2,start,start);
            map.addAction(action);
        }
    }

    //set-get基本操作
    public int getI() {
        return i;
    }
    public int getJ() {
        return j;
    }
    public Image getImage(){return this.image;}
    public Image getDeadImage() {return this.deadImage; }
    public Image getBattleImage(){
        return this.battleImage;
    }
    public boolean getActionFlag(){
        return this.actionFlag;
    }
    public boolean getType(){
        return this.type;
    }
    public int getTempHp(){return this.tempHp;}
    public int getHp(){return this.hp;}
    public String getName(){
        return this.name;
    }
    public boolean getAlive(){
        return this.isAlive;
    }
    public void setPosition(int i, int j) {
        this.i = i;
        this.j = j;
    }
    public void setActionFlag(boolean flag){
        this.actionFlag = flag;
    }
    private void setMoveRound(boolean flag){
        this.moveRound = flag;
    }

    //确定目标位置
    public int[] getTargetPos() {
        int[] pos = {this.getI(),this.getJ()};
        for(int m = this.i - 1; m < this.i + 1 ; m++) {
            for (int n = this.j - 1; n < this.j + 1; n++) {
                if (m >= 0 && m < this.N && n >= 0 && n < this.N) {
                    if(map.judgePosEmpty(m,n)){
                        pos[0] = m;
                        pos[1] = n;
                        break;
                    }
                }
            }
        }
        int[] dirNum = map.countEnemyNum(this);//长度为4，分别为当前生物上下左右方向的敌人数目
        boolean left = true;
        boolean up = true;
        if(dirNum[0] < dirNum[1])
            up = false;
        if(dirNum[2] < dirNum[3])
            left = false;
        //System.out.println("at creatures.getTargetPos,已经确定移动的方向");
        int count = 0;
        //设置坐标
        while (true) {
            int x = this.i;
            int y = this.j;
            //三种移动情况，以上左true为例：只向上，只向左，向上向左，
            Random t = new Random();
            int choose = t.nextInt(6);
            if (choose < 3) {
                x = getTargetX(up, x);
                y = getTargetY(left, y);
            } else if (choose == 3) {
                y = getTargetY(left, y);
            } else if (choose == 4) {
                x = getTargetX(up, x);
            }
            else {
                //相反方向
                Random k = new Random();
                int r = k.nextInt(3);
                if (r == 0)
                    x = getTargetX(!up, x);
                else if (r == 1)
                    y = getTargetY(!left, y);
                else {
                    x = getTargetX(!up, x);
                    y = getTargetY(!left, y);
                }
            }
            //确定目标位置的坐标
            Creature temp = map.getCreature(x, y);
            //判断目标位置是否可以放置生物
            if (temp != null) {
                if (temp.getAlive() == false) {
                    count++;
                }
            }
            else {
                pos[0] = x;
                pos[1] = y;
                break;
            }
            //System.out.println("at creatures.getTargetPos,需要更换移动位置！");
            //找不到:随机移动到周围的空位上
            if(count == 5) {
                for(int m = this.i - 2; m < this.i + 2 ; m++) {
                    for (int n = this.j - 2; n < this.j + 2; n++) {
                        if (m >= 0 && m < this.N && n >= 0 && n < this.N) {
                            if(map.judgePosEmpty(m,n)){
                                pos[0] = m;
                                pos[1] = n;
                                break;
                            }
                        }
                    }
                }
                return pos;
            }
        }
        return pos;
    }
    private int getTargetY(boolean to_left, int y) {
        if(to_left== true){
            y--;
            if(y < 0)
                y = 0;
        }
        else{
            y++;
            if(y > N)
                y = N-1;
        }
        return y;
    }
    private int getTargetX(boolean to_up, int x) {
        if(to_up == true){
            x--;
            if(x < 0)
                x = 0;
        }
        else{
            x++;
            if(x >= N)
                x = N-1;
        }
        return x;
    }

    //攻击
    public Creature findEnemy(){
        Creature opponent = map.findRoundEnemy(this);
        return opponent;
    }
    public void attackEnemy(Creature enemy) {
        if(!this.getAlive()) return;
        int damage = this.ap - enemy.dp;
        if(damage <= 0)
            damage = 0;
        enemy.lost_blood(damage);
        int enemy_x = enemy.i;
        int enemy_y = enemy.j; //坐标
        //根据坐标进行绘图，进行攻击显示
        map.getShowMap().showAattack(enemy_x,enemy_y,this);
    }
    public void lost_blood(int i){
        this.tempHp -= i;
        if(this.tempHp <= 0) {
            this.tempHp = 0;
            this.isAlive = false;
        }
    }

    public void usingSkill(Map ground, Canvas canvas){};
    public void addBlood(int blood){
        this.tempHp += blood;
        if (this.tempHp > this.hp){
            this.tempHp = this.hp;
        }
    }
}