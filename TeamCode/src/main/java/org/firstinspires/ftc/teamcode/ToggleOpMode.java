package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@TeleOp
public class ToggleOpMode extends OpMode {

    TestBench testBench = new TestBench();
    boolean aAlreadyPressed;
    boolean motorOn;

    @Override
    public void init() {
        testBench.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.a && !aAlreadyPressed) { // money part is NOT aAlreadyPressed
            motorOn = !motorOn;
            telemetry.addData("Motor State", motorOn);
            if (motorOn) {
                testBench.setMotorSpeed(0.5);
            }
            else {
                testBench.setMotorSpeed(0);
            }
        }

        aAlreadyPressed = gamepad1.a;
        telemetry.addData("A Button", aAlreadyPressed);
    }
}
