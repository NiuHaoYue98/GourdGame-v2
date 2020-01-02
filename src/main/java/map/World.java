package map;

import creatures.*;
import gui.MyScene;
import reply.Action;
import reply.Record;
import javafx.scene.canvas.Canvas;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class World {
    //生物
    private Creature[] creatures;
    private Gourd[]Huluwas;
    private GrandPa grandfather;
    private Snake snake;
    private Scorpion scorpion;
    private Bandit[] bandits;
    //地图
    private Map map;
    private Formation form;
    private Canvas mycanvas;
    private final static int N = 15;        //战场大小
    private ShowMap showMap;
    private MyScene myScene;
    //记录
    private Record record;
    //线程池
    ExecutorService exec= Executors.newCachedThreadPool();

    public World(Canvas canvas, ArrayList<Action> actions, ShowMap showMap,Record record){
        this.mycanvas = canvas;
        this.showMap = showMap;
        map = new Map(showMap,actions);

        //【回放】记录并读取
        this.record = record;
        creatures = new Creature[17];
        form = new Formation(0);

        grandfather = new GrandPa(map);            //创建爷爷
        snake = new Snake(map);                //创建蛇精
        Huluwas = new Gourd[7];
        for(int i  = 0; i < 7;i++) {
            Huluwas[i] = new Gourd(map,i);
            creatures[i] = Huluwas[i];
        }
        //爷爷
        creatures[7] = grandfather;
        //蛇精
        creatures[8] = snake;
        //蝎子精
        scorpion = new Scorpion(map);
        creatures[9] = scorpion;
        //小怪
        bandits = new Bandit[7];
        for(int i = 0; i < 7; i++) {
            bandits[i] = new Bandit(map);
            creatures[i + 10] = bandits[i];
        }
        grandfather.initKids(Huluwas);
        snake.initKids(bandits);
    }

    //初始化放置生物进入地图
    public void initCreatures(){
        //初始化爷爷
        map.setCreature(grandfather,7,0);
        System.out.println("【爷爷】进入世界！");
        //初始化葫芦娃和喽啰
        for(int i = 0; i <7;i++) {
            map.setCreature(Huluwas[i],i+4,3);
            System.out.println("【"+Huluwas[i].getName()+"】" + "进入世界！");
            map.setCreature(bandits[i],i+4,11);
            System.out.println("【喽啰"+i+"】进入世界！");
        }
        //初始化蛇精
        map.setCreature(snake,7,14);
        System.out.println("【蛇精】进入世界！");
        //初始化蝎子精
        map.setCreature(scorpion,6,14);
        System.out.println("【蝎子精】进入世界！");
    }
    //绘制地图网格线
    public void printThefield() {
        this.showMap.showLines();
        for(int i = 0; i< N ;i++) {
            for(int j = 0; j < N;j++) {
                if (map.getCreature(i,j) != null)
                    map.showCreatures(i,j);
            }
        }
    }

    //战斗开始、结束及回放
    public void startBattle(){
        this.map.setStart();
        myScene = new MyScene(this.map,this.creatures,this.mycanvas,this,false,this.showMap,record);
        for (Creature creature:creatures){
            exec.execute(creature);
        }
        exec.execute(myScene);
    }
    public void shutDownCreatures(){
        for(Creature creature:creatures){
            Thread.interrupted();
        }
    }
    public void replyBattle(){
        myScene = new MyScene(this.map,this.creatures,this.mycanvas,this,true,this.showMap,record);
        exec.execute(myScene);
    }

    public void change_war_to_start() {
        this.map.setStart();
    }

    //【回放】获得初始化的地图
    public int[][] getInitBattledFiled(){
        return this.map.initFiled(this.creatures);
    }

    //set-get基本操作
    public Creature[] getCreatures(){
        return this.creatures;
    }
    public void setRecord(Record record){
        this.record = record;
    }
    public Leader getGrandPa(){
        return this.grandfather;
    }
    public Leader getSnake(){
        return this.snake;
    }


}
