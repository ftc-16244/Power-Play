package org.firstinspires.ftc.teamcode.Subsystem2;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Enums.SlideTrainerState;

@Config // this is so the dashboard will pick up variables
public class Slide_Trainer2 {

    //Define Hardware Objects


    public  DcMotorEx       slidemotorback;  // config name is "slideMotor"
    public  DcMotorEx       slidemotorfront;  // config name is "slideMotor"

    Telemetry       telemetry;
    LinearOpMode    opmode; // need content from Linear opmodes here. Elapsed time mainly

    ElapsedTime runtime = new ElapsedTime();

    //Constants Lift
    public  static double           SLIDELIFTSPEED                  = 0.70; //
    public  static double           SLIDELIFTSPEEDSLOWER            = 0.4; //
    public static  double           SLIDERESETSPEED                 = -0.2; //
    public static final double      SLIDE_LEVEL_1                   = 0; // inches Load cone level
    public static final double      SLIDE_LEVEL_2                   = 2; // inches ground junction
    public static final double      SLIDE_LEVEL_3                   = 13; // inches 12" Junction
    public static final double      SLIDE_LEVEL_4                   = 23; // inches 24" Junction
    public static final double      SLIDE_LEVEL_5                   = 32.5; // inches 33" Junction
    public static final double      TURNER_MIN_HEIGHT_2_ROTATE      = 11; // preventes turner from moving if it will hit the control hubd

    // Levels for Cone Stack
    public static final double      CONE_6_HEIGHT                   = 10; // inches right below the wall
    public static final double      CONE_5_HEIGHT                   = 5.25; // inches Load cone level
    public static final double      CONE_4_HEIGHT                   = 4; // inches ground junction
    public static final double      CONE_3_HEIGHT                   = 3; // inches 12" Junction
    public static final double      CONE_2_HEIGHT                   = 2.0; // inches 24" Junction



    private static final double     LIFT_HEIGHT_CORRECTION_FACTOR   =   1.13;
    private static final double     TICKS_PER_MOTOR_REV             = 145.1; // goBilda 1150  //312 RPM  537.7
    private static final double     PULLEY_DIA                      = 40; // milimeters
    private static final double     SLIDE_LIFT_DISTANCE_PER_REV     = PULLEY_DIA * Math.PI / (25.4*LIFT_HEIGHT_CORRECTION_FACTOR);
    private static final double     TICKS_PER_LIFT_IN               = TICKS_PER_MOTOR_REV / SLIDE_LIFT_DISTANCE_PER_REV;

    public static double            SLIDE_NEW_P                     = 10.0; // 2.5 default
    public static double            SLIDE_NEW_I                     = 0.5;// 0.1 default
    public static double            SLIDE_NEW_D                     = 0.0; // 0.2 default
    public static double            SLIDE_NEW_F                     = 0; // 10 default


    public double  targetHeight;

    SlideTrainerState slideTrainerState = SlideTrainerState.UNKNOWN;


    /// constructor with opmmode passed in
    public Slide_Trainer2(LinearOpMode opmode) {
        this.opmode = opmode;

    }

