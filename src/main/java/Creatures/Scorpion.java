package Creatures;

import Map.*;
import Reply.Action;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

public class Scorpion extends Creature implements Fighting{
    public Scorpion(Map map) {
        super(map);
        this.name = "蝎子精";
        this.nature = false;
        this.ap = 6;
        this.dp = 2;
        this.image = new Image(getClass().getClassLoader().getResource("pic/scorpion.png").toString()
                , this.size, this.size, false, false);
        this.battleImage = new Image(this.getClass().getClassLoader().getResource(new String("pic/scorpionSkill.png")).toString(),
                20, 20, false, false);
        this.skillImage = new Image(this.getClass().getClassLoader().getResource(new String("pic/scorpionSkill.png")).toString(),
                20, 20, false, false);
    }

    @Override
    //技能：攻击地图上的所有敌人
    public void usingSkill(Map ground, Canvas canvas) {
        if (this.isAlive == false) return;
        for (int i = 0; i < this.N; i++) {
            for (int j = 0; j < this.N; j++) {
                Creature temp = ground.getCreature(i, j);
                if (temp != null) {
                    //攻击对方所有成员
//                    if(temp.getAlive() && temp.retNature() != this.nature) {
                    if (temp.getAlive() && temp.getNature() != this.nature) {
                        temp.lost_blood(2);
                        canvas.getGraphicsContext2D().drawImage(this.skillImage, j * this.size + 15, i * this.size + 15);
                    }
                }
            }
        }
    }
}
