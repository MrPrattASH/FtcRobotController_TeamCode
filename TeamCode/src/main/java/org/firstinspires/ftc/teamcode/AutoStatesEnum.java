package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@Autonomous
public class AutoStatesEnum extends OpMode {
    TestBench testBench = new TestBench();
    enum State {
        START,
        WAIT_FOR_SENSOR_RELEASE,
        WAIT_FOR_HEADING,
        STOP,
        DONE
    }
    State state = State.START;

    @Override
    public void init() {
        testBench.init(hardwareMap);

    }

    @Override
    public void start() {
        state = State.START;
    }

    @Override
    public void loop() {
        telemetry.addData("State", state);

        switch (state) {
            case START:
                testBench.setServoPos(0.5);
                if (testBench.isTouchSensorPressed()) {
                    state = State.WAIT_FOR_SENSOR_RELEASE;
                }
                break;

            case WAIT_FOR_SENSOR_RELEASE:
                testBench.setServoPos(0);
                if (!testBench.isTouchSensorPressed()) {
                    state = State.WAIT_FOR_HEADING;
                }
                break;

            case WAIT_FOR_HEADING:
                testBench.setServoPos(1);
                testBench.setMotorSpeed(0.5);
                if (testBench.getHeading(AngleUnit.DEGREES) > 10) {
                    state = State.STOP;
                }
                break;
            case STOP:
                testBench.setMotorSpeed(0);
                state = State.DONE;
                break;
            default:
                telemetry.addData("Auto", "Finished");
                break;
        }
    }
}
