import org.junit.Test;

public class AddBlood {
    @Test
    public void addBlood() {
        int blood = 2;
        int hp = 10;
        int[] tempHpList = {0,1,2,3,4,5,6,7,8,9,10};
        for(int i = 0;i < tempHpList.length;i++) {
            tempHpList[i] += blood;
            if (tempHpList[i] > hp) {
                tempHpList[i] = hp;
            }
        }
        for(Integer i:tempHpList)
            System.out.print(i+" ");
    }
}
