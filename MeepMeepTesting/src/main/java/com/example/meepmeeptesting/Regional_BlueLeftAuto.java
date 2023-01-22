package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class Regional_BlueLeftAuto {
    public static void main(String[] args) {
        MeepMeep meepMeep =         new MeepMeep(800);
        Pose2d BlueLeftStart =      new Pose2d(34,61,Math.toRadians(-180));
        Pose2d BlueLeftFirstJunct = new Pose2d(34,45.5,Math.toRadians(-180));
        Pose2d SignalDropOff =      new Pose2d(34,10,Math.toRadians(-180));
        Pose2d BlueStack =          new Pose2d(60,12,Math.toRadians(-180));
        Pose2d BlueStackStaging =    new Pose2d(50,12,Math.toRadians(-180));
        Pose2d BlueSecondJunct =    new Pose2d(29,17,Math.toRadians(130));
        Pose2d Park3 =              new Pose2d(12,12,Math.toRadians(180));

        // Declare our first bot
        //Blue Left Start Pose
        RoadRunnerBotEntity myFirstBot = new DefaultBotBuilder(meepMeep)

                // We set this bot to be blue
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 13)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(BlueLeftStart)
                                .lineToSplineHeading(BlueLeftFirstJunct)
                                //lift and turner
                                .forward(4)
                                // open gripper
                                .waitSeconds(1) //
                                .back(5)
                                // lower lift
                                // strafe and push signal ut of the way
                                .lineToSplineHeading(SignalDropOff)
                                .splineToLinearHeading(BlueStack, Math.toRadians(0))
                                .waitSeconds(1) //
                                .lineToSplineHeading(BlueStackStaging)
                                // go to second junction (med goal)
                                // raise lift
                                .splineToSplineHeading(BlueSecondJunct,Math.toRadians(0))
                                .waitSeconds(1) //
                                // open gripper
                                // go get another cone
                                //turn the turner and lower the lift
                                .splineToSplineHeading(BlueStackStaging,Math.toRadians(0))
                                .lineToSplineHeading(BlueStack)
                                .waitSeconds(1) //
                                //repeat for one 3
                                .lineToSplineHeading(BlueStackStaging)
                                // go to second junction (med goal)
                                // raise lift
                                .splineToSplineHeading(BlueSecondJunct,Math.toRadians(0))
                                .waitSeconds(1) //
                                // open gripper
                                // go get another cone
                                //turn the turner and lower the lift
                                .splineToSplineHeading(BlueStackStaging,Math.toRadians(0))
                                .lineToSplineHeading(BlueStack)
                                .waitSeconds(1) //
                                //repeat for one 4
                                .lineToSplineHeading(BlueStackStaging)
                                // go to second junction (med goal)
                                // raise lift
                                .splineToSplineHeading(BlueSecondJunct,Math.toRadians(0))
                                .waitSeconds(1) //
                                // open gripper
                                // go get another cone
                                //turn the turner and lower the lift
                                .splineToSplineHeading(BlueStackStaging,Math.toRadians(0))
                                .lineToSplineHeading(Park3)
                                .waitSeconds(1) //

                                .build()
                );

        // Declare out second bot
        RoadRunnerBotEntity mySecondBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be red
                .setColorScheme(new ColorSchemeRedDark())
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(30, 30, Math.toRadians(180)))
                                .forward(30)
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)

                // Add both of our declared bot entities
                .addEntity(myFirstBot)
                //.addEntity(mySecondBot)
                .start();
    }
}