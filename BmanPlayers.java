public class BmanPlayers{
  protected int xPos;
  protected int yPos;
  protected int lives;
  protected int bombs;
  protected int explodeSize;
  public BmanPlayers(){
    xPos = 0;
    yPos = 0;
    lives = 3;
    bombs = 3;
    explodeSize = 3;
  }
  public static void setPos(BmanPlayers player, int x, int y){
    player.xPos = x;
    player.yPos = y;
  }
  public static void setLives(BmanPlayers player, int l){
    player.lives = l;
  }
  public static void setBombs(BmanPlayers player, int b){
    player.bombs = b;
  }
  public static void setexplodeSize(BmanPlayers player, int x){
    player.explodeSize += x;
  }
  public static int getxPos(BmanPlayers player){
    return player.xPos;
  }
  public static int getyPos(BmanPlayers player){
    return player.yPos;
  }
  public int getLives(){
    return this.lives;
  }
  public static int getBombs(BmanPlayers player){
    return player.bombs;
  }
  public static int getexplodeSize(BmanPlayers player){
    return player.explodeSize;
  }
  public static void moveX(BmanPlayers player, int x){
    player.xPos += x;
  }
  public static void moveY(BmanPlayers player, int y){
    player.yPos -= y;
  }
  public void loseLife(){
    lives -=1;
  }
  public static void changeBombs(BmanPlayers player, int a){
    player.bombs += a;
  }
}
