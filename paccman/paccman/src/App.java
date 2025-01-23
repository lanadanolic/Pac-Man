import javax.swing.JFrame; //our frame window

public class App {
    public static void main(String[] args) throws Exception 
    {
        int rowcount = 21;
        int columncount = 19;
        int tilesize = 32;
        int boardwidth = columncount * tilesize;
        int boardheight = rowcount* tilesize;

        //creating window
        JFrame frame = new JFrame("Pac Man");
        //frame.setVisible(true); //da frame bude vidljiv
        frame.setSize(boardwidth, boardheight);  //setting the size of a windows
        frame.setLocationRelativeTo(null);  //to be in center of screen
        frame.setResizable(false);//tkd user ne moze mijenjati dimenzije
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //izlazak iz igre 


        //instanca PacMan.java
        PacMan pacmangame = new PacMan();
        frame.add(pacmangame); //adding to the window
        frame.pack(); //full size of the window
        pacmangame.requestFocus();
        frame.setVisible(true);
    }
}


