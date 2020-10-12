package utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CommonTask {

    public static File importImage(Stage stage) throws IOException {
        File selectedFile = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("JPG Image","*.jpg"),
                new FileChooser.ExtensionFilter("PNG Image","*.png")
        );
        File selectedDirectory = fileChooser.showOpenDialog(stage);
        if(selectedDirectory != null){
            selectedFile = selectedDirectory.getAbsoluteFile();
        }

        return selectedFile;
    }

    public static BufferedImage applyGrayScale(BufferedImage inputImage){
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(),
                inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                int rgb = inputImage.getRGB(col, row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;

                int gray = (red+green+blue)/3;

                outputImage.setRGB(col, row, (gray<<16)|(gray<<8)|gray);
            }
        }
        return outputImage;
    }
    public static int[][] GSArray(BufferedImage img) {
        int[][] gs = null;
        int height = img.getHeight();
        int width = img.getWidth();

        if (height > 0 && width > 0) {
            gs = new int[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int bits = img.getRGB(j, i);
                    long avg = Math.round((((bits >> 16) & 0xff) + ((bits >> 8) & 0xff) + (bits & 0xff)) / 3.0);
                    gs[i][j] = (int) avg;
                }
            }
        }

        return gs;
    }


    public static BufferedImage GSImg(int[][] raw) {
        BufferedImage img = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 0 && width > 0) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    img.setRGB(j, i, (raw[i][j] << 16) | (raw[i][j] << 8) | (raw[i][j]));
                }
            }
        }

        return img;
    }
}
