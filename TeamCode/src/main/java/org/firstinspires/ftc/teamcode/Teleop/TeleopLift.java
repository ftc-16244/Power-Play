package org.firstinspires.ftc.teamcode.Teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Enums.SlideTrainerState;
import org.firstinspires.ftc.teamcode.Subsystems.Slide_Trainer;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

/**
 * This is a opmode is the first step to making the Viper slide trainer more user friendly. Here
 * we use the gamepad buttone to go to 4 pre-set heights. The servo and the intake motor are still
 * controlled manually. This makes operation better but there is still room for improvement.
 */

@Config
@TeleOp(group = "Test")
@Disabled
public class TeleopLift<slideTrainerState> extends LinearOpMode {

    ElapsedTime runtime = new ElapsedTime();
    FtcDashboard dashboard;

    Slide_Trainer slideTrainer = new Slide_Trainer(this);
    SlideTrainerState slideTrainerState = SlideTrainerState.UNKNOWN;

    double pos;
    //public static double SLIDE_NEW_P = 2.5; // 2.5
    //public static double SLIDE_NEW_I = 0.1;// 0.1
    //public static double SLIDE_NEW_D = 0.2; // 0.2
    //public static double SLIDE_NEW_F = 10; // 10

    @Override
    public void runOpMode() throws InterruptedException {
        // initialize HW by calling the init method stored in the subsystem

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap); // this has to be here inside the runopmode. The others go above as class variables
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        slideTrainer.init(hardwareMap);

        slideTrainer.slideMechanicalReset(); // run reset on init to make sure slide is retracted all the way\
        slideTrainerState = SlideTrainerState.MECH_RESET; // init puts us in this state, the timew and limit swtch tell when we coe out of it.
        telemetry.addData("Lift State", slideTrainerState);
        dashboard = FtcDashboard.getInstance();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        ////////////////////////////////////////////////////////////////////////////////////////////
        // WAIT FOR MATCH TO START
        ///////////////////////////////////////////////////////////////////////////////////////////



        waitForStart();

        while (!isStopRequested()) {
            drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y,
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x
                    )
            );


            if (gamepad1.dpad_right) {
                slideTrainer.setSlideLevel4();;
            }

            if (gamepad1.dpad_up) {
                slideTrainer.setSlideLevel3();

            }

            if (gamepad1.dpad_down) {
                slideTrainer.setSlideLevel1();

            }

            if (gamepad1.dpad_left) {
                slideTrainer.setSlideLevel2();
            }


            /**
             *
             * Gamepad #1 Back Button
             *
             **/
            if (gamepad1.back) {

                slideTrainer.slideMechanicalReset();
                slideTrainer.targetHeight = 0;
            }


        }



    }

    void debounce(long debounceTime){
        try {
            Thread.sleep(debounceTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////  Slide State Switch Case     ////////////////



}