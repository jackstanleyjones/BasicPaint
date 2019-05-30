import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.io.FileFilter;
import java.util.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.List;



public class GUIMk1 {
    JPanel panel1;

    /**
     * @param args
     */
    public static void main(String[] args){

        SwingUtilities.invokeLater(GUIMk1::createGUI);

    }

    /**
     * Creates GUI, Adds all elements of the GUI into the main frame
     */
    private static void createGUI(){
        DrawingArea drawingArea = new DrawingArea();
        ToolSelect utiltyBar = new ToolSelect(drawingArea);
        MenuBar menuBar = new MenuBar(drawingArea);
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("GUIMk1");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(utiltyBar, BorderLayout.WEST);
        frame.getContentPane().add(menuBar,BorderLayout.NORTH);
        frame.getContentPane().add(drawingArea);
        //frame.setSize(400, 400);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
        frame.pack();
    }




    /**
     * Defines Tool Select, a class where the tool selection bar is created
     */
    static class ToolSelect extends JPanel implements ActionListener{
        static String toolSelection = "plot";
        final ButtonGroup toolGroup = new ButtonGroup();
        static Color borderColor = Color.BLACK;
        static Color fillColor = null;

        private final DrawingArea drawingArea;

        /**
         * @param drawingArea the main canvas of the application
         */
        ToolSelect(DrawingArea drawingArea){
            this.drawingArea = drawingArea;
            JToolBar toolbar  = new JToolBar(null, JToolBar.VERTICAL);

            ColorChooserButton borderColorChooser = new ColorChooserButton(Color.WHITE);
            borderColorChooser.addColorChangedListener(this::setBorderColor);

            ColorChooserButton fillColorChooser = new ColorChooserButton(Color.WHITE);
            fillColorChooser.addColorChangedListener(this::setFillColor);

            toolbar.add(makeButton("plot"));
            toolbar.add(makeButton("line"));
            toolbar.add(makeButton("rectangle"));
            toolbar.add(makeButton("ellipse"));
            toolbar.add(makeButton("polygon"));
            toolbar.add(makeButton("zoom/pan"));
            toolbar.add(borderColorChooser);
            toolbar.add(fillColorChooser);
            add(toolbar);

        }

        /**
         * @param text The name of each button
         * @return button a button ready to be used by the Toolbar
         */
         private JRadioButton makeButton(String text){
            JRadioButton button = new JRadioButton(text);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setVerticalAlignment(SwingConstants.BOTTOM);
            toolGroup.add(button);


             button.addActionListener( this );
            return button;
         }

        /**
         * @param tool the type of tool selected by each button
         */
         private void setToolSelction(String tool){
            toolSelection = tool;
        }

        /**
         * @param c border color
         */
        private void setBorderColor(Color c){
            borderColor = c;
        }

        /**
         * @return borderColor
         */
        static Color GetBorderColor(){
            return borderColor;
        }

        /**
         * @param c the inner fill color
         */
        private void setFillColor(Color c){
            fillColor = c;
        }

        /**
         * @return fillColor
         */
        static Color GetFillColor(){
            return fillColor;
        }

        /**
         * @return toolSelection - which tool is currently selected
         */
        static String GetTool(){
            return toolSelection;
        }


        /**
         * @param e an action such as a mouseclick
         */
        public void actionPerformed(ActionEvent e) {
            JRadioButton button = (JRadioButton)e.getSource();

            setToolSelction(e.getActionCommand());
            System.out.print(GetTool());

        }
    }

    static class MenuBar extends JPanel implements ActionListener{

        MenuBar(DrawingArea drawingArea){
            JMenuBar menuBar = new JMenuBar();
            JButton openButton = new JButton("open");
            JButton saveButton = new JButton("save");
            openButton.addActionListener( this );
            saveButton.addActionListener( this );

            menuBar.add(openButton);
            menuBar.add(saveButton);
            add(menuBar);


        }
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if(command.equals("open")){
                JFileChooser openFileChooser = new JFileChooser();
                openFileChooser.setAcceptAllFileFilterUsed(false);

                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("VEC Files","vec");
                openFileChooser.addChoosableFileFilter(fileFilter);

                int dialog = openFileChooser.showOpenDialog(null);
                if (dialog == JFileChooser.APPROVE_OPTION) {
                    System.out.print(openFileChooser.getSelectedFile().getAbsolutePath());
                    File newFile = openFileChooser.getSelectedFile();
                    Scanner sc = null;
                    try {
                        sc = new Scanner(newFile);
                    } catch (FileNotFoundException e1) {
                    }
                    DrawingArea.setColoredRectangles(DrawingArea.VecToArray(newFile));


                }
            }
        }
    }



    /**
     * Handles the color chooser buttons that sets the line color and fill color.
     * Adapted from a Stack Overflow answer here: https://stackoverflow.com/a/30433662/9852941
     */
    static class ColorChooserButton extends JButton {

        private Color current;

        /**
         * @param c the current selected color
         */
        ColorChooserButton(Color c) {
            setSelectedColor(c);
            addActionListener(arg0 -> {
                Color newColor = JColorChooser.showDialog(null, "Choose a color", current);
                setSelectedColor(newColor);
                System.out.print(newColor);
            });
        }

        /**
         * @return current - currently selected color
         */
        public Color getSelectedColor() {
            return current;
        }

        /**
         * @param newColor new selected color
         */
        void setSelectedColor(Color newColor) {
            setSelectedColor(newColor, true);
        }

        /**
         * @param newColor new selected color
         * @param notify boolean determining if a notification is sent out to any listeners
         */
        void setSelectedColor(Color newColor, boolean notify) {

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

        /**
         * a listener for any changes in color
         */
        interface ColorChangedListener {
            void colorChanged(Color newColor);
        }

        private final List<ColorChangedListener> listeners = new ArrayList<>();

        /**
         * @param toAdd a new listener
         */
        void addColorChangedListener(ColorChangedListener toAdd) {
            listeners.add(toAdd);
        }

        /**
         * @param main main color of icon
         * @param width width of the icon
         * @param height height of the icon
         * @return ImageIcon(image) a new icon for a button
         */
        ImageIcon createIcon(Color main, int width, int height) {
            BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(main);
            graphics.fillRect(0, 0, width, height);
            graphics.setXORMode(Color.DARK_GRAY);
            graphics.drawRect(0, 0, width-1, height-1);
            image.flush();
            return new ImageIcon(image);
        }
    }




    /**
     * Class which contains the main canvas
     */
    static class DrawingArea extends JPanel {
        private final static int AREA_SIZE = 400;
        public static final ArrayList<ColoredRectangle> coloredRectangles = new ArrayList<>();
        private ColoredRectangle shape = new ColoredRectangle(ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), new Rectangle(), ToolSelect.GetTool(), null);

        public static void setColoredRectangles(ArrayList<ColoredRectangle> rectangles){
            for (ColoredRectangle cr : rectangles) {


                coloredRectangles.add(cr);
            }
        }


        public static ArrayList<ColoredRectangle> VecToArray(File file){
            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e1) {
            }

            Color currentLineColor = Color.BLACK;
            Color currentFillColor = null;

            ArrayList<ColoredRectangle> shapes = new ArrayList<>();

            while (sc.hasNextLine()) {

                ColoredRectangle shape = new ColoredRectangle(currentLineColor, currentFillColor, new Rectangle(), null, null);
                String currentLine = sc.nextLine();
                String[] splitStr = currentLine.split("\\s+");
                if (splitStr[0].equals("PEN")){
                    System.out.print(splitStr[1]);
                    currentLineColor = Color.decode(splitStr[1]);

                } else if(splitStr[0].equals("FILL")){
                    System.out.print(splitStr[1]);
                    if (splitStr[1].equals("OFF")){
                        currentFillColor = null;
                    } else {
                        currentFillColor = Color.decode((splitStr[1]));
                    }


                } else if(splitStr[0].equals("LINE")){
                    shape.type = "line";
                    shape.shape.setBounds((int)(Float.parseFloat(splitStr[1]) * AREA_SIZE),(int)(Float.parseFloat(splitStr[2]) * AREA_SIZE),
                            (int)(Float.parseFloat(splitStr[3]) * AREA_SIZE),(int)(Float.parseFloat(splitStr[4]) *AREA_SIZE));
                    shapes.add(shape);


                } else if(splitStr[0].equals("PLOT")){
                    shape.type = "plot";
                    shape.shape.setBounds((int)(Float.parseFloat(splitStr[1]) * AREA_SIZE),(int)(Float.parseFloat(splitStr[2]) * AREA_SIZE),
                            1,1);
                    shapes.add(shape);

                } else if(splitStr[0].equals("RECTANGLE")){
                    shape.type = "rectangle";
                    shape.shape.setBounds((int)(Float.parseFloat(splitStr[1]) * AREA_SIZE),(int)(Float.parseFloat(splitStr[2]) * AREA_SIZE),
                            ((int)(Float.parseFloat(splitStr[3]) * AREA_SIZE)) - ((int)(Float.parseFloat(splitStr[1]) * AREA_SIZE)),
                            ((int)(Float.parseFloat(splitStr[4]) * AREA_SIZE)) - ((int)(Float.parseFloat(splitStr[2]) * AREA_SIZE)));
                    shapes.add(shape);

                } else if(splitStr[0].equals("ELLIPSE")){
                    shape.type = "ellipse";
                    System.out.println((int)(Float.parseFloat(splitStr[3]) * AREA_SIZE));
                    shape.shape.setBounds((int)(Float.parseFloat(splitStr[1]) * AREA_SIZE),(int)(Float.parseFloat(splitStr[2]) * AREA_SIZE),
                            ((int)(Float.parseFloat(splitStr[3]) * AREA_SIZE)) - ((int)(Float.parseFloat(splitStr[1]) * AREA_SIZE)),
                            ((int)(Float.parseFloat(splitStr[4]) * AREA_SIZE)) - ((int)(Float.parseFloat(splitStr[2]) * AREA_SIZE)));
                    shapes.add(shape);

                } else if(splitStr[0].equals("POLYGON")){

                }
            }
            return shapes;
        }

        /**
         * A new drawing area
         */
        DrawingArea() {
            setBackground(Color.WHITE);
            MyMouseListener ml = new MyMouseListener();
            addMouseListener(ml);
            addMouseMotionListener(ml);
            addMouseWheelListener(ml);


        }




        /**
         * @return the prefered size for the canvas
         */
        @Override
        public Dimension getPreferredSize() {
            return isPreferredSizeSet() ?
                    super.getPreferredSize() : new Dimension(AREA_SIZE,AREA_SIZE);
        }

        private double zoomFactor = 1;
        private double prevZoomFactor = 1;
        private boolean zoomer;
        private double xOffset = 0;
        private double yOffset = 0;
        private boolean dragger;
        private boolean released;
        private int xDiff;
        private int yDiff;
        private Point sPoint;
        AffineTransform at = new AffineTransform();

        /**
         * @param graphic main graphic
         */
        @Override
        public void paintComponent(Graphics graphic) {

            super.paintComponent(graphic);
            Graphics2D g2 = (Graphics2D) graphic;
            if (zoomer) {
                AffineTransform at = new AffineTransform();

                double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
                double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

                double zoomDiv = zoomFactor / prevZoomFactor;

                xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
                yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

                at.translate(xOffset, yOffset);
                at.scale(zoomFactor, zoomFactor);
                prevZoomFactor = zoomFactor;
                g2.transform(at);
                zoomer = false;
            }

            if (dragger) {
                at.translate(xOffset + xDiff, yOffset + yDiff);
                at.scale(zoomFactor, zoomFactor);
                g2.transform(at);

                if (released) {
                    xOffset += xDiff;
                    yOffset += yDiff;
                    dragger = false;
                    revalidate();

                }


            }

            //  Custom code to paint all the Rectangles from the List



            graphic.setColor(getForeground());

                for (DrawingArea.ColoredRectangle cr : coloredRectangles) {
                    //g.setColor(getForeground());
                    Rectangle r = cr.getRectangle().getBounds();
                    //g.setColor(ToolSelect.GetFillColor());
                    if (Objects.equals(cr.getType(), "rectangle")) {
                        graphic.setColor(cr.border);
                        graphic.drawRect(r.x, r.y, r.width, r.height);
                        graphic.setColor(cr.fill);
                        if (cr.fill != null) {
                            graphic.fillRect(r.x, r.y, r.width, r.height);
                        }
                    } else if (Objects.equals(cr.getType(), "ellipse")){
                        graphic.setColor(cr.border);
                        graphic.drawOval(r.x, r.y, r.width, r.height);
                        graphic.setColor(cr.fill);
                        if (cr.fill != null) {
                            graphic.fillOval(r.x, r.y, r.width, r.height);
                        }
                    } else if (Objects.equals(cr.getType(), "plot")){
                        graphic.setColor(cr.border);
                        graphic.drawOval(r.x, r.y, r.width, r.height);
                    } else if (Objects.equals(cr.getType(), "line")){
                        graphic.setColor(cr.border);
                        graphic.drawLine(r.x,r.y,r.width, r.height);

                    }else if (Objects.equals(cr.getType(), "polygon")){
                        if (cr.poly != null) {
                            System.out.print(cr.poly.npoints);
                            graphic.setColor(cr.border);
                            graphic.drawPolygon(cr.poly);
                            // graphic.drawRect(r.x,r.y,r.width,r.height);
                            graphic.setColor(cr.fill);
                            if (cr.fill != null) {
                                graphic.fillPolygon(cr.poly);
                            }
                        }
                    }
                    graphic.setColor(null);
            //  Paint the Rectangle as the mouse is being dragged

                if (shape != null) {
                    Graphics2D g2d = (Graphics2D) graphic;
                    g2d.setColor(ToolSelect.GetBorderColor());
                    Rectangle c = shape.shape.getBounds();
                    if (Objects.equals(ToolSelect.GetTool(), "rectangle")) {
                        graphic.setColor(cr.border);
                        graphic.drawRect(c.x, c.y, c.width, c.height);
                        graphic.setColor(shape.fill);
                        if (shape.fill != null) {
                            graphic.fillRect(c.x, c.y, c.width, c.height);
                        }
                    } else if (Objects.equals(ToolSelect.GetTool(), "ellipse")){
                        graphic.setColor(shape.border);
                        graphic.drawOval(c.x, c.y, c.width, c.height);
                        graphic.setColor(shape.fill);
                        if (cr.fill != null) {
                            graphic.fillOval(c.x, c.y, c.width, c.height);
                        }
                    }else if (Objects.equals(ToolSelect.GetTool(), "line")){
                        graphic.drawLine(c.x,c.y, c.width,c.height);
                    }
                    else if (Objects.equals(ToolSelect.GetTool(), "polygon")){
                        if (cr.poly != null) {
                            graphic.setColor(shape.border);
                            graphic.drawPolygon(shape.poly);
                            // graphic.drawRect(r.x,r.y,r.width,r.height);
                            graphic.setColor(shape.fill);
                            if (shape.fill != null) {
                                graphic.fillPolygon(shape.poly);
                            }
                        }
                    }

                }
            }

        }

        /**
         * @param rectangle the main shape, including its size/ coords etc.
         * @param Bcolor Border color of shape
         * @param Fcolor Fill color of shape
         * @param type The type of shape
         */
        void addRectangle(Rectangle rectangle, Color Bcolor, Color Fcolor, String type, Polygon poly) {
            //  Add the Rectangle to the List so it can be repainted
                ColoredRectangle cr = new ColoredRectangle(Bcolor, Fcolor, rectangle, type, poly);
                //System.out.print(color);
                coloredRectangles.add(cr);
                repaint();

        }


        /**
         * Class that checks for any mouse action in the canvas
         */
        class MyMouseListener extends MouseInputAdapter {

            private Point startPoint;
            private Point pointEnd;
            int vertices = 0; //to store number of vertices
            //use vector instead of array because dynamic structure is required as there can be any number of vertices >= 3
            final Vector<Integer> PolyX = new Vector<>(3, 1); //to store x coordinates
            final Vector<Integer> PolyY = new Vector<>(3, 1); //to store y coordinates








            /**
             * @param e The specific mouse event
             */
            public void mousePressed(MouseEvent e) {

                if(ToolSelect.GetTool() == "zoom/pan"){
                    released = false;
                    sPoint = MouseInfo.getPointerInfo().getLocation();
                }else {

                    startPoint = e.getPoint();
                    shape = new ColoredRectangle(ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), new Rectangle(), ToolSelect.GetTool(), null);
                    if (Objects.equals(ToolSelect.GetTool(), "plot") || Objects.equals(ToolSelect.GetTool(), "line")) {
                        shape.shape.setBounds(e.getX(), e.getY(), 1, 1);
                    }
                }

            }

            /**
             * @param e The specific mouse event
             */
            public void mouseDragged(MouseEvent e) {

                if(ToolSelect.GetTool() == "zoom/pan") {
                    Point curPoint = e.getLocationOnScreen();
                    xDiff = curPoint.x - sPoint.x;
                    yDiff = curPoint.y - sPoint.y;

                    dragger = true;
                    repaint();
                }else {

                    int x = Math.min(startPoint.x, e.getX());
                    int y = Math.min(startPoint.y, e.getY());
                    int width = Math.abs(startPoint.x - e.getX());
                    int height = Math.abs(startPoint.y - e.getY());
                    pointEnd = e.getPoint();

                    if (!Objects.equals(ToolSelect.GetTool(), "plot") && !Objects.equals(ToolSelect.GetTool(), "line") && !Objects.equals(ToolSelect.GetTool(), "polygon")) {
                        shape.shape.setBounds(x, y, width, height);
                    } else if (Objects.equals(ToolSelect.GetTool(), "line")) {
                        shape.shape.setBounds(startPoint.x, startPoint.y, pointEnd.x, pointEnd.y);
                    }
                    repaint();
                }
            }


            /**
             * @param e The specific mouse event
             */
            public void mouseReleased(MouseEvent e) {

                if(ToolSelect.GetTool() == "zoom/pan"){
                    released = true;
                    repaint();
                }else {

                    if (!Objects.equals(ToolSelect.GetTool(), "polygon")) {
                        if (shape.shape.width != 0 || shape.shape.height != 0) {
                            if (Objects.equals(ToolSelect.GetTool(), "line")) {
                                shape.shape.setBounds(shape.shape.x, shape.shape.y, e.getX(), e.getY());
                            }
                            addRectangle(shape.shape, ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), ToolSelect.GetTool(), null);
                        }
                    } else {

                        //copy coordinates from vector to array
                        /*

                         */

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            PolyX.addElement(e.getX());
                            PolyY.addElement(e.getY());
                            vertices++;
                            shape.shape.setBounds(e.getX(), e.getY(), 1, 1);
                            addRectangle(shape.shape, ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), ToolSelect.GetTool(), null);

                        } else if (SwingUtilities.isRightMouseButton(e)) {

                            int[] xPoints = new int[PolyX.size()];
                            int[] yPoints = new int[PolyY.size()];


                            for (int i = 0; i < vertices; i++) {
                                xPoints[i] = PolyX.elementAt(i);
                            }
                            for (int i = 0; i < vertices; i++) {
                                yPoints[i] = PolyY.elementAt(i);
                                shape.shape.setBounds(xPoints[i], yPoints[i], vertices, vertices);
                            }
                            Polygon poly = new Polygon(xPoints, yPoints, vertices);
                            shape.poly = poly;

                            PolyX.clear();
                            PolyY.clear();
                            vertices = 0;

                        }
                        addRectangle(shape.shape, ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), ToolSelect.GetTool(), shape.poly);

                    }
                    shape = null;
                }



            }
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (ToolSelect.GetTool() == "zoom/pan") {
                    zoomer = true;
                    //Zoom in
                    if (e.getWheelRotation() < 0) {
                        zoomFactor /= 1.1;
                        repaint();
                    }
                    //Zoom out
                    if (e.getWheelRotation() > 0) {
                        zoomFactor *= 1.1;
                        repaint();
                    }
                }
            }
        }

        /**
         *
         */
        static class ColoredRectangle {
            private Color border;
            private final Rectangle shape;
            private String type;
            private Color fill;
            private Polygon poly;


            /**
             * @param border Shape border color
             * @param fill Shape fill color
             * @param shape The main shape, including its size/ coords etc.
             * @param type The type of shape
             */
            ColoredRectangle(Color border, Color fill, Rectangle shape, String type, Polygon poly) {
                this.border = border;
                this.fill = fill;
                this.shape = shape;
                this.type = type;
                this.poly = poly;
            }

            /**
             * @return border- the border color
             */
            public Color getBorder() {
                return border;
            }

            /**
             * @return type- what type of shape it is
             */
            String getType(){ return type;}

            /**
             * @return fill- the fill color of the shape
             */
            public Color getFill(){return fill;}

            /**
             * @param fill the fill color of the shape
             */
            public void setFill(Color fill){
                this.fill = fill;
            }

            /**
             * @param border the border color
             */
            public void setBorder(Color border) {
                this.border = border;
            }

            /**
             * @return The main shape, including its size/ coords etc.
             */
            Rectangle getRectangle() {
                return shape;
            }

            Polygon getPoly(){
                return poly;
            }
        }
    }
}
