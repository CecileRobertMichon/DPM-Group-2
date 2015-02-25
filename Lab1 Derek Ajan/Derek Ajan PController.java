import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;
public class PController implements UltrasonicController {
	private final int bandCenter, bandwith;
	private final int motorStraight = 150, f_o = 2;/*default values were different, we changed it somewhat with
	values that work best with our robot*/
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int currentLeftSpeed;
	private int currentRightSpeed;//we added up right wheel to gain more control over our vehicle
	private int gap_avoid;
	private int fDis;
	private int sDis;
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		gap_avoid = 0;
	}
	
	@Override
	public void processUSData(UltrasonicData usData) {
		
		// rudimentary filter
		if (usData.sDis == 255 && gap_avoid < f_o) {
			// bad value, do not set the distance var, however do increment the filter value
			gap_avoid ++;
		} else if (usData.sDis == 255){
			// true 255, therefore set distance to 255
			this.sDis = usData.sDis;
		} else {
			// distance went below 255, therefore reset everything.
			gap_avoid = 0;
			this.sDis = usData.sDis;/**/
		}
		
		this.distance=usData.sDis;
		this.fDis=usData.fDis;
		
		if(fDis<101*bandCenter/60){/*again, tried out different values and used the one which fitted best*/
			leftMotor.setSpeed(motorStraight*2);//leftmotor moves faster for robot to turn right
			rightMotor.setSpeed(0);
		}
		
		else{
			float yield = (float) (1.0/40.0);//this value has to be very low. With ours, this works.
			float dis_diff = sDis-bandCenter;//if dis_diff=0, then everything has same speed
			float x = -dis_diff*yield;
			//If x is [+]ve, robot moves left wheel faster and turns right
			currentLeftSpeed = (int) (motorStraight*(1.0+x));
			currentRightSpeed = (int) (motorStraight*(1.0-x));


			if (currentLeftSpeed<0){
				currentLeftSpeed=0;//fixes so that wheels don't turn the opposite way
			}
			if (currentRightSpeed<0){
				currentRightSpeed=0;//same for right wheel
			}
			if (currentLeftSpeed>3*motorStraight){
				currentLeftSpeed=3*motorStraight;
			}
			if(currentRightSpeed>3*motorStraight){
				currentRightSpeed=3*motorStraight;
			}
/*Last two fixes the problems of running too fast*/
			
			leftMotor.setSpeed(currentLeftSpeed);
			rightMotor.setSpeed(currentRightSpeed);
			leftMotor.forward();
			rightMotor.forward();
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
