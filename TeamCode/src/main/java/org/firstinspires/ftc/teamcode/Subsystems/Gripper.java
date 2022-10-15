package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Enums.LiftState;

import static java.lang.Thread.sleep;

public class Gripper {

    //Define Hardware Objects
    public Servo        turner              = null;
    public Servo        gripperLeft           = null;
    public Servo        gripperRight           = null;
    public VoltageSensor voltSensor         = null;

    // Need some features from the Linear Opmode to make the lift work

    ElapsedTime runtime = new ElapsedTime();

    //Constants for rollers
    public static final double      GRIPPER_LEFT_INITIAL =  0.5;
    public static final double      GRIPPER_LEFT_FINAL =  0.55;
    public static final double      GRIPPER_RIGHT_INITIAL =  0.5;
    public static final double      GRIPPER_RIGHT_FINAL =  0.45;


    //Constants for turnythingy
    public static final double      POS_ONE       = 0.5;
    public static final double      POS_TWO      = 0;
    public static final double      POS_THREE      = 1;

    LinearOpMode opmode;
    // Constructor with opmode so we can access opmode features
    public Gripper(LinearOpMode opmode) {
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
    public void turnerSetPosition1() throws InterruptedException {
        turner.setPosition(POS_ONE);
    }

    public void turnerSetPosition2() throws InterruptedException {
        turner.setPosition(POS_TWO);
    }

    public void turnerSetPosition3() throws InterruptedException {
        turner.setPosition(POS_THREE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Auto Only for roller suctioning and removing.
    public void rollersInit(){
        gripperLeft.setPosition(GRIPPER_LEFT_INITIAL);
        gripperRight.setPosition(GRIPPER_RIGHT_INITIAL);
    }
    public void rollersFinal(){
        gripperLeft.setPosition(GRIPPER_LEFT_FINAL);
        gripperRight.setPosition(GRIPPER_RIGHT_FINAL);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}




