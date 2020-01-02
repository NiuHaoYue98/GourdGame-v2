package map;

import creatures.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class ShowMap {
    //设置信息
    final static int N = 15;                        //地图大小
    final static int size = 50;                     //格子大小

    //画布信息
    private Canvas canvas;

    public ShowMap(Canvas mycanvas){
        this.canvas = mycanvas;
    }

    //显示地图中的网格线
    public void showLines() {
        canvas.getGraphicsContext2D().clearRect(0, 0, this.size*this.N, this.size*this.N); //清空画布
        canvas.getGraphicsContext2D().clearRect(0, 0, this.size * N, this.size * N); //清空画布
        //画格子
        canvas.getGraphicsContext2D().setStroke(Color.WHITE); //白线
        for (int i = 0; i <= N; i++) {
            canvas.getGraphicsContext2D().strokeLine(0, i * this.size, this.size * this.N, i * this.size);
        }
        for (int j = 0; j <= N; j++) {
            canvas.getGraphicsContext2D().strokeLine(j * this.size, 0, j * this.size, this.size * this.N);
        }
    }
    //显示单个生物
    public void showCreature(Creature creature){
        int i = creature.getI();
        int j = creature.getJ();
        //需要在之前就把该判断的判断好，这里只需要绘制
        if(creature.getAlive() == true) {
            canvas.getGraphicsContext2D().drawImage(creature.getImage(), j * this.size, i * this.size);
            //显示血量条
            double rate = (double) creature.getTempHp() / creature.getHp(); //进行初始化
            canvas.getGraphicsContext2D().setFill(Color.GREENYELLOW);
            canvas.getGraphicsContext2D().fillRect(j * this.size, i * this.size, this.size * rate, 5);
            canvas.getGraphicsContext2D().setFill(Color.RED);
            canvas.getGraphicsContext2D().fillRect(j * this.size + this.size * rate, i * this.size, this.size * (1 - rate), 5);
        }
        else {
            canvas.getGraphicsContext2D().drawImage(creature.getDeadImage(), j * this.size, i * this.size);
        }
    }

    //显示生物攻击
    public void showAattack(int t_x ,int t_y,Creature creature) {
        int cur_i = creature.getI();
        int cur_j = creature.getJ();
        //考虑四个方向
        synchronized (canvas) {
            //向上
            System.out.println("at creature.draw_attack 开始绘制画布");
            if (cur_j == t_y && cur_i < t_x) {
                for (int ti = cur_i * this.size + 25; ti < t_x * this.size + 25; ti += 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), cur_j * this.size + 25, ti);
                }
            }
            else if (cur_j == t_y && cur_i > t_x) {
                for (int ti = cur_i * this.size + 25; ti > t_x * this.size + 25; ti -= 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), cur_j * this.size + 25, ti);
                }
            }
            else if (cur_i == t_x && cur_j < t_y) {
                for (int tj = cur_j * this.size + 25; tj < t_y * this.size + 25; tj += 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), tj, cur_i * this.size + 25);
                }
            }
            else if (cur_i == t_x && cur_j > t_y) {
                for (int tj = cur_j * this.size + 25; tj > t_y * this.size + 25; tj -= 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), tj, cur_i * this.size + 25);
                }
            }
            //左上
            else if (cur_i > t_x && cur_j < t_y) {
                for (int ti = cur_i * this.size + 25, tj = cur_j * this.size + 25; ti > t_x * this.size + 25 && tj < t_y * this.size + 25; ti -= 20, tj += 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), tj, ti);
                }
            }
            //左下
            else if (cur_i > t_x && cur_j > t_y) {
                for (int ti = cur_i * this.size + 25, tj = cur_j * this.size + 25; ti > t_x * this.size + 25 && tj > t_y * this.size + 25; ti -= 20, tj -= 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), tj, ti);
                }
            }
            //右上
            else if (cur_i < t_x && cur_j < t_y) {
                for (int ti = cur_i * this.size + 25, tj = cur_j * this.size + 25; ti < t_x * this.size + 25 && tj < t_y * this.size + 25; ti += 20, tj += 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), tj, ti);
                }
            }
            //右下
            else if (cur_i < t_x && cur_j > t_y) {
                for (int ti = cur_i * this.size + 25, tj = cur_j * this.size + 25; ti < t_x * this.size + 25 && tj > t_y * this.size + 25; ti += 20, tj -= 20) {
                    canvas.getGraphicsContext2D().drawImage(creature.getBattleImage(), tj, ti);
                }
            }
            System.out.println("攻击绘制完成！");
        }
    }

    //set-get
    public Canvas getCanvas(){
        return this.canvas;
    }
}

