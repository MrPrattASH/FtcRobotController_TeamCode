package org.firstinspires.ftc.teamcode.cl;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Disabled
@TeleOp(name = "FOD", group = "Drive")
public class testCode2 extends OpMode {

    // ---------------------- DRIVE HARDWARE ----------------------
    private DcMotor lf, rf, lb, rb;
    private DcMotor intake1, intake2;
    private DcMotorEx outtake;

    // Shooter (outtake) constants
    private static final double SHOOTER_VEL_FAR   = 1700;  // far shot
    private static final double SHOOTER_VEL_MID   = 1400;  // middle shot
    private static final double SHOOTER_VEL_SHORT = 1250;  // short shot
    private static final double OUTTAKE_STOP_VEL  = 0.0;

    private enum ShooterState {
        OFF,
        FAR,
        MID,
        SHORT
    }

    private ShooterState shooterState = ShooterState.OFF;

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


    private boolean lastX = false;

    private boolean fired = false;
    private boolean prevButton = false;

    private final ElapsedTime timer = new ElapsedTime();

    // Universal flicker timer – only one flick action can be active at a time
    private final ElapsedTime flickTimer = new ElapsedTime();
    private boolean flickActive = false;
    // 0 = none, 1 = flick1, 2 = flick2, 3 = flick3
    private int activeFlick = 0;
    private boolean lastFlick1Btn = false;
    private boolean lastFlick2Btn = false;
    private boolean lastFlick3Btn = false;

    // Pinpoint Odometry Computer
    private GoBildaPinpointDriver pinpoint;

    // ---------------------- FIELD-ORIENTED STATE ----------------------
    // Heading offset so driver can "zero" the field orientation with Y
    private double headingOffsetRad = 0.0;

    // Smoothed heading to reduce jitter
    private double filteredHeadingRad = 0.0;
    private static final double HEADING_ALPHA = 0.2; // 0..1 (lower = smoother)

    // Slow mode toggle
    private double driveScale = 1.0;
    private boolean lastRightBumper = false;

    // Intake state toggle
    private boolean intakeOn = false;
    private boolean lastIntakeRB = false;
    private boolean intakeReverseOn = false;
    private boolean lastIntakeLB = false;

    // Debounce for Y (zero heading)
    private boolean lastY = false;

    // Toggle between field-oriented and robot-centric
    private boolean fieldOrientedEnabled = true;

    @Override
    public void init() {

        // ===== DRIVE MOTORS =====
        lf = hardwareMap.get(DcMotor.class, "lf");
        rf = hardwareMap.get(DcMotor.class, "rf");
        lb = hardwareMap.get(DcMotor.class, "lb");
        rb = hardwareMap.get(DcMotor.class, "rb");
        intake1 = hardwareMap.get(DcMotor.class, "intake1");
        intake2 = hardwareMap.get(DcMotor.class, "intake2");
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
        // ===== PINPOINT SETUP =====
        // Make sure you have a "goBILDAPinpoint" device in the configuration named "pinpoint"
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        // OPTIONAL: set encoder resolution if you're using goBILDA pods
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        // Reset position and IMU at init (robot must be still)
        pinpoint.resetPosAndIMU();

        telemetry.addLine("FOD: Pinpoint Field-Oriented Mecanum + Intake Toggle");
        telemetry.addLine("Gamepad1:");
        telemetry.addLine("  Left stick: move (field or robot-centric)");
        telemetry.addLine("  Right stick X: rotate");
        telemetry.addLine("  Y = zero heading (set current as field-forward)");
        telemetry.addLine("  X = toggle field-oriented / robot-centric");
        telemetry.addLine("  RB = toggle slow mode");
        telemetry.addLine("Gamepad2:");
        telemetry.addLine("  RB = toggle intake on/off");
    }

    @Override
    public void loop() {

        // ==========================================================
        //                     PINPOINT / HEADING
        // ==========================================================
        // Only update heading for faster I2C reads since we only need IMU
        try {
            pinpoint.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);
            //TODO: ReadData
        } catch (Exception e) {
            telemetry.addData("Pinpoint EX", e.getClass().getSimpleName());
        }
        //TODO Pass in AngleUnit.RADIANS
        double rawHeadingRad = wrapToPi(pinpoint.getHeading(AngleUnit.RADIANS));   // already in radians, wrap to [-pi, pi]
        double robotHeadingRad = wrapToPi(rawHeadingRad - headingOffsetRad);

