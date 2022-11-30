package org.firstinspires.ftc.teamcode.Subsystem2;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class TrackingWheelLifters {

    //Define Hardware Objects
    public Servo            trkWhlLiftPerp             = null;
    public Servo            trkWhlLiftParal             = null;


    ElapsedTime runtime = new ElapsedTime();

    //Constants for rollers
    public static final double      TRACK_WHL_PERP_UP    = 0.5; // not gripped
    public static final double       TRACK_WHL_PERP_DOWN = 0.0; // not gripped

    public static final double      TRACK_WHL_PARAL_UP    = 0.5; // not gripped
    public static final double       TRACK_WHL_PARAL_DOWN      = 0.25; // not gripped



    LinearOpMode opmode;
    // Constructor with opmode so we can access opmode features
    public  TrackingWheelLifters(LinearOpMode opmode) {
        this.opmode = opmode;
    }

    public void init(HardwareMap hwMap)  {

              // Initialize tuner the servo that rotates the cone capture bucket
        trkWhlLiftPerp = hwMap.get(Servo.class,"trkWhlLiftPerp");// port 4
        //turner.setPosition(BACK);

        // Initialize the left gripper
        trkWhlLiftParal = hwMap.get(Servo.class,"trkWhlLiftParal"); //port 0


    }


    //Tracking WHeel Lift Methods - Lift up an keep up in Teleop - Put Down in Auto
    public void trkWhlsUp(){
        trkWhlLiftPerp.setPosition(TRACK_WHL_PERP_UP); // back
        trkWhlLiftParal.setPosition(TRACK_WHL_PARAL_UP); // back
    }

    public void trkWhlsDown(){
        trkWhlLiftPerp.setPosition(TRACK_WHL_PERP_DOWN); // back
        trkWhlLiftParal.setPosition(TRACK_WHL_PARAL_DOWN); // back
    }

}
