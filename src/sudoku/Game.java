package sudoku;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

//game class paints board and options pane, handles options selections
public class Game extends Canvas implements Runnable, MouseListener{
    
    public static final int WIDTH = 646, HEIGHT = WIDTH+23;
    
    private Thread thread;
    private Boolean running = false;
    private Handler handler;
    public GameState gameState;
    
    public enum GameState{
        game,
        checking,
        finished,
        solving,
    };
    
    public Game(){
        handler = new Handler(this);
        new Window(WIDTH, HEIGHT+40, "Sudoku Solver", this);       
        addMouseListener(this);
        gameState = GameState.game;
    }
    
    //cycle numbers and solve/exit buttons
    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        
        //if clicking on non-given space, cycle number by +1
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(mouseOver(mx, my, i*71, j*71, 71, 71)){
                    if(!handler.isGiven(i, j)){
                        handler.cycleNumber(i, j);
                    }
                }
           }
        }
        
        //if clicking on solve button - solve function
        if(mouseOver(mx, my, 1, HEIGHT-30, 150, 40)){
            gameState = GameState.solving;
            handler.solver();
        }
        //end button click detection
        if(mouseOver(mx, my, WIDTH-158, HEIGHT-30, 150, 40)){
            System.exit(0);
        }     
    }
    
    //bound detection for clicks
    private boolean mouseOver(int mx, int my, int x, int y, int width, int height){
        if(mx > x && mx < x + width){
            return my > y && my < y + height;
        }else return false;
    }
    
    public void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    //paint components
    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        if(gameState == GameState.solving || gameState == GameState.finished){
            handler.sortingRender(g);
        }
 
        drawBoard(g);
        handler.render(g);

        g.dispose();
        bs.show();
    }
    
    //draws board outlines
    public void drawBoard(Graphics2D g){
        
       
        g.setColor(Color.gray);
        for(int i=0; i<9; i++){
           for(int j=0; j<9; j++){
               g.drawRect(i*71, j*71, 71, 71);
           }
        }
        
        //sets line widths to 2 pixels
        g.setStroke(new BasicStroke(2)); 
        
        Font font = new Font("Arial", 1, 30);
        g.setFont(font);
        
        //draws main borders
        g.setColor(Color.black);
        g.drawRect(1,1, WIDTH-7, HEIGHT-30);
        
        //draw options pane
        g.drawRect(1, HEIGHT-30, WIDTH-8, 40);
        
        //solve pane
        g.drawRect(1, HEIGHT-30, 150, 40);
        g.drawString("Solve", 35, HEIGHT);
        
        //exit pane
        g.drawRect(WIDTH-158, HEIGHT-30, 150, 40);
        g.drawString("Exit", WIDTH-115, HEIGHT);
        
        //title pane
        g.drawRect(152, HEIGHT-30, WIDTH-308, 40);
        g.drawString("Sudoku Solver", 215, HEIGHT);

        //draws game board squares
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++)
                g.drawRect(i*213, j*213, 213, 213);
        }
    }
    
    public void stop(){
       try{
            thread.join();
            running = false;
        }catch(InterruptedException e){
        } 
    }
    
    
    public static void main(String[] args) {
        new Game();
    }

    // Game engine makes time go choo choo
    @Override
    public void run() {
        //makes it so you dont have to click on window before enabling controls
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            delta+= (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                delta--;
            }
            if(running)
                render();
            frames++;
        
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS " + frames);
                frames = 0;
            }
        }
        stop();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    } 
}
