package Creatures;

import Reply.Action;
import javafx.scene.image.Image;

import Map.*;

import java.util.ArrayList;
//
//public class Bandit extends Creatures {
//    //喽啰序号
//    int index;
//
//    //初始化
//    public Bandit(int i,String name,Map map){
//        super(name,map);
//        this.name = "喽啰";
//        index = i;
//    }
//
//    public void init(){
//        this.set_hp(30);        //小喽啰生命值为30
//        this.set_ap(4);         //小喽啰攻击值为4
//    }
//}


public class Bandit extends Creature {
    public Bandit(Map map) {
        super(map);
        this.nature = false;
        this.name = "喽啰";
        this.ap = 4;
        this.dp = 2;
        this.image =  new Image(getClass().getClassLoader().getResource("pic/bandit.png").toString()
                ,this.size,this.size,false,false);
        this.battleImage =  new Image(this.getClass().getClassLoader().getResource(new String("pic/banditAttact.png")).toString(),
                20,20,false,false);
    }
}
