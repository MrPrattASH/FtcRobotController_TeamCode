package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.ArcadeDriveTwoWheel;
import org.firstinspires.ftc.teamcode.mechanisms.TelescopicLift;

@TeleOp
@Disabled
public class ExampleLevelTwoBase extends OpMode {
    ArcadeDriveTwoWheel drive = new ArcadeDriveTwoWheel();
    TelescopicLift telescopicLift = new TelescopicLift();
    double leftJoyY, rightJoyX, rightJoyY;
    boolean aButton, bButton;

    @Override
    public void init() {
        telescopicLift.init(hardwareMap);
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {
        leftJoyY = -gamepad1.left_stick_y;
        rightJoyX = gamepad1.right_stick_x/2;
        rightJoyY = gamepad1.right_stick_y;
        aButton = gamepad1.a;
        bButton = gamepad1.b;

        drive.drive(rightJoyY, rightJoyX);
        telemetry.addData("Lift Motor Position", telescopicLift.getCurrentPosition());
        if (aButton) {
            telescopicLift.setTargetPosition(3500,0.5);
        }
        else if (bButton) {
            telescopicLift.setTargetPosition(0, 0.5);
        }
    }
}
