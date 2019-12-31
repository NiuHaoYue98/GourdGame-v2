package Map;

import Creatures.*;
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
    final static int num = 7;               //阵型中的生物数

    String name;                            //阵型名称
    Position[] pos = new Position[num];     //阵型中的生物位置坐标

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

    //变换阵型
    public void changeFormation_Hulu(int index, Gourd[] Huluwa, Map ground) {
        //index是阵型编号，Huluwa是葫芦娃列表，ground是地图

        //新建两个变量，center_x和center_y，用于确定阵型的中心位置
        int center_x = 7;
        int center_y = 3;
        Formation formation = new Formation(index);
        for(int i=0;i<7;i++){
            //获取阵型，阵型中存入生物
            formation.setPosCreature(i,Huluwa[i]);
            //将阵型放入地图
            int temp_x = center_x - formation.getPosY(i);
            int temp_y = center_y + formation.getPosX(i);
            ground.moveCreature(Huluwa[i],temp_x,temp_y);
        }
    }
    public void changeFormation_mon(int index, Scorpion scorpion, Bandit[] bandit, Map ground) {
        int center_x = 7;
        int center_y = 11;
        Formation formation = new Formation(index);

        formation.setPosCreature(0,scorpion);
        ground.moveCreature(scorpion,6,14);

        //喽啰为其余位置
        for(int i=0;i<7;i++){
            //获取阵型，阵型中存入生物
            formation.setPosCreature(i, bandit[i]);
            //将阵型放入地图
            int temp_x = center_x - formation.getPosY(i);
            int temp_y = center_y + formation.getPosX(i);
            ground.moveCreature(bandit[i],temp_x,temp_y);
        }
    }
}