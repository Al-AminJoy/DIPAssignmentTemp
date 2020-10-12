package dipassignment;

import edges.CannyOperation;
import edges.SobelOperation;
import filters.MeanFilter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import threshold.BGThreshold;
import utils.CommonTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DipAssignmentTemplateController {

    public BorderPane root_layout;
    public ImageView imported_imageview;
    public ImageView output_imageview;
    public MenuBar menu_bar;

    private BufferedImage inputBufferedImage;
    private int[] histogram;

    private static final double CANNY_THRESHOLD_RATIO = .2;
    private static final int CANNY_STD_DEV = 1;


    public void doImportImageFromComputer(ActionEvent actionEvent) throws IOException {
        Stage parent = (Stage) root_layout.getScene().getWindow();
        File imageFile = CommonTask.importImage(parent);
        imported_imageview.setImage(new Image(new FileInputStream(imageFile)));
        inputBufferedImage = ImageIO.read(imageFile);
    }

    public void doImageRotateLeft(ActionEvent actionEvent) {
        int height = inputBufferedImage.getHeight();
        int width = inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage =
                new BufferedImage(height,width,BufferedImage.TYPE_3BYTE_BGR);
        int[][] outputImage = new int[width][height]; //3,4

        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                outputImage[width-col-1][row] = inputBufferedImage.getRGB(col,row);
            }
        }

       for(int row=0;row<width;row++){
            for(int col=0;col<height;col++){
                outputBufferedImage.setRGB(col,row,outputImage[row][col]);
            }
        }

        output_imageview.setImage(SwingFXUtils.toFXImage(outputBufferedImage, null));

    }

    public void doImageRotateRight(ActionEvent actionEvent) {
        // TODO try your self
    }

    public void doImageFullRotate(ActionEvent actionEvent) {
        // TODO try your self
    }

    public void doHistogramChart(ActionEvent actionEvent) throws IOException {
        histogram = new int[256];

        WritableRaster raster = inputBufferedImage.getRaster();

        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int pixel = raster.getSample(col, row, 0);
                histogram[pixel]++;
            }
        }


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dipHistogramChart.fxml"));
        Parent parentView = fxmlLoader.load();

        HistogramChartController chartController = (HistogramChartController) fxmlLoader.getController();

        Scene sceneView = new Scene(parentView);
        Stage  stage = (Stage) menu_bar.getScene().getWindow();
        stage.setScene(sceneView);

        chartController.showChart(histogram);
    }

    public void doBGT(ActionEvent actionEvent) {
        BGThreshold bgThreshold = new BGThreshold(inputBufferedImage);
        BufferedImage outputImage = bgThreshold.applyThreshold();
        output_imageview.setImage(SwingFXUtils.toFXImage(outputImage, null));
    }

    public void doOtsusMethod(ActionEvent actionEvent) {
        // TODO try your self
    }

    public void doMeanFiltering(ActionEvent actionEvent) {
        MeanFilter meanFilter = new MeanFilter(inputBufferedImage);
        BufferedImage outputImage = meanFilter.applyMeanFilter();
        output_imageview.setImage(SwingFXUtils.toFXImage(outputImage, null));
    }

    public void doGaussianFiltering(ActionEvent actionEvent) {
         // TODO try your self
    }

    public void doSobelOperation(ActionEvent actionEvent) {
        SobelOperation sobelOperation = new SobelOperation(inputBufferedImage);
        BufferedImage outputImage = sobelOperation.applySobelOperator();
        output_imageview.setImage(SwingFXUtils.toFXImage(outputImage, null));
    }

    public void doCannyEdgeDetection(ActionEvent actionEvent) {
        BufferedImage output = CannyOperation.CannyEdges(inputBufferedImage, CANNY_STD_DEV, CANNY_THRESHOLD_RATIO);
       output_imageview.setImage(SwingFXUtils.toFXImage(output, null));
    }
}
