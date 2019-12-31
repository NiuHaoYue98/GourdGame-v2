package Creatures;

import Map.*;
import Reply.Action;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Snake extends Leader {
    public Snake(Map map) {
        super(map);
        this.name = "蛇精";
        this.nature = false;
        this.image =  new Image(getClass().getClassLoader().getResource("pic/snake.png").toString()
                ,this.size,this.size,false,false);
        this.battleImage =  new Image(this.getClass().getClassLoader().getResource(new String("pic/snakeSkill.png")).toString(),
                20,20,false,false);
        this.skillImage =  new Image(getClass().getClassLoader().getResource("pic/snakeSkill.png").toString()
                ,20,20,false,false);
    }
}