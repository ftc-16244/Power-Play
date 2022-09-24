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

public class Arnold {

    //Define Hardware Objects
    public DcMotor      linearActuator      = null;
    public Servo        turner            = null;
    public Servo        rollerOne       = null;
    public Servo        rollerTwo        = null;
    public VoltageSensor voltSensor         = null;

    // Need some features from the Linear Opmode to make the lift work


    ElapsedTime runtime = new ElapsedTime();

    public static final double      SLOWDRIVEFACTOR             =   0.35;

    //Constants for Wadlow the Lift
    public static final double      WADLOWLIFTSPEED               =   0.9; // for up and down on linear actuator and PID control
    public static final double      WADLOWLIFTPARTIAL             =   5.75; // Alliance hub height
    public static final double      WADLOWLIFTUP                  =   7.0; //Number is in inches used in auto
    public static final double      WADLOWLIFTLOAD                =   0.3; //Number is in inches
    public static double            WADLOW_SPEED_HOLD             =   0.0; // for screw type this countercats gravity and the weight of the lift
    private static final double     TICKS_PER_MOTOR_REV         =   145.1; // goBilda 1150 RPM motor
    private static final double     ACTUATOR_DISTANCE_PER_REV   =   8/25.4; // 8mm of travel per rev converted to inches
    public static final double      TICKS_PER_LIFT_IN           =   TICKS_PER_MOTOR_REV / ACTUATOR_DISTANCE_PER_REV; // 460 and change


    //Constants for rollers
    public static final double      ROLLER_ONE_INITIAL =  0;
    public static final double      ROLLER_ONE_FINAL =  0.25;
    public static final double      ROLLER_TWO_INITIAL =  0;
    public static final double      ROLLER_TWO_FINAL =  0.75;


    //Constants for turnythingy
    public static final double      POS_ONE       = 0;
    public static final double      POS_TWO      = 0.25;
    public static final double      POS_THREE     = 0.5;
    public static final double      POS_FOUR     = 0.75;


    LiftState mliftstate = LiftState.UNKNOWN;

    LinearOpMode opmode;
    // Constructor with opmode so we can access opmode features
    public Arnold(LinearOpMode opmode) {
        this.opmode = opmode;
    }

    public void init(HardwareMap hwMap)  {

        voltSensor = hwMap.voltageSensor.get("Expansion Hub 2");

        // Initialize Juan - Linear Actuator type of lift
        linearActuator = hwMap.get(DcMotor.class,"wadlowLift");
        linearActuator.setDirection(DcMotor.Direction.FORWARD);
        linearActuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearActuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Initialize tuner the servo that rotates the cone capture bucket
        turner = hwMap.get(Servo.class,"turner");

        // Initialize the roller one
        rollerOne = hwMap.get(Servo.class,"rollerOne");

        // Initialize the roller two
        rollerTwo = hwMap.get(Servo.class,"rollerTwo");

        // pre-position servos
        turner.setPosition(POS_ONE);
        rollerOne.setPosition(ROLLER_ONE_INITIAL);
        rollerTwo.setPosition(ROLLER_TWO_INITIAL);

        // get motor details

    }

       //Wadlow the lift's methods -  while loop type (use during Auto)

    public void liftRise() {
        liftToTargetHeight(WADLOWLIFTUP ,3);
    }
    public void liftPartial() {

        liftToTargetHeight(WADLOWLIFTPARTIAL ,3);
    }
    public void liftLoad() {
        liftToTargetHeight(WADLOWLIFTLOAD,1);


    }

    // get Wadlow's Position need a local variable to do this
    public double getWadlowPosition(){
        double wadlowPositionLocal;
        wadlowPositionLocal = linearActuator.getCurrentPosition()/ TICKS_PER_LIFT_IN; //returns in inches
        return  wadlowPositionLocal;
    }

    public void setWADLOWToLoad(){
        linearActuator.setTargetPosition( (int)(WADLOWLIFTLOAD *  TICKS_PER_LIFT_IN));

    }

    public void setWADLOWToPartial(){
        linearActuator.setTargetPosition( (int)(WADLOWLIFTPARTIAL *  TICKS_PER_LIFT_IN));

    }

    // WADLOW mechanical reset use in all opmodes Telop and Auto to reset the encoders

    public void WADLOWMechanicalReset() throws InterruptedException {
        linearActuator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // need to swich off encoder to run with a timer
        linearActuator.setPower(-0.3);
        runtime.reset();
        // opmode is not active during init so take that condition out of the while loop
        while ((runtime.seconds() < 2.0)) {

            //Time wasting loop
        }
        sleep(250);
        // set everything back the way is was before reset so encoders can be used
        linearActuator.setPower(0);
        linearActuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearActuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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

    public void turnerSetPosition4() throws InterruptedException {
        turner.setPosition(POS_FOUR);
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

    public void liftToTargetHeight(double height, double timeoutS){

        int newTargetHeight;


        // Ensure that the opmode is still active
        if (opmode.opModeIsActive()) {

            // Determine new target lift height in ticks based on the current position.
            // When the match starts the current position should be reset to zero.

            newTargetHeight = (int)(height *  TICKS_PER_LIFT_IN);
            // Set the target now that is has been calculated
            linearActuator.setTargetPosition(newTargetHeight); //1000 ticks extends lift from 295mm to 530 mm which is 9.25 in per 1000 ticks or 108 ticks per in
            // Turn On RUN_TO_POSITION
            linearActuator.setPower(Math.abs(WADLOWLIFTSPEED));
            // reset the timeout time and start motion.
            runtime.reset();
            linearActuator.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // keep looping while we are still active, and there is time left, and thr motor is running.
            // Note: We use (isBusy() in the loop test, which means that when the motor hits
            // its target position, motion will stop.

            while (opmode.opModeIsActive() &&
                    (runtime.seconds() < timeoutS) && linearActuator.isBusy()) {

                // Display it for the driver.
                //  telemetry.addData("Moving to New Lift Height",  "Running to %7d", newTargetHeight);

                // telemetry.update();
            }

        }
    }



}




