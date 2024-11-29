import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

class DemoViewer {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());


        JSlider headingSlider = new JSlider(-180, 180, 0);
        pane.add(headingSlider, BorderLayout.SOUTH);

        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        JPanel renderPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // rendering
                double size = 100; 
                ArrayList<Square> squares = new ArrayList<Square>();
                squares.add(new Square(new Vertex(size, size, size), 
                                    new Vertex(-size, size, size), 
                                    new Vertex(-size, -size, size), 
                                    new Vertex(size, -size, size), 
                                    Color.WHITE));
                squares.add(new Square(new Vertex(size, size, -size), 
                                    new Vertex(-size, size, -size), 
                                    new Vertex(-size, -size, -size), 
                                    new Vertex(size, -size, -size), 
                                    Color.RED));
                squares.add(new Square(new Vertex(size, size, size), 
                                    new Vertex(size, size, -size), 
                                    new Vertex(-size, size, -size), 
                                    new Vertex(-size, size, size), 
                                    Color.GREEN));
                squares.add(new Square(new Vertex(size, -size, size), 
                                    new Vertex(size, -size, -size), 
                                    new Vertex(-size, -size, -size), 
                                    new Vertex(-size, -size, size), 
                                    Color.BLUE));
                // ArrayList<Triangle> tris = new ArrayList<Triangle>();
                // tris.add(new Triangle(new Vertex(size, size, size),
                //                 new Vertex(-size, -size, size),
                //                 new Vertex(-size, size, -size),
                //                 Color.WHITE));
                // tris.add(new Triangle(new Vertex(size, size, size),
                //                 new Vertex(-size, -size, size),
                //                 new Vertex(size, -size, -size),
                //                 Color.RED));
                // tris.add(new Triangle(new Vertex(-size, size, -size),
                //                 new Vertex(size, -size, -size),
                //                 new Vertex(size, size, size),
                //                 Color.GREEN));
                // tris.add(new Triangle(new Vertex(-size, size, -size),
                //                 new Vertex(size, -size, -size),
                //                 new Vertex(-size, -size, size),
                //                 Color.BLUE));
                // g2.translate(getWidth()/2, getHeight()/2);
                // g2.setColor(Color.WHITE);
                // for (Triangle t : tris) {
                //     Path2D path = new Path2D.Double();
                //     path.moveTo(t.v1.x, t.v1.y);
                //     path.lineTo(t.v2.x, t.v2.y);
                //     path.lineTo(t.v3.x, t.v3.y);
                //     path.closePath();
                //     g2.draw(path);
                // }
                
                Matrix3 headingTransform = Matrix3.MatrixXZ(Math.toRadians(headingSlider.getValue()));
                Matrix3 pitchTransform = Matrix3.MatrixYZ(Math.toRadians(pitchSlider.getValue()));
                Matrix3 transform = headingTransform.multiply(pitchTransform);

                //BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                g2.translate(getWidth() / 2, getHeight() / 2);
                g2.setColor(Color.WHITE);
                for (Square s : squares) {
                    Vertex v1 = transform.transform(s.v1);
                    Vertex v2 = transform.transform(s.v2);
                    Vertex v3 = transform.transform(s.v3);
                    Vertex v4 = transform.transform(s.v4);
                    

                    Path2D path = new Path2D.Double();
                    path.moveTo(v1.x, v1.y);
                    path.lineTo(v2.x, v2.y);
                    path.lineTo(v3.x, v3.y);
                    path.lineTo(v4.x, v4.y);
                    path.closePath();
                    g2.draw(path);
                }
                
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e ->renderPanel.repaint());

        frame.setSize(400, 400);
        frame.setVisible(true);

        
    }
}