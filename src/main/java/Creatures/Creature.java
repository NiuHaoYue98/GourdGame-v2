package Creatures;

import Map.*;

import Reply.Action;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

/**
 * @ Author     ：NHY
 * @ Description：生物线程
 */

public class Creature implements Runnable {
    //多线程
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
    boolean nature;                                //阵营
    int hp = 10;                                   //总体的血量
    int tempHp = 10;                               //当前的血量，防止加血过多
    int ap;                                        //攻击力
    int dp;                                        //防御力
    boolean isAlive = true;                        //是否存活
    boolean moveRound;
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
                int end[] = this.getNewpos(this.map); //检测
                int x= end[0];
                int y =end[1]; //移动的x，y
                int []start = {this.getI(),this.getJ()};
                Action action = new Action(0,start,end);
//                System.out.print("移动");
//                System.out.println(start[0]+" " + start[1] + " " +pos[0] + " "+pos[1]);
                //System.out.println("已经生成移动后的坐标！");
                map.addAction(action);
                map.moveCreature(this, x, y);
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
            //四周都没有敌人就不攻击
            Creature enemy = this.map.checkEnemy(this.getI(),this.getJ()); //根据坐标获取
            if (enemy == null) return;
            //都活着
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
    public void set_hp(int hp){
        this.hp = hp;
    }
    public void set_ap(int ap){
        this.ap = ap;
    }
    public void setActionFlag(boolean flag){
        this.actionFlag = flag;
    }
    public boolean getActionFlag(){
        return this.actionFlag;
    }
    public boolean getNature(){
        return this.nature;
    }
    public void setPosition(int i, int j) {
        this.i = i;
        this.j = j;
    }
    public int getI() {
        return i;
    }
    public int getJ() {
        return j;
    }
    public Image getImage(){return this.image;}
    public Image getDeadImage() {return this.deadImage; }
    public int getTempHp(){return this.tempHp;}
    public int getHp(){return this.hp;}
    public String getName(){
        return this.name;
    }
    public boolean getAlive(){
        return this.isAlive;
    }
    public boolean retNature() {
        return this.nature;
    }
    public Image getBattleImage(){
        return this.battleImage;
    }
    private void setMoveRound(boolean flag){
        this.moveRound = flag;
    }


    //移动
    private int getY(boolean to_left, int y) {
        if(to_left== true){
            y--;
            if(y < 0) y = 0;
        }
        else{
            y++;
            if(y > 19) y = 19;
        }
        return y;
    }
    private int getX(boolean to_up, int x) {
        if(to_up == true){
            x--;
            if(x < 0) x = 0;
        }
        else{
            x++;
            if(x > 9) x = 9;
        }
        return x;
    }
    public int[] getNewpos(Map ground) {
        ///检测，通往敌人多的方向去
        //System.out.println("进入Creature.want_to_move_x_y函数");
        int num_up = 0;
        int num_down = 0;
        int num_right = 0;
        int num_left = 0;
        boolean to_left = true;
        boolean to_up = true; //向左和向上
        int pos[] = new int[2];
        pos[0] = this.i;
        pos[1] = this.j;
        //判断各个方位上的怪物
        for(int i = 0; i < this.N;i++){
            for(int j = 0; j < this.N; j++){
                if(i != this.i && j != this.j){
                    Creature temp = ground.getCreature(i,j); //得到
                    if(temp != null){
                        if(temp.isAlive == true&& this.isAlive == true && this.nature != temp.nature){ //敌人
                            if(i < this.i){
                                num_up++;
                            }
                            else {
                                num_down++;
                            }
                            if(j < this.j){
                                num_left ++;
                            }
                            else{
                                num_right++;
                            }
                        }
                    }
                }
            }
        }
        if(num_left < num_right){
            to_left =  false;
        }
        if(num_up < num_down){
            to_up = false;
        }
        //System.out.println("at Creatures.getNewpos,已经确定移动的方向");
        int count = 0;
        //设置坐标
        while (true) {
            int x = this.i;
            int y = this.j;
            Random t = new Random();
            int choose = t.nextInt(4);
            if (choose == 0) {
                x = getX(to_up, x);
            } else if (choose == 1) {
                y = getY(to_left, y);
            } else if (choose == 2) {
                x = getX(to_up, x);
                y = getY(to_left, y);
            } else { //相反方向
                Random k = new Random();
                int r = k.nextInt(3);
                if (r == 0)
                    x = getX(!to_up, x);
                else if (r == 1)
                    y = getY(!to_left, y);
                else {
                    x = getX(!to_up, x);
                    y = getY(!to_left, y);
                }
            }
            //确定目标位置的坐标
            Creature temp = ground.getCreature(x, y);
            //判断目标位置是否可以放置生物
            if (temp != null) {
                //墓碑
                if (temp.getAlive() == false) {
                    count++;
                }
            }
            else {
                pos[0] = x;
                pos[1] = y;
                break;
            }
            //System.out.println("at Creatures.getNewpos,需要更换移动位置！");
            //找不到
            if(count == 10) {
                for(int m = this.i - 1; m < this.i + 1 ; m++) {
                    for (int n = this.j - 1; n < this.j + 1; n++) {
                        if (m >= 0 && m < this.N && n >= 0 && n < this.N) {
                            Creature cre_temp = ground.getCreature(m, n);
                            if (cre_temp == null) {
                                Random can_move = new Random();
                                int chance = can_move.nextInt(2);
                                if (chance == 0) {
                                    pos[0] = m;
                                    pos[1] = n;
                                    //这里增加了两个Break用于快速确定位置坐标
                                    break;
                                }
                            } else if (cre_temp.getAlive() == false) {
                                Random can_move = new Random();
                                int chance = can_move.nextInt(2);
                                if (chance == 0) {
                                    pos[0] = m;
                                    pos[1] = n;
                                    //这里增加了两个Break用于快速确定位置坐标
                                    break;
                                }
                            }
                        }
                    }
                }
                return pos;
            }
        }
        return pos;
    }

    //攻击
    public void attackEnemy(Creature enemy) {
//        if(!this.getAlive() || !enemy.getAlive()) return;
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
    public void killSelf() {
        this.isAlive = false; //死亡
    }

    public void usingSkill(Map ground, Canvas canvas){}
    public void addBlood(int blood){}
}