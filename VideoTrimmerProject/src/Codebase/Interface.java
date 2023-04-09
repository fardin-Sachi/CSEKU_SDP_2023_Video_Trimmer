package Codebase;

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
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Interface extends Application {

    public final List<Trim> trims = new ArrayList<>();
    Label timeLabel;
    Slider timeSlider;
    Media media;
    MediaPlayer mediaPlayer;
    MediaView mediaView;

    @Override
    public void start(Stage primaryStage) {
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

        //Start time label & textField
        Label startLabel = new Label("Start time (seconds):");
        grid.add(startLabel, 0, 2);
        TextField startField = new TextField();
        grid.add(startField, 1, 2);

        //End time label & textField
        Label endLabel = new Label("End time (seconds):");
        grid.add(endLabel, 0, 3);
        TextField endField = new TextField();
        grid.add(endField, 1, 3);

        //"Add Trim" button and taking multiple trim ranges
        Button addTrimButton = new Button("Add Trim");
        addTrimButton.setOnAction(event -> {
            double startTime = Double.parseDouble(startField.getText());
            double endTime = Double.parseDouble(endField.getText());
            trims.add(new Trim(startTime, endTime));
            startField.clear();
            endField.clear();
        });
        addTrimButton.setFocusTraversable(false);
        grid.add(addTrimButton, 2, 2);

        //Doing the main trimming
        Button trimButton = new Button("Trim Video");
        trimButton.setOnAction(event -> {
            File file = new File(fileField.getText());
            if (file.exists()) {
                TrimCodeBase objT = new TrimCodeBase(file,trims);
                objT.trimVideo();
                mediaPlayer.stop();
            }
        });
        trimButton.setFocusTraversable(false);

        //Timeslider
        timeSlider = new Slider();
        timeSlider.setMin(0);
        timeSlider.setMax(1);
        timeSlider.setMaxSize(1200, 40);
        timeSlider.setMinSize(1100, 40);

        //Current time label for timeslider
        timeLabel = new Label("00:00/00:00");

        //Browse button and after browsing, the media player will open and autoplay the media content
        Button fileButton = new Button("Browse");
        fileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                trims.clear();
                fileField.setText(file.getName());
            }
            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            mediaView.setFitWidth(1280);
            mediaView.setFitHeight(720);
            grid.setAlignment(Pos.CENTER);
            grid.add(mediaView, 1, 0);
            fileButton.setVisible(false);
            fileField.setVisible(false);
            fileLabel.setVisible(false);

            //while dragging the timeslider, the changed value will be updated in the timeLabel
            timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(newValue.doubleValue()));
                }
            });
            grid.add(timeSlider, 1, 1);

            // Update the time label as the media is playing
            mediaPlayer.currentTimeProperty().addListener(e -> {
                updateValues();
            });
            grid.add(timeLabel, 2, 1);
        });
        fileButton.setFocusTraversable(false);
        grid.add(fileButton, 2, 0);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(trimButton);
        grid.add(hbBtn, 2, 3);

        Scene scene = new Scene(grid, 1600, 900);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void updateValues() {
        if (timeLabel != null && timeSlider != null && mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalTime = mediaPlayer.getMedia().getDuration();
            // Update the time label
            timeLabel.setText(formatTime(currentTime) + "/" + formatTime(totalTime));

            // Update the time slider
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(currentTime.toSeconds() / totalTime.toSeconds());
            }
        }
    }
    private String formatTime(Duration duration) {
        int hours = (int) duration.toHours();
        int minutes = (int) (duration.toMinutes() - hours * 60);
        int seconds = (int) (duration.toSeconds() - hours * 3600 - minutes * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}