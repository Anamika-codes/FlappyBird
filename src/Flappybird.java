import java.awt.*;
import java.awt.event.*;
import java.nio.channels.Pipe;
import java.util.ArrayList;//to store all the pipes in our game
import java.util.Random; //used for placing pipes in random position
import javax.swing.*;
import javax.sound.sampled.*;//used for sounds in game


public class Flappybird extends JPanel implements ActionListener,KeyListener {
    int bw = 360;
    int bh = 640;
    //Image
    Image backgroundImg;//these variables will store our image object
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird class
    int birdX = bw / 8;
    int birdY = bh / 2;
    int birdwidth = 50;
    int birdheight = 40;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        //adds bird's image to the class
        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipes
    int pipeX = bw;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 500;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }


    //Game Logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;// move the bird up or down
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    Clip flapSound;
    Clip gameOverSound;

    JButton startButton;
    JButton menuButton;
    JPanel buttonPanel;
    boolean gameStarted = false;


    //Image bottomPipeImg;
    Flappybird() {

        setPreferredSize(new Dimension(bw, bh));
        setFocusable(true);
        addKeyListener(this);
// Load images using getResource() if the images are inside your project
        backgroundImg = new ImageIcon(getClass().getResource("/Background.jpg")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/Bird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/pillar.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/pillar_rev.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //loads sounds
        loadSounds();

        //Initialize buttons
        startButton = new JButton("Start/Press ESC");
        menuButton = new JButton("Menu");

        // Customize button appearance (optional)
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        menuButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(200, 50));
        menuButton.setPreferredSize(new Dimension(120, 50));
        //add Action Listeners for buttons
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        menuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMenu();
            }
        });

        //button panel
        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical layout

        // Create a space for centering the buttons
        buttonPanel.setOpaque(false); // Transparent background to let the game background show
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center align
        buttonPanel.add(Box.createVerticalGlue());  // Add vertical space before buttons (to center vertically)
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing between buttons
        buttonPanel.add(menuButton);
        buttonPanel.add(Box.createVerticalGlue());  // Add vertical space after buttons

        // Add button panel to the main JPanel
        setLayout(new GridBagLayout()); // Use GridBagLayout to center the panel
        remove(buttonPanel); // Center the button panel


        //Place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

    }

    public void loadSounds() {
        try {
            AudioInputStream flapStream = AudioSystem.getAudioInputStream(getClass().getResource("flap.wav"));
            flapSound = AudioSystem.getClip();
            flapSound.open(flapStream);

            AudioInputStream gameOverStream = AudioSystem.getAudioInputStream(getClass().getResource("gameOver.wav"));
            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(gameOverStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startGame() {
        remove(buttonPanel);//removes the button panels
        revalidate();
        repaint();
        bird.y =birdY;
        velocityY =0;
        pipes.clear();
        score =0;
        gameOver =false;
        gameStarted =true;
        gameLoop.start();
        placePipesTimer.start();
    }

    public void showMenu() {
        // Logic to display a menu (could be implemented later)
        JOptionPane.showMessageDialog(this, "Menu: Start a new game or quit.");
    }

    public void placePipes(){
        //(0-1)*pipeHeight/2 ->(0-256)
        //128
        //0-128 -(0-256) -->pipeHeight/4 -> 3/4 pipeHeight

        int randompipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = bh/4;
        Pipe topPipe=new Pipe(topPipeImg);
        topPipe.y = randompipeY;
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);


    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg,0,0,bw,bh,null);

        //bird
        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe=pipes.get(i);
            g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver){
            g.drawString("GAME OVER : " + String.valueOf((int)score),10,35);
        }
        else{
            g.drawString(String.valueOf((int)score),10,35);
        }

    }

    public Bird getBird() {
        return bird;
    }
    public void move(){//used for gameloop timer

        //bird movement
        velocityY += gravity;
        bird.y = bird.y + velocityY;
        bird.y = Math.max(bird.y,0);
        //pipe movement
        for(int i=0;i<pipes.size();i++){
            Pipe pipe=pipes.get(i);
            pipe.x=pipe.x+velocityX;

            if(!pipe.passed && bird.x > pipe.x+pipe.width) {
                pipe.passed = true ;
                score += 0.5;
            }

            if(collision(bird,pipe)){
                gameOver = true;
                playGameOverSound();
            }
        }

        if(bird.y > bh){
            gameOver = true;
            playGameOverSound();
        }
    }
    public boolean collision(Bird a,Pipe b){
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y+a.height > b.y;
    }
    public void playFlapSound(){
        if (flapSound != null) {
            flapSound.setFramePosition(0); // Reset sound to the start
            flapSound.start();
        }
    }
    public void playGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.setFramePosition(0);
            gameOverSound.start();
        }
    }

    public void actionPerformed(ActionEvent e){
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();;
            gameLoop.stop();
            //Show Buttons when GameOver
            add(buttonPanel);
            revalidate();
            repaint();



        }//calls the paint method
    }
    public void keyReleased(KeyEvent e){

    }
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_SPACE) {
            velocityY = -9;
            playFlapSound();
        }
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
            if(gameOver){
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0 ;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
                // Removes button to restart the game
                remove(buttonPanel);
                revalidate();
                repaint();


            }

        }
    }
    public void keyTyped(KeyEvent e){

    }
}