package org.firstinspires.ftc.teamcode.Subsystem2;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Gripper2 {

    //Define Hardware Objects
    public Servo            turner             = null;
    public Servo            gripperLeft        = null;
    public Servo            gripperRight       = null;
    public VoltageSensor    voltSensor         = null;

    // Need some features from the Linear Opmode to make the lift work

    ElapsedTime runtime = new ElapsedTime();

    //Constants for rollers
    public static final double      GRIPPER_LEFT_INITIAL    = 0.5; // not gripped
    public static final double      GRIPPER_LEFT_OPEN       = 0.60; // not gripped
    public static final double      GRIPPER_LEFT_CLOSED      = 0.40 ; // cone gripped
    public static final double      GRIPPER_RIGHT_INITIAL   = 0.50 ; // not gripped
    public static final double      GRIPPER_RIGHT_OPEN      = 0.40; // not gripped
    public static final double      GRIPPER_RIGHT_CLOSED     = 0.60; // gripped


    //Constants for turnythingy
    public static final double      BACK       = 0.17; // facing to the back
    public static final double      FRONT      = 0.83; // facing to the front

    LinearOpMode opmode;
    // Constructor with opmode so we can access opmode features
    public Gripper2(LinearOpMode opmode) {
        this.opmode = opmode;
    }

    public void init(HardwareMap hwMap)  {

        voltSensor = hwMap.voltageSensor.get("Expansion Hub 2");

        // Initialize tuner the servo that rotates the cone capture bucket
        turner = hwMap.get(Servo.class,"turner");


        // Initialize the roller one
        gripperLeft = hwMap.get(Servo.class,"gripperLeft");

        // Initialize the roller two
        gripperRight = hwMap.get(Servo.class,"gripperRight");

        // pre-position servos

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
    public void gripperInit(){
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
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}




