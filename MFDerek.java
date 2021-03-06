import lejos.util.*;
import lejos.nxt.*;

public class MFDerek {
  private static SensorPort usPort = SensorPort.S1;
  private static UltrasonicSensor us;
  private static double[] input = new double[100]; //100 is the size of the entire array
  private static double[] movingFrame = new double[5]; //whilst 5 is the size of the moving frame
  
  public static void main(String[] args) {
    //initialize usSensor to be used?? 
    UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
    int movingFrameSetPoint = 0;
    MFDerek.inputRun(input, us);
    MFDerek.setMovingFrame(input, movingFrame, movingFrameSetPoint);
    MFDerek.bubbleSort(movingFrame);
    MFDerek.displayValues();
    movingFrame [5] = medianReturn(movingFrame);
    for (int h = 0; h < input.length; h++) {
      movingFrameSetPoint++;
  }
  }
  
  //copying distance values from the ultrasonic sensor to the array. it's a while(true) hence we need the thread to sleep.
  //100ms per check.
  public static void inputRun(double input[], UltrasonicSensor us) {
    while(true) {
      for (int i = 0; i < input.length; i++) {
        input[i] = us.getDistance();
      }
      try {Thread.sleep(100);
      }
      catch (Exception e) {
        System.out.println("Error 1: inputRun" + e.getMessage());
      }
    }
  }
  
  //displays the values within the moving frame, one on each line.
  public static void displayValues() {
    while (true) {
      LCD.clear();
      for (int l = 0; l < movingFrame.length; l++) {
        LCD.drawString("Moving Frame Values: " + l + movingFrame[l], 0, l);
      }
      try {Thread.sleep(200);
      }
      catch (Exception e) {
        System.out.println("Error 2 DisplayValues: " + e.getMessage());
      }
  }
  }
  
  //returns median to the sorted moving array by taking the middle value 
  public static double medianReturn(double sorted_input[]) {
    double average;
    if ((sorted_input.length % 2) == 1) { 
    //array is of odd length, chose middle value
      average = sorted_input[(int) (sorted_input.length + 1) / 2];
    }
    else { 
    //array is of even length, average out the two middle values
      average = (sorted_input[ (int) (sorted_input.length / 2) ] + sorted_input[(int) (sorted_input.length + 1) / 2]) / 2;
    }
    return average;
  }
  
  //typical bubblesort method. despite the O(N^2) complexity it has very little difference if N is small. 
  public static void bubbleSort(double value[]) {
    double swap;
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value.length; j++) {
        if (value[j] > value[j+1]) /* For descending order use < */
        {
          swap       = value[j];
          value[j]   = value[j+1];
          value[j+1] = swap;
        }
      }
    }
    return;
  }
  
  //we continually update the moving frame - sliding it across our array. 
  public static void setMovingFrame(double input[], double movingFrame[], int movingFrameStartPoint) {
        for (int k = 0; k < movingFrame.length; k++) {
          movingFrame[k] = input[movingFrameStartPoint];
        }
        return; 
  
  }
  
  
}
