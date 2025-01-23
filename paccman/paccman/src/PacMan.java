import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener
{
      //a version for jpanel but with more properties for the game
      private int rowcount = 21;
      private int columncount = 19;
      private int tilesize = 32;
      private int boardwidth = columncount * tilesize;
      private int boardheight = rowcount* tilesize;


      //variables to store the images
      private Image wallImage;
      private Image blueGhostImage; //for the ghost images
      private Image orangeGhostImage; 
      private Image pinkGhostImage; 
      private Image redGhostImage;
      private Image pacmanUpImage;
      private Image pacmanDownImage;
      private Image pacmanLeftImage;
      private Image pacmanRightImage;

      //constructor
      PacMan() 
      {
        setPreferredSize(new Dimension(boardwidth, boardheight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //loading images (static images)
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for(Block ghost : ghosts) 
        {
            char newDirection = directions[random.nextInt(4)]; //za duhove
            ghost.updateDirection(newDirection);
    
        }
        gameLoop = new Timer(50, this); //svako 50 milisek se osvjezava nova slika
        gameLoop.start();
      }


      class Block
      {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;//for restarting the game
        int startY;
 
        char direction = 'U'; //up
        int velocityX = 0;
        int velocityY = 0;

        //construcstor
        Block(Image image, int x, int y, int width, int height)
        {
            this.image=image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection (char direction)
        {
            char prevDirection = this.direction;  
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls)
            {
                if (collision (this, wall))  //this predstavlja pacmana ili duhove
                {
                     this.x -= this.velocityX; //za step back
                     this.y -= this.velocityY;
                     this.direction = prevDirection;       //updating direction
                     updateVelocity();
                }
            }
        }

        void updateVelocity()
        {
            if (this.direction == 'U')
            {
                this.velocityX = 0;
                this.velocityY = -tilesize/7; //jer idemo put gore tj put 0, zato je -
            }
            else if (this.direction == 'D')
            {
                this.velocityX = 0;
                this.velocityY = tilesize/7; //jer idemo put dole tj put pozitivnijih brojeva, zato je +
            }
            else if (this.direction == 'L')
            {
                this.velocityX = -tilesize/7; //neg je jer idemo prema 0 
                this.velocityY = 0; 
            }
            else if (this.direction == 'R')
            {
                this.velocityX = tilesize/7; //poz je jer idemo desno od 0 tj prema poz brojevima 
                this.velocityY = 0; 
            }
        }

        //da resetira game ako dode do kolizije s pacmanom i duhom tj da ih vrati na prvobitne pozicije
        void reset()
        {
            this.x = this.startX;
            this.y = this.startY; 
        }
    }

    HashSet<Block> walls;  //blokovi koji reprezentiraju zidove
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char [] directions = {'U', 'D', 'L', 'R'}; //za duhove
    Random random = new Random();  //za duhove da se random micu

    int score = 0; //pacman eats the food
    int lives = 3;
    boolean gameOver = false;  //pacman ima 3 zivota, ako pacman colidira s duhom i izgubi postupno sva 3 zivota, bool vrijednost cemo promijeniti da bude true

    // Nova varijabla za praćenje pauze
    boolean isPaused = false;

    char nextDirection = 'U'; // Sljedeći smjer koji Pacman treba pratiti

    //tilemap (array of strings)
     //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = 
    {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "X       bpo       X",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 

        
    };



//kreiranje objekata za ghosts pacman walls i food
public void loadMap()
{
    walls = new HashSet<Block>();  //inicijaliziranje svih hashsetova
    foods = new HashSet<Block>();
    ghosts = new HashSet<Block>();

    for (int r = 0; r < rowcount; r++)//iteriranje kroz mapu //rows
    {
        for(int c= 0; c < columncount; c++) //columns
        {
            String row = tileMap[r]; //current row
            char tilemapChar = row.charAt(c); //trenutni character

            int x = c * tilesize;  //how many tiles from the left
            int y = r * tilesize; //how many rows from the top

            if(tilemapChar == 'X')   //block wall
            {
               Block wall = new Block(wallImage, x, y, tilesize, tilesize);
               walls.add(wall);
            }
            else if (tilemapChar == 'b') //blue ghost
            {
                Block ghost = new Block(blueGhostImage, x, y, tilesize, tilesize);
                ghosts.add(ghost);
            }
            else if (tilemapChar == 'o') //orange ghost
            {
                Block ghost = new Block(orangeGhostImage, x, y, tilesize, tilesize);
                ghosts.add(ghost);
            }
            else if (tilemapChar == 'p') //pink ghost
            {
                Block ghost = new Block(pinkGhostImage, x, y, tilesize, tilesize);
                ghosts.add(ghost);
            }
            else if (tilemapChar == 'r')  //red ghost
            {
                Block ghost = new Block(redGhostImage, x, y, tilesize, tilesize);
                ghosts.add(ghost);
            }
            else if (tilemapChar == 'P')  //pacman
            {
                pacman = new Block(pacmanRightImage, x, y, tilesize, tilesize);
            }
            else if (tilemapChar == ' ')  //food
            {
                Block food = new Block (null, x + 14, y + 14, 4, 4); //pixeli (kvadrat je 32 pixela)
                foods.add(food);
            }

        }
    }
}

  public void paintComponent(Graphics g)
  {
     super.paintComponent(g);
     draw(g);
  }

  public void draw (Graphics g)
  {
     g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

     for(Block ghost : ghosts)
     {
        g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

     }

     for(Block wall : walls )
     {
        g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
     }

     g.setColor(Color.WHITE);
     for(Block food : foods )
     {
        g.fillRect(food.x, food.y, food.width, food.height);
     }

     //score for eaten food
     g.setFont(new Font ("Arial", Font.PLAIN, 18));
     if(gameOver)
     {
        g.drawString("Game over: " + String.valueOf(score), tilesize/2, tilesize/2);
     }
     else if (isPaused) 
     {
        g.drawString("The game is paused", boardwidth / 2 - 80, boardheight / 2);
    }
     else //display the score + number of lives
     {
         g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tilesize/2, tilesize/2);
     }
  }

public void move()
{
// Promjena smjera ako je moguća
    if (canMove(pacman, nextDirection)) 
    {
       pacman.updateDirection(nextDirection);
    }

    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;


     //check wall collision
     for (Block wall : walls)
     {
        if (collision(pacman, wall))
        {
          pacman.x -= pacman.velocityX;
          pacman.y -= pacman.velocityY;
          break;
        }
     }

     //check ghost collisions
     for (Block ghost : ghosts) 
     {
        //provjera jel duh colidira s pacmanom
        if (collision(ghost, pacman))
        {
           lives -= 1;
           if (lives == 0)
           {
               gameOver = true;
               return;
           }
           resetPositions();
        }

        if (ghost.y == tilesize*9 && ghost.direction != 'U' && ghost.direction != 'D') //ako duh stane na toj pocentoj liniji i ide samo lijevo desno
        {
            ghost.updateDirection('U');
        }
        ghost.x += ghost.velocityX;
        ghost.y += ghost.velocityY;
        //kolizije za zidove
        for (Block wall : walls) 
        {
            if(collision (ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardwidth)
            {
                ghost.x -= ghost.velocityX;
                ghost.y -= ghost.velocityY;
                //da promini direction kad se dogodi kolizija sa zidom
                char newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection);
            }
        }
     }

     //check food collision sa pacmanom (neka nestane kada pacman prolazi preko nje)
     Block foodEaten = null; //trenutno pacman nije pojia nista
     for (Block food : foods) 
     {
        if (collision(pacman, food))
        {
            foodEaten = food;
            score += 10;
        }
     }
     foods.remove(foodEaten);
     if (foods.isEmpty())
     {
        loadMap();
        resetPositions();
     }
}

// Pomoćna metoda za provjeru može li Pacman ići u određenom smjeru
private boolean canMove(Block block, char direction) {
    int nextX = block.x;
    int nextY = block.y;

    if (direction == 'U') nextY -= tilesize / 4;
    else if (direction == 'D') nextY += tilesize / 4;
    else if (direction == 'L') nextX -= tilesize / 4;
    else if (direction == 'R') nextX += tilesize / 4;

    // Provjeri sudar s bilo kojim zidom
    for (Block wall : walls) {
        if (collision(new Block(null, nextX, nextY, block.width, block.height), wall)) {
            return false;
        }
    }
    return true;
}

public boolean collision(Block a, Block b)
{
    return a.x < b.x + b.width &&
           a.x + a.width > b.x &&
           a.y < b.y + b.height &&
           a.y + a.height > b.y;

          
}

public void resetPositions()
{
    pacman.reset();
    pacman.velocityX = 0;  //za pacmana
    pacman.velocityY = 0;

    for (Block ghost : ghosts)  //za duhove
    {
        ghost.reset();
        char newDirection = directions[random.nextInt(4)];
        ghost.updateDirection(newDirection);
    }
}

// Dodana metoda pause
public void pause() {
    if (isPaused) {
        gameLoop.start();  // Nastavlja igru
        isPaused = false;
    } else {
        gameLoop.stop();  // Zaustavlja igru
        isPaused = true;
    }
}

@Override
public void actionPerformed(ActionEvent e) {
    move();
    repaint();
    if (gameOver)
    {
        gameLoop.stop();
    }
}

@Override
public void keyPressed(KeyEvent e) {}

@Override
public void keyReleased(KeyEvent e) {

    if (gameOver)
    {
        loadMap(); //da se hrana vrati
        resetPositions();
        lives = 3;
        score = 0;
        gameOver = false;
        gameLoop.start(); 
    }

    // Provjera za pauziranje
    if (e.getKeyCode() == KeyEvent.VK_P) {
        pause();
        repaint(); //azurira prikaz kako bi se poruka ispisala
        return; // Ne želimo da se druge tipke obrađuju kad je P pritisnuto
    }

    //System.out.println("KeyEvent: " + e.getKeyCode());
   if(e.getKeyCode() == KeyEvent.VK_UP)
   {
       nextDirection = 'U';
       //pacman.updateDirection('U');
   }
   else if(e.getKeyCode() == KeyEvent.VK_DOWN)
   {
         nextDirection = 'D';
       //pacman.updateDirection('D');
   }
   else if(e.getKeyCode() == KeyEvent.VK_LEFT)
   {
    nextDirection = 'L';
       //pacman.updateDirection('L');
   }
   else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
   {
    nextDirection = 'R';
       //pacman.updateDirection('R');
   }

   if (pacman.direction == 'U')
   {
       pacman.image = pacmanUpImage;
   }
   else if (pacman.direction == 'D')
   {
       pacman.image = pacmanDownImage;
   }
   else if (pacman.direction == 'R')
   {
       pacman.image = pacmanRightImage;
   }
   else if (pacman.direction == 'L')
   {
       pacman.image = pacmanLeftImage;
   }
}

@Override
public void keyTyped(KeyEvent e) {}
}