        // Smooth the heading to reduce jitter
        filteredHeadingRad = wrapToPi(
                filteredHeadingRad + HEADING_ALPHA * wrapToPi(robotHeadingRad - filteredHeadingRad)
        );

        // ==========================================================
        //                     DRIVER INPUTS
        // ==========================================================
        // FTC: +Y on stick is down, so invert so pushing up = forward (+Y field)
        double inputY  = -gamepad1.left_stick_y;  // Forward/back
        double inputX  =  gamepad1.left_stick_x;  // Strafe left/right
        double inputRx =  gamepad1.right_stick_x; // Rotation

        // Deadband to avoid drift
        inputY  = applyDeadband(inputY,  0.05);
        inputX  = applyDeadband(inputX,  0.05);
        inputRx = applyDeadband(inputRx, 0.05);

        // ---------------------- SLOW MODE TOGGLE (G1 RB) ----------------------
        boolean rbPressed = gamepad1.right_bumper && !lastRightBumper;
        if (rbPressed) {
            driveScale = (driveScale == 1.0) ? 0.4 : 1.0; // toggle full / 40%
        }
        lastRightBumper = gamepad1.right_bumper;

        inputY  *= driveScale;
        inputX  *= driveScale;
        inputRx *= driveScale;

        // ---------------------- FIELD-ORIENTED TOGGLE (G1 X) ----------------------
        boolean xPressed = gamepad1.x && !lastX;
        if (xPressed) {
            fieldOrientedEnabled = !fieldOrientedEnabled;
        }
        lastX = gamepad1.x;

        // ==========================================================
        //                 FIELD-ORIENTED TRANSFORM
        // ==========================================================
        double driveX;
        double driveY;

        if (fieldOrientedEnabled) {
            // Take joystick (field) and rotate by negative robot heading
            double cosA = Math.cos(-filteredHeadingRad);
            double sinA = Math.sin(-filteredHeadingRad);

            driveX = inputX * cosA - inputY * sinA;
            driveY = inputX * sinA + inputY * cosA;
        } else {
            // Robot-centric: use joystick directly
            driveX = inputX;
            driveY = inputY;
        }

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

        // ==========================================================
        //                      HEADING ZERO (G1 Y)
        // ==========================================================
        boolean yPressed = gamepad1.y && !lastY;
        if (yPressed) {
            // Current heading becomes 0 (field-forward)
            headingOffsetRad = rawHeadingRad;
        }
        lastY = gamepad1.y;

        // ---------------------- INTAKE TOGGLE (G2 RB) ----------------------
        boolean rbPressed2 = gamepad1.right_bumper && !lastIntakeRB;
        if (rbPressed2) {
            // Toggle intake state
            intakeOn = !intakeOn;
        }
        lastIntakeRB = gamepad1.right_bumper;

        // ---------------------- REVERSE INTAKE TOGGLE (G2 LB) ----------------------
        boolean lbPressed2 = gamepad1.left_bumper && !lastIntakeLB;
        if (lbPressed2) {
            intakeReverseOn = !intakeReverseOn;
        }
        lastIntakeLB = gamepad1.left_bumper;

        // PRIORITIZED INTAKE LOGIC
        // intakeOn ALWAYS overrides reverse mode
        if (intakeOn) {
            // Always forward when intakeOn is true
            intake1.setPower(1);
            intake2.setPower(-1);
        } else if (intakeReverseOn) {
            // Only reverse if intakeOn is OFF
            intake1.setPower(-1);
            intake2.setPower(1);
        } else {
            // Off when neither on nor reverse is active
            intake1.setPower(0);
            intake2.setPower(0);
        }

        boolean flick1Btn = gamepad2.x;
        boolean flick2Btn = gamepad2.a;
        boolean flick3Btn = gamepad2.b;

        // Start a new flick only if none is currently active
        if (!flickActive) {
            if (flick1Btn && !lastFlick1Btn) {
                // Flicker 1 up
                flick1.setPosition(flick1Start + delta1);
                flickTimer.reset();
                flickActive = true;
                activeFlick = 1;
            } else if (flick2Btn && !lastFlick2Btn) {
                // Flicker 2 up
                flick2.setPosition(flick2Start + delta2);
                flickTimer.reset();
                flickActive = true;
                activeFlick = 2;
            } else if (flick3Btn && !lastFlick3Btn) {
                // Flicker 3 up
                flick3.setPosition(flick3Start + delta3);
                flickTimer.reset();
                flickActive = true;
                activeFlick = 3;
            }
        }

        // If a flick is active, bring the correct one back down after 300 ms
        if (flickActive && flickTimer.milliseconds() > 300) {
            if (activeFlick == 1) {
                flick1.setPosition(flick1Start);
            } else if (activeFlick == 2) {
                flick2.setPosition(flick2Start);
            } else if (activeFlick == 3) {
                flick3.setPosition(flick3Start);
            }
            flickActive = false;
            activeFlick = 0;
        }

        // Update last button states for edge detection
        lastFlick1Btn = flick1Btn;
        lastFlick2Btn = flick2Btn;
        lastFlick3Btn = flick3Btn;

        // ==========================================================
        //                     SHOOTER CONTROL
        // ==========================================================
        // Presets (Gamepad2):
        //  - dpad_up    = FAR shot   (high RPM, hood ~0.4)
        //  - dpad_left  = MID shot   (mid RPM,  hood ~0.25)
        //  - dpad_down  = SHORT shot (low RPM,  hood ~0.1)
        //  - dpad_right = OFF (shooter stopped)
        boolean farPreset   = gamepad2.dpad_up;
        boolean midPreset   = gamepad2.dpad_left;
        boolean shortPreset = gamepad2.dpad_down;
        boolean offRequest  = gamepad2.dpad_right;

        if (offRequest) {
            shooterState = ShooterState.OFF;
        } else if (farPreset) {
            shooterState = ShooterState.FAR;
        } else if (midPreset) {
            shooterState = ShooterState.MID;
        } else if (shortPreset) {
            shooterState = ShooterState.SHORT;
        }

        switch (shooterState) {
            case OFF:
                // Stop shooter, leave hood where it was
                outtake.setVelocity(OUTTAKE_STOP_VEL);
                break;

            case FAR:
                // Far shot: high RPM, higher hood angle
                outtake.setVelocity(SHOOTER_VEL_FAR);
                hood.setPosition(0.4);
                break;

            case MID:
                // Middle shot: medium RPM, medium hood angle
                outtake.setVelocity(SHOOTER_VEL_MID);
                hood.setPosition(0.25);
                break;

            case SHORT:
                // Short shot: lower RPM, lower hood angle
                outtake.setVelocity(SHOOTER_VEL_SHORT);
                hood.setPosition(0.1);
                break;
        }
        if(gamepad2.y){
            turret.setPosition(0.5);
        } else if (gamepad2.left_bumper) {
            turret.setPosition(0.21);
        }
        else if (gamepad2.right_bumper) {
            turret.setPosition(0.79);
        }

        // ==========================================================
        //                        TELEMETRY
        // ==========================================================
        telemetry.addData("Raw Heading (deg)", Math.toDegrees(rawHeadingRad));
        telemetry.addData("Filtered Heading (deg)", Math.toDegrees(filteredHeadingRad));
        telemetry.addData("FO Enabled", fieldOrientedEnabled);
        telemetry.addData("Drive Scale", driveScale);
        telemetry.addData("lf", lfPower);
        telemetry.addData("rf", rfPower);
        telemetry.addData("lb", lbPower);
        telemetry.addData("rb", rbPower);
        telemetry.addData("Intake On", intakeOn);
        telemetry.addData("Intake Reverse", intakeReverseOn);
        telemetry.addData("Shooter State", shooterState);
        telemetry.addData("Shooter Vel", outtake.getVelocity());
        telemetry.update();
    }

    // ====== Helpers ======

    private double applyDeadband(double value, double db) {
        return (Math.abs(value) < db) ? 0.0 : value;
    }

    /** Wrap an angle to [-pi, pi] without while loops (avoids hangs on huge values). */
    private double wrapToPi(double angle) {
        angle = (angle + Math.PI) % (2.0 * Math.PI);
        if (angle < 0) angle += 2.0 * Math.PI;
        return angle - Math.PI;
    }
}