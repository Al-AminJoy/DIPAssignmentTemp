package edges;

import filters.Gaussian;
import utils.CommonTask;

import java.awt.image.BufferedImage;

public class CannyOperation {
    private static final int GAUSSIAN_RADIUS = 7;
    private static final double GAUSSIAN_INTENSITY = 1.5;

    private static int stDev;
    private static int mean;
    private static int numDev;
    private static double tHi;
    private static double tLo;
    private static double tFract;
    private static int[][] dir;
    private static int[][] gx;
    private static int[][] gy;
    private static double[][] mag;


    public static BufferedImage CannyEdges(BufferedImage img, int numberDeviations, double fract) {
        int[][] raw = null;
        int[][] blurred = null;
        BufferedImage edges = null;
        numDev = numberDeviations;
        tFract = fract;

        if (img != null && numberDeviations > 0 && fract > 0) {
            raw = CommonTask.GSArray(img);
            blurred = Gaussian.BlurGS(raw, GAUSSIAN_RADIUS, GAUSSIAN_INTENSITY);
            gx = SobelOperation.Horizontal(blurred);
            gy = SobelOperation.Vertical(blurred);

            Magnitude();
            Direction();
            Suppression();

            edges = CommonTask.GSImg(Hysteresis());
        }

        return edges;
    }


    private static void Magnitude() {
        double sum = 0;
        double var = 0;
        int height = gx.length;
        int width = gx[0].length;
        double pixelTotal = height * width;
        mag = new double[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                mag[r][c] = Math.sqrt(gx[r][c] * gx[r][c] + gy[r][c] * gy[r][c]);

                sum += mag[r][c];
            }
        }

        mean = (int) Math.round(sum / pixelTotal);

        //Get variance
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double diff = mag[r][c] - mean;

                var += (diff * diff);
            }
        }

        stDev = (int) Math.sqrt(var / pixelTotal);
    }


    private static void Direction() {
        int height = gx.length;
        int width = gx[0].length;
        double piRad = 180 / Math.PI;
        dir = new int[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double angle = Math.atan2(gy[r][c], gx[r][c]) * piRad;

                //Check for negative angles
                if (angle < 0) {
                    angle += 360.;
                }
               //find directions
                if (angle <= 22.5 || (angle >= 157.5 && angle <= 202.5) || angle >= 337.5) {
                    dir[r][c] = 0;
                } else if ((angle >= 22.5 && angle <= 67.5) || (angle >= 202.5 && angle <= 247.5)) {
                    dir[r][c] = 45;
                } else if ((angle >= 67.5 && angle <= 112.5) || (angle >= 247.5 && angle <= 292.5)) {
                    dir[r][c] = 90;
                } else {
                    dir[r][c] = 135;
                }
            }
        }
    }


    private static void Suppression() {
        int height = mag.length - 1;
        int width = mag[0].length - 1;

        for (int r = 1; r < height; r++) {
            for (int c = 1; c < width; c++) {
                double magnitude = mag[r][c];

                switch (dir[r][c]) {
                    case 0 :
                        if (magnitude < mag[r][c - 1] && magnitude < mag[r][c + 1]) {
                            mag [r - 1][c - 1] = 0;
                        }
                        break;
                    case 45 :
                        if (magnitude < mag[r - 1][c + 1] && magnitude < mag[r + 1][c - 1]) {
                            mag [r - 1][c - 1] = 0;
                        }
                        break;
                    case 90 :
                        if (magnitude < mag[r - 1][c] && magnitude < mag[r + 1][c]) {
                            mag [r - 1][c - 1] = 0;
                        }
                        break;
                    case 135 :
                        if (magnitude < mag[r - 1][c - 1] && magnitude < mag[r + 1][c + 1]) {
                            mag [r - 1][c - 1] = 0;
                        }
                        break;
                }
            }
        }
    }


    private static int[][] Hysteresis() {
        int height = mag.length - 1;
        int width = mag[0].length - 1;
        int[][] bin = new int[height - 1][width - 1];

        tHi = mean + (numDev * stDev);
        tLo = tHi * tFract;

        for (int r = 1; r < height; r++) {
            for (int c = 1; c < width; c++) {
                double magnitude = mag[r][c];

                if (magnitude >= tHi) {
                    bin[r - 1][c - 1] = 255;
                } else if (magnitude < tLo) {
                    bin[r - 1][c - 1] = 0;
                } else {
                    boolean connected = false;

                    for (int nr = -1; nr < 2; nr++) {
                        for (int nc = -1; nc < 2; nc++) {
                            if (mag[r + nr][c + nc] >= tHi) {
                                connected = true;
                            }
                        }
                    }

                    bin[r - 1][c - 1] = (connected) ? 255 : 0;
                }
            }
        }

        return bin;
    }
}
