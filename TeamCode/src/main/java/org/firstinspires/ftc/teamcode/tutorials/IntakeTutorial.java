package org.firstinspires.ftc.teamcode.tutorials;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Teleop_Intake_Tutorial", group = "Standard Mecanum Driving")
public class IntakeTutorial extends OpMode {
    org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive mecanumDrive = new org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive();
    private DcMotor intake;
    double power;


    @Override
    public void init() {
        mecanumDrive.init(hardwareMap, false);
        intake = hardwareMap.get(DcMotor.class, "intake");


        telemetry.addData("Robot", "Initialized");
            }

    @Override
    public void loop() {
        //update our pinpoint
        //mecanumDrive.updatePinpoint();
        //Pose2D pose2D = mecanumDrive.getPosition();

        mecanumDrive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        if (gamepad1.right_bumper) {
            intake.setPower(0.9);
        } else {
            intake.setPower(0);
        }

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
