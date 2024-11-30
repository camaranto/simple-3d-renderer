import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
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

        JSlider zoom = new JSlider( SwingConstants.VERTICAL, 50, 200, 100);
        pane.add(zoom, BorderLayout.WEST);

        JPanel renderPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // rendering
                double size = zoom.getValue(); 
                // ArrayList<Square> squares = new ArrayList<Square>();
                // squares.add(new Square(new Vertex(size, size, size), 
                //                     new Vertex(-size, size, size), 
                //                     new Vertex(-size, -size, size), 
                //                     new Vertex(size, -size, size), 
                //                     Color.WHITE));
                // squares.add(new Square(new Vertex(size, size, -size), 
                //                     new Vertex(-size, size, -size), 
                //                     new Vertex(-size, -size, -size), 
                //                     new Vertex(size, -size, -size), 
                //                     Color.RED));
                // squares.add(new Square(new Vertex(size, size, size), 
                //                     new Vertex(size, size, -size), 
                //                     new Vertex(-size, size, -size), 
                //                     new Vertex(-size, size, size), 
                //                     Color.GREEN));
                // squares.add(new Square(new Vertex(size, -size, size), 
                //                     new Vertex(size, -size, -size), 
                //                     new Vertex(-size, -size, -size), 
                //                     new Vertex(-size, -size, size), 
                //                     Color.BLUE));
                ArrayList<Triangle> tris = new ArrayList<Triangle>();
                tris.add(new Triangle(new Vertex(size, size, size),
                                new Vertex(-size, -size, size),
                                new Vertex(-size, size, -size),
                                Color.WHITE));
                tris.add(new Triangle(new Vertex(size, size, size),
                                new Vertex(-size, -size, size),
                                new Vertex(size, -size, -size),
                                Color.RED));
                tris.add(new Triangle(new Vertex(-size, size, -size),
                                new Vertex(size, -size, -size),
                                new Vertex(size, size, size),
                                Color.GREEN));
                tris.add(new Triangle(new Vertex(-size, size, -size),
                                new Vertex(size, -size, -size),
                                new Vertex(-size, -size, size),
                                Color.BLUE));
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

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];

                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }

                // g2.translate(getWidth() / 2, getHeight() / 2);
                // g2.setColor(Color.WHITE);
                for (Triangle t : tris) {
                    Vertex v1 = transform.transform(t.v1);
                    Vertex v2 = transform.transform(t.v2);
                    Vertex v3 = transform.transform(t.v3);

                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;                    
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;

                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() -1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            double b1 = 
                              ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 =
                              ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 =
                              ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                // img.setRGB(x, y, t.color.getRGB());
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    img.setRGB(x, y, t.color.getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }
                    // Path2D path = new Path2D.Double();
                    // path.moveTo(v1.x, v1.y);
                    // path.lineTo(v2.x, v2.y);
                    // path.lineTo(v3.x, v3.y);
                    // path.closePath();
                }
                g2.drawImage(img, 0, 0, null);
                
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e ->renderPanel.repaint());
        zoom.addChangeListener(e -> renderPanel.repaint());

        frame.setSize(400, 400);
        frame.setVisible(true);

        
    }
}