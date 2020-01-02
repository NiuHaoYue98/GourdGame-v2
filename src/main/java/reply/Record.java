package reply;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Record {
    //基本参数设置
    private final static int N = 15;                //地图大小

    //记录的信息
    int[][] initMap = new int[N][N];             //初始的地图
    int num_of_flash = 0;                       //战斗轮数
    ArrayList<Action> actions;                  //战斗动作序列

    private Writer write;
    private FileReader read;

    public Record(int[][] init,ArrayList<Action> actions){
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                initMap[i][j] = init[i][j];
            }
        }
        this.actions = actions;
        this.read = null;
    }

    //生成记录文件
    public void genFile(){
        Date date = new Date();//获取当前的日期
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String filename = df.format(date) + ".txt";
        System.out.println(filename);
        File saveRes = new File(filename);
        if (!saveRes.exists()) {
            try {
                saveRes.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.write = new FileWriter(saveRes,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //记录战斗开始时的地图，有双方的布阵信息
    public void recordInit(){
        this.genFile();
        for(int i = 0; i < N; i++) {
            String line = "";
            for(int j = 0; j < N; j++){
                line = line + Integer.toString(initMap[i][j]) + " ";
            }
            try {
                this.write.write(line);
                this.write.write("\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //记录战斗过程
    public void recordActions() {
        try {
            this.write.write(Integer.toString(num_of_flash)+"\r\n");
            num_of_flash++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < this.actions.size(); i++) {
            int type_of_acction = this.actions.get(i).getAttack_or_move();
            int x_start = this.actions.get(i).getXStart();
            int y_start = this.actions.get(i).getYStart();
            int x_end = this.actions.get(i).getXEnd();
            int y_end = this.actions.get(i).getYEnd();
            String temp = type_of_acction + " " + x_start + " " + y_start + " " + x_end + " " + y_end;
            try {
                this.write.write(temp);
                this.write.write("\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        actions.clear(); //每一帧都清空一次
    }

    //结束记录，关闭文件
    public void endRecord(boolean winner){
        try {
            if(winner == true)
                this.write.write("calash win");
            else
                this.write.write("monster win");
            this.write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取文件列表
    public ArrayList<String> readRecordList(){
        BufferedReader bf = new BufferedReader(this.read);
        ArrayList<String> recordList = new ArrayList<String>();
        recordList.clear();
        while (true) {
            String temp = "";
            try {
                if((temp = bf.readLine()) == null)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            recordList.add(temp);
        }
        return recordList;
    }

    //设置读取文件路径
    public void setRead(FileReader read){
        this.read = read;
    }
}
