import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUIMk1 {
    private JPanel panel1;

    abstract class Mycanvas extends JPanel implements MouseListener {
        Image img;      // Contains the image to draw on MyCanvas

        public Mycanvas() {
            // Initialize img here.
            this.addMouseListener(this);
        }

        public void paintComponent(Graphics g) {
            // Draws the image to the canvas
            g.drawImage(img, 0, 0, null);
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            Graphics g = img.getGraphics();
            g.fillOval(x, y, 3, 3);
            g.dispose();
        }


    }

    public static void main(String[] args){
        JFrame frame = new JFrame("GUIMk1");
        frame.setContentPane(new GUIMk1().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
