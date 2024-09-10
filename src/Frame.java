import javax.swing.*;
import java.awt.*;

public class Frame {
    public static void main(String[] args) throws Exception {
        int bw = 360;
        int bh = 640;
        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        frame.setSize(bw, bh);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(34, 87, 121));
        Flappybird flappyBird = new Flappybird();
        frame.add(flappyBird);frame.pack(); //for not including the title bar
        flappyBird.requestFocus(true);
        frame.setVisible(true);

    }
}
