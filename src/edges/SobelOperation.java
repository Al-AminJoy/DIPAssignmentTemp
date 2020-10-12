package edges;

import java.awt.image.BufferedImage;

public class SobelOperation {
    private BufferedImage originalImage;
    private static int[][] H_Kernel = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    private static int[][] V_Kernel = {{-1, 0, 1}, {-2, 0, 2},{-1, 0, 1}};
    int kernelSize;

    public SobelOperation(BufferedImage originalImage) {
        this.originalImage = originalImage;
        kernelSize = V_Kernel.length;
    }
    public static int[][] Horizontal(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 2 && width > 2) {
            out = new int[height - 2][width - 2];

            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    int sum = 0;

                    for (int kr = -1; kr < 2; kr++) {
                        for (int kc = -1; kc < 2; kc++) {
                            sum += (H_Kernel[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                        }
                    }

                    out[r - 1][c - 1] = sum;
                }
            }
        }

        return out;
    }


    public static int[][] Vertical(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 2 || width > 2) {
            out = new int[height - 2][width - 2];

            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    int sum = 0;

                    for (int kr = -1; kr < 2; kr++) {
                        for (int kc = -1; kc < 2; kc++) {
                            sum += (V_Kernel[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                        }
                    }

                    out[r - 1][c - 1] = sum;
                }
            }
        }

        return out;
    }

    public BufferedImage applySobelOperator(){

        BufferedImage outputImage = new BufferedImage(this.originalImage.getWidth(),this.originalImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        try{
            int Gx,Gy;
            int G;
            int kerX = this.kernelSize/2;
            int kerY = this.kernelSize/2;
            for(int row=0;row<this.originalImage.getHeight();row++){
                for(int col=0;col<this.originalImage.getWidth();col++){
                    Gx=Gy=0;
                    G=0;
                    for(int kx = 0;kx<this.H_Kernel.length;kx++){
                        int kernelXIndex = this.kernelSize - 1 - kx;
                        for(int ky = 0;ky<this.V_Kernel.length;ky++){
                            int kernelYIndex = this.kernelSize - 1 - ky;
                            int imageXBoundary = row + (kx - kerX);
                            int imageYBoundary = col + (ky - kerY);
                            if(imageXBoundary>=0 && imageXBoundary<this.originalImage.getHeight() && imageYBoundary>=0 && imageYBoundary<this.originalImage.getWidth()){
                                int rgb = this.originalImage.getRGB(imageYBoundary, imageXBoundary);
                                Gx += ((this.H_Kernel[kernelXIndex][kernelYIndex] * rgb));
                                Gy += ((this.V_Kernel[kernelXIndex][kernelYIndex] * rgb));
                            }
                        }
                    }
                    G = Math.abs(Gx)+Math.abs(Gy);
                    outputImage.setRGB(col,row,(G<<16)|(G<<8)|G);
                }
            }


        }catch(Exception ex){
            System.out.println("getEdgeDetection Problem" + " " + ex.getMessage());
        }
        return outputImage;
    }
}
