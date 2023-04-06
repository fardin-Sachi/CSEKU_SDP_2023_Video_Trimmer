package trimmer;
public class Trim{
    private final double startTime;
    private final double endTime;
    
    public Trim(double startTime, double endTime){
        if (startTime<0 || endTime<0 || startTime>=endTime)
            throw new IllegalArgumentException("Invalid Start/End Time for Trim");
        
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public double getStartTime(){
        return startTime;
    }
    public double getEndTime(){
        return endTime;
    }
    public double getDuration(){
        return endTime - startTime;
    }
}