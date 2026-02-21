package org.firstinspires.ftc.teamcode.cl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;


@Autonomous(name = "Quick and Dirty Auto", group = "Drive")
public class testQuickandDirtyAuto extends LinearOpMode {

    // ---------------------- DRIVE HARDWARE ----------------------
    private DcMotor lf, rf, lb, rb;
    private DcMotorEx outtake;

    // Shooter (outtake) constants
    private static final double SHOOTER_VEL_MID   = 1400;  // middle shot
    private static final double SHOOTER_VEL_SHORT = 1250;  // short shot


    // Individual Servo Deltas
    private double delta1 = 0.65;
    private double delta2 = -0.65;
    private double delta3 = 0.65;

    private double flick1Start = 0.11;
    private double flick2Start = 0.95;
    private double flick3Start = 0.01;

    private Servo flick1;
    private Servo flick2;
    private Servo flick3;
    private Servo turret;
    private Servo hood;






    @Override
    public void runOpMode() { //init

        // ===== DRIVE MOTORS =====
        lf = hardwareMap.get(DcMotor.class, "lf");
        rf = hardwareMap.get(DcMotor.class, "rf");
        lb = hardwareMap.get(DcMotor.class, "lb");
        rb = hardwareMap.get(DcMotor.class, "rb");

        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        // Set directions – adjust if your robot drives backwards
        lf.setDirection(DcMotorSimple.Direction.FORWARD);
        lb.setDirection(DcMotorSimple.Direction.FORWARD);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        rb.setDirection(DcMotorSimple.Direction.REVERSE);
        // Reverse outtake direction so it spins the other way
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Shooter outtake setup
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(300, 0, 0, 10));

        // ===== Servo SETUP =====
        flick1 = hardwareMap.get(Servo.class, "flick1");
        flick2 = hardwareMap.get(Servo.class, "flick2");
        flick3 = hardwareMap.get(Servo.class, "flick3");
        turret = hardwareMap.get(Servo.class, "turret");
        hood = hardwareMap.get(Servo.class, "hood");


        flick1.setPosition(flick1Start);
        flick2.setPosition(flick2Start);
        flick3.setPosition(flick3Start);
        turret.setPosition(0.5);
        hood.setPosition(0.1);


        telemetry.addLine("Robot init complete");
        telemetry.update(); // this will actually make the telemetry display

        waitForStart();

        while (opModeIsActive()) { // start of our auto code
             /*
             you have to set up your turret at the correct angle at the start!
             1. back up a specficied distance
             2. spin up flywheel
             3. when at speed, launch 1 ball
             4. wait a breif peried, repeat x3
             5. move left off the launch line
              */

            // back up a specficed distance
            drive(-0.5, 0, 0);
            sleep(2000); // TUNE THIS
            drive(0, 0, 0); // stop the drive motors

            //outtake.setVelocity(SHOOTER_VEL_MID);
            // hood.setPosition(0.25); // mid hood angle

            // charge up the flywheel
            // Short shot: lower RPM, lower hood angle
            outtake.setVelocity(SHOOTER_VEL_SHORT);
            hood.setPosition(0.1);
            sleep(4000); // TUNE THIS wait time for the flywheel to get up to speed!

            // run each flick  twice for error control
            flick1.setPosition(flick1Start + delta1);
            sleep(300); // time for a flick to come up may TUNE this?
            flick1.setPosition(flick1Start);
            sleep(300); // may not be necessary, but we'll give reset time just in case
            flick1.setPosition(flick1Start + delta1);
            sleep(300); // time for a flick to come up may TUNE this?
            flick1.setPosition(flick1Start);
            sleep(300); // may not be necessary, but we'll give reset time just in case

            sleep(1000); // give flywheel time to get back up to speed! TUNE THIS

            flick2.setPosition(flick2Start + delta2);
            sleep(300);
            flick2.setPosition(flick2Start);
            sleep(300);
            flick2.setPosition(flick2Start + delta2);
            sleep(300);
            flick2.setPosition(flick2Start);
            sleep(300);

            sleep(1000); // give flywheel time to get back up to speed! TUNE THIS

            flick3.setPosition(flick3Start + delta3);
            sleep(300);
            flick3.setPosition(flick3Start);
            sleep(300);
            flick3.setPosition(flick3Start + delta3);
            sleep(300);
            flick3.setPosition(flick3Start);
            sleep(300);

            //stop the flywheel!
            outtake.setVelocity(0);

            drive(0, 0.5, 0);
            sleep(1000); // TUNE THIS make sure you get off the launch line!
            drive(0, 0, 0);

            requestOpModeStop(); // end the auto program!
        }
    }


    public void drive(double driveY, double driveX, double inputRx) {
            // ==========================================================
            //                   MECANUM KINEMATICS
            // ==========================================================
            double lfPower = driveY + driveX + inputRx;
            double lbPower = driveY - driveX + inputRx;
            double rfPower = driveY - driveX - inputRx;
            double rbPower = driveY + driveX - inputRx;

            // Normalize to keep |power| <= 1
            double max = Math.max(1.0,
                    Math.max(Math.abs(lfPower),
                            Math.max(Math.abs(lbPower),
                                    Math.max(Math.abs(rfPower), Math.abs(rbPower)))));

            lfPower /= max;
            lbPower /= max;
            rfPower /= max;
            rbPower /= max;

            // ---------------------- SEND TO MOTORS ----------------------
            lf.setPower(lfPower);
            lb.setPower(lbPower);
            rf.setPower(rfPower);
            rb.setPower(rbPower);
        }
}