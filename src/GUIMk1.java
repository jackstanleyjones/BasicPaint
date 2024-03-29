
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
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
        ToolSelect utilityBar = new ToolSelect();
        MenuBar menuBar = new MenuBar();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("GUIMk1");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(utilityBar, BorderLayout.WEST);
        frame.getContentPane().add(menuBar,BorderLayout.NORTH);
        frame.getContentPane().add(drawingArea);
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

        /**
         */
        ToolSelect(){
            JToolBar toolbar  = new JToolBar(null, JToolBar.VERTICAL);
            JLabel penLabel = new JLabel("Pen colour:");
            JLabel fillLabel = new JLabel("Fill Colour:");

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
            toolbar.add(penLabel);
            toolbar.add(borderColorChooser);
            toolbar.add(fillLabel);
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

    /**
     *
     */
    static class MenuBar extends JPanel implements ActionListener{

        MenuBar(){
            JMenuBar menuBar = new JMenuBar();
            JMenuItem openButton = new JMenuItem("open");
            JMenuItem saveButton = new JMenuItem("save");
            JMenuItem undoButton = new JMenuItem("undo");
            undoButton.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_Z, InputEvent.CTRL_MASK));

            undoButton.addActionListener(this);
            openButton.addActionListener(this);
            saveButton.addActionListener(this);


            menuBar.add(openButton);
            menuBar.add(saveButton);
            menuBar.add(undoButton);
            add(menuBar);


        }

        /**
         * @param e a user action
         */
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "open": {
                    JFileChooser openFileChooser = new JFileChooser();
                    openFileChooser.setAcceptAllFileFilterUsed(false);

                    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("VEC Files", "vec");
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
                    break;
                }
                case "save": {
                    JFileChooser saveFileChooser = new JFileChooser();
                    saveFileChooser.setAcceptAllFileFilterUsed(false);

                    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("VEC Files", "vec");
                    saveFileChooser.addChoosableFileFilter(fileFilter);

                    int dialog = saveFileChooser.showOpenDialog(null);
                    if (dialog == JFileChooser.APPROVE_OPTION) {
                        try {
                            DrawingArea.ArrayToVec(Objects.requireNonNull(DrawingArea.getColoredRectangles()), saveFileChooser.getSelectedFile());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }


                    break;
                }
                case "undo":
                    DrawingArea.deleteLastRectangle();
                    repaint();


                    break;
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
            setIcon(createIcon(current));
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
         * @return ImageIcon(image) a new icon for a button
         */
        ImageIcon createIcon(Color main) {
            BufferedImage image = new BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(main);
            graphics.fillRect(0, 0, 16, 16);
            graphics.setXORMode(Color.DARK_GRAY);
            graphics.drawRect(0, 0, 16 -1, 16 -1);
            image.flush();
            return new ImageIcon(image);
        }
    }




    /**
     * Class which contains the main canvas
     */
    static class DrawingArea extends JPanel {
        private final static int AREA_SIZE = 400;
        static final ArrayList<ColoredRectangle> coloredRectangles = new ArrayList<>();
        private ColoredRectangle shape = new ColoredRectangle(ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), new Rectangle(), ToolSelect.GetTool(), null);

        /**
         * @param rectangles the current array of all shapes
         */
        static void setColoredRectangles(ArrayList<ColoredRectangle> rectangles){
            coloredRectangles.addAll(rectangles);
        }


        /**
         * deletes the last drawn rectangle
         */
        static void deleteLastRectangle(){
            System.out.println(coloredRectangles.size());
            coloredRectangles.remove(coloredRectangles.size() - 1);
            //revalidate();
        }

        /**
         * @return coloredRectangles, the current arry of all rectangles drawn
         */
        static ArrayList<ColoredRectangle> getColoredRectangles(){
            if (!(coloredRectangles.size() == 0)) {
                return coloredRectangles;
            } else return null;
        }


        /**
         * @param file a VEC file with drawing instructions
         * @return shapeArray, the shapes specified by the VEC file in an array
         */
        static ArrayList<ColoredRectangle> VecToArray(File file){
            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException ignored) {
            }

            Color currentLineColor = Color.BLACK;
            Color currentFillColor = null;

            ArrayList<ColoredRectangle> shapes = new ArrayList<>();

            while (Objects.requireNonNull(sc).hasNextLine()) {

                ColoredRectangle shape = new ColoredRectangle(currentLineColor, currentFillColor, new Rectangle(), null, null);
                String currentLine = sc.nextLine();
                String[] splitStr = currentLine.split("\\s+");
                switch (splitStr[0]) {
                    case "PEN":
                        currentLineColor = Color.decode(splitStr[1]);

                        break;
                    case "FILL":
                        if (splitStr[1].equals("OFF")) {
                            currentFillColor = null;
                        } else {
                            currentFillColor = Color.decode((splitStr[1]));
                        }


                        break;
                    case "LINE":
                        shape.type = "line";
                        shape.shape.setBounds((int) (Float.parseFloat(splitStr[1]) * AREA_SIZE), (int) (Float.parseFloat(splitStr[2]) * AREA_SIZE),
                                (int) (Float.parseFloat(splitStr[3]) * AREA_SIZE), (int) (Float.parseFloat(splitStr[4]) * AREA_SIZE));
                        shapes.add(shape);


                        break;
                    case "PLOT":
                        shape.type = "plot";
                        shape.shape.setBounds((int) (Float.parseFloat(splitStr[1]) * AREA_SIZE), (int) (Float.parseFloat(splitStr[2]) * AREA_SIZE),
                                1, 1);
                        shapes.add(shape);

                        break;
                    case "RECTANGLE":
                        shape.type = "rectangle";
                        shape.shape.setBounds((int) (Float.parseFloat(splitStr[1]) * AREA_SIZE), (int) (Float.parseFloat(splitStr[2]) * AREA_SIZE),
                                ((int) (Float.parseFloat(splitStr[3]) * AREA_SIZE)) - ((int) (Float.parseFloat(splitStr[1]) * AREA_SIZE)),
                                ((int) (Float.parseFloat(splitStr[4]) * AREA_SIZE)) - ((int) (Float.parseFloat(splitStr[2]) * AREA_SIZE)));
                        shapes.add(shape);

                        break;
                    case "ELLIPSE":
                        shape.type = "ellipse";
                        shape.shape.setBounds((int) (Float.parseFloat(splitStr[1]) * AREA_SIZE), (int) (Float.parseFloat(splitStr[2]) * AREA_SIZE),
                                ((int) (Float.parseFloat(splitStr[3]) * AREA_SIZE)) - ((int) (Float.parseFloat(splitStr[1]) * AREA_SIZE)),
                                ((int) (Float.parseFloat(splitStr[4]) * AREA_SIZE)) - ((int) (Float.parseFloat(splitStr[2]) * AREA_SIZE)));
                        shapes.add(shape);

                        break;
                    case "POLYGON":
                        shape.type = "polygon";
                        int[] PolyX = new int[(splitStr.length - 1) / 2];
                        int[] PolyY = new int[(splitStr.length - 1) / 2];
                        int vertices = 0;

                        for (int i = 1; i < splitStr.length; i += 2) {
                            PolyX[vertices] = ((int) (Float.parseFloat(splitStr[i]) * AREA_SIZE));
                            PolyY[vertices] = ((int) (Float.parseFloat(splitStr[i + 1]) * AREA_SIZE));
                            vertices++;
                        }
                        Polygon poly = new Polygon(PolyX, PolyY, vertices);
                        shape.poly = poly;
                        shapes.add(shape);


                        break;
                }
            }
            return shapes;
        }

        /**
         * @param array the current array of all shapes
         * @param file a VEC file with instructions to draw all shapes
         * @throws IOException
         */
        static void ArrayToVec(ArrayList<ColoredRectangle> array, File file) throws IOException{
            FileWriter newFile = new FileWriter(file + ".vec");
            Color currentBorderColor = null;
            Color currentFillColor = null;

            for (ColoredRectangle cr : array){
                System.out.print(cr);
                String fillColor;
                String borderColor;
                Rectangle currentRectangle = cr.getRectangle();
                if (!(cr.fill == currentFillColor)){
                    //if (cr.fill.equals(null)){
                    //    fillColor = "FILL OFF";
                    //} else {
                        fillColor = "FILL " + "#" + Integer.toHexString(cr.fill.getRGB()).substring(2);
                    //}
                    currentFillColor = cr.fill;
                    newFile.write(fillColor + System.lineSeparator());
                }

                if (!cr.border.equals(currentBorderColor)){
                    borderColor = "PEN " + "#" + Integer.toHexString(cr.border.getRGB()).substring(2);
                    currentBorderColor = cr.border;
                    newFile.write(borderColor+ System.lineSeparator());
                }

                switch (cr.type) {
                    case "plot":
                        String plot = "PLOT " + ((float) currentRectangle.x / AREA_SIZE)
                                + " " + ((float) currentRectangle.y / AREA_SIZE);
                        newFile.write(plot + System.lineSeparator());
                        break;
                    case "line":
                        String line = "LINE " + ((float) currentRectangle.x / AREA_SIZE)
                                + " " + ((float) currentRectangle.y / AREA_SIZE)
                                + " " + ((float) currentRectangle.width / AREA_SIZE)
                                + " " + ((float) currentRectangle.height / AREA_SIZE);
                        newFile.write(line + System.lineSeparator());
                        break;
                    case "rectangle":
                        String rectangle = "RECTANGLE " + ((float) currentRectangle.x / AREA_SIZE)
                                + " " + ((float) currentRectangle.y / AREA_SIZE)
                                + " " + (((float) currentRectangle.width / AREA_SIZE) + ((float) currentRectangle.x / AREA_SIZE))
                                + " " + (((float) currentRectangle.height / AREA_SIZE) + ((float) currentRectangle.y / AREA_SIZE));
                        newFile.write(rectangle + System.lineSeparator());
                        break;
                    case "ellipse":
                        String ellipse = "ELLIPSE " + ((float) currentRectangle.x / AREA_SIZE)
                                + " " + ((float) currentRectangle.y / AREA_SIZE)
                                + " " + (((float) currentRectangle.width / AREA_SIZE) + ((float) currentRectangle.x / AREA_SIZE))
                                + " " + (((float) currentRectangle.height / AREA_SIZE) + ((float) currentRectangle.y / AREA_SIZE));
                        newFile.write(ellipse + System.lineSeparator());
                        break;
                    case "polygon":
                        Polygon currentPoly = cr.poly;
                        if (currentPoly != null) {
                            StringBuilder polygon = new StringBuilder("POLYGON");
                            for (int i = 0; i < currentPoly.xpoints.length; i++) {
                                String point = " " + ((float) currentPoly.xpoints[i] / AREA_SIZE)
                                        + " " + ((float) currentPoly.ypoints[i] / AREA_SIZE);
                                polygon.append(point);
                            }
                            newFile.write(polygon + System.lineSeparator());
                        }
                        break;
                }

            }
            newFile.close();
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
        final AffineTransform at = new AffineTransform();

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

            /* Custom code to paint all the Rectangles from the List */

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

                if(Objects.equals(ToolSelect.GetTool(), "zoom/pan")){
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

                if(Objects.equals(ToolSelect.GetTool(), "zoom/pan")) {
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

                if(Objects.equals(ToolSelect.GetTool(), "zoom/pan")){
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
                            shape.poly = new Polygon(xPoints, yPoints, vertices);

                            PolyX.clear();
                            PolyY.clear();
                            vertices = 0;

                        }
                        addRectangle(shape.shape, ToolSelect.GetBorderColor(), ToolSelect.GetFillColor(), ToolSelect.GetTool(), shape.poly);

                    }
                    shape = null;
                }



            }

            /**
             * @param e a mousewheel action
             */
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (Objects.equals(ToolSelect.GetTool(), "zoom/pan")) {
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
         * a single shape
         */
        static class ColoredRectangle {
            private final Color border;
            private final Rectangle shape;
            private String type;
            private final Color fill;
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
             * @return type- what type of shape it is
             */
            String getType(){ return type;}

            /**
             * @return The main shape, including its size/ coords etc.
             */
            Rectangle getRectangle() {
                return shape;
            }

        }
    }
}
