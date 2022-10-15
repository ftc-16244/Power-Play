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
    public Servo        rollerOne           = null;
    public Servo        rollerTwo           = null;
    public VoltageSensor voltSensor         = null;

    // Need some features from the Linear Opmode to make the lift work

    ElapsedTime runtime = new ElapsedTime();

    //Constants for rollers
    public static final double      ROLLER_ONE_INITIAL =  0;
    public static final double      ROLLER_ONE_FINAL =  0.25;
    public static final double      ROLLER_TWO_INITIAL =  0;
    public static final double      ROLLER_TWO_FINAL =  0.75;


    //Constants for turnythingy
    public static final double      POS_ONE       = 0;
    public static final double      POS_TWO      = 1 - POS_ONE;

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
        rollerOne = hwMap.get(Servo.class,"rollerOne");

        // Initialize the roller two
        rollerTwo = hwMap.get(Servo.class,"rollerTwo");

        // pre-position servos

    }

    //Turner methods
    public void turnerSetPosition1() throws InterruptedException {
        turner.setPosition(POS_ONE);
    }

    public void turnerSetPosition2() throws InterruptedException {
        turner.setPosition(POS_TWO);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Auto Only for roller suctioning and removing.
    public void rollersInit(){
        rollerOne.setPosition(ROLLER_ONE_INITIAL);
        rollerTwo.setPosition(ROLLER_TWO_INITIAL);
    }
    public void rollersFinal(){
        rollerOne.setPosition(ROLLER_ONE_FINAL);
        rollerTwo.setPosition(ROLLER_TWO_FINAL);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}




