package Map;

import Creatures.*;
import Reply.Action;
import javafx.scene.canvas.Canvas;
import java.util.ArrayList;

import static java.lang.System.exit;

public class Map {
    //基本设置
    private final static int N = 15;                    //地图大小
    Position[][] pos;        //地图位置
    private boolean start;                  //战争启动标志，控制战斗启动后不能改变阵型
    //多线程控制变量
    public int creatureActionNum = 0;                      //生物动作计数
    //绘制地图
    ShowMap showMap;
    //记录
    ArrayList<Action> actions;                  //战斗动作俩表

    public Map(ShowMap showMap,ArrayList<Action> actions){
        pos = new Position[N][N];
        for(int i =0;i<N;i++) {
            for(int j = 0; j < N;j++) {
                pos[i][j] = new Position(i,j);
            }
        }
        start = false;
        this.showMap = showMap;
        this.actions = actions;
    }

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
        while(!creature.getActionFlag()){
            wait();
        }
    }
    public synchronized void waitForPrint() throws InterruptedException{
        System.out.println("开始等待页面更新……"+this.getCreatureActionNum());
        while(this.getCreatureActionNum() != 17){
            wait();
        }
    }
    public synchronized void addAction(Action action){
        this.actions.add(action);
    }
    public synchronized Creature getCreature(int i, int j){
        return this.pos[i][j].getCreature();
    }
    public synchronized ShowMap getShowMap(){
        return this.showMap;
    }
    public synchronized Canvas getShowMapCanvas(){
        return this.showMap.getCanvas();
    }
    public synchronized void showMap(){
        showMap.showLines();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Creature tempCreature = this.getCreature(i,j);
                if (tempCreature != null)
                    showMap.showCreature(this.getPosCreature(i,j));
            }
        }
    }


    //set-get
    public void setCreature(Creature temp, int i, int j) {
        if(i < 0 || i >= N || j < 0 || j >= N) {
            System.err.println("error at Map.setCreature 放置生物时坐标错误!");
            exit(-1);
        }
        pos[i][j].getCre(temp);
    }
    public void setStart() {
        start = true;
    }
    public Creature getPosCreature(int x,int y){
        return this.pos[x][y].getCreature();
    }
    public int getCreatureActionNum(){
        return this.creatureActionNum;
    }


    //改变阵型，移动生物
    public void moveCreature(Creature temp, int i, int j) {
        if(i < 0 || i >= N || j < 0 || j >= N) {
            System.err.println("移动时坐标错误!");
        }
        int x = temp.getI();
        int y = temp.getJ();
        //未放置
        if(x == -1 && y == -1) {
            pos[i][j].getCre(temp);
        }
        else if(x != i || y != j) {
            if(pos[i][j].isEmpty() == false) {
                if(start == false)
                    pos[i][j].swapCre(pos[x][y]); //战斗没开始可以交换位置
                else{
                    System.err.println("at Map.moveCreature 已开始，目标位置非空，生物无法移动");
                }
            }
            else {
                pos[i][j].movCre(pos[x][y]);
            }
        }
    }
    //显示地图生物
    public void showCreatures(int i, int j) {
        Creature tempCreature = this.getPosCreature(i,j);
        this.showMap.showCreature(tempCreature);
    }

    //返回待攻击位置的生物
    public Creature checkEnemy(int i,int j){
        Creature res = null;
        boolean tempNature;
        //判断当前位置是否有生物
        if(this.pos[i][j].getCreature()!=null)
            tempNature = this.pos[i][j].getCreature().getNature();
        else
            return null;
        //判断四周是否有敌人
        for(int x = i - 1; x <=  i + 1; x ++) {
            for(int y = j - 1; y <= j + 1; y ++){
                //位置超出地图
                if(x < 0 || x >= N || y < 0 || y >= N) {
                    continue;
                }
                if (this.pos[x][y].isEmpty())
                    continue;
                Creature t = this.pos[x][y].getCreature();
                if(t.getAlive() == false)
                    continue;
                if(t.getNature() != tempNature) {
                    res = t;
                    break;
                }
            }
        }
        return res;
    }
    //清除位置信息
    public void clearPos(int i, int j) {
        this.pos[i][j].clearPos();
    }
    //判断某一阵营是否有生物存活
    public boolean judgeAlive(boolean nature){
        boolean alive = false;
        for(int i = 0; i < N; i ++) {
            for(int j = 0; j < N; j++) {
                if(this.pos[i][j].isEmpty() == false){
                    Creature temp = this.pos[i][j].getCreature();
                    if(temp.getAlive() && temp.retNature() == nature) {
                        alive = true;
                        break;
                    }
                }
            }
        }
        return alive;
    }
    public void killAll() {
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                this.pos[i][j].killPos();
            }
        }
    }


    //【回放】根据记录初始化地图
    public int[][] initFiled(Creature[] creatures) {
        int [][]init = new int[N][N];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++) {
                Creature t = this.pos[i][j].getCreature();
                if (t == null)
                    init[i][j] = -1;
                else {
                    for(int k = 0; k < creatures.length; k++){
                        if(creatures[k] == t) {
                            init[i][j] = k;
                        }
                    }
                }
            }
        }
        return init;
    }

}
