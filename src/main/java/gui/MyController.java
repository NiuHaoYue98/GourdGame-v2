package gui;

import map.ShowMap;
import map.World;
import reply.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyController implements Initializable {

    @FXML
    private Canvas mycanvas;
    @FXML
    private MenuItem regulations;
    @FXML
    private MenuItem about;

    private World world;                //世界
    private FileReader read;
    //显示相关
    private ShowMap showMap;
    //记录相关
    private ArrayList<Action> actions;  //动作列表
    private Record record;
    //控制相关
    private boolean is_reviewing;       //控制：是否是回放状态
    private boolean start;              //控制：是否战斗开始


    public void initialize(URL location, ResourceBundle resources) {
        this.record = null;

        this.is_reviewing = false;
        this.start = false;
        //以下全是界面初始化的内容：初始化葫芦娃阵型、敌方阵型
        this.showMap = new ShowMap(this.mycanvas);
        this.actions = new ArrayList<Action>(); //清空操作
        world = new World(this.mycanvas, this.actions, this.showMap,this.record); //开始新的战斗
        world.initCreatures();
        world.printThefield();

        regulations.setOnAction(event -> {
            System.out.println("游戏规则说明");
            this.showRegulations();
        });
        about.setOnAction(event -> {
            System.out.println("关于游戏");
            this.showAbout();
        });
    }

    //改变阵型
    public void set_queue_Calash(ActionEvent event) {
        if(this.start){
            this.showAlert("正在进行战斗，无法给葫芦娃重新列阵！");
            return;
        }
        if (this.is_reviewing) {
            this.showAlert("正在进行战斗回放，无法给葫芦娃重新列阵！");
            return;
        }
        world.getGrandPa().changeFormation();
        this.showMap.showLines();
        world.printThefield();

    }
    public void set_queue_monster(ActionEvent event) {
        if(this.start){
            this.showAlert("正在进行战斗，无法给妖精重新列阵！");
            return;
        }
        if (this.is_reviewing) {
            this.showAlert("正在进行战斗回放，无法给妖精重新列阵！");
            return;
        }
        world.getSnake().changeFormation();
        this.showMap.showLines();
        world.printThefield();
    }
    //重新开始
    public void reStart(ActionEvent event){
        if(!start && !is_reviewing){
            this.showAlert("当前没有进行中的任务，无需重新开始！");
            return;
        }

        this.record = null;
        this.start = false;
        this.is_reviewing = false;
        //以下全是界面初始化的内容：初始化葫芦娃阵型、敌方阵型
        this.showMap = new ShowMap(this.mycanvas);
        this.actions = new ArrayList<Action>(); //清空操作
        world = new World(this.mycanvas, this.actions, this.showMap,this.record); //开始新的战斗
        world.initCreatures();
        world.printThefield();
    }
    //误操作提示框
    public void showAlert(String text){
        Stage dialog = new Stage();
        DialogPane dp = new DialogPane();
        BorderPane bp = new BorderPane();
        Label label = new Label();
        label.setText(text);
        bp.setCenter(label);
        dp.setContent(bp);
        dialog.setScene(new javafx.scene.Scene(dp));
        dialog.setTitle("HuluGame");
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();

    }

    //开始战斗
    public void startfight(ActionEvent event) throws Exception {
        if (this.is_reviewing == true) {
            this.showAlert("正在进行战斗回放，无法重新开始！");
            return;
        }
        if(this.start){
            this.showAlert("正在进行战斗，无法立即开始，若想重新开始请点击重新开始按钮！");
            return;
        }
        this.start = true;

        //先保存战场初始化
        System.out.println("战斗开始！");
        //初始化记录信息
        this.record = new Record(this.world.getInitBattledFiled(), this.actions);
        this.record.recordInit();
        this.world.setRecord(this.record);
        //开始执行
        this.world.startBattle();
    }
    //战斗回放
    public void reviewAction() throws IOException {
        if(this.start){
            this.showAlert("正在进行战斗，无法立即进行回放！若想立即回放请点击重新开始！");
            return;
        }
        if(this.is_reviewing){
            this.showAlert("正在进行战斗回放，无法立即回放！若想立即回放请点击重新开始！");
        }
        this.is_reviewing = true;
        System.out.println("开始回放！");
        FileChooser fileChooser = new FileChooser();
        Stage mainStage = null;
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        try {
            this.read = new FileReader(selectedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //这里生成了read，获得读取的文件
        this.record = new Record(this.world.getInitBattledFiled(), this.actions);
        this.record.setRead(this.read);
        this.world = new World(this.mycanvas, this.actions, this.showMap,this.record);
        this.world.change_war_to_start();
        this.world.replyBattle();
    }

    //帮助
    private void showRegulations() {
        Stage dialog = new Stage();
        DialogPane dp = new DialogPane();
        BorderPane bp = new BorderPane();
        Label label = new Label();
        label.setText("游戏说明\n"
                + "游戏开始后双方成员在地图上随机移动。\n"
                + "当生物发现有对方成员在自己周围9宫格内时，立即锁定对方成员并完成一次攻击。\n"
                + "在攻击的过程中爷爷、蛇精、蝎子精以一定概率释放自身的特殊技能。\n"
                + "生物死亡后会留下实体，占据地图中的一个位置，此后其他生物无法再进入该位置。\n"
                + "当某一方生物全部死亡后游戏结束。\n"
                + "-by NHY");
        bp.setCenter(label);
        dp.setContent(bp);
        dialog.setScene(new javafx.scene.Scene(dp));
        dialog.setTitle("HuluGame");
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }
    private void showAbout() {
        Stage dialog = new Stage();
        DialogPane dp = new DialogPane();
        BorderPane bp = new BorderPane();
        Label label = new Label();
        label.setText("关于葫芦世界\n"
                + "开发语言：java\n"
                + "图形界面：JavaFX \n"
                + "-by NHY");
        bp.setCenter(label);
        dp.setContent(bp);
        dialog.setScene(new javafx.scene.Scene(dp));
        dialog.setTitle("HuluGame");
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }
}


