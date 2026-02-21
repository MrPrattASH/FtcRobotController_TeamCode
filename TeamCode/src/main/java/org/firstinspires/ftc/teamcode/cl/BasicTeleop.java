package org.firstinspires.ftc.teamcode.cl;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class BasicTeleop extends OpMode {
    DcMotor frontLeft, rearLeft, frontRight, rearRight, flyWheel, intake;
    Servo rightServo, leftServo, rearServo;

    boolean flyWheelActive = false;


    public void init() {
        //here runs when you press "init"
        frontLeft = hardwareMap.get(DcMotor.class, "frontleft");
        rearLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontright");
        rearLeft = hardwareMap.get(DcMotor.class, "backright");

        flyWheel = hardwareMap.get(DcMotor.class, "flywheel"); //rename this as your config file!
        intake = hardwareMap.get(DcMotor.class, "intake");

        rightServo = hardwareMap.get(Servo.class, "rightservo");
        leftServo = hardwareMap.get(Servo.class, "leftservo");
        rearServo = hardwareMap.get(Servo.class, "rearservo");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // TODO reverese intake or flyhweel motor?

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // init servos
        leftServo.setPosition(1);
        rightServo.setPosition(0);
        rearServo.setPosition(0);

        telemetry.addData("Robot", "initialized");
    }

    public void loop() {
        // here runs when you press start!
        if (gamepad1.aWasPressed()) {
            if (flyWheelActive) {
                flyWheelActive = false;
            }
            else {
                flyWheelActive = true;
            }
        }

        if (flyWheelActive) {
            flyWheel.setPower(0.5); // TODO tune this!
        }
        else {
            flyWheel.setPower(0); // stop your flywheel!
        }

        if (gamepad1.right_trigger > 0.5) {
            intake.setPower(0.5); // TODO tune this!
        }
        else {
            intake.setPower(0);
        }

        if (gamepad1.b) { // while holding b, servo moves to "kick" postiion
            leftServo.setPosition(0.5); //TODO tune this
        }
        else {
            leftServo.setPosition(1);
        }

        drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        telemetry.addData("Drive", "Active");
        telemetry.addData("Flywheel State", flyWheelActive);
        telemetry.addData("Flywheel velocity", flyWheel.getPower());


    }

    public void drive(double forward, double strafe, double rotate) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        double maxPower = 1.0;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        // command the motors to move
        frontLeft.setPower(frontLeftPower / maxPower);
        rearLeft.setPower(backLeftPower / maxPower);
        frontRight.setPower(frontRightPower / maxPower);
        rearRight.setPower(backRightPower / maxPower);
    }

}
