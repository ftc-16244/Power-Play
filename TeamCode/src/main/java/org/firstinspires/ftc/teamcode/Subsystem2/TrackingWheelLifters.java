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
    public static final double      TRACK_WHL_PERP_UP       = 0.5; //perpendicular wheel up position
    public static final double      TRACK_WHL_PERP_DOWN     = 0; // perpendicular wheel down position

    public static final double      TRACK_WHL_PARAL_UP      = 0.5; //parallel wheel up position
    public static final double      TRACK_WHL_PARAL_DOWN    = 0.25; // parallel wheel down position



    LinearOpMode opmode;
    // Constructor with opmode so we can access opmode features
    public  TrackingWheelLifters(LinearOpMode opmode) {
        this.opmode = opmode;
    }

    public void init(HardwareMap hwMap)  {

              // Initialize tuner the servo that rotates the cone capture bucket
        trkWhlLiftPerp = hwMap.get(Servo.class,"trkWhlLiftPerp");

        trkWhlLiftParal = hwMap.get(Servo.class,"trkWhlLiftParal");

    }


    //Tracking Wheel Lift Methods - Lift up an keep up in Teleop - Put Down in Auto
    public void trkWhlsUp(){
        trkWhlLiftPerp.setPosition(TRACK_WHL_PERP_UP); //
        trkWhlLiftParal.setPosition(TRACK_WHL_PARAL_UP); // may not have this for parallel wheel
    }

    public void trkWhlsDown(){
        trkWhlLiftPerp.setPosition(TRACK_WHL_PERP_DOWN); //
        trkWhlLiftParal.setPosition(TRACK_WHL_PARAL_DOWN); //
    }

}
