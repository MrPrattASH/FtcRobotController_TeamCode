package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@Autonomous
public class AutoState1 extends OpMode {
    TestBench testBench = new TestBench();
    int state;
    double heading;

    @Override
    public void init() {
        testBench.init(hardwareMap);
    }

    public void start() {
        state = 0;
    }

    @Override
    public void loop() {
        telemetry.addData("State", state);
        heading = testBench.getHeading(AngleUnit.DEGREES);
        telemetry.addData("Heading", heading);

        switch (state) {
            case 0:
                testBench.setServoPos(0.5);
                if (testBench.isTouchSensorPressed()) {
                    state = 1;
                }
                break;
            case 1:
                testBench.setServoPos(0);
                if (!testBench.isTouchSensorPressed()) {
                    state = 2;
                }
                break;
            case 2:
                testBench.setMotorSpeed(0.5);
                testBench.setServoPos(1.0);
                if (testBench.getHeading(AngleUnit.DEGREES) > 10) {
                    state = 3;
                }
                break;
            case 3:
                testBench.setMotorSpeed(0);
                state = 4;
                break;
            case 4:
                telemetry.addData("Auto", "Finished");
                break;
        }

    }

}