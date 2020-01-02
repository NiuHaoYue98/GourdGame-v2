package creatures;

import map.*;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Leader extends Creature{
    Formation formation;                        //阵型信息
    int center_x;                               //队伍中心位置
    int center_y;                               //队伍中心位置
    int kidsNum = 7;                            //队伍成员数量
    Creature[] kids = new Creature[7];          //领导的队伍

    public Leader(Map map){
        super(map);
        this.ap = 10;
        this.dp = 4;
    }

    public void initKids(Creature[] kids){
        for(int i = 0;i < kidsNum;i++){
            this.kids[i] = kids[i];
        }
    }

    //加血技能
    public void usingSkill(Map map, Canvas canvas){
        if(this.isAlive == false) return;
        for(int i = 0; i < this.N; i++){
            for(int j = 0; j < this.N; j++){
                Creature temp = map.getCreature(i,j);
                if(temp != null){
                    //给己方加血
                    if(temp.getAlive() && temp.getType() == this.type) {
                        temp.addBlood(2);
                        //绘制使用技能
                        canvas.getGraphicsContext2D().drawImage(this.skillImage, j * this.size, i * this.size);
                    }
                }
            }
        }
    }
    //控制队伍变换阵型
    public void changeFormation(){
        System.out.println("领导队伍变换阵型！");
        Random rand = new Random();
        int index = rand.nextInt(8);
        this.formation = new Formation(index);
        for(int i=0;i<7;i++){
            //获取阵型，阵型中存入生物
            formation.setPosCreature(i,kids[i]);
            //将阵型放入地图
            int temp_x = center_x - formation.getPosY(i);
            int temp_y = center_y + formation.getPosX(i);
            map.moveCreature(kids[i],temp_x,temp_y);
        }
    }
}