package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class NanoLifts extends OpMode {
    private DcMotor intake;
    double power = 0.1;

    CRServo s1, s2;


    @Override
    public void init() {
        //mecanumDrive.init(hardwareMap, false);
        s1 = hardwareMap.get(CRServo.class, "left");
        s2 = hardwareMap.get(CRServo.class, "right");

        s2.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Robot", "Initialized");
            }

    @Override
    public void loop() {

        if (gamepad1.right_bumper) {
            power = 0.1;
        } else {
            power = 1.0;
        }

        if (gamepad1.a) {
            s1.setPower(power);
            s2.setPower(power);
        }
        else if (gamepad1.b) {
            s1.setPower(-power);
            s2.setPower(-power);
        } else {
            s1.setPower(0);
            s2.setPower(0);
        }

        telemetry.addLine("A = Up");
        telemetry.addLine("B = Down");
        telemetry.addLine("Right bumper toggles speed");
        telemetry.addData("Power", power);

        //telemetry.addData("X", pose2D.getX(DistanceUnit.MM));
        //telemetry.addData("Y", pose2D.getY(DistanceUnit.MM));
        //telemetry.addData("Heading", pose2D.getHeading(AngleUnit.DEGREES));
    }
}

/*
1. Create a separate class in a mechanisms package
2. The goal of this class is to simply return a variety of telemetry messages.
3. Create an opMode that uses this class, and can print out the variety of telementy messages


 */
