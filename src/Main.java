import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;


public class Main extends JPanel implements KeyListener {
    public static final int CELL_SIZE = 40; //此視窗中的每一格格子的大小
    public static int width = 800;
    public static int height = 800;
    public static int row = height / CELL_SIZE; //總共有幾列
    public static int column = width / CELL_SIZE; //總共有幾行
    private Snake snake;
    private Fruit fruit;
    private Timer t;
    private int speed = 100; //每0.1秒執行一次
    private static String direction; //蛇要跑的方向(因為等等會用到它,所以設定成static)
    private int score;
    private int highest_score;
    public static Boolean hasEatenFruit = false;

    //我測試用
    private ImageIcon img;

    //要把filename.txt放在執行此遊戲的電腦的某一個地方,可以是OS裡的文件資料夾,或是藏在OS中某一個很深的地方等等...,
    //這裡老師為了簡單,就把filename.txt放在user的桌面
    //System.getProperty("user.home"):可以取得不同OS的根目錄
    String desktop = System.getProperty("user.home") + "/Desktop/";
    String myFile = desktop + "filename.txt";

    //為了防止亂按而導致蛇突然咬到自己的狀況。
    //雖然看起來和沒做之前沒什麼差別,但等等做其他功能時,
    //會發現這個allowKeyPress可以避免這整個遊戲出現bugs!!!)－Controlling the snake(單元89)
    private boolean allowKeyPress; //按完一次keyPress後,要等畫面重畫完後,再次按keyPress才會有效果

    public static String getDirection() {
        return direction;
    }

    public Main(){
        //寫法一:原始寫法
        /*
        snake = new Snake(); //做出Snake物件
        fruit = new Fruit();

        //後來把這段Timer相關的code移到setTime()了,為了簡化程式碼
        t = new Timer();

        //在每一個固定時間裡(time interval),讓這個Timer去執行某件事情
        t.scheduleAtFixedRate(new TimerTask() { //我:這種寫法是匿名類別(Anonymous Class)
            @Override
            public void run(){
                repaint(); //會去執行下面的paintComponent() method
            }
        }, 0, speed); //0:執行此TimerTask不會有delay //speed:每隔多久要執行一次的時間(毫秒)

        direction = "Right"; //預設這條蛇會不斷地往右邊走

        addKeyListener(this);

        allowKeyPress = true; //遊戲一開始是可以使用EventListener
        */

        //寫法二:由於寫法一的code和reset()的code重複的東西太多了,所以直接寫reset()就可以了!
        //再加上沒有重複的code
        img = new ImageIcon(getClass().getResource("background.jpg")); //我測試用
        read_highest_score();
        reset();
        addKeyListener(this);
    }

    private void setTimer(){
        t = new Timer();

        //在每一個固定時間裡(time interval),讓這個Timer去執行某件事情
        t.scheduleAtFixedRate(new TimerTask() { //我:這種寫法是匿名類別(Anonymous Class)
            @Override
            public void run(){
                repaint(); //會去執行下面的paintComponent() method
            }
        }, 0, speed); //0:執行此TimerTask不會有delay //speed:每隔多久要執行一次的時間(毫秒)
    }

    private void reset(){ //整個遊戲重來一遍,也就是把所有東西都規零
        score = 0;

        if(snake != null){
            snake.getSnakeBody().clear(); //把這隻蛇的所有部分清空
        }

        //因為剛剛蛇咬到身體而遊戲結束時,我們設定allowKeyPress為false,所以這裡要重新設定回true
        allowKeyPress = true;
        direction = "Right"; //設定回遊戲的初始值"Right"
        snake = new Snake(); //重新作一隻蛇(它會執行Snake的constructor,因此又會得到一隻新的蛇)
        fruit = new Fruit();
        setTimer(); //重新作一個Timer
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(width,height); //視窗大小
    }

