import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

public class Bman extends JPanel{
  protected static int[][] well;//a reference of type for each unit: 0 for breakable boxes, 1 for pathway for movement (black),
  // 2 for set obstacle (gray), 3 for bomb P1, 4 for bomb P2, 5 p1 explosion, 6 is p2 explosion,
  protected static int units = 13;//each square is a units
  protected static int unitSize = 50;//size of each square
  protected static ArrayList<Integer> boxes = new ArrayList<Integer>(); //breakable boxes, each have two indexes (x and y positions)
  public static BmanPlayers playerOne = new BmanPlayers();
  public static BmanPlayers playerTwo = new BmanPlayers();
  public static double boxprob = 0.4;    //CHANGED
  public double random;     //CHANGED

  public static void main(String[] args){
    JFrame test = new JFrame("BomberMan!");//title in window bar
    test.setSize(units*unitSize + 10, units*unitSize + 10);//size of whole frame, which is # of squares times its size
    test.setVisible(true);
    Bman game = new Bman();
    test.add(game);
    BmanPlayers.setPos(playerOne, units - 2, units - 2);
    BmanPlayers.setPos(playerTwo, 1, 1);
    game.init();

    //KEYLISTENER
    test.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
        int p1x = BmanPlayers.getxPos(playerOne);
        int p1y = BmanPlayers.getyPos(playerOne);
        int p2x = BmanPlayers.getxPos(playerTwo);
        int p2y = BmanPlayers.getyPos(playerTwo);
        if(e.getKeyCode() == KeyEvent.VK_UP && (well[p1x][p1y-1] == 1 || well[p1x][p1y-1] > 4)){
          if(well[p1x][p1y-1] > 4){
            BmanPlayers.loseLife(playerOne);
          }
          BmanPlayers.moveY(playerOne, 1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && (well[p1x][p1y+1] == 1 || well[p1x][p1y+1] > 4)){
          if(well[p1x][p1y+1] > 4){
            BmanPlayers.loseLife(playerOne);
          }
          BmanPlayers.moveY(playerOne, -1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && (well[p1x-1][p1y] == 1 || well[p1x-1][p1y] > 4)){
          if(well[p1x-1][p1y] > 4){
            BmanPlayers.loseLife(playerOne);
          }
          BmanPlayers.moveX(playerOne, -1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && (well[p1x+1][p1y] == 1 || well[p1x+1][p1y] > 4)){
          if(well[p1x+1][p1y] > 4){
            BmanPlayers.loseLife(playerOne);
          }
          BmanPlayers.moveX(playerOne, 1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_SPACE && BmanPlayers.getBombs(playerOne) > 0){
          new Thread() {
            @Override public void run() {
              try {
                if(well[p1x][p1y] == 1 && BmanPlayers.getBombs(playerOne) > 0){
                  // drop bomb, timer for 3 sec
                  BmanPlayers.changeBombs(playerOne, -1);
                  well[p1x][p1y] = 3;
                  game.repaint();
                  Thread.sleep(3000);
                  // bomb explodes
                  game.explode(playerOne, p1x, p1y, BmanPlayers.getexplodeSize(playerOne));
                  //bomb disappears
                  well[p1x][p1y] = 1;
                  BmanPlayers.changeBombs(playerOne, +1);
                  game.repaint();
                  //explosion 'rays' disappear
                  Thread.sleep(1000);
                  bombReset();
                  game.repaint();
                }
              } catch ( InterruptedException e ) {
                e.printStackTrace();
              }
            }
          }.start();
        }
        game.repaint();

        // player two (WASD)
        if(e.getKeyCode() == KeyEvent.VK_W && well[p2x][p2y-1] == 1){
          BmanPlayers.moveY(playerTwo, 1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_S && well[p2x][p2y+1] == 1){
          BmanPlayers.moveY(playerTwo, -1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_A && well[p2x-1][p2y] == 1){
          BmanPlayers.moveX(playerTwo, -1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_D && well[p2x+1][p2y] == 1){
          BmanPlayers.moveX(playerTwo, 1);
        }
        else if(e.getKeyCode() == KeyEvent.VK_T && BmanPlayers.getBombs(playerTwo) > 0){
          new Thread() {
            @Override public void run() {
              try {
                if(well[p2x][p2y] == 1 && BmanPlayers.getBombs(playerTwo) > 0){
                  // drop bomb, timer for 3 sec
                  BmanPlayers.changeBombs(playerTwo, -1);
                  well[p2x][p2y] = 4;
                  game.repaint();
                  Thread.sleep(3000);
                  //bomb explodes
                  game.explode(playerTwo, p2x, p2y, BmanPlayers.getexplodeSize(playerTwo));
                  //bomb disappears
                  well[p2x][p2y] = 1;
                  BmanPlayers.changeBombs(playerTwo, +1);
                  game.repaint();
                  //explosion 'rays' disappear
                  Thread.sleep(2000);
                  bombReset();
                  game.repaint();
                }
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }.start();
        }
        game.repaint();
		  }

			public void keyReleased(KeyEvent e) {
			}
		});
  }

  public void explode(BmanPlayers player, int x, int y, int e){
    int p1x = BmanPlayers.getxPos(playerOne);
    int p1y = BmanPlayers.getyPos(playerOne);
    int p2x = BmanPlayers.getxPos(playerTwo);
    int p2y = BmanPlayers.getyPos(playerTwo);
    int exp = -1;
    if(player == playerOne){
      exp = 5;
    }
    else if(player == playerTwo){
      exp = 6;
    }
    //check units in all four directions up to radius e, sets well to explosions unless hits wall
    for(int i = 0 ; i < e; i++){
      if(well[x][y+i] == 0 || well[x][y+i] == 2){
        if(well[x][y+i] == 0){
          well[x][y+i] = 1;
        }
        break;
      }
      if(x == p1x && y + i == p1y){
        BmanPlayers.loseLife(playerOne);
      }
      if(x == p2x && y + i == p2y){
        BmanPlayers.loseLife(playerTwo);
      }
      well[x][y+i] = exp;
    }
    for(int j = 1; j < e; j++){
      if(well[x][y-j] == 0 || well[x][y-j] == 2){
        if(well[x][y-j] == 0){
          well[x][y-j] = 1;
        }
        break;
      }
      if(x == p1x && y - j == p1y){
        BmanPlayers.loseLife(playerOne);
      }
      if(x == p2x && y - j == p2y){
        BmanPlayers.loseLife(playerTwo);
      }
      well[x][y-j] = exp;
    }
    for(int k = 1; k < e; k++){
      if(well[x+k][y] == 0 || well[x+k][y] == 2){
        if(well[x+k][y] == 0){
          well[x+k][y] = 1;
        }
        break;
      }
      if(x + k == p1x && y == p1y){
        BmanPlayers.loseLife(playerOne);
      }
      if(x + k== p2x && y == p2y){
        BmanPlayers.loseLife(playerTwo);
      }
      well[x+k][y] = exp;
    }
    for(int l = 1; l < e; l++){
      if(well[x-l][y] == 0 || well[x-l][y] == 2){
        if(well[x-l][y] == 0){
          well[x-l][y] = 1;
        }
        break;
      }
      if(x - l == p1x && y== p1y){
        BmanPlayers.loseLife(playerOne);
      }
      if(x - l == p2x && y== p2y){
        BmanPlayers.loseLife(playerTwo);
      }
      well[x-l][y] = exp;
    }
    System.out.println("p1 " + BmanPlayers.getLives(playerOne));
    System.out.println("p2 " + BmanPlayers.getLives(playerTwo));
    repaint();
  }

  public void init(){//initialize game,       //CHANGED
      System.out.println("init");
      well = new int[units][units];
      //fills well with black, with gray on border and with the pattern, brown for breakable boxes
      for(int i = 0; i < units; i ++){
        for(int j = 0; j < units; j ++){
          if(i == 0 || i == units-1 || j == 0 || j == units-1 || (i % 2 == 0 && j % 2 == 0)){
            well[i][j] = 2;
          }
          else if ((i == 1 && (j == 1 || j == 2)) || (i == 2 && j == 1) || (i == 11 && (j == 11 || j == 10)) || (i == 10 && j == 11)){
            well[i][j] = 1;
          }
          else{
            random = Math.random();
            if (random <= boxprob){
              well[i][j]=0;
            }
            else{
              well[i][j]=1;
            }
           // well[i][j] = 1;
          }
        }
      }
      repaint();
    }
  public void paintComponent(Graphics g){
    //paints rectangles with corresponding key in 'well' matrix, then calls other draw functions
    int color;
    for (int i = 0; i < units; i++) {
      for (int j = 0; j < units; j++) {
        color = well[i][j];
        if(color == 0){      //CHANGED
          g.setColor(new Color(139,69,19));
        }
        else if(color == 1){
          g.setColor(Color.black);
        }
        else if(color == 2){
          g.setColor(Color.darkGray);
        }
        g.fillRect(unitSize*i, unitSize*j, unitSize-1, unitSize-1);
        g.setColor(Color.black);
      }
    }
    drawAll(g);
  }
  public void drawAll(Graphics f){
    //paints over background drawn by paintComponent

    // player one (pink)
    f.setColor(Color.pink);
    f.fillOval(unitSize*BmanPlayers.getxPos(playerOne) + unitSize/4, unitSize*BmanPlayers.getyPos(playerOne) + unitSize/4, 25, 25);
    // player two (blue)
    f.setColor(Color.blue);
    f.fillOval(unitSize*BmanPlayers.getxPos(playerTwo) + unitSize/4, unitSize*BmanPlayers.getyPos(playerTwo) + unitSize/4, 25, 25);
    //bombs
    BufferedImage pyr1 = null;
    BufferedImage pyr2 = null;
    BufferedImage redb = null;
    BufferedImage blueb = null;
    try {
      // pyr1 = ImageIO.read(new File("pyr1.png"));
      // pyr2 = ImageIO.read(new File("pyr2.png"));
      redb = ImageIO.read(new File("redbomb.png"));
      blueb = ImageIO.read(new File("bluebomb.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    int color;
    for(int i = 0; i < units; i ++){
      for(int j = 0; j <units; j ++){
        color = well[i][j];
        Color transPink = new Color(255, 192, 203, 160);
        Color transBlue = new Color(0, 0, 255, 160);
        if(color == 3){
          f.setColor(transPink);
          f.drawImage(redb,unitSize*i + 5, unitSize*j + 5, unitSize-9, unitSize-9, null);
        }
        else if(color == 4){
          f.setColor(transBlue);
          f.drawImage(blueb,unitSize*i + 5, unitSize*j + 5, unitSize-9, unitSize-9, null);
        }
        else if(color == 5){
          f.setColor(transPink);
          f.fillRect(unitSize*i, unitSize*j, unitSize-1, unitSize-1);
        }
        else if(color == 6){
          f.setColor(transBlue);
          f.fillRect(unitSize*i, unitSize*j, unitSize-1, unitSize-1);
        }
      }
    }
  }
  public static void bombReset(){
    for(int i = 1; i < units-1; i++){
      for(int j = 1; j < units-1; j++){
        if(well[i][j] == 5 || well [i][j] == 6){
          well[i][j] = 1;
        }
      }
    }
  }
}