    public void init(HardwareMap hwMap)  {

        // Initialize the slide motor
        slidemotorback = hwMap.get(DcMotorEx.class,"slideMotorBack");
        slidemotorfront = hwMap.get(DcMotorEx.class,"slideMotorFront");
        slidemotorback.setDirection(DcMotorEx.Direction.REVERSE);
        slidemotorfront.setDirection(DcMotorEx.Direction.REVERSE);

        PIDFCoefficients pidfOrig = slidemotorback.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        // change coefficients using methods included with DcMotorEx class.
        //PIDFCoefficients pidSlide_New = new PIDFCoefficients(SLIDE_NEW_P, SLIDE_NEW_I, SLIDE_NEW_D, SLIDE_NEW_F);
        //slidemotorback.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidSlide_New);
        //slidemotorfront.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidSlide_New);
        // re-read coefficients and verify change.
        //PIDFCoefficients pidModifiedback = slidemotorback.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        //PIDFCoefficients pidModifiedfront = slidemotorfront.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        //slidemotorback.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidSlide_New);
        //slidemotorfront.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidSlide_New);


        slidemotorback.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        slidemotorfront.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        slidemotorback.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        slidemotorfront.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // simple getter and setter methods for use in state machines
    public double getSlidePos(){
        double slidePos;
        slidePos = slidemotorback.getCurrentPosition()/ TICKS_PER_LIFT_IN; //returns in inches
        return  slidePos;
    }

    public void  setSlideLevel1(){
        targetHeight = ( SLIDE_LEVEL_1 );
        liftToTargetHeight(targetHeight,3, SLIDELIFTSPEEDSLOWER);

    }

    public void setSlideLevel2(){
        targetHeight = ( SLIDE_LEVEL_2);
        liftToTargetHeight(targetHeight,3,  SLIDELIFTSPEEDSLOWER);

    }

    public void setSlideLevel3(){
        targetHeight = ( SLIDE_LEVEL_3);
        liftToTargetHeight(targetHeight,3, SLIDELIFTSPEED);

    }

    public void setSlideLevel4(){
        targetHeight = ( SLIDE_LEVEL_4);
        liftToTargetHeight(targetHeight,3, SLIDELIFTSPEED);

    }

    public void setSlideLevel5(){
        targetHeight = ( SLIDE_LEVEL_5);
        liftToTargetHeight(targetHeight,3, SLIDELIFTSPEED);

    }
    public void setSlideCone6(){
        targetHeight = (CONE_6_HEIGHT);
        liftToTargetHeight(targetHeight,2, SLIDELIFTSPEED);

    }

    public void setSlideCone5(){
        targetHeight = (CONE_5_HEIGHT);
        liftToTargetHeight(targetHeight,2, SLIDELIFTSPEED);

    }

    public void setSlideCone4(){
        targetHeight = (CONE_4_HEIGHT);
        liftToTargetHeight(targetHeight,2, SLIDELIFTSPEED);

    }
      public void setSlideCone3(){
        targetHeight = (CONE_3_HEIGHT);
        liftToTargetHeight(targetHeight,2, SLIDELIFTSPEED);

    }

    public void setSlideCone2(){
        targetHeight = (CONE_2_HEIGHT);
        liftToTargetHeight(targetHeight,2, SLIDELIFTSPEED);

    }


    public void slideMechanicalReset(){

        slidemotorback.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // need to swich off encoder to run with a timer
        slidemotorfront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // need to swich off encoder to run with a timer
        slidemotorback.setPower(SLIDERESETSPEED);
        slidemotorfront.setPower(SLIDERESETSPEED);
        runtime.reset();
        // opmode is not active during init so take that condition out of the while loop
        // reset for time allowed or until the limit/ touch sensor is pressed.
        while (runtime.seconds() < 2.0) {

            //Time wasting loop so slide can retract. Loop ends when time expires or tiuch sensor is pressed
        }
        slidemotorback.setPower(0);
        slidemotorfront.setPower(0);
        runtime.reset();
        while ((runtime.seconds() < 0.25)) {

            //Time wasting loop to let spring relax
        }
        // set everything back the way is was before reset so encoders can be used
        slidemotorback.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        slidemotorfront.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        slidemotorback.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        slidemotorfront.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //slideTrainerState = SlideTrainerState.IDLE;// once this is done we are at zero power or idling.

    }

    public void liftToTargetHeight(double height, double timeoutS, double SLIDELIFTSPEED){

        int newTargetHeight;


        // Ensure that the opmode is still active
        if (opmode.opModeIsActive()) {

            // Determine new target lift height in ticks based on the current position.
            // When the match starts the current position should be reset to zero.

            newTargetHeight = (int)(height *  TICKS_PER_LIFT_IN);
            // Set the target now that is has been calculated
            slidemotorback.setTargetPosition(newTargetHeight);
            slidemotorfront.setTargetPosition(newTargetHeight);
            // Turn On RUN_TO_POSITION
            slidemotorback.setPower(Math.abs(SLIDELIFTSPEED));
            slidemotorfront.setPower(Math.abs(SLIDELIFTSPEED));
            // reset the timeout time and start motion.
            runtime.reset();
            slidemotorback.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slidemotorfront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           // while (opmode.opModeIsActive() &&
             //       (runtime.seconds() < timeoutS) && slidemotorback.isBusy() && slidemotorfront.isBusy()) {
                // holds up execution to let the slide go up to the right place

            // }


        }


    }

}
