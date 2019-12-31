import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.junit.Test;



public class ShowMapTest {
    //设置信息
    final static int N = 15;                        //地图大小
    final static int size = 50;                     //格子大小

    //画布信息
    private Canvas canvas = new Canvas();//显示地图中的网格线
    @Test
    public void showLinesTest() {
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
    //set-get
    @Test
    public Canvas getCanvasTest(){
            return this.canvas;
        }
}
