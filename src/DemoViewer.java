import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;


class DemoViewer {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple 3D renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container pane = frame.getContentPane();
        JPanel controlPanel = new JPanel();

        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(400, 400));

        JSlider zoom = new JSlider( SwingConstants.HORIZONTAL, 50, 200, 100);
        JSlider pitchSlider = new JSlider(SwingConstants.HORIZONTAL, -90, 90, 0);
        JSlider headingSlider = new JSlider(SwingConstants.HORIZONTAL,-180, 180, 0);
        JSlider phiSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 90, 0);
        JSlider thetaSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 180, 0);
        JLabel textAxisY = new JLabel("Rotation y-axis");
        JLabel textAxisX = new JLabel("Rotation x-axis");
        JLabel textZoom = new JLabel("Zoom");
        JLabel textTheta = new JLabel("θ");
        JLabel textPhi = new JLabel("ϕ");
        Function<Integer, Component> createRigidArea = x -> Box.createRigidArea(new Dimension(0, x));


        controlPanel.add(createRigidArea.apply(20));
        controlPanel.add(textAxisX);
        controlPanel.add(headingSlider);
        controlPanel.add(createRigidArea.apply(20));
        controlPanel.add(textAxisY);
        controlPanel.add(pitchSlider);
        controlPanel.add(createRigidArea.apply(20));
        controlPanel.add(textZoom);
        controlPanel.add(zoom);
        controlPanel.add(createRigidArea.apply(30));
        controlPanel.add(textPhi);
        controlPanel.add(phiSlider);
        controlPanel.add(createRigidArea.apply(20));
        controlPanel.add(textTheta);
        controlPanel.add(thetaSlider);

        Vertex triangleXx = new Vertex(60, 60, 65);
        Vertex triangleXy = new Vertex(17, 23, 20);
        Vertex triangleYx = new Vertex(17, 23, 20);
        Vertex triangleYy = new Vertex(60, 60, 65);

        JPanel renderPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                Path2D lineX = new Path2D.Double();
                Path2D lineY = new Path2D.Double();
                // Path2D lineZ = new Path2D.Double();

                lineX.moveTo(20, 20);
                lineY.moveTo(20, 20);
                lineX.lineTo(60, 20);
                lineY.lineTo(20, 60);


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

                Vertex lightVector = Shading.calculateLightVector(Math.toRadians(thetaSlider.getValue()), Math.toRadians(phiSlider.getValue()));

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

                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    Vertex norm = new Vertex(
                        ab.y * ac.z - ab.z * ac.y,
                        ab.z * ac.x - ab.x * ac.z,
                        ab.x * ac.y - ab.y * ac.x
                    );
                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;


                    // TODO: implement the real calculation of the cosine angle
                    double angleCos = Math.abs(UtilsMath.dotProduct(lightVector, norm));

                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() -1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                // img.setRGB(x, y, t.color.getRGB());
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    img.setRGB(x, y, Shading.getShade(t.color, angleCos).getRGB());
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
                g2.setColor(Color.RED);
                g2.drawPolygon(triangleYx.getPointsInt(), triangleYy.getPointsInt(), 3);
                g2.draw(lineY);
                g2.setColor(Color.GREEN);
                g2.draw(lineX);
                g2.drawPolygon(triangleXx.getPointsInt(), triangleXy.getPointsInt(), 3);
                
            }
        };

        renderPanel.setPreferredSize(new Dimension(400, 400));
        pane.add(renderPanel, BorderLayout.WEST);
        pane.add(controlPanel, BorderLayout.EAST);

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e ->renderPanel.repaint());
        zoom.addChangeListener(e -> renderPanel.repaint());
        thetaSlider.addChangeListener(e -> renderPanel.repaint());
        phiSlider.addChangeListener(e -> renderPanel.repaint());
        

        frame.setSize(800, 400);
        frame.setVisible(true);

        
    }
}