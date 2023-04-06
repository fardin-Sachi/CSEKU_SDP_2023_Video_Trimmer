package ModuledTrimmer;

import java.io.File;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import trimmer.Trim;

public class TrimCodeBase {
    File inputFile;
    List<Trim> trims;
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.loadLibrary("C:\\Program Files\\opencv\\build\\java\\x64\\opencv_java460.dll");
    }
    TrimCodeBase(File inputFile,List<Trim> trims){
        this.inputFile = inputFile;
        this.trims = trims;
    }
    public void trimVideo(){
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
        for(Trim trim : trims){
            int startFrameIndex = (int) Math.round(trim.getStartTime() * fps);
            int endFrameIndex = (int) Math.round(trim.getEndTime() * fps);
            //Reading and writing each frame within the trim section
            for (int i=startFrameIndex; i<endFrameIndex; i++) {
                Mat frame = new Mat();
                capture.read(frame);
                writer.write(frame);
            }
        }
        capture.release();
        writer.release();
        System.out.println("Trimming Completed");
    }
}
