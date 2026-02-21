/*   MIT License
 *   Copyright (c) [2025] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */


package org.firstinspires.ftc.teamcode.cl;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

/*
 * This file includes a teleop (driver-controlled) file for the goBILDA® Robot in 3 Days for the
 * 2025-2026 FIRST® Tech Challenge season DECODE™!
 */

@Autonomous(name = "DECODE Ri3D AUTO", group = "StarterBot")
//@Disabled
public class LimeLightAutoEncoder extends LinearOpMode {
    final double FEED_TIME_SECONDS = 0.80; //The feeder servos run this long when a shot is requested.
    final double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    final double FULL_SPEED = 1.0;

    final double LAUNCHER_CLOSE_TARGET_VELOCITY = 1350; //in ticks/second for the close goal.
    final double LAUNCHER_CLOSE_MIN_VELOCITY = 1325; //minimum required to start a shot for close goal.

    final double LAUNCHER_FAR_TARGET_VELOCITY = 1540; //Target velocity for far goal
    final double LAUNCHER_FAR_MIN_VELOCITY = 1465; //minimum required to start a shot for far goal.
    private final ElapsedTime runtime = new ElapsedTime();

    double launcherTarget = LAUNCHER_CLOSE_TARGET_VELOCITY; //These variables allow
    double launcherMin = LAUNCHER_CLOSE_MIN_VELOCITY;
    double power;
    double forward;
    double strafe;
    double rotate;
    final double LEFT_POSITION = 0.2962; //the left and right position for the diverter servo
    final double RIGHT_POSITION = 0;
    final double driveMotorRatio = (1+(46.0/17.0)) * (1+(46.0/11.0)) * 28;

    // Declare OpMode members.
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotorEx leftLauncher = null;
    private DcMotorEx rightLauncher = null;
    private DcMotor intake = null;
    private DcMotor Lifter = null;
    private CRServo leftFeeder = null;
    private CRServo rightFeeder = null;
    private Servo diverter = null;
    private Servo light = null;
    private Servo lght = null;
    private Servo diverterLight = null;
    private Servo LifterLock = null;

    ElapsedTime leftFeederTimer = new ElapsedTime();
    ElapsedTime rightFeederTimer = new ElapsedTime();


    private enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    private LaunchState leftLaunchState;
    private LaunchState rightLaunchState;


    private enum DiverterDirection {
        LEFT,
        RIGHT;
    }

    private DiverterDirection diverterDirection = DiverterDirection.LEFT;

    private enum IntakeState {
        ON,
        OFF;
    }

    private IntakeState intakeState = IntakeState.OFF;

    private enum LauncherDistance {
        CLOSE,
        FAR;
    }

    private LauncherDistance launcherDistance = LauncherDistance.CLOSE;

    private enum FlyWheelState {
        OFF,
        ON;
    }

    private FlyWheelState flywheelstate = FlyWheelState.OFF;
    // Setup a variable for each drive wheel to save power level for telemetry
    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    private int lfpos;
    private int rfpos;
    private int lrpos;
    private int rrpos;

    private Limelight3A limelight3A;

    @Override
    public void runOpMode() {
        leftLaunchState = LaunchState.IDLE;
        rightLaunchState = LaunchState.IDLE;

        leftFrontDrive = hardwareMap.get(DcMotor.class, "l_f");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "r_f");
        leftBackDrive = hardwareMap.get(DcMotor.class, "l_r");
        rightBackDrive = hardwareMap.get(DcMotor.class, "r_r");
        leftLauncher = hardwareMap.get(DcMotorEx.class, "r_l");
        rightLauncher = hardwareMap.get(DcMotorEx.class, "l_l");
        intake = hardwareMap.get(DcMotor.class, "ntk");
        Lifter = hardwareMap.get(DcMotor.class, "lftr");
        leftFeeder = hardwareMap.get(CRServo.class, "r_feed");
        rightFeeder = hardwareMap.get(CRServo.class, "l_feed");
        diverter = hardwareMap.get(Servo.class, "dvrtr");
        light = hardwareMap.get(Servo.class, "dstnc");
        lght = hardwareMap.get(Servo.class, "dstnc1");
        diverterLight = hardwareMap.get(Servo.class, "dvrtrlght") ;
        LifterLock = hardwareMap.get(Servo.class, "lftrlck") ;

        /*
         * To drive forward, most robots need the motor on one side to be reversed,
         * because the axles point in opposite directions. Pushing the left stick forward
         * MUST make robot go forward. So adjust these two lines based on your first test drive.
         * Note: The settings here assume direct drive on left and right wheels. Gear
         * Reduction or 90 Deg drives may require direction flips
         */
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        leftLauncher.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        leftLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lfpos = 0;
        rfpos = 0;
        lrpos = 0;
        rrpos = 0;

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        leftFrontDrive.setZeroPowerBehavior(BRAKE);
        rightFrontDrive.setZeroPowerBehavior(BRAKE);
        leftBackDrive.setZeroPowerBehavior(BRAKE);
        rightBackDrive.setZeroPowerBehavior(BRAKE);
        leftLauncher.setZeroPowerBehavior(BRAKE);
        rightLauncher.setZeroPowerBehavior(BRAKE);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);

        leftLauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));
        rightLauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

        /*
         * Much like our drivetrain motors, we set the left feeder servo to reverse so that they
         * both work to feed the ball into the robot.
         */
        rightFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
         * Tell the driver that initialization is complete.
         */

        // init the limelight
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(0); // 21,22,23 obleisk marker pipeline
        limelight3A.start(); // begins capturing

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        // start limelight here if battery is getting too low

        //TODO WRITE CODE HERE
        //use encoderDrive(w,x,y,z) 
        //w is forward and backward in cm
        //x is side to side in cm
        //y is rotate in degrees
        //z is speed at which you move 
        //use shoot and the the color and then the distance with open then close parenthesis to shoot a ball
        //ex: shootPurpleClose()   or   shootGreenFar()
        diverter.setPosition(LEFT_POSITION);
        leftLauncher.setVelocity(LAUNCHER_CLOSE_TARGET_VELOCITY-100);
        rightLauncher.setVelocity(LAUNCHER_CLOSE_TARGET_VELOCITY-100);

        encoderDrive(-142,0,0,0.6); // backup to "shooting position"

        // TODO Rotate robot to face the obelisk
        encoderDrive(0,0,45,0.6); //TODO this is a straight up guess
        //do stuff with the limelight

        // read the lime light
        // start a 3sec timer safety check if we get stuck in our loop for too long
        int aprilTagID = 0;
        runtime.reset();
        while (aprilTagID != 21 || aprilTagID != 22 || aprilTagID != 23 || runtime.seconds() <3) {
            LLResult llResult = limelight3A.getLatestResult(); // get latest result
            if (llResult != null & llResult.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = llResult.getFiducialResults();
                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    aprilTagID = fiducial.getFiducialId(); // get current April Tag ID reading
                    telemetry.addData("ID", aprilTagID);
                    telemetry.update();
            }
            }
        }




        //TODO Rotate back to face the goal
        encoderDrive(0,0,-45,0.6);

        if (aprilTagID == 21) {
            shootMotif21();
        }
        else if (aprilTagID == 22) {
            // shoot motif 22
        }
        else {
            //shoot motif 23
        }
        /*
        shootPurpleClose();
        intake.setPower(1);
        sleep(1000);
        shootPurpleClose();
        shootGreenClose();
        sleep(200);
        */


        // ------------ pickup spike #1 artifacts ------------
        encoderDrive(7.07,0,0,0.6);
        encoderDrive(0,0,-45,0.5);
        //encoderDrive(0,-5,0,0.6);
        diverter.setPosition(RIGHT_POSITION);
        encoderDrive(50,0,0,0.6);
        encoderDrive(11,0,0,0.6);
        sleep(100);
        encoderDrive(-5,0,0,0.6);
        diverter.setPosition(LEFT_POSITION);
        sleep(300);
        encoderDrive(29,0,0,0.6);
        sleep(100);
        //encoderDrive(-5,0,0,0.6);
        //diverter.setPosition(LEFT_POSITION);
        //sleep(300);
        //encoderDrive(17,0,0,0.6);
        sleep(400);
        intake.setPower(0);
        encoderDrive(-85,0,0,0.6);
        //encoderDrive(0,5,0,0.6);
        encoderDrive(0,0,45,0.5);
        encoderDrive(-7.07,0,0,0.6);

        // -----------2nd shoot cycle --------------
        // TODO repeat the same april tag logic.
        shootPurpleClose();
        intake.setPower(-1);
        sleep(100);
        intake.setPower(1);
        shootPurpleClose();
        shootGreenClose();
        sleep(200);
        encoderDrive(0,0,-115,0.5);
        encoderDrive(70,0,0,0.6);
        encoderDrive(0,0,90,0.5);
        encoderDrive(75,0,0,0.6);
        encoderDrive(11,0,0,0.6);
        sleep(100);
        encoderDrive(-5,0,0,0.6);
        diverter.setPosition(RIGHT_POSITION);
        sleep(300);
        encoderDrive(17,0,0,0.6);
        sleep(100);
        encoderDrive(-5,0,0,0.6);
        diverter.setPosition(LEFT_POSITION);
        sleep(300);
        encoderDrive(17,0,0,0.6);
        sleep(400);
        intake.setPower(0);
        encoderDrive(-117,0,0,0.6);
        encoderDrive(0,0,90,0.5);
        encoderDrive(70,0,0,0.6);
        encoderDrive(0,0,-45,0.5);
        shootPurpleClose();
        intake.setPower(-1);
        sleep(100);
        intake.setPower(1);
        shootPurpleClose();
        shootGreenClose();
        sleep(200);
        encoderDrive(0, 60, 0, 1);



        requestOpModeStop();


    }
    public void shootPurpleClose() {
        leftLauncher.setVelocity(LAUNCHER_CLOSE_TARGET_VELOCITY-100);
        rightLauncher.setVelocity(LAUNCHER_CLOSE_TARGET_VELOCITY-100);
        while (leftLauncher.getVelocity() < LAUNCHER_CLOSE_MIN_VELOCITY-100) {
            // waiting to get up to speed
        }
        leftFeeder.setPower(FULL_SPEED);

        runtime.reset();
        while (opModeIsActive() && runtime.seconds() < FEED_TIME_SECONDS) {
            // loop until feed time is done
        }

        leftFeeder.setPower(STOP_SPEED);
    }

    public void shootGreenClose() {
        leftLauncher.setVelocity(LAUNCHER_CLOSE_TARGET_VELOCITY-100);
        rightLauncher.setVelocity(LAUNCHER_CLOSE_TARGET_VELOCITY-100);
        while (rightLauncher.getVelocity() < LAUNCHER_CLOSE_MIN_VELOCITY-100) {
            // waiting to get up to speed
        }
        rightFeeder.setPower(FULL_SPEED);

        runtime.reset();
        while (opModeIsActive() && runtime.seconds() < FEED_TIME_SECONDS) {
            // loop until feed time is done
        }

        rightFeeder.setPower(STOP_SPEED);
    }

    public void shootMotif21() {
        shootGreenClose();
        shootPurpleClose();
        // run intake before a 2nd purple shot!
        intake.setPower(1);
        sleep(1000);
        shootPurpleClose();
        sleep(200);
    }

    public void shootPurpleFar() {
        leftLauncher.setVelocity(LAUNCHER_FAR_TARGET_VELOCITY);
        rightLauncher.setVelocity(LAUNCHER_FAR_TARGET_VELOCITY);
        while (leftLauncher.getVelocity() < LAUNCHER_FAR_MIN_VELOCITY) {
            // waiting to get up to speed
        }
        leftFeeder.setPower(FULL_SPEED);

        runtime.reset();
        while (opModeIsActive() && runtime.seconds() < FEED_TIME_SECONDS) {
            // loop until feed time is done
        }

        leftFeeder.setPower(STOP_SPEED);
    }

    public void shootGreenFar() {
        leftLauncher.setVelocity(LAUNCHER_FAR_TARGET_VELOCITY);
        rightLauncher.setVelocity(LAUNCHER_FAR_TARGET_VELOCITY);
        while (leftLauncher.getVelocity() < LAUNCHER_FAR_MIN_VELOCITY) {
            // waiting to get up to speed
        }
        rightFeeder.setPower(FULL_SPEED);

        runtime.reset();
        while (opModeIsActive() && runtime.seconds() < FEED_TIME_SECONDS) {
            // loop until feed time is done
        }

        rightFeeder.setPower(STOP_SPEED);
    }

    //4.9386772059306
    private void encoderDrive(double frwrd, double strf, double rtt, double speed) {
        forward = frwrd * (driveMotorRatio / (10.4 * 3.14159));
        strafe = strf * 1.4 * (driveMotorRatio / (10.4 * 3.14159));
        rotate = ((rtt * 7.17) / 360) * driveMotorRatio;
        lfpos = lfpos + (int)Math.round(forward + strafe + rotate);
        rfpos = rfpos + (int)Math.round(forward - strafe - rotate);
        lrpos = lrpos + (int)Math.round(forward - strafe + rotate);
        rrpos = rrpos + (int)Math.round(forward + strafe - rotate);
        leftFrontDrive.setTargetPosition(lfpos);
        rightFrontDrive.setTargetPosition(rfpos);
        leftBackDrive.setTargetPosition(lrpos);
        rightBackDrive.setTargetPosition(rrpos);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);
        while(opModeIsActive() && leftFrontDrive.isBusy() && rightFrontDrive.isBusy() && leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
            idle ();
            telemetry.addData("LF Target/Current", "%d / %d",
                    lfpos, leftFrontDrive.getCurrentPosition());
            telemetry.addData("RF Target/Current", "%d / %d",
                    rfpos, rightFrontDrive.getCurrentPosition());
            telemetry.addData("LB Target/Current", "%d / %d",
                    lrpos, leftBackDrive.getCurrentPosition());
            telemetry.addData("RB Target/Current", "%d / %d",
                    rrpos, rightBackDrive.getCurrentPosition());
            telemetry.addData("LF Target/Actual (cm)", "%.1f / %.1f",
                    lfpos * (10.4 * Math.PI / driveMotorRatio),
                    leftFrontDrive.getCurrentPosition() * (10.4 * Math.PI / driveMotorRatio));
            telemetry.addData("RF Target/Actual (cm)", "%.1f / %.1f",
                    rfpos * (10.4 * Math.PI / driveMotorRatio),
                    rightFrontDrive.getCurrentPosition() * (10.4 * Math.PI / driveMotorRatio));
            telemetry.addData("LB Target/Actual (cm)", "%.1f / %.1f",
                    lrpos * (10.4 * Math.PI / driveMotorRatio),
                    leftBackDrive.getCurrentPosition() * (10.4 * Math.PI / driveMotorRatio));
            telemetry.addData("RB Target/Actual (cm)", "%.1f / %.1f",
                    rrpos * (10.4 * Math.PI / driveMotorRatio),
                    rightBackDrive.getCurrentPosition() * (10.4 * Math.PI / driveMotorRatio));
            telemetry.update();


        }
    }


    //void mecanumDriveEncoders(double leftFrontTarget, double leftBackTarget, double rightFrontTarget, double rightBackTarget, double speed) {

        /* the denominator is the largest motor power (absolute value) or 1
         * This ensures all the powers maintain the same ratio,
         * but only if at least one is out of the range [-1, 1]
         
        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(rotate), 1);

        leftFrontPower = (-forward - strafe - rotate)  / denominator;
        rightFrontPower = (forward - strafe - rotate) / denominator;
        leftBackPower = (-forward + strafe - rotate)  / denominator;
        rightBackPower = (forward + strafe - rotate) / denominator;

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

    }*/

}