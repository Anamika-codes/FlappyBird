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