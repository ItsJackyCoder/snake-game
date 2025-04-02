import java.awt.*;
import java.util.ArrayList;

public class Snake {
    //我:建立ArrayList存放Node物件(我們自己創的Node Class)
    private ArrayList<Node> snakeBody; //儲存蛇的身體,然後每一個身體的部分就叫做一個Node

    public Snake(){
        snakeBody = new ArrayList<>();

        //設定蛇的身體長度為4
        snakeBody.add(new Node(160, 0)); //第一個身體的位置(頭)
        snakeBody.add(new Node(120, 0)); //第二個身體的位置
        snakeBody.add(new Node(80, 0)); //第三個身體的位置
        snakeBody.add(new Node(40, 0)); //第四個身體的位置(尾巴)
    }

    public ArrayList<Node> getSnakeBody() {
        return snakeBody;
    }

    public void drawSnake(Graphics g){
        for(int i = 0; i < snakeBody.size(); i++){
            if(i == 0){ //index[0]是頭部
                g.setColor(Color.GREEN); //頭部顏色
            }else{
                g.setColor(new Color(29, 123, 29)); //身體顏色
            }

            Node n = snakeBody.get(i);

            if(i == 0) { //頭部
                if(Main.getDirection().equals("Left")) {
                    g.fillArc(n.x, n.y, Main.CELL_SIZE, Main.CELL_SIZE, 210, 300);

                    g.setColor(Color.BLACK);
                    g.fillOval(n.x + 15, n.y + 5, 10, 10);
                }else if(Main.getDirection().equals("Up")){
                    g.fillArc(n.x, n.y, Main.CELL_SIZE, Main.CELL_SIZE, 120, 300);

                    g.setColor(Color.BLACK);
                    g.fillOval(n.x + 25, n.y + 15, 10, 10);
                }else if(Main.getDirection().equals("Down")){
                    g.fillArc(n.x, n.y, Main.CELL_SIZE, Main.CELL_SIZE, 300, 300);

                    g.setColor(Color.BLACK);
                    g.fillOval(n.x + 25, n.y + 15, 10, 10);
                }else{ //右邊方向
                    g.fillArc(n.x, n.y, Main.CELL_SIZE, Main.CELL_SIZE, 30, 300);

                    //畫眼睛
                    g.setColor(Color.BLACK);
                    g.fillOval(n.x + 15, n.y + 5, 10, 10);
                }
            }else{ //身體部分
                g.fillOval(n.x, n.y, Main.CELL_SIZE, Main.CELL_SIZE);

                g.setColor(new Color(132, 231, 132));
                g.fillOval(n.x + 10, n.y + 10, 20, 20);
            }
        }
    }
}
