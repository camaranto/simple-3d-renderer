import java.awt.Color;
import java.util.ArrayList;

class Vertex {
    double x;
    double y;
    double z;
    Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double[] getPoints(){
        return new double[] {this.x, this.y, this.z};
    }

    public int[] getPointsInt() {
        return new int[]{(int)this.x, (int)this.y, (int)this.z};
    }

    public double dotProduct(Vertex other){
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }
}

class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Color color;
    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
}

class Square {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Vertex v4;
    Color color;

    public Square(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.color = color;
    }
}

class Matrix3 {
    double[] values;
    Matrix3(double[] values) {
        this.values = values;
    }
    Matrix3 multiply(Matrix3 other) {
        double[] result = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] +=
                        this.values[row * 3 + i] * other.values[i * 3 + col];
                }
            }
        }
        return new Matrix3(result);
    }
    Vertex transform(Vertex in) {
        return new Vertex(
            in.x * values[0] + in.y * values[3] + in.z * values[6],
            in.x * values[1] + in.y * values[4] + in.z * values[7],
            in.x * values[2] + in.y * values[5] + in.z * values[8]
        );
    }
    static Matrix3 MatrixXZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Matrix3 xzMatrix = new Matrix3(new double[]{
            cos, 0, -sin,
            0,   1,   0,
            sin, 0,  cos
        });
        return xzMatrix;
    }

    static Matrix3 MatrixYZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Matrix3 yzMatrix = new Matrix3(new double[]{
            1,   0,   0,
            0,  cos, sin,
            0, -sin, cos
        });
        return yzMatrix;
    }
    
}

class UtilsMath {
    UtilsMath() {}

    public static double dotProduct(Vertex a, Vertex b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static double norm(Vertex v){
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }
}

class Shading {
    Shading() {}

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);
        return new Color(red, green, blue);
    }

    public static Vertex calculateLightVector(double theta, double phi){
        double sinPhi = Math.sin(phi);
        double x = sinPhi * Math.cos(theta);
        double y = sinPhi * Math.sin(theta);
        return new Vertex(x, y, Math.cos(phi));
    }

    public static ArrayList<Triangle> inflate(ArrayList<Triangle> tris){
        ArrayList<Triangle> result = new ArrayList<Triangle>();
        for (Triangle t : tris) {
            Vertex m1 = new Vertex((t.v1.x + t.v2.x)/2, (t.v1.y + t.v2.y)/2, (t.v1.z + t.v2.z)/2);
            Vertex m2 = new Vertex((t.v2.x + t.v3.x)/2, (t.v2.y + t.v3.y)/2, (t.v2.z + t.v3.z)/2);
            Vertex m3 = new Vertex((t.v1.x + t.v3.x)/2, (t.v1.y + t.v3.y)/2, (t.v1.z + t.v3.z)/2);
            result.add(new Triangle(t.v1, m1, m3, t.color));
            result.add(new Triangle(t.v2, m1, m2, t.color));
            result.add(new Triangle(t.v3, m2, m3, t.color));
            result.add(new Triangle(m1, m2, m3, t.color));
        }

        for (Triangle t: result) {
            for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3}) {
                double l = UtilsMath.norm(v) / Math.sqrt(30000);
                v.x /= l;
                v.y /= l;
                v.z /= l;
            }
        }
        return result;
    }
}