import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Fruit {
    //x,y代表此水果會出現在哪一個位置
    private int x;
    private int y;
    private ImageIcon img; //特地把圖片尺寸設定成40x40,因為這樣才能和我們的遊戲視窗fit
    private ArrayList<ImageIcon> imgs = new ArrayList<>();
    private int index;

    public Fruit(){
        //原始寫法:
        //img = new ImageIcon("watermelon.png");

        //為了輸出此遊戲,所以移動了此watermelon.png檔案
        //getClass()等同於this.getClass();
        //img = new ImageIcon(getClass().getResource("apple.png"));
        imgs.add(new ImageIcon(getClass().getResource("apple.png")));
        imgs.add(new ImageIcon(getClass().getResource("banana.png")));
        imgs.add(new ImageIcon(getClass().getResource("grapes.png")));
        imgs.add(new ImageIcon(getClass().getResource("guava.png")));
        imgs.add(new ImageIcon(getClass().getResource("peach.png")));
        imgs.add(new ImageIcon(getClass().getResource("pineapple.png")));
        imgs.add(new ImageIcon(getClass().getResource("watermelon.png")));

        index = (int) (Math.random() * imgs.size()); //隨機產生圖片的index

        //任意地在這整個視窗中,選一個位置,使水果出現在那裡
        //Math.random():0~0.99999....(不包含1)1
        //Math.floor(Math.random() * Main.column)會得到一個0～“column-1”的數字
        //我:用Math.floor()的原因在於不要超出邊界!因為畫圖是先定位(x,y),接著往右邊和下面畫
        this.x = (int)(Math.floor(Math.random() * Main.column) * Main.CELL_SIZE);
        this.y = (int)(Math.floor(Math.random() * Main.row) * Main.CELL_SIZE);
    }

    //因為x,y都是private,所以做出getter
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void drawFruit(Graphics g){
        //有用圖片的水果
        //img.paintIcon(null, g, this.x, this. y);

        if(Main.hasEatenFruit) { //代表吃到水果
            index = (int) (Math.random() * imgs.size());
        }

        imgs.get(index).paintIcon(null, g, this.x, this.y);
    }

    //這邊放Snake s參數的用意在於,在幫fruit選一個新位置時,要避開目前這隻蛇的身體所佔的位置
    //也就是說整個畫面當中,除了這隻蛇的身體所佔的位置外,其他位置都可以當作此fruit的新位置
    public void setNewLocation(Snake s){
        int new_x;
        int new_y;
        boolean overlapping; //目前所選的水果新位置有沒有和蛇的身體有重疊

        do{
            new_x = (int)(Math.floor(Math.random() * Main.column) * Main.CELL_SIZE);
            new_y = (int)(Math.floor(Math.random() * Main.row) * Main.CELL_SIZE);
            overlapping = check_overlap(new_x, new_y, s);
        }while(overlapping);

        this.x = new_x;
        this.y = new_y;
    }

    public boolean check_overlap(int x, int y, Snake s){
        ArrayList<Node> snake_body = s.getSnakeBody();

        for(int j = 0; j < snake_body.size(); j++){
            if(x == snake_body.get(j).x
                    && y == snake_body.get(j).y){
                return true; //代表找到了overlap
            }
        }

        return false;
    }
}
