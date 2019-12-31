package Creatures;

import Map.*;
import Reply.Action;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class GrandPa extends Leader {
    public GrandPa(Map map) {
        super(map);
        name = "爷爷";
        nature = true;
        this.image =  new Image(getClass().getClassLoader().getResource("pic/grandpa.png").toString()
                ,this.size,this.size,false,false);
        this.battleImage =  new Image(this.getClass().getClassLoader().getResource(new String("pic/grandpaSkill.png")).toString(),
                20,20,false,false);
        this.skillImage =  new Image(getClass().getClassLoader().getResource("pic/grandpaSkill.png").toString()
                ,20,20,false,false);
    }
}