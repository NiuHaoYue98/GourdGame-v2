package map;

import creatures.*;
public class Formation {
    static String[] formation_name_list = {"鹤翼","雁行","衡轭","长蛇","鱼鳞","方円","偃月","锋矢"};
    Position[][] pos_list = new Position[][]{
            {new Position(0,0),new Position(-1,1),new Position(1,1),new Position(-2,2),new Position(2,2),new Position(-3,3),new Position(3,3)},
            {new Position(0,0),new Position(-1,-1),new Position(1,1),new Position(-2,-2),new Position(2,2),new Position(-3,-3),new Position(3,3)},
            {new Position(0,0),new Position(0,1),new Position(0,-1),new Position(1,1),new Position(1,-1),new Position(1,2),new Position(1,-2)},
            {new Position(0,0),new Position(0,1),new Position(0,-1),new Position(0,2),new Position(0,-2),new Position(0,3),new Position(0,-3)},
            {new Position(0,0),new Position(0,2),new Position(0,-1),new Position(-2,0),new Position(2,0),new Position(-1,1),new Position(1,1)},
            {new Position(0,0),new Position(-1,-1),new Position(1,-1),new Position(-2,-2),new Position(2,-2),new Position(-1,-3),new Position(1,-3)},
            {new Position(0,0),new Position(1,0),new Position(1,1),new Position(1,-1),new Position(2,0),new Position(2,2),new Position(2,-2)},
            {new Position(0,0),new Position(-1,1),new Position(1,1),new Position(0,-1),new Position(0,-2),new Position(0,1),new Position(0,2)}
    };
    String name;                            //阵型名称
    Position[] pos;     //阵型中的生物位置坐标

    public Formation(int id){
        this.name = formation_name_list[id];
        this.pos = pos_list[id];
    }

    //set-get基本操作
    public int getPosX(int i){
        return this.pos[i].pos_x;
    }
    public int getPosY(int i){
        return this.pos[i].pos_y;
    }
    public void setPosCreature(int i,Creature creature){
        this.pos[i].creature = creature;
    }
}