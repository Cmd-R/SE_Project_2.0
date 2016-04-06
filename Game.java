package package1.game;

import package1.GameClockTimer;
import package1.game.entity.Asteroid;
import package1.game.entity.Entity;
import package1.game.entity.Ship;
import package1.game.gameUtil.Movement;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by tyleranson on 3/15/16.
 */
public class Game extends JFrame {
    private int playerLives;
    private boolean isGameOver;
    protected int numAsteroids = 25;


    boolean rightPressed=false, leftPressed=false, downPressed=false,
            upPressed=false;
    /**
     * The frames per second that the game is going to refresh at
     */
    private static final int FPS = 60;

    /**
     * The number of nanoseconds that should elapse each frame.
     */
    private static final long FRAME_TIME = (long)(1000000000.0 / FPS);

    /**
     * the GUI instance or world
     */
    private GUI gui;

    /**
     * A list of enties that are in the game currently
     */
    private List<Entity> entities;

    /**
     * A list of entities that are going to be added to the game
     */
    private List<Entity> pendingEntities;

    /**
     * The rocketShip (RocketShip)
     */
    private Ship rocketShip;

    /**
     * a clock for handling updates to the game
     */
    private GameClockTimer timer;

    public boolean gameOver;




    private Game() {
        super("ASTROYED");


        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        add(this.gui = new GUI(this), BorderLayout.CENTER);


        addKeyListener(new KeyAdapter(){ /******************************************************************
         * keyPressed method determines what keys are being pressed in
         * combination with others to set the rocket ship in a direction.
         * @param e
         *****************************************************************/
        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if(code == KeyEvent.VK_UP){// && (!downPressed)){
                rocketShip.setUpPressed(true);
            }
            if(code == KeyEvent.VK_DOWN){
                rocketShip.setDownPressed(true);
            }
            if(code == KeyEvent.VK_LEFT){
                rocketShip.setLeftPressed(true);
            }
            if(code == KeyEvent.VK_RIGHT){
                rocketShip.setRightPressed(true);
            }
        }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if(code == KeyEvent.VK_UP){
                   rocketShip.setUpPressed(false);
                }

                if(code == KeyEvent.VK_DOWN){
                    rocketShip.setDownPressed(false);
                }

                if(code == KeyEvent.VK_LEFT){
                    rocketShip.setLeftPressed(false);
                }

                if(code == KeyEvent.VK_RIGHT){
                    rocketShip.setRightPressed(false);
                }

            }

        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void addAsteroid(List<Entity> entities) {
        Random rand = new Random();
        double randomNumber = -3 + (3 + 3) * rand.nextDouble();
        double randomNumber2 = -3 + (3 + 3) * rand.nextDouble();
        entities.add(new Asteroid(new Movement(rand.nextInt(1200),rand.nextInt(900)), new Movement(randomNumber,randomNumber2),Asteroid.getSize()));
    }

    /**
     * Starts the game and keeps the game running.
     */
    private void startGame() {
        entities = new LinkedList<Entity>();
        pendingEntities = new ArrayList<Entity>();
        rocketShip = new Ship();

        //Sets everything back to its default values
        resetGame();
        this.timer = new GameClockTimer(FPS);
        for(int i = 0; i < numAsteroids; i++)
        {
            addAsteroid(entities);
        }
        while(true) {
            //Gets the initial time of the start
            long start = System.nanoTime();
                updateGame();
            timer.update();
            for (int i = 0; i < 5 && timer.hasElapsedCycle(); i++){
                //updateGame();
            }
            gui.repaint();

            long delta = FRAME_TIME - (System.nanoTime() - start);
            if(delta > 0) {
                try {
                    Thread.sleep(delta / 1000000L, (int) delta % 1000000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            for(Entity entity : entities) {
                entity.update(this);

            }
        }
    }

    private void resetGame(){
        this.playerLives = 0;
        clearLists();
    }

    private void updateGame(){

        for(int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            for(int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if(i != j && a.isIntercepting(b) && ((a == rocketShip && b != rocketShip))) {
                    a.handleInterception(this, b);
                    b.handleInterception(this, a);
                }
            }
        }
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()){
            if(iterator.next().isDeadObject()){
                iterator.remove();
            }
        }
            if(gameOver){
                resetGame();
            }
    }

    private void clearLists(){
        pendingEntities.clear();
        entities.clear();
        entities.add(rocketShip);
    }

    public void killPlayer(){
        this.playerLives--;
        gameOver = true;

        if(playerLives == 0){
            this.isGameOver = true;
        }
    }

    /******************************************************************
     * main method of the GUI that makes an instance of the GUI
     * @param args
     *****************************************************************/
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        Game game = new Game();
        game.startGame();
    }
}
