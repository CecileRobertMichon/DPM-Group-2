import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorStraight = 200, f_o=2;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int fDis;//distance from the wall when sensor is parallel to the wall
	private int sDis;//distance when sensor is perpendicular to the wall
	private int gap_avoid;/*this will later be used to avoid gaps within the wall
	                         so that it does not try to go into the gaps. It will also be used for p-type as well */
	
	public BangBangController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(UltrasonicData usData) {
		
		/* A basic preliminary sort of filter which causes the robot to assume that its still 
		   on the right path when there is a small gap within the wall. So it does not try to 
		   move into the gap. So the robot ignores the gap but still goes around corners */
				if (usData.sDis == 255 && gap_avoid < f_o) {
// This is an unacceptable value. So a value for the variable is NOT set.
					//value of gap_avoid has to be incremented
					gap_avoid ++;
				} else if (usData.sDis == 255){
					// if the ultrasonic sensor gives a value of 255, the distance is set equal to that.
					this.sDis = usData.sDis;
				} else {
					
					gap_avoid = 0;//here the distance goes below 255, so all variable values are set to their intial values
					this.sDis = usData.sDis;
				}
		this.fDis=usData.fDis;
		this.distance=usData.sDis;
		this.sDis=usData.sDis;
	
		if(fDis<101*bandCenter/60){
			rightMotor.setSpeed(0);
			leftMotor.setSpeed((motorStraight*100)/49);
		} /*The left motors have to be quick enough so that the robot turns quickly before
		hitting the wall. Its better to set right motor to 0 so that it does not go any further forward
		as wall is too close. We tried out a bunch of stuffs to find out what value of the constant 
		to multiply to bandCenter needed to be and the above values work best*/
		
		
		else if (sDis<bandCenter-bandwith/2){
			/*For this condition, it means that the robot is really close to the wall and that is why 
			  It needs to stop going left any further.*/
			rightMotor.setSpeed((48*motorStraight)/100);/*slowing down right motor*/
			leftMotor.setSpeed(motorStraight);/*leftmotor has normal speed and thus robot turns to right moving away from wall*/
			leftMotor.forward();
			rightMotor.forward();
		}
		
		else if (sDis>bandCenter+bandwith/2){
			leftMotor.setSpeed(motorStraight/2);/*Same goes for the other way around as well*/
			rightMotor.setSpeed(motorStraight);/*left motor slows down and right motor stays same to make the robot go closer to the wall when its too far out*/
			leftMotor.forward();
			rightMotor.forward();
		
		}
		
		else{/*if its in the right path, then no need to change direction: GO STRAIGHT!!!!!*/
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		/*we cannot let the whole thing stop all together. So at least one of the left or right motors is always in motion*/
		
	}/*The way this code works is it avoids the need for motor high and motor low
	which was a default given. But we changed the code to gain full control over the motion
	of the wheels so we can control it as we want to.*/

	@Override
	public int readUSDistance(){
		return this.distance;
	}
}
