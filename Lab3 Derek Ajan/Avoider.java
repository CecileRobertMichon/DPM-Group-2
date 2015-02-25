/*Ajan Ahmed and Derek Yu Lab 3 Code[Group 33]*/
/***********************************************************************************************************************************************************************************/
import lejos.nxt.*;
public class Avoider extends Thread {
    private static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B; 
    
    //Starting here, are the essential variables and class constants
    private final int forwardSpeed = 250;                          // moderate speed used-the one which gives perfect results
    private final int RotationSpeed = 80;                          // also not too slow, not too fast-works best at this value
    private final double radiusRight = 2.1;                        // right wheel radius that we measured   
    private final double radiusLeft = 2.095;                         // left wheel radius that we measured
    private final double distanceFromLeftToRightWheel = 15.9;  //this is the distance from the centre of the left wheel  to the center of the right wheel 
    private final double errorOfAngle = 0.1;                         // this is the maximum error in angle allowed
    private final double errorOfPosition = 2;                        // maximum error allowed in distance
    private static Odometer odometer = new Odometer();         
    private static OdometryDisplay display = new OdometryDisplay (odometer);        // odometer display
    private static double current_Theta;                             // value of present theta
    private static boolean isNavigating = false;                      // this is the variable that is going to be used in the method travel-to
    private SensorPort usPort = SensorPort.S1;              // initialize US sensor
    private UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
/*************************************************************************************************************************************************************************************/
  // Making a Constructor
    public Avoider (){ 
        odometer.start();
        display.start();
    }
/**************************************************************************************************************************************************************************************/
public void run(){  
        //the destinations asked to be followed by the code
        travelTo(0,60);
        travelTo(60,0);
  
    }
/***************************************************************************************************************************************************************************************/
// this method returns the boolean value used later in travel-to method
    public static  boolean isNavigating(){ 
        return isNavigating;
    }
/****************************************************************************************************************************************************************************************/
public  void turnTo (double theta){
          
        isNavigating = true;                             // setting the  value of  isNavigating to be true
        double delTheta = theta-current_Theta;         // find change in theta between current position and desired destination
          
        //Setting the optimal rotation
        if (delTheta  < -Math.PI && delTheta  >= (-2*Math.PI))            // the code is required to have theta values of -pi to pi
            delTheta = delTheta + Math.PI;
        else if (delTheta  > Math.PI && delTheta  <=(2*Math.PI))      
            delTheta = delTheta - Math.PI;
        leftMotor.setSpeed(RotationSpeed);                                 // setting speed of motors running the wheels
        rightMotor.setSpeed(RotationSpeed);                                //setting speed of  motors running the wheels
  
      
        double angle_In_Degrees = Math.toDegrees(delTheta);                 // convert angle Theta to degrees
                  
        leftMotor.rotate(convertAngle(radiusLeft, distanceFromLeftToRightWheel, angle_In_Degrees), true);   // rotate wheels until correct heading
        rightMotor.rotate(-convertAngle(radiusRight, distanceFromLeftToRightWheel, angle_In_Degrees), false);   // rotate wheels until correct heading
}
/*******************************************************************************************************************************************************************************************/      
       public void travelTo (double y, double x){
          isNavigating = true;
        int threshold = 18;     // threshold distance away from obstacle-this is the max distance allowed 
        int usDistance = usSensor.getDistance();        // gets distance from obstacle
        while (isNavigating()){
              // gets current position of robot
            double currentY = odometer.getX();      
            double currentX = odometer.getY();
            current_Theta =  odometer.getTheta();
            // Setting the angle between -pi and pi
            if (current_Theta  < - Math.PI && current_Theta  >= (-2*Math.PI))
                current_Theta = current_Theta + Math.PI;
            else if (current_Theta  > Math.PI && current_Theta  <=(2*Math.PI))
                current_Theta = current_Theta - Math.PI;
      // the following formula calculates the angle of heading it should turn to
             double heading = Math.atan2(y - currentY, x - currentX);
             // the distance left to travel is calculated and executed
             double distance = Math.sqrt(Math.pow(y - currentY, 2) + Math.pow(x - currentX, 2));
          // Once robot gets to the desired position, isNavigating returns to false
           //then it starts to run the next piece of instructions
             if (Math.abs(currentX - x) < errorOfPosition && Math.abs(currentY - y) < errorOfPosition){
                 isNavigating = false;
                 break;
             }
            
               
             if (current_Theta > (heading - errorOfAngle) && current_Theta < (heading + errorOfAngle) ){
                   
                // Commands to get robot to desired position
                 leftMotor.forward();                       
                 rightMotor.forward();
                 leftMotor.setSpeed(forwardSpeed);
                 rightMotor.setSpeed(forwardSpeed);
                 leftMotor.rotate(convertDistance(radiusLeft, distance), true); 
                rightMotor.rotate(convertDistance(radiusLeft, distance), true);
                 usDistance = usSensor.getDistance();
                 if(usDistance <threshold){
                       
                     	leftMotor.rotate(convertAngle(radiusLeft, distanceFromLeftToRightWheel, 90), true); 
                        rightMotor.rotate(-convertAngle(radiusRight, distanceFromLeftToRightWheel, 90), false);
                        leftMotor.setSpeed(forwardSpeed);
                        rightMotor.setSpeed(forwardSpeed);
                        leftMotor.rotate(convertDistance(radiusLeft, 35), true); 
                        rightMotor.rotate(convertDistance(radiusRight, 35), false);
                        leftMotor.rotate(-convertAngle(radiusLeft, distanceFromLeftToRightWheel, 90),true); 
                        rightMotor.rotate(convertAngle(radiusRight, distanceFromLeftToRightWheel, 90),false);
                        leftMotor.rotate(convertDistance(radiusLeft, 35), true); 
                        rightMotor.rotate(convertDistance(radiusRight, 35), false);
                   }
                 
             } 
              else{
                
                turnTo(heading);
            }
        }
}
/*******************************************************************************************************************************************/        
    // methods taken from square driver 
    private  int convertDistance(double radius, double distance) {
        //tells robot how much to move forward
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }
/*******************************************************************************************************************************************/ 
   private  int convertAngle(double radius, double width, double angle) {
        //tells robot how much it should turn in degrees.
  
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    } 
   public static int convertToDegrees(double radians) {
	   return (int) (radians * 180 / Math.PI);
   }
} 
/***********************************************************THE END*********************************************************************************/