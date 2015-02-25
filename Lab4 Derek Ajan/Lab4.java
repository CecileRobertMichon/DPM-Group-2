import lejos.nxt.*;
/**************************************************************************************************************************************************/
public class Lab4 {
/**************************************************************************************************************************************************/
	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		//This lab uses two methods to localize: US sensor or light sensor: thus the two options
	    int choice_of_button;
        do {
            
            LCD.clear();//clearing the display screen 
            
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString("   US  | light  ", 0, 2);
            //choose left or us and right for light sensor
            choice_of_button = Button.waitForAnyPress();
        } while (choice_of_button != Button.ID_LEFT && choice_of_button != Button.ID_RIGHT);
  
        if (choice_of_button == Button.ID_LEFT) { 
        	//If US sensor chosen, Two more options appear
            int nextButton; //displays for next button press after removing everything from previous screen
            do{
            LCD.clear();
            
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString(" Rising| Falling", 0, 2);
            //left to use rising and right to use falling
            
            nextButton= Button.waitForAnyPress();
            } while (nextButton != Button.ID_LEFT
                    && nextButton != Button.ID_RIGHT);
              
            if (nextButton == Button.ID_LEFT){
                 //if rising chosen, the sensors are turned on and starts to display values on screen
                LCDInfo lcdReading = new LCDInfo(odo);
                UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
                ColorSensor light_sensor = new ColorSensor(SensorPort.S2);
                //we changed variable names from original lab4 for our convinience
                Navigation navigator = new Navigation(odo);
                USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE, navigator);
                usl.doLocalization();
                } else { 
                	//if falling edge localization is chosen
                    LCDInfo lcd = new LCDInfo(odo);
                    UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
                    LightSensor ls = new LightSensor(SensorPort.S2);
                    Navigation steer = new Navigation(odo);//used to navigate the Robot
                    USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, steer);
                    usl.doLocalization();   
                }
        } else {
        	//if wants to choose light localization
            LCDInfo lcdReading = new LCDInfo(odo);
            UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
            ColorSensor light_sensor = new ColorSensor(SensorPort.S2);
            Navigation steer = new Navigation(odo);
            USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, steer);
            usl.doLocalization();
            LightLocalizer lsl = new LightLocalizer(odo, light_sensor);
            lsl.doLocalization();   
            steer.turnTo(0);    
        }
        // exits program 
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);    
    } 
}//We used part of Razi Murshed's code