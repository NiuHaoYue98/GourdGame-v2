package GUI;

import Creatures.*;
import Map.*;
import Reply.Record;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * @ Author     ：NHY
 * @ Description：界面显示线程
 */

public class myScene implements Runnable{
    //基本设置
    private final static int N = 15;        //画布大小
    //实体
    private Canvas mycanvas;
    private Map map;
    private World world;
    private Creature[] creatures;
    //控制信号
    private boolean is_finished;           //战斗是否结束
    private boolean winnerOfBattle;        //战斗结果
    private boolean is_reloaded;           //是否回放
    //图片相关
    private Image winner;                  //胜利图标
    //信息记录
    private Record record;                  //记录相关，引用
    private int temp_line;
    private int total_line;

    public myScene(Map map, Creature[]creatures, Canvas mycanvans, World world, boolean is_reloaded, ShowMap showMap, Record record){
        this.mycanvas = mycanvans;
        this.world = world;
        this.is_finished = false;
        this.record = record;
        this.is_reloaded = is_reloaded;

        this.map = map;             //引用地图
        this.creatures = creatures; //引用全部生物
    }

    @Override
    public void run() {
        //战斗
        if (this.is_reloaded == false) {
            this.battle();
        }
        //回放
        else{
            this.replyBattle();
        }

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
                world.closeAll();
                record.endRecord(this.winnerOfBattle);
                world.shutDownCreatures();
                Thread.interrupted();
                return;
            }
        }
    }
    //检查战斗是否结束
    public void check() {
        boolean is_Calash_Alive;
        boolean is_Monster_Alive;
        //上锁，其他进程不能访问，通过其获得状态
        synchronized (this.map){
            is_Calash_Alive = this.map.judgeAlive(true);
            is_Monster_Alive = this.map.judgeAlive(false);
        }
        System.out.print("葫芦娃存活状态:"+is_Calash_Alive+"\t 敌人存活状态："+is_Monster_Alive+"\n");
        if(is_Calash_Alive == false && is_Monster_Alive == false){
            this.is_finished = true;
            this.winnerOfBattle = true;
            System.out.println("未分出胜负，双方同归于尽！\n");
        }
        if(is_Monster_Alive == false ) {
            this.is_finished = true;
            this.winnerOfBattle = true;
            System.out.println("葫芦娃获得胜利！");
        }
        if(is_Calash_Alive == false) {
            this.is_finished = true;
            this.winnerOfBattle = false;
            System.out.println("妖怪获得胜利");
        }
    }

    //更新画面
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
    //等待更新画面
    public void waitForPrint() throws InterruptedException{
        System.out.println("开始等待页面更新……"+this.map.getCreatureActionNum());
        map.waitForPrint();
    }

    //绘制战斗结果
    private void showResult(){
        if(this.winnerOfBattle) {
            this.winner = new Image(this.getClass().getClassLoader().getResource(new String("pic/葫芦娃胜利.png")).toString(),
                    400, 200, false, false);
        }
        else{
            this.winner = new Image(this.getClass().getClassLoader().getResource(new String("pic/妖精胜利.png")).toString(),
                    400, 200, false, false);
        }
        this.mycanvas.getGraphicsContext2D().drawImage(this.winner, 200, 200);
    }
    //【回放】
    public void replyBattle(){
        map.setStart();
        ArrayList<String> recordList = this.record.readRecordList();
        temp_line = 0;
        total_line = recordList.size();
        //读取地图
        int[][] initPos = new int[N][N];
        while (temp_line < N) {
            String[] t = recordList.get(temp_line).split(" ");
            for (int j = 0; j < N; j++) {
                initPos[temp_line][j] = Integer.parseInt(t[j]); //
                System.out.print(initPos[temp_line][j]);
            }
            System.out.println();
            temp_line++;
        }
        //初始化地图
        this.replyInitMap(initPos);
        map.showMap();
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.replyRefresh(recordList);
            if (temp_line >= total_line) {
                System.out.println("回放结束");
                map.showMap();
                this.showResult();
                Thread.interrupted();
                return;
            }
        }
    }
    public void replyInitMap(int [][]t){
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N;j++){
                int num = t[i][j];
                //地图上什么都没有，清除位置信息
                if(num == -1){
                    this.map.clearPos(i,j);
                    continue;
                }
                //放置生物
                this.map.moveCreature(creatures[num],i,j);
            }
        }
    }
    private void replyAction(int type,int[] start_pos,int[] end_pos){
        if(type == 0) {
            Creature cre = this.map.getCreature(start_pos[0], start_pos[1]);
            if (cre != null) {
                if(cre.getAlive()){
                    this.map.moveCreature(cre, end_pos[0], end_pos[1]);
                }
                else
                    System.err.println("at GUIThread.replyAction 当前位置生物已死亡，无法移动！");
            }
            else
                System.err.println("at GUIThread.replyAction 当前位置无生物，无法移动！");
        }
        else if (type == 1) {
            Creature cre = this.map.getCreature(start_pos[0], start_pos[1]);
            Creature enemy = this.map.getCreature(end_pos[0], end_pos[1]);
            if (cre != null && enemy != null) {
                if(cre.getAlive()) {
                    cre.attackEnemy(enemy);
                }
                else
                    System.err.println("at GUIThread.replyAction 当前位置生物已死亡，无法攻击！");
            }
            else if(cre == null){
                System.err.println("at GUIThread.replyAction 当前位置无对象，无法攻击！");
            }
            else if(enemy == null){
                System.err.println("at GUIThread.replyAction 目标位置无对象，无法攻击！");
            }
            else System.err.println("at GUIThread.replyAction 对象错误，无法攻击！");
        }
        //别加，加了会触发灵异事件【墓碑移动】！！！？？为什么又进行一次就好了~又不好了……
        else if(type == 2){
            Creature cre = this.map.getCreature(start_pos[0], start_pos[1]);
            if(cre != null){
                if(cre.getAlive()){
                    cre.usingSkill(this.map,this.mycanvas);
                }
                else{
                    System.err.println(cre.getName()+"已经死亡，无法释放技能");
                }
            }
            else System.err.println("at GUIThread.review 释放技能错误！");
        }

    }
    public void replyRefresh(ArrayList<String> arrayList){
        while(temp_line < total_line) {
            String[] t = arrayList.get(temp_line).split(" ");
            if (t.length == 1) {
                System.out.println("Round"+t[0]);
                temp_line++;
                System.out.println(temp_line);
                map.showMap();
                return;
            }
            else if(t.length == 5){
                int type = Integer.parseInt(t[0]);
                int []start_pos = {Integer.parseInt(t[1]),Integer.parseInt(t[2])};
                int []end_pos = {Integer.parseInt(t[3]),Integer.parseInt(t[4])};
                this.replyAction(type,start_pos,end_pos);
                temp_line++;
            }
            else if(t.length == 2){
                temp_line++;
                System.out.println(t[0]);
                if(t[0].equals("calash")) {
                    this.is_finished = true;
                    this.winnerOfBattle = true;
                    System.out.println("葫芦娃获得胜利！");
                }
                else if(t[0].equals("monster"))
                {
                    this.is_finished = true;
                    this.winnerOfBattle = false;
                    System.out.println("妖怪获得胜利");
                }
            }
        }
    }


}
