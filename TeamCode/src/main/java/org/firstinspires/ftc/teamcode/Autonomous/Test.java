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

package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Subsystems.Slide_Trainer;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Gripper;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

import java.util.ArrayList;

@Autonomous
@Disabled
public class Test extends LinearOpMode
{
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    Gripper gripper = new Gripper(this); // instantiate Felipe (the main implement)
    Slide_Trainer slideTrainer = new Slide_Trainer(this);

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
    public static double DISTANCE = 22;
    public static double DISTANCE_HALF = DISTANCE/2.0;
    public static double DISTANCE1 = 30;
    public static double LEFTDISTANCE = 30;
    public static double RIGHTDISTANCE = 30;

    AprilTagDetection tagOfInterest = null;

    @Override
    public void runOpMode()
    {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

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

        gripper.turnerSetPosition1();
        gripper.rollersFinal();

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

        if(tagOfInterest.id == LEFT) {
            Pose2d startPose = new Pose2d(-36, -50, Math.toRadians(90));

            drive.setPoseEstimate(startPose);
            TrajectorySequence traj1 = drive.trajectorySequenceBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(-36, -20,Math.toRadians(0)))
                    .waitSeconds(0.5)
                    .lineToLinearHeading(new Pose2d(-36, 10,Math.toRadians(0)))
                    .addTemporalMarker(2,()->{slideTrainer.setSlideLevel5();})
                    .addTemporalMarker(4,()->{gripper.turnerSetPosition2();})
                    .waitSeconds(1)
                    .forward(8)
                    .waitSeconds(1)
                    .addTemporalMarker(7,()->{gripper.rollersInit();})
                    .waitSeconds(1)
                    .back(8)
                    .waitSeconds(1)
                    .addTemporalMarker(10,()->{gripper.turnerSetPosition1();})
                    .addTemporalMarker(11,()->{slideTrainer.setSlideLevel3();})

                    .build();
            TrajectorySequence traj2 = drive.trajectorySequenceBuilder(traj1.end())
                    .strafeRight(11)
                    .waitSeconds(0.5)
                    .back(22)
                    .addTemporalMarker(3,()->{slideTrainer.setSlideLevel2();})
                    .addTemporalMarker(4,()->{gripper.rollersFinal();})
                    .build();

            TrajectorySequence traj3 = drive.trajectorySequenceBuilder(traj2.end())
                    .waitSeconds(1)
                    .addTemporalMarker(1.25,()->{slideTrainer.setSlideLevel5();})
                    .forward(22)
                    .strafeLeft(12)
                    .addTemporalMarker(3,()->{gripper.turnerSetPosition2();})
                    .forward(8)
                    .addTemporalMarker(5,()->{gripper.rollersInit();})
                    .back(8)
                    .addTemporalMarker(7,()->{gripper.turnerSetPosition1();})
                    .strafeRight(11)
                    .addTemporalMarker(8,()->{slideTrainer.setSlideLevel1();})
                    .back(22)


                    .build();


            drive.followTrajectorySequence(traj1);
            drive.followTrajectorySequence(traj2);
            drive.followTrajectorySequence(traj3);


        }
        else if(tagOfInterest.id == MIDDLE) {
            Pose2d startPose = new Pose2d(-36, -50, Math.toRadians(90));

            drive.setPoseEstimate(startPose);
            TrajectorySequence traj1 = drive.trajectorySequenceBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(-36, -20,Math.toRadians(0)))
                    .waitSeconds(0.5)
                    .lineToLinearHeading(new Pose2d(-36, 10,Math.toRadians(0)))
                    .addTemporalMarker(2,()->{slideTrainer.setSlideLevel5();})
                    .addTemporalMarker(4,()->{gripper.turnerSetPosition2();})
                    .waitSeconds(1)
                    .forward(8)
                    .waitSeconds(1)
                    .addTemporalMarker(7,()->{gripper.rollersInit();})
                    .waitSeconds(1)
                    .back(8)
                    .waitSeconds(1)
                    .addTemporalMarker(10,()->{gripper.turnerSetPosition1();})
                    .addTemporalMarker(11,()->{slideTrainer.setSlideLevel3();})

                    .build();
            TrajectorySequence traj2 = drive.trajectorySequenceBuilder(traj1.end())
                    .strafeRight(11)
                    .waitSeconds(0.5)
                    .back(22)
                    .addTemporalMarker(3,()->{slideTrainer.setSlideLevel2();})
                    .addTemporalMarker(4,()->{gripper.rollersFinal();})
                    .build();

            TrajectorySequence traj3 = drive.trajectorySequenceBuilder(traj2.end())
                    .waitSeconds(1)
                    .addTemporalMarker(1.25,()->{slideTrainer.setSlideLevel5();})
                    .forward(22)
                    .strafeLeft(12)
                    .addTemporalMarker(3,()->{gripper.turnerSetPosition2();})
                    .forward(8)
                    .addTemporalMarker(5,()->{gripper.rollersInit();})
                    .back(7)
                    .addTemporalMarker(7,()->{gripper.turnerSetPosition1();})
                    .strafeRight(11)
                    .addTemporalMarker(8,()->{slideTrainer.setSlideLevel1();})


                    .build();


            drive.followTrajectorySequence(traj1);
            drive.followTrajectorySequence(traj2);
            drive.followTrajectorySequence(traj3);
        }
        else if(tagOfInterest.id == RIGHT){
            Pose2d startPose = new Pose2d(-36, -50, Math.toRadians(90));

            drive.setPoseEstimate(startPose);
            TrajectorySequence traj1 = drive.trajectorySequenceBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(-36, -20,Math.toRadians(0)))
                    .waitSeconds(0.5)
                    .lineToLinearHeading(new Pose2d(-36, 10,Math.toRadians(0)))
                    .addTemporalMarker(2,()->{slideTrainer.setSlideLevel5();})
                    .addTemporalMarker(4,()->{gripper.turnerSetPosition2();})
                    .waitSeconds(1)
                    .forward(8)
                    .waitSeconds(1)
                    .addTemporalMarker(7,()->{gripper.rollersInit();})
                    .waitSeconds(1)
                    .back(8)
                    .waitSeconds(1)
                    .addTemporalMarker(10,()->{gripper.turnerSetPosition1();})
                    .addTemporalMarker(11,()->{slideTrainer.setSlideLevel3();})

                    .build();
            TrajectorySequence traj2 = drive.trajectorySequenceBuilder(traj1.end())
                    .strafeRight(11)
                    .waitSeconds(0.5)
                    .back(22)
                    .addTemporalMarker(3,()->{slideTrainer.setSlideLevel2();})
                    .addTemporalMarker(4,()->{gripper.rollersFinal();})
                    .build();

            TrajectorySequence traj3 = drive.trajectorySequenceBuilder(traj2.end())
                    .waitSeconds(1)
                    .addTemporalMarker(1.25,()->{slideTrainer.setSlideLevel5();})
                    .forward(22)
                    .strafeLeft(12)
                    .addTemporalMarker(3,()->{gripper.turnerSetPosition2();})
                    .forward(8)
                    .addTemporalMarker(5,()->{gripper.rollersInit();})
                    .back(8)
                    .addTemporalMarker(7,()->{gripper.turnerSetPosition1();})
                    .strafeRight(13)
                    .addTemporalMarker(8,()->{slideTrainer.setSlideLevel1();})
                    .forward(25)


                    .build();


            drive.followTrajectorySequence(traj1);
            drive.followTrajectorySequence(traj2);
            drive.followTrajectorySequence(traj3);
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
