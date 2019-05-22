import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
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
        public static String toolSelection = "plot";
        public ButtonGroup toolGroup = new ButtonGroup();
        public static Color borderColor = Color.BLACK;

        private DrawingArea drawingArea;

        public ToolSelect(DrawingArea drawingArea){
            this.drawingArea = drawingArea;
            JToolBar toolbar  = new JToolBar(null, JToolBar.VERTICAL);
            toolbar.add(makeButton("plot"));
            toolbar.add(makeButton("line"));
            toolbar.add(makeButton("rectangle"));
            toolbar.add(makeButton("ellipse"));
            ColorChooserButton colorChooser = new ColorChooserButton(Color.WHITE);
            colorChooser.addColorChangedListener(new ColorChooserButton.ColorChangedListener() {
                @Override
                public void colorChanged(Color newColor) {
                    setColor(newColor);
                }
            });
            toolbar.add(makeButton("polygon"));
            toolbar.add(colorChooser);
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
            this.toolSelection = tool;
        }

        private void setColor(Color c){
            this.borderColor = c;
        }

        public static Color GetColor(){
            return borderColor;
        }


        public static String GetTool(){
            return toolSelection;
        }


        public void actionPerformed(ActionEvent e) {
            JRadioButton button = (JRadioButton)e.getSource();

            setToolSelction(e.getActionCommand());
            System.out.print(GetTool());

        }
    }


    public static class ColorChooserButton extends JButton {

        private Color current;

        public ColorChooserButton(Color c) {
            setSelectedColor(c);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Color newColor = JColorChooser.showDialog(null, "Choose a color", current);
                    setSelectedColor(newColor);
                    System.out.print(newColor);
                }
            });
        }

        public Color getSelectedColor() {
            return current;
        }

        public void setSelectedColor(Color newColor) {
            setSelectedColor(newColor, true);
        }

        public void setSelectedColor(Color newColor, boolean notify) {

            if (newColor == null) return;

            current = newColor;
            setIcon(createIcon(current, 16, 16));
            repaint();

            if (notify) {
                // Notify everybody that may be interested.
                for (ColorChangedListener l : listeners) {
                    l.colorChanged(newColor);
                }
            }
        }

        public interface ColorChangedListener {
            public void colorChanged(Color newColor);
        }

        private List<ColorChangedListener> listeners = new ArrayList<ColorChangedListener>();

        public void addColorChangedListener(ColorChangedListener toAdd) {
            listeners.add(toAdd);
        }

        public ImageIcon createIcon(Color main, int width, int height) {
            BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(main);
            graphics.fillRect(0, 0, width, height);
            graphics.setXORMode(Color.DARK_GRAY);
            graphics.drawRect(0, 0, width-1, height-1);
            image.flush();
            ImageIcon icon = new ImageIcon(image);
            return icon;
        }
    }


    static class DrawingArea extends JPanel {
        private final static int AREA_SIZE = 400;
        private ArrayList<ColoredRectangle> coloredRectangles = new ArrayList<ColoredRectangle>();
        private ColoredRectangle shape = new ColoredRectangle(ToolSelect.GetColor(), new Rectangle(), ToolSelect.GetTool());



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
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //  Custom code to paint all the Rectangles from the List

            Color foreground = g.getColor();

            g.setColor(ToolSelect.GetColor());

                for (DrawingArea.ColoredRectangle cr : coloredRectangles) {
                    g.setColor(cr.getForeground());
                    Rectangle r = cr.getRectangle().getBounds();
                    if (cr.getType() == "rectangle") {
                        g.drawRect(r.x, r.y, r.width, r.height);
                    } else if (cr.getType() == "ellipse"){
                        g.drawOval(r.x, r.y, r.width, r.height);
                    } else if (cr.getType() == "plot"){
                        g.drawOval(r.x, r.y, r.width, r.height);
                    } else if (cr.getType() == "line"){
                        g.drawLine(r.x,r.y,r.width, r.height);
                    }
            //  Paint the Rectangle as the mouse is being dragged

                if (shape != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(ToolSelect.GetColor());
                    Rectangle c = shape.shape.getBounds();
                    if (ToolSelect.GetTool() == "rectangle") {
                        g.drawRect(c.x, c.y, c.width, c.height);
                    } else if (ToolSelect.GetTool() == "ellipse"){
                        g.drawOval(c.x, c.y, c.width, c.height);
                    }else if (ToolSelect.GetTool() == "line"){
                        g.drawLine(c.x,c.y, c.width,c.height);
                    }

                }
            }

        }

        public void addRectangle(Rectangle rectangle, Color color, String type) {
            //  Add the Rectangle to the List so it can be repainted
                ColoredRectangle cr = new ColoredRectangle(color, rectangle, type);
                System.out.print(color);
                coloredRectangles.add(cr);
                repaint();

        }


        class MyMouseListener extends MouseInputAdapter {

            private Point startPoint;



            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();

                shape = new ColoredRectangle(ToolSelect.GetColor(), new Rectangle(), ToolSelect.GetTool());
                if (ToolSelect.GetTool() == "plot" || ToolSelect.GetTool() == "line"){
                    shape.shape.setBounds(e.getX(),e.getY(),1,1);
                }
            }

            public void mouseDragged(MouseEvent e) {
                int x = Math.min(startPoint.x, e.getX());
                int y = Math.min(startPoint.y, e.getY());
                int width = Math.abs(startPoint.x - e.getX());
                int height = Math.abs(startPoint.y - e.getY());

                if(ToolSelect.GetTool() != "plot" && ToolSelect.GetTool() != "line") {
                    shape.shape.setBounds(x, y, width, height);
                } else if (ToolSelect.GetTool() == "line"){
                    shape.shape.setBounds(shape.shape.x,shape.shape.y, e.getXOnScreen(), e.getYOnScreen());
                }
                repaint();
            }



            public void mouseReleased(MouseEvent e) {
                if (shape.shape.width != 0 || shape.shape.height != 0) {
                    if(ToolSelect.GetTool() == "line"){
                        shape.shape.setBounds(shape.shape.x, shape.shape.y, e.getX(),e.getY());
                    }
                    addRectangle(shape.shape, ToolSelect.GetColor(), ToolSelect.GetTool());
                }

                shape = null;
            }
        }

        class ColoredRectangle {
            private Color foreground;
            private Rectangle shape;
            private String type;



            public ColoredRectangle(Color foreground, Rectangle shape, String type) {
                this.foreground = foreground;
                this.shape = shape;
                this.type = type;
            }

            public Color getForeground() {
                return foreground;
            }

            public String getType(){ return type;}


            public void setForeground(Color foreground) {
                this.foreground = foreground;
            }

            public Rectangle getRectangle() {
                return shape;
            }
        }
    }
}
