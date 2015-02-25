//This code turns the motors 360 degrees to shoot the ball using elastic
//stops for a while to let 2nd ball come in to position
//repeats process

import lejos.nxt.*;
public class Ballistics {
    public static void main(String[] args) {
    	NXTRegulatedMotor loader = Motor.A;
        loader.setSpeed(1000000);//wanted to use largest value possible 
        loader.setAcceleration(100000);//wanted to use largest value possible
     for(int i=0; i<100; i++){
         loader.rotate(-360);
         try {
             Thread.sleep(1500);
         } catch (InterruptedException e) {}
         loader.stop();
     }
      
     
     try {
         Thread.sleep(500);
     } catch (InterruptedException e) {}
     loader.stop();
    }
}
    
    