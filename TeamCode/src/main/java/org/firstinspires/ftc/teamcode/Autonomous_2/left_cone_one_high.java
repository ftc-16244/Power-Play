/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.Autonomous_2;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Subsystem2.Gripper2;
import org.firstinspires.ftc.teamcode.Subsystem2.Slide_Trainer2;
import org.firstinspires.ftc.teamcode.Subsystem2.TrackingWheelLifters;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Autonomous
@Disabled
public class left_cone_one_high extends LinearOpMode
{
    OpenCvCamera camera;
    AprilTagDetectionPipeline2 aprilTagDetectionPipeline;

    Gripper2 gripper = new Gripper2(this); // instantiate Felipe (the main implement)
    Slide_Trainer2 slideTrainer = new Slide_Trainer2(this);
    TrackingWheelLifters trackingWheelLifters = new TrackingWheelLifters(this);

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    int LEFT = 1; // Tag ID 1 from the 36h11 family
    int MIDDLE = 2; // Tag ID 2 from the 36h11 family
    int RIGHT = 3; // Tag ID 3 from the 36h11 family

    AprilTagDetection tagOfInterest = null;

    @Override
    public void runOpMode()
    {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 2"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline2(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

        telemetry.setMsTransmissionInterval(50);

        slideTrainer.init(hardwareMap);
        gripper.init(hardwareMap);
        trackingWheelLifters.init(hardwareMap);


        gripper.turnerSetPosition1();
        gripper.gripperClosed();
        trackingWheelLifters.trkWhlsDown(); //tracking wheels must be donw for auto

        /*
         * The INIT-loop:
         * This REPLACES waitForStart!
         */
        while (!isStarted() && !isStopRequested())
        {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if(currentDetections.size() != 0)
            {
                boolean tagFound = false;

                for(AprilTagDetection tag : currentDetections)
                {
                    if(tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT)
                    {
                        tagOfInterest = tag;
                        tagFound = true;
                        break;
                    }
                }

                if(tagFound)
                {
                    telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                    tagToTelemetry(tagOfInterest);
                }
                else
                {
                    telemetry.addLine("Don't see tag of interest :(");

                    if(tagOfInterest == null)
                    {
                        telemetry.addLine("(The tag has never been seen)");
                    }
                    else
                    {
                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                        tagToTelemetry(tagOfInterest);
                    }
                }

            }
            else
            {
                telemetry.addLine("Don't see tag of interest :(");

                if(tagOfInterest == null)
                {
                    telemetry.addLine("(The tag has never been seen)");
                }
                else
                {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                    tagToTelemetry(tagOfInterest);
                }

            }

            telemetry.update();
            sleep(20);
        }

        /*
         * The START command just came in: now work off the latest snapshot acquired
         * during the init loop.
         */

        /* Update the telemetry */
        if(tagOfInterest != null)
        {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
        }
        else
        {
            telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
            telemetry.update();
        }


        // This set of moves is always the same rerdless of 1,2 or 3....
        // even if the April tag fails this will still run.
        // Plus you only have to edit one block of code for all 3 cases.
        // This is to place on the LHS blue high goal
        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        //Positions the robot at the low pole
        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(startPose)
                .strafeLeft(61.5)
                .UNSTABLE_addTemporalMarkerOffset(0,()->{slideTrainer.setSlideLevel5();})
                .UNSTABLE_addTemporalMarkerOffset(0.5,()->{gripper.turnerSetPosition2();})
                .forward(4.5)
                .build();

        drive.followTrajectorySequence(traj1);

        //Delivers the first cone
        sleep(500);
        gripper.gripperOpen();

        TrajectorySequence traj2 = drive.trajectorySequenceBuilder(traj1.end())
                .UNSTABLE_addTemporalMarkerOffset(0.5,()->{gripper.turnerSetPosition1();})
                .UNSTABLE_addTemporalMarkerOffset(1,()->{slideTrainer.setSlideCone5();})
                .back(5.5)
                .strafeRight(12)
                .back(20)
                .waitSeconds(0.5)
                .back(9)
                .UNSTABLE_addTemporalMarkerOffset(1,()->{gripper.gripperClosed();})
                .build();

        drive.followTrajectorySequence(traj2);

        sleep(100);
        slideTrainer.setSlideCone6();

        TrajectorySequence traj3 = drive.trajectorySequenceBuilder(traj2.end())
                .forward(8)
                .UNSTABLE_addTemporalMarkerOffset(0,()->{slideTrainer.setSlideLevel3();})
                .UNSTABLE_addTemporalMarkerOffset(0.5,()->{gripper.turnerSetPosition2();})
                .strafeRight(12)
                .forward(3.5)
                .build();

        drive.followTrajectorySequence(traj3);

        //Delivers the second cone
        sleep(100);
        gripper.gripperOpen();

        TrajectorySequence traj4 = drive.trajectorySequenceBuilder(traj3.end())
                .UNSTABLE_addTemporalMarkerOffset(0.5,()->{gripper.turnerSetPosition1();})
                .UNSTABLE_addTemporalMarkerOffset(1,()->{slideTrainer.setSlideCone4();})
                .back(3.5)
                .strafeLeft(12)
                .back(8)
                .UNSTABLE_addTemporalMarkerOffset(0.5,()->{gripper.gripperClosed();})
                .build();

        drive.followTrajectorySequence(traj4);
        sleep(100);
        slideTrainer.setSlideCone6();

        TrajectorySequence traj5 = drive.trajectorySequenceBuilder(traj4.end())
                .forward(8)
                .UNSTABLE_addTemporalMarkerOffset(0,()->{slideTrainer.setSlideLevel3();})
                .UNSTABLE_addTemporalMarkerOffset(0.5,()->{gripper.turnerSetPosition2();})
                .strafeRight(12)
                .forward(3.5)
                .build();

        drive.followTrajectorySequence(traj5);

        //Delivers the second cone
        sleep(100);
        gripper.gripperOpen();




////// Now decide where to park after cone placement
/*
        if(tagOfInterest.id == LEFT) {
            TrajectorySequence traj6 = drive.trajectorySequenceBuilder(traj5.end())
                    .back(3.5)
                    .UNSTABLE_addTemporalMarkerOffset(0, ()->{gripper.turnerSetPosition1();})
                    .strafeRight(11)
                    .forward(15)
                    //.UNSTABLE_addTemporalMarkerOffset(0.1, ()->{slideTrainer.setSlideLevel1();})
                    .addTemporalMarker(1,()->{slideTrainer.setSlideLevel1();})
                    .back(15)
                    .build();

            drive.followTrajectorySequence(traj6);
        }
        else if(tagOfInterest.id == MIDDLE) {

            TrajectorySequence traj7 = drive.trajectorySequenceBuilder(traj5.end())
                    .back(3.5)
                    .UNSTABLE_addTemporalMarkerOffset(0, ()->{gripper.turnerSetPosition1();})
                    .strafeRight(11)
                    .forward(23)
                    //.UNSTABLE_addTemporalMarkerOffset(0.1, ()->{slideTrainer.setSlideLevel1();})
                    .addTemporalMarker(1,()->{slideTrainer.setSlideLevel1();})
                    .build();

            drive.followTrajectorySequence(traj7);

        }
        else if(tagOfInterest.id == RIGHT){

            TrajectorySequence traj8 = drive.trajectorySequenceBuilder(traj5.end())
                    .back(3.5)
                    .UNSTABLE_addTemporalMarkerOffset(0, ()->{gripper.turnerSetPosition1();})
                    .strafeRight(11)
                    .forward(45)
                    //.UNSTABLE_addTemporalMarkerOffset(0.1, ()->{slideTrainer.setSlideLevel1();})
                    .addTemporalMarker(1.5,()->{slideTrainer.setSlideLevel1();})
                    .build();

            drive.followTrajectorySequence(traj8);



        }

        // make sure to have a null case to try and park if you dont see a tag

        else {

        }


        /* You wouldn't have this in your autonomous, this is just to prevent the sample from ending */
        while (!isStopRequested() && opModeIsActive());
        camera.stopStreaming();
    }

    void tagToTelemetry(AprilTagDetection detection)
    {
        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }
}
