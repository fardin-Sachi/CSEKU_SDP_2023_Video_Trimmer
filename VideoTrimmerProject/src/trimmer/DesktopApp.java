package trimmer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.opencv.core.Size;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesktopApp extends Application {
    private final List<Trim> trims = new ArrayList<>();

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.loadLibrary("C:\\Program Files\\opencv\\build\\java\\x64\\opencv_java460.dll");
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Video Trimmer");

        //Creating the user interface elements
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label fileLabel = new Label("Select video file:");
        grid.add(fileLabel, 0, 0);

        //for browsing file -> TextFile i mean
        TextField fileField = new TextField();
        fileField.setEditable(false);
        grid.add(fileField, 1, 0);
//        File file;
//        Media media;
//        MediaPlayer player;

        Button fileButton = new Button("Browse");
        fileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                trims.clear();
                fileField.setText(file.getName());
            }
//            Media media = new Media(file.toURI().toString());
//            MediaPlayer player = new MediaPlayer(media);
//            MediaView mediaView = new MediaView(player);
//            grid.add(mediaView,0,0);
        });
        fileButton.setFocusTraversable(false);
        grid.add(fileButton, 2, 0);

        Label startLabel = new Label("Start time (seconds):");
        grid.add(startLabel, 0, 1);

        TextField startField = new TextField();
        grid.add(startField,1,1);
        
        Label endLabel = new Label("End time (seconds):");
        grid.add(endLabel, 0, 2);

        //End time textField
        TextField endField = new TextField();
        grid.add(endField, 1, 2);

        Button addTrimButton = new Button("Add Trim");
        addTrimButton.setOnAction(event -> {
            double startTime = Double.parseDouble(startField.getText());
            double endTime = Double.parseDouble(endField.getText());
            trims.add(new Trim(startTime, endTime));
            startField.clear();
            endField.clear();
        });
        addTrimButton.setFocusTraversable(false);
//        System.out.println("Check under Add Trime");
        grid.add(addTrimButton, 2, 1);

        Button trimButton = new Button("Trim Video");
        trimButton.setOnAction(event -> {
            File file = new File(fileField.getText());
            if (file.exists()) {
                trimVideo(file);
//                System.out.println("Check File exist");
            }
//            else System.out.println("Check File not exist");
        });
        trimButton.setFocusTraversable(false);
//        System.out.println("Check under Trim Video");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(trimButton);
        grid.add(hbBtn, 2, 2);

        Scene scene = new Scene(grid, 500,200);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
//        System.out.println("Check last");
    }

    public void trimVideo(File inputFile) {
        //Loading the input video file
//        System.out.println("Check Under function");
        VideoCapture capture = new VideoCapture(inputFile.getAbsolutePath());

        //Getting the frame rate of the input video
        double fps = capture.get(Videoio.CAP_PROP_FPS);

        //Creating a VideoWriter object to write on the output video file
        VideoWriter writer = new VideoWriter();
        String outputFilePath = "trimmed_video.mp4";
        writer.open(outputFilePath, VideoWriter.fourcc('H', '2', '6', '4'), fps, new Size((int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT)), true);

        //Iterating over each trim section and write the frames to the output video file
        for (Trim trim : trims) {
            int startFrameIndex = (int) Math.round(trim.getStartTime() * fps);
            int endFrameIndex = (int) Math.round(trim.getEndTime() * fps);
            //Reading and writing each frame within the trim section
            for (int i = startFrameIndex; i < endFrameIndex; i++) {
                Mat frame = new Mat();
                capture.read(frame);
                writer.write(frame);
            }
        }
        capture.release();
        writer.release();
        System.out.println("Trimming Completed");
    }
    public static void main(String[] args) {
        launch(args);
    }
}
