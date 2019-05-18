import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GUIMk1 {
    JPanel panel1;
    private boolean draw = false;
    private Point myPoint;

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });

    }

    public static void createGUI(){
        DrawingArea drawingArea = new DrawingArea();
        ToolSelect utiltyBar = new ToolSelect();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("GUIMk1");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.getContentPane().add(utiltyBar);

        frame.getContentPane().add(drawingArea);
        frame.setSize(400, 420);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    static class ToolSelect extends JPanel{
        JRadioButton plot = new JRadioButton("plot");
        JRadioButton line;
        JRadioButton rectangle;
        JRadioButton ellipse;
        JRadioButton polygon;

        public void ToolSelect(){
            JToolBar toolSelectBar = new JToolBar();
            toolSelectBar.add(plot);
            toolSelectBar.add(line);
            toolSelectBar.add(rectangle);
            toolSelectBar.add(ellipse);
            toolSelectBar.add(polygon);
        }



    }

    static class DrawingArea extends JPanel {
        private final static int AREA_SIZE = 400;
        private ArrayList<ColoredRectangle> coloredRectangles = new ArrayList<ColoredRectangle>();
        private Rectangle shape;

        public DrawingArea() {
            setBackground(Color.WHITE);

            MyMouseListener ml = new MyMouseListener();
            addMouseListener(ml);
            addMouseMotionListener(ml);
        }


        @Override
        public Dimension getPreferredSize() {
            return isPreferredSizeSet() ?
                    super.getPreferredSize() : new Dimension(AREA_SIZE, AREA_SIZE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //  Custom code to paint all the Rectangles from the List

            Color foreground = g.getColor();

            g.setColor(Color.BLACK);


            for (DrawingArea.ColoredRectangle cr : coloredRectangles) {
                g.setColor(cr.getForeground());
                Rectangle r = cr.getRectangle();
                g.drawRect(r.x, r.y, r.width, r.height);
            }

            //  Paint the Rectangle as the mouse is being dragged

            if (shape != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(foreground);
                g2d.draw(shape);
            }
        }

        public void addRectangle(Rectangle rectangle, Color color) {
            //  Add the Rectangle to the List so it can be repainted

            ColoredRectangle cr = new ColoredRectangle(color, rectangle);
            coloredRectangles.add(cr);
            repaint();
        }

        public void clear() {
            coloredRectangles.clear();
            repaint();
        }

        class MyMouseListener extends MouseInputAdapter {
            private Point startPoint;

            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                shape = new Rectangle();
            }

            public void mouseDragged(MouseEvent e) {
                int x = Math.min(startPoint.x, e.getX());
                int y = Math.min(startPoint.y, e.getY());
                int width = Math.abs(startPoint.x - e.getX());
                int height = Math.abs(startPoint.y - e.getY());

                shape.setBounds(x, y, width, height);
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                if (shape.width != 0 || shape.height != 0) {
                    addRectangle(shape, e.getComponent().getForeground());
                }

                shape = null;
            }
        }

        class ColoredRectangle {
            private Color foreground;
            private Rectangle rectangle;

            public ColoredRectangle(Color foreground, Rectangle rectangle) {
                this.foreground = foreground;
                this.rectangle = rectangle;
            }

            public Color getForeground() {
                return foreground;
            }

            public void setForeground(Color foreground) {
                this.foreground = foreground;
            }

            public Rectangle getRectangle() {
                return rectangle;
            }
        }
    }
}










