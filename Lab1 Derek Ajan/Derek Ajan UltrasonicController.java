
public interface UltrasonicController {
	public void processUSData(UltrasonicData usData);
	
	public int readUSDistance();
}/*Two pieces of important data and sensed by the ultrasonic sensir
1. Side distance
2. Front distance
Afterwards, each data is processed to control the motion*/
