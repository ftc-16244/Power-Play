package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Enums.SlideTrainerState;

import static java.lang.Thread.sleep;
@Config // this is so the dashboard will pick up variables
public class Slide_Trainer {

    //Define Hardware Objects


    public  DcMotorEx       slidemotor;

    Telemetry       telemetry;
    LinearOpMode    opmode; // need content from Linear opmodes here. Elapsed time mainly

    ElapsedTime runtime = new ElapsedTime();

    //Constants Lift
    public  static double           SLIDELIFTSPEED                  = 1.0; //
    public static  double           SLIDELOWERSPEED                 = -0.8; // use the LOAD instead of down. Zero pushes wheels off the mat
    public static final double      SLIDE_LEVEL_1                   = 0; // inches Ground Level
    public static final double      SLIDE_LEVEL_2                   = 7; // inches Cone Loading Level
    public static final double      SLIDE_LEVEL_3                   = 18; // inches 12" Junction
    public static final double      SLIDE_LEVEL_4                   = 30; // inches 24" Junction


    private static final double     TICKS_PER_MOTOR_REV             = 145.1; // goBilda 1150  //312 RPM  537.7
    private static final double     PULLEY_DIA                      = 40; // milimeters
    private static final double     SLIDE_LIFT_DISTANCE_PER_REV     = PULLEY_DIA * Math.PI / 25.4;
    private static final double     TICKS_PER_LIFT_IN               = TICKS_PER_MOTOR_REV / SLIDE_LIFT_DISTANCE_PER_REV;

    public static double            SLIDE_NEW_P                     = 10.0; // 2.5 default
    public static double            SLIDE_NEW_I                     = 0.5;// 0.1 default
    public static double            SLIDE_NEW_D                     = 0.0; // 0.2 default
    public static double            SLIDE_NEW_F                     = 0; // 10 default


    public double  targetHeight;

    SlideTrainerState slideTrainerState = SlideTrainerState.UNKNOWN;


    /// constructor with opmmode passed in
    public Slide_Trainer(LinearOpMode opmode) {
        this.opmode = opmode;

    }

    public void init(HardwareMap hwMap)  {

        // Initialize the slide motor
        slidemotor = hwMap.get(DcMotorEx.class,"slideMotor");
        slidemotor.setDirection(DcMotorEx.Direction.REVERSE);

        PIDFCoefficients pidfOrig = slidemotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        // change coefficients using methods included with DcMotorEx class.
        PIDFCoefficients pidSlide_New = new PIDFCoefficients(SLIDE_NEW_P, SLIDE_NEW_I, SLIDE_NEW_D, SLIDE_NEW_F);
        slidemotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidSlide_New);
        // re-read coefficients and verify change.
        PIDFCoefficients pidModified = slidemotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        //slideTrainer.setSlideLow(); comment out to leave in IDLE state if desired.

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // simple getter and setter methods for use in state machines
    public double getSlidePos(){
        double slidePos;
        slidePos = slidemotor.getCurrentPosition()/ TICKS_PER_LIFT_IN; //returns in inches
        return  slidePos;
    }

    public void  setSlideLevel1(){
        targetHeight = ( SLIDE_LEVEL_1 );
        liftToTargetHeight(targetHeight,3);

    }

    public void setSlideLevel2(){
        targetHeight = ( SLIDE_LEVEL_2);
        liftToTargetHeight(targetHeight,3);

    }

    public void setSlideLevel3(){
        targetHeight = ( SLIDE_LEVEL_3);
        liftToTargetHeight(targetHeight,3);

    }

    public void setSlideLevel4(){
        targetHeight = ( SLIDE_LEVEL_4);
        liftToTargetHeight(targetHeight,3);

    }


    public void slideMechanicalReset(){

        slidemotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // need to swich off encoder to run with a timer
        slidemotor.setPower(SLIDELOWERSPEED);
        runtime.reset();
        // opmode is not active during init so take that condition out of the while loop
        // reset for time allowed or until the limit/ touch sensor is pressed.
        while (runtime.seconds() < 3.0) {

            //Time wasting loop so slide can retract. Loop ends when time expires or tiuch sensor is pressed
        }
        slidemotor.setPower(0);
        runtime.reset();
        while ((runtime.seconds() < 0.25)) {

            //Time wasting loop to let spring relax
        }
        // set everything back the way is was before reset so encoders can be used
        slidemotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        slidemotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //slideTrainerState = SlideTrainerState.IDLE;// once this is done we are at zero power or idling.

    }

    public void liftToTargetHeight(double height, double timeoutS){

        int newTargetHeight;


        // Ensure that the opmode is still active
        if (opmode.opModeIsActive()) {

            // Determine new target lift height in ticks based on the current position.
            // When the match starts the current position should be reset to zero.

            newTargetHeight = (int)(height *  TICKS_PER_LIFT_IN);
            // Set the target now that is has been calculated
            slidemotor.setTargetPosition(newTargetHeight);
            // Turn On RUN_TO_POSITION
            slidemotor.setPower(Math.abs(SLIDELIFTSPEED));
            // reset the timeout time and start motion.
            runtime.reset();
            slidemotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        }


    }

}
