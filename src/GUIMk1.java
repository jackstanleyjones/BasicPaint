import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
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
        ToolSelect utiltyBar = new ToolSelect(drawingArea);
        //ButtonGroup tools = new ButtonGroup();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("GUIMk1");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(utiltyBar, BorderLayout.WEST);


        frame.getContentPane().add(drawingArea);
        frame.setSize(450, 400);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
        frame.pack();
    }

    static class ToolSelect extends JPanel implements ActionListener{
        public static String toolSelction;
        public ButtonGroup toolGroup = new ButtonGroup();

        private DrawingArea drawingArea;

        public ToolSelect(DrawingArea drawingArea){
            this.drawingArea = drawingArea;
            JToolBar toolbar  = new JToolBar(null, JToolBar.VERTICAL);
            toolbar.add(makeButton("plot"));
            toolbar.add(makeButton("line"));
            toolbar.add(makeButton("rectangle"));
            toolbar.add(makeButton("ellipse"));
            toolbar.add(makeButton("polygon"));
            add(toolbar);

        }

         private JRadioButton makeButton(String text){
            JRadioButton button = new JRadioButton(text);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setVerticalAlignment(SwingConstants.BOTTOM);
            toolGroup.add(button);


             button.addActionListener( this );
            return button;
         }

         private void setToolSelction(String tool){
            this.toolSelction = tool;
        }


        public String GetTool(){
            return this.toolSelction;
        }


        public void actionPerformed(ActionEvent e) {
            JRadioButton button = (JRadioButton)e.getSource();

            setToolSelction(e.getActionCommand());
            System.out.print(GetTool());

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










