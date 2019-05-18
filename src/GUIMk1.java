import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUIMk1 {
    private JPanel panel1;
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MakeGUI();
            }
        });
    }

    private static void MakeGUI(){
        JFrame frame = new JFrame("GUIMk1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Toolbar());

        frame.add(new MyPanel());
        frame.pack();
        frame.setVisible(true);
    }


}

class MyPanel extends JPanel {
    private int squareX = 50;
    private int squareY = 50;
    private int squareW = 20;
    private int squareH = 20;

    MyPanel() {

        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });

    }

    private void moveSquare(int x, int y) {
        int OFFSET = 1;
        if ((squareX != x) || (squareY != y)) {
            repaint(squareX, squareY, squareW + OFFSET, squareH + OFFSET);
            squareX = x;
            squareY = y;
            repaint(squareX, squareY, squareW + OFFSET, squareH + OFFSET);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(250, 250);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(squareX, squareY, squareW, squareH);
        g.setColor(Color.BLACK);
        g.drawRect(squareX, squareY, squareW, squareH);
    }

}

class Toolbar extends JPanel {

    Toolbar(){

        setBorder(BorderFactory.createLineBorder(Color.black));
        setSize(250,20);


        JToolBar toolBar = new JToolBar("Still draggable");
        JButton plot = new JButton("plot");
        toolBar.add(plot, BorderLayout.NORTH);

        //Create the text area used for output.  Request
        //enough space for 5 rows and 30 columns.
        //Lay out the main panel.
        //setPreferredSize(new Dimension(450, 130));
    }
}







