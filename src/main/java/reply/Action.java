package reply;

public class Action {
    int actionType;                 //动作类型：移动-0，攻击-1，使用技能-2
    int[] start_pos;                //生物起始位置
    int[] end_pos;                  //生物终点位置

    public Action(int i, int[] start_pos, int[] end_pos){
        this.actionType = i;
        this.start_pos = start_pos;
        this.end_pos = end_pos;
    }
    public int getAttack_or_move()
    {
        return this.actionType;
    }
    public int getXStart(){
        return start_pos[0];
    }
    public int getYStart(){
        return start_pos[1];
    }
    public int getXEnd() {
        return end_pos[0];
    }
    public int getYEnd(){
        return end_pos[1];
    }
}
