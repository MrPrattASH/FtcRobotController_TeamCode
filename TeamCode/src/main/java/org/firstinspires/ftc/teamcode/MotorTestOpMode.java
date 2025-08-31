package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@TeleOp
public class MotorTestOpMode extends OpMode {
    TestBench testBench = new TestBench();

    @Override
    public void init() {
        testBench.init(hardwareMap);
    }
        double squareInputWithSign(double input){
            double output = input * input;
            if (input < 0) {
                output *= -1;
            }
            return output;
        }



    @Override
    public void loop() {
        double motorSpeed = -gamepad1.left_stick_y;

        motorSpeed = squareInputWithSign(motorSpeed);

        testBench.setMotorSpeed(motorSpeed);

        testBench.setServoPos(gamepad1.left_trigger);
        telemetry.addData("left_trigger", gamepad1.left_trigger);

        NormalizedRGBA colors = testBench.getColorReading();

        telemetry.addData("red", "%.3f", colors.red);
        telemetry.addData("green", "%.3f", colors.green);
        telemetry.addData("blue", "%.3f", colors.blue);
        telemetry.addData("brightness", "%.3f", colors.alpha);

        telemetry.addData("Range", testBench.getRange(DistanceUnit.CM));
    }
}
