package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@Autonomous
public class AutoStatesString extends OpMode {
    TestBench testBench = new TestBench();
    String state = "START";

    @Override
    public void init() {
        testBench.init(hardwareMap);
    }

    @Override
    public void start() {
        state = "START";
    }

    @Override
    public void loop() {
        telemetry.addData("State", state);

        switch (state) {
            case "START":
                testBench.setServoPos(0.5);
                if (testBench.isTouchSensorPressed()) {
                    state = "WAIT_FOR_SENSOR_RELEASE";
                }
                break;

            case "WAIT_FOR_SENSOR_RELEASE":
                testBench.setServoPos(0);
                if (!testBench.isTouchSensorPressed()) {
                    state = "WAIT_FOR_IMU_HEADING";
                }
                break;

            case "WAIT_FOR_IMU_HEADING":
                testBench.setServoPos(1);
                testBench.setMotorSpeed(0.5);
                if (testBench.getHeading(AngleUnit.DEGREES) > 10) {
                    state = "STOP";
                }
                break;
            case "STOP":
                testBench.setMotorSpeed(0);
                state = "DONE";
                break;
            default:
                telemetry.addData("Auto", "Finished");
                break;
        }
    }
}
