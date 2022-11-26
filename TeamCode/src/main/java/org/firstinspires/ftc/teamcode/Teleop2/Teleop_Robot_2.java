package org.firstinspires.ftc.teamcode.Teleop2;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Enums.SlideTrainerState;
import org.firstinspires.ftc.teamcode.Subsystem2.Gripper2;
import org.firstinspires.ftc.teamcode.Subsystem2.Slide_Trainer2;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;


@Config
@TeleOp(group = "Test")

public class Teleop_Robot_2 extends LinearOpMode {


    ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime teleopTimer = new ElapsedTime();
    private double TELEOP_TIME_OUT = 130;

    FtcDashboard dashboard;

    Slide_Trainer2 slideTrainer = new Slide_Trainer2(this);
    SlideTrainerState slideTrainerState = SlideTrainerState.UNKNOWN;

    Gripper2 gripper = new Gripper2(this);

    private TurnerState turnerState = TurnerState.DISABLED; // "m" = class variable that all methods can share. Not a local variable

    //ENUMS
    enum TurnerState {
        ENABLED,
        DISABLED}

    @Override
    public void runOpMode() throws InterruptedException {





        // initialize HW by calling the init method stored in the subsystem
        double  slidePosition;

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap); // this has to be here inside the runopmode. The others go above as class variables
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        slideTrainer.init(hardwareMap);

        gripper.init(hardwareMap);
        gripper.turnerSetPosition1();//back
        gripper.gripperOpen(); // for teleop start with the gripper open. for Auto is needs to be closed to hold the cone
        //gripper.rollersInit();

        //slideTrainer.slideMechanicalReset(); // run reset on init to make sure slide is retracted all the way\
        //slideTrainerState = SlideTrainerState.MECH_RESET; // init puts us in this state, the timew and limit swtch tell when we coe out of it.
        telemetry.addData("Lift State", slideTrainerState);
        telemetry.addData("Turner State", turnerState);
        dashboard = FtcDashboard.getInstance();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        ////////////////////////////////////////////////////////////////////////////////////////////
        // WAIT FOR MATCH TO START
        ///////////////////////////////////////////////////////////////////////////////////////////

        waitForStart();

        while (!isStopRequested() && teleopTimer.time() < TELEOP_TIME_OUT) {
            drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y,
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x
                    )
            );

          // get slide height each time thhrouhg the loop to decide if turner can be used or not
          // this returns height in inches
          slidePosition = slideTrainer.getSlidePos();


          if (slidePosition <= slideTrainer.TURNER_MIN_HEIGHT_2_ROTATE){
             turnerState = TurnerState.DISABLED;

          }
          else { turnerState = TurnerState.ENABLED;

          }


            if (gamepad1.dpad_right) {

            }

            if (gamepad1.dpad_up) {


            }

            if (gamepad1.dpad_down) {


            }

            if (gamepad1.dpad_left) {

            }

            if (gamepad1.back) {
                //slideTrainer.slideMechanicalReset();
                //slideTrainer.targetHeight = 0;
            }
            if (gamepad1.left_trigger > 0.25) {
                gripper.gripperOpen();
            }

            if (gamepad1.right_trigger > 0.25) {
                gripper.gripperClosed();
            }
//// GAMEPAD #2/////////////////////////
            if (gamepad2.a  &&  turnerState == TurnerState.ENABLED) {
                gripper.turnerSetPosition1();
            }

            if (gamepad2.y  &&  turnerState == TurnerState.ENABLED) {
                gripper.turnerSetPosition2();
            }


            if (gamepad2.dpad_left) {
                slideTrainer.setSlideLevel3();
            }

            if (gamepad2.dpad_up) {
                slideTrainer.setSlideLevel4();
            }

            if (gamepad2.dpad_right) {
                slideTrainer.setSlideLevel5();
            }

            if (gamepad2.left_trigger > 0.25) {
                slideTrainer.setSlideLevel2();
            }

            if (gamepad2.right_trigger > 0.25) {
                slideTrainer.setSlideLevel1();
            }

            if (gamepad2.back) {
                slideTrainer.slideMechanicalReset();
                slideTrainer.targetHeight = 0;
            }


        }


    }

    void debounce(long debounceTime) {
        try {
            Thread.sleep(debounceTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}