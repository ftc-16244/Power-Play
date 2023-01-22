package org.firstinspires.ftc.teamcode.Subsystem2;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Gripper2 {

    //Define Hardware Objects
    public Servo            turner             = null;
    public Servo            gripperLeft        = null; //configis the same name
    public Servo            gripperRight       = null;
    public Servo            gripperTop         = null;
    public VoltageSensor    voltSensor         = null;

    // Need some features from the Linear Opmode to make the lift work

    ElapsedTime runtime = new ElapsedTime();

    //Constants for rollers
    public static final double      GRIPPER_LEFT_INITIAL    = 0.5; // not gripped
    public static final double      GRIPPER_LEFT_OPEN       = 0.25; // not gripped
    public static final double      GRIPPER_LEFT_CLOSED      = 0.64 ; // cone gripped
    public static final double      GRIPPER_RIGHT_INITIAL   = 0.50 ; // not gripped
    public static final double      GRIPPER_RIGHT_OPEN      = 0.75; // not gripped
    public static final double      GRIPPER_RIGHT_CLOSED     = 0.36; // gripped

    public static final double      GRIPPER_TOP_INITIAL    = 0.15; // should be horizontal at init
    public static final double      GRIPPER_TOP_OPEN       = 0.38; // just above cone on ground
    public static final double      GRIPPER_TOP_CLOSED      = 0.50 ; // cone pulled in for gripper
    public static final long    GRIPPER_DELAY            = 300 ; // delay between top and side gripper action (miliseconds)


    //Constants for turnythingy
    public static final double      BACK       = 0.17; // facing to the back
    public static final double      FRONT      = 0.86; // facing to the front

    LinearOpMode opmode;
    // Constructor with opmode so we can access opmode features
    public Gripper2(LinearOpMode opmode) {
        this.opmode = opmode;
    }

    public void init(HardwareMap hwMap)  {

        voltSensor = hwMap.voltageSensor.get("Expansion Hub 2");

        // Initialize tuner the servo that rotates the cone capture bucket
        turner = hwMap.get(Servo.class,"turner");// port 4
        //turner.setPosition(BACK);

        // Initialize the left gripper
        gripperLeft = hwMap.get(Servo.class,"gripperLeft"); //port 0

        // Initialize the right gripper
        gripperRight = hwMap.get(Servo.class,"gripperRight"); // port 2

        // Initialize the top grab arm
        gripperTop = hwMap.get(Servo.class,"gripperTop");//port 5

        // pre-position turner servo


    }

    //Turner methods
    public void turnerSetPosition1() {
        turner.setPosition(BACK); // back
    }

    public void turnerSetPosition2() {
        turner.setPosition(FRONT);//fwd
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Gripper init is at half way. There is also open and closed
    public void gripperInitTeleop(){
        gripperLeft.setPosition(GRIPPER_LEFT_INITIAL);
        gripperRight.setPosition(GRIPPER_RIGHT_INITIAL);
        gripperTop.setPosition(GRIPPER_TOP_OPEN);
    }

    public void gripperInitAuto(){
        gripperLeft.setPosition(GRIPPER_LEFT_INITIAL);
        gripperRight.setPosition(GRIPPER_RIGHT_INITIAL);

    }
    public void gripperClosed(){
        gripperLeft.setPosition(GRIPPER_LEFT_CLOSED);
        gripperRight.setPosition(GRIPPER_RIGHT_CLOSED);

    }

    public void gripperOpen(){
        gripperLeft.setPosition(GRIPPER_LEFT_OPEN);
        gripperRight.setPosition(GRIPPER_RIGHT_OPEN);
        gripperTop.setPosition(GRIPPER_TOP_INITIAL); // keep top gripper out of th way in Auto
    }


    public void topArmClosed(){
        gripperTop.setPosition(GRIPPER_TOP_CLOSED);

    }

    public void TopArmOpen(){

        gripperTop.setPosition(GRIPPER_TOP_OPEN);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}




