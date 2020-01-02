package creatures;

import map.*;
import javafx.scene.image.Image;

import java.net.URL;

public class Gourd extends Creature{
    String[] name_list = {"红娃","橙娃","黄娃","绿娃","蓝娃","青娃","紫娃"};  //葫芦娃姓名列表
    int index;
    private URL url_of_image = null;    //图片
//葫芦娃序号

    //初始化
    public Gourd(Map map, int i){
        super(map);
        this.index = i;
        this.type = true;
        this.ap = i+1;
        this.dp = 8-i;
        this.url_of_image = this.getClass().getClassLoader().getResource(new String("pic/"+ (this.index +1) +".png"));
        this.image =  new Image(url_of_image.toString(),this.size,this.size,false,false);
        this.battleImage =  new Image(this.getClass().getClassLoader().getResource(new String("pic/bullet"+(i+1)+".png")).toString(),
                10,10,false,false);
        this.name = this.name_list[i];
    }
}