    @Override
    public void paintComponent(Graphics g){ //把這條蛇畫出來
        //System.out.println("We are calling paintComponent......"); //測試看看TimerTask有無正常運作

        //check if the snake bites itself
        //把蛇畫出來之前去確認有沒有咬到自己的尾巴
        ArrayList<Node> snake_body = snake.getSnakeBody();
        Node head = snake_body.getFirst();

        for(int i = 1; i < snake_body.size(); i++){ //index[0]是頭部的位置
            if((snake_body.get(i).x == head.x &&
                    snake_body.get(i).y == head.y) || //代表這隻蛇咬到它自己
                    head.x == width || head.x < 0 || //代表碰到四周牆壁會死掉
                    head.y == height || head.y < 0){
                allowKeyPress = false; //不可以再按KeyPress了,因為遊戲要結束了

                //讓timer停止
                t.cancel();
                t.purge();

                //讓使用者知道遊戲結束了
                //score得分可以寫在任何自己喜歡的地方,這邊老師就示範寫在遊戲結束後!
                int response = JOptionPane.showOptionDialog(
                        this,
                        "Game Over!!! Your score is " + score + ". The highest score was " +
                                highest_score + ". Would you like to start over?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        null,
                        JOptionPane.YES_OPTION); //讓使用者選擇繼續玩或是停止遊戲

                write_a_file(score);

                switch (response){
                    case JOptionPane.CLOSED_OPTION: //如果關閉此Game Over視窗(也就是showOptionDialog)的話
                        System.exit(0); //結束此遊戲

                        break;
                    case JOptionPane.NO_OPTION:
                        System.exit(0); //結束此遊戲

                        break;
                    case JOptionPane.YES_OPTION:
                        reset();

                        return; //這行code以下的所有程式碼都不會被執行!!!
                }
            }
        }

        //draw a black background
        img.paintIcon(null,g,0,0); //我測試用
        //g.fillRect(0, 0, width, height); //因為這邊無法resize視窗,所以寫getWidth()和getHeight()也可以

        //作一個透明黑色的遮罩-->只是美工而已@@
        g.setColor(new Color(0f, 0f, 0f, .3f)); //我:.3f等於0.3f
        g.fillRect(0, 0, width, height);

        fruit.drawFruit(g);
        snake.drawSnake(g); //會把fruit蓋掉,如果吃到水果的話

        //remove snake tail and put it in head(把尾端切下來,然後放到頭端)
        //我:只針對頭部作運算
        int snakeX = snake.getSnakeBody().getFirst().x; //index[0]是頭部的Node
        int snakeY = snake.getSnakeBody().getFirst().y; //index[0]是頭部的Node

        if(direction.equals("Left")){ //往左走:x-=CELL_SIZE; y不變
            snakeX -= CELL_SIZE;
        }else if(direction.equals("Up")){ //往上走:y-=CELL_SIZE; x不變
            snakeY -= CELL_SIZE;
        }else if(direction.equals("Right")){ //往右走:x+=CELL_SIZE; y不變
            snakeX += CELL_SIZE;
        } else if(direction.equals("Down")){ //往下走:y+=CELL_SIZE; x不變
            snakeY += CELL_SIZE;
        }

        Node newHead = new Node(snakeX, snakeY); //新的頭部位置

        //check if the snake eats the fruit
        //如果蛇有吃到水果的話,它的身體就要拉長,因此我們就不要remove它的最後一個值
        if(snake.getSnakeBody().getFirst().x == fruit.getX() && //代表目前已經吃到此fruit了,所以不切除尾巴
                snake.getSnakeBody().getFirst().y == fruit.getY()){
            //System.out.println("Eating the fruit!!!!!"); //測試是否有吃到fruit的邏輯正不正確

            hasEatenFruit = true; //代表吃到水果(我要更改水果圖片)

            //1.get fruit to a new location
            fruit.setNewLocation(snake);

            //2.drawFruit(重新畫水果)
            fruit.drawFruit(g);

            //3.score++(計算得一分)
            score++; //吃到水果,所以加1分

        }else{ //代表沒有吃到水果,所以要把尾端切除
            snake.getSnakeBody().removeLast(); //切除尾巴
        }

        hasEatenFruit = false;

        snake.getSnakeBody().addFirst(newHead);

        allowKeyPress = true; //代表重新畫完了,可以讓user再次按keyPress了!
        requestFocusInWindow();

    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Snake Game");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new Main());
        window.pack(); //我:自動調整視窗大小以適應內容
        window.setLocationRelativeTo(null); //我:讓視窗顯示在螢幕中央
        window.setVisible(true);
        window.setResizable(false); //視窗打開後,user是不可以調整視窗大小的(resize)-->左上角也無法按全螢幕的按鈕
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) { //37:左; 38:上; 39:右; 40:下
        if(allowKeyPress){
            //注意:要往左走的話,先決條件是目前這隻蛇不是往右跑(因為如果目前是往右跑,它就只能往右或下或上而已)
            if(e.getKeyCode() == 37 && !direction.equals("Right")){ //!direction.equals("Right"):不是往右跑
                direction = "Left";
            }else if(e.getKeyCode() == 38 && !direction.equals("Down")){
                direction = "Up";
            }else if(e.getKeyCode() == 39 && !direction.equals("Left")){
                direction = "Right";
            }else if(e.getKeyCode() == 40 && !direction.equals("Up")){
                direction = "Down";
            }

            //按完鍵之後,改為false,也就是說在下次被改成true之前,都無法做keypress
            allowKeyPress = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void read_highest_score(){
        try{
            //如果有此檔案,就可以去讀它
            //File myObj = new File("filename.txt");

            //輸出此遊戲的寫法:
            File myObj = new File(myFile);

            Scanner myReader = new Scanner(myObj); //讀取此檔案裡面的內容
            highest_score = myReader.nextInt();
            myReader.close();
        }catch (FileNotFoundException e){
            highest_score = 0; //代表之前沒有玩過此遊戲(也就是沒有此檔案)

            try{ //在user電腦裡面新增一個filename.txt的檔案
                //只是建立一個File物件,檔案可能不存在
                //File myObj = new File("filename.txt");

                //輸出此遊戲的寫法:
                File myObj = new File(myFile);

                if(myObj.createNewFile()){ //如果此檔案有成功被建立的話...
                    System.out.println("File created: " + myObj.getName());
                }

                FileWriter myWriter = new FileWriter(myObj.getName());
                myWriter.write("" + 0); //寫一個0的String
                myWriter.close(); //老師沒寫
            }catch (IOException err){
                System.out.println("An error occurred!");
                err.printStackTrace(); //印出問題出在哪裡
            }
        }
    }

    public void write_a_file(int score){ //參數score是目前此局遊戲的分數
        try{
            //開啟file name.txt檔案,並準備寫入資料!
            //當你使用FileWriter開啟一個檔案時,預設行為是「清空檔案內容」,即使該檔案已經存在,
            //原有的資料也會被清除,然後新的內容會被寫入檔案
            //FileWriter myWriter = new FileWriter("filename.txt");

            //輸出此遊戲的寫法:
            FileWriter myWriter = new FileWriter(myFile);

            if(score > highest_score){
                myWriter.write("" + score);
                highest_score = score;
            }else{
                myWriter.write("" + highest_score);
            }

            myWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
