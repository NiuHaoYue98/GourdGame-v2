package Creatures;

import Map.*;
import Reply.Action;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Leader extends Creature implements Fighting{
    public Leader(Map map){
        super(map);
        this.ap = 10;
        this.dp = 4;
    }

    //加血技能
    public void usingSkill(Map map, Canvas canvas){
        if(this.isAlive == false) return;
        for(int i = 0; i < this.N; i++){
            for(int j = 0; j < this.N; j++){
                Creature temp = map.getCreature(i,j);
                if(temp != null){
                    //给己方加血
                    if(temp.getAlive() && temp.retNature() == this.nature) {
                        temp.addBlood(2);
                        canvas.getGraphicsContext2D().drawImage(this.skillImage, j * this.size, i * this.size);
                    }
                }
            }
        }
    }
}