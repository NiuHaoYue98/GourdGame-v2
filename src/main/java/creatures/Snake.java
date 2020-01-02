package creatures;

import map.*;
import javafx.scene.image.Image;

public class Snake extends Leader {
    public Snake(Map map) {
        super(map);
        this.name = "蛇精";
        this.type = false;
        center_x = 7;
        center_y = 11;
        this.image =  new Image(getClass().getClassLoader().getResource("pic/snake.png").toString()
                ,this.size,this.size,false,false);
        this.battleImage =  new Image(this.getClass().getClassLoader().getResource(new String("pic/snakeSkill.png")).toString(),
                20,20,false,false);
        this.skillImage =  new Image(getClass().getClassLoader().getResource("pic/snakeSkill.png").toString()
                ,20,20,false,false);
    }
}