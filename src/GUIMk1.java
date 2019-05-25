import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.tools.Tool;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

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
        public static Color fillColor = null;

        private DrawingArea drawingArea;

        public ToolSelect(DrawingArea drawingArea){
            this.drawingArea = drawingArea;
            JToolBar toolbar  = new JToolBar(null, JToolBar.VERTICAL);

            ColorChooserButton borderColorChooser = new ColorChooserButton(Color.WHITE);
            borderColorChooser.addColorChangedListener(new ColorChooserButton.ColorChangedListener() {
                @Override
                public void colorChanged(Color newColor) {
                    setBorderColor(newColor);
                }
            });

            ColorChooserButton fillColorChooser = new ColorChooserButton(Color.WHITE);
            fillColorChooser.addColorChangedListener(new ColorChooserButton.ColorChangedListener() {
                @Override
                public void colorChanged(Color newColor) {
                    setFillColor(newColor);
                }
            });

            toolbar.add(makeButton("plot"));
            toolbar.add(makeButton("line"));
            toolbar.add(makeButton("rectangle"));
            toolbar.add(makeButton("ellipse"));

            toolbar.add(makeButton("polygon"));
            toolbar.add(borderColorChooser);
            toolbar.add(fillColorChooser);
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

        private void setBorderColor(Color c){
            this.borderColor = c;
        }

        public static Color GetBorderColor(){
            return borderColor;
        }

        private void setFillColor(Color c){
            this.fillColor = c;
        }

        public static Color GetFillColor(){
            return fillColor;
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
        private ColoredRectangle shape = new ColoredRectangle(ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), new Rectangle(), ToolSelect.GetTool());



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
            Graphics2D graphx = (Graphics2D) g;

            //  Custom code to paint all the Rectangles from the List



            g.setColor(getForeground());

                for (DrawingArea.ColoredRectangle cr : coloredRectangles) {
                    //g.setColor(getForeground());
                    Rectangle r = cr.getRectangle().getBounds();
                    //g.setColor(ToolSelect.GetFillColor());
                    if (cr.getType() == "rectangle") {
                        g.setColor(cr.border);
                        g.drawRect(r.x, r.y, r.width, r.height);
                        g.setColor(cr.fill);
                        if (cr.fill != null) {
                            g.fillRect(r.x, r.y, r.width, r.height);
                        }
                    } else if (cr.getType() == "ellipse"){
                        g.setColor(cr.border);
                        g.drawOval(r.x, r.y, r.width, r.height);
                        g.setColor(cr.fill);
                        if (cr.fill != null) {
                            g.fillOval(r.x, r.y, r.width, r.height);
                        }
                    } else if (cr.getType() == "plot"){
                        g.setColor(cr.border);
                        g.drawOval(r.x, r.y, r.width, r.height);
                    } else if (cr.getType() == "line"){
                        g.setColor(cr.border);
                        g.drawLine(r.x,r.y,r.width, r.height);
                    }else if (cr.getType() == "polygon"){
                        g.setColor(cr.border);
                        //graphx.draw(poly);
                        g.drawRect(r.x,r.y,r.width,r.height);
                        g.setColor(cr.fill);
                        if (cr.fill != null) {
                            g.fillRect(r.x, r.y, r.width, r.height);
                        }
                    }
                    g.setColor(null);
            //  Paint the Rectangle as the mouse is being dragged

                if (shape != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(ToolSelect.GetBorderColor());
                    Rectangle c = shape.shape.getBounds();
                    if (ToolSelect.GetTool() == "rectangle") {
                        g.setColor(cr.border);
                        g.drawRect(c.x, c.y, c.width, c.height);
                        g.setColor(shape.fill);
                        if (shape.fill != null) {
                            g.fillRect(c.x, c.y, c.width, c.height);
                        }
                    } else if (ToolSelect.GetTool() == "ellipse"){
                        g.setColor(shape.border);
                        g.drawOval(c.x, c.y, c.width, c.height);
                        g.setColor(shape.fill);
                        if (cr.fill != null) {
                            g.fillOval(c.x, c.y, c.width, c.height);
                        }
                    }else if (ToolSelect.GetTool() == "line"){
                        g.drawLine(c.x,c.y, c.width,c.height);
                    }

                }
            }

        }

        public void addRectangle(Rectangle rectangle, Color Bcolor, Color Fcolor, String type) {
            //  Add the Rectangle to the List so it can be repainted
                ColoredRectangle cr = new ColoredRectangle(Bcolor, Fcolor, rectangle, type);
                //System.out.print(color);
                coloredRectangles.add(cr);
                repaint();

        }


        class MyMouseListener extends MouseInputAdapter {

            private Point startPoint;
            private Point pointEnd;

            int vertices = 0; //to store number of vertices
            //use vector instead of array because dynamic structure is required as there can be any number of vertices >= 3
            Vector<Integer> PolyX = new Vector<Integer>(3,1); //to store x coordinates
            Vector<Integer> PolyY = new Vector<Integer> (3,1); //to store y coordinates



            public void mousePressed(MouseEvent e) {

                startPoint = e.getPoint();
                PolyX.addElement(e.getX());
                PolyY.addElement(e.getY());
                shape = new ColoredRectangle(ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), new Rectangle(), ToolSelect.GetTool());
                if (ToolSelect.GetTool() == "plot" || ToolSelect.GetTool() == "line"){
                    shape.shape.setBounds(e.getX(),e.getY(),1,1);
                }

            }

            public void mouseDragged(MouseEvent e) {
                int x = Math.min(startPoint.x, e.getX());
                int y = Math.min(startPoint.y, e.getY());
                int width = Math.abs(startPoint.x - e.getX());
                int height = Math.abs(startPoint.y - e.getY());
                pointEnd = e.getPoint();

                if(ToolSelect.GetTool() != "plot" && ToolSelect.GetTool() != "line") {
                    shape.shape.setBounds(x, y, width, height);
                } else if (ToolSelect.GetTool() == "line"){
                    shape.shape.setBounds(startPoint.x,startPoint.y, pointEnd.x, pointEnd.y);
                }
                repaint();
            }



            public void mouseReleased(MouseEvent e) {
                if(ToolSelect.GetTool() != "polygon") {
                    if (shape.shape.width != 0 || shape.shape.height != 0) {
                        if (ToolSelect.GetTool() == "line") {
                            shape.shape.setBounds(shape.shape.x, shape.shape.y, e.getX(), e.getY());
                        }
                        addRectangle(shape.shape, ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), ToolSelect.GetTool());
                    }
                }else {
                    int[] xPoints = new int[PolyX.size() - 1];
                    int[] yPoints = new int[PolyY.size() - 1];
                    //copy coordinates from vector to array
                    for (int i = 0; i < vertices; i++) {
                        xPoints[i] = PolyX.elementAt(i + 1);
                        shape.shape.setBounds(xPoints[i],yPoints[i],vertices,vertices);
                    }
                    for (int i = 0; i < vertices; i++) {
                        yPoints[i] = PolyY.elementAt(i + 1);
                        shape.shape.setBounds(xPoints[i],yPoints[i],vertices,vertices);
                    }

                    Polygon poly = new Polygon(xPoints, yPoints, vertices);
                    addRectangle(shape.shape,ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), ToolSelect.GetTool());
                    vertices++;

                    shape = null;
                }

            }

        }

        class ColoredRectangle {
            private Color border;
            private Rectangle shape;
            private String type;
            private Color fill;



            public ColoredRectangle(Color border, Color fill, Rectangle shape, String type) {
                this.border = border;
                this.fill = fill;
                this.shape = shape;
                this.type = type;
            }

            public Color getBorder() {
                return border;
            }

            public String getType(){ return type;}

            public Color getFill(){return fill;}

            public void setFill(Color fill){
                this.fill = fill;
            }

            public void setBorder(Color border) {
                this.border = border;
            }

            public Rectangle getRectangle() {
                return shape;
            }
        }
    }
}
