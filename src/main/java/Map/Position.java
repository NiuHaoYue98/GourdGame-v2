package Map;

import Creatures.*;

public class Position{
    int pos_x;                  //位置坐标 x
    int pos_y;                  //位置坐标 y
    boolean flag = false;       //位置上是否有生物
    Creature creature;          //生物

    public Position(int x,int y){
        this.pos_x = x;
        this.pos_y = y;
        this.creature = null;
    }

    //判断当前位置是否为空
    public boolean isEmpty() {
        return creature == null;
    }

    //清除位置生物
    public void clearPos(){
        this.creature = null;
        this.flag = false;
    }

    public void getCre(Creature temp) {
        this.creature = temp;
        this.flag = true;
        temp.setPosition(pos_x,pos_y);
    }

    //战场移动
    public void movCre(Position temp) {
        //生物改变
        this.creature = temp.creature;
        this.creature.setPosition(this.pos_x,this.pos_y);
        this.flag = true;
        temp.creature = null;
        temp.flag = false;
    }

    //战场交换
    public void swapCre(Position temp) {
        Creature tempswap = temp.creature;
        temp.creature = this.creature;
        this.creature = tempswap;
        this.creature.setPosition(this.pos_x,this.pos_y);
        temp.creature.setPosition(temp.pos_x,temp.pos_y);
    }

    public void killPos() {
        if(this.creature!=null){
            if(this.creature.getAlive() == true){
                this.creature.killSelf();
            }
            this.creature = null;
        }
        this.flag = false;
    }

    //set-get基本操作
    public void setCreature(Creature creature) {
        if (this.creature != null) {
            System.err.printf("(%d, %d)已有生物,进入错误！", pos_x, pos_y);
            return;
        }
        this.flag = true;
        this.creature = creature;
    }
    public Creature getCreature() {
        return this.creature;
    }
}

