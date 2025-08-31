package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@TeleOp
public class IMUOpMode extends OpMode {
    TestBench testBench = new TestBench();

    @Override
    public void init() {
        testBench.init(hardwareMap);  // link our hardware to the op mode
    }

    @Override
    public void loop() {
        double heading = testBench.getHeading(AngleUnit.DEGREES);
        if (heading > 0.1) {
            testBench.setMotorSpeed(0.5);
        }
        else if (heading < -0.1) {
            testBench.setMotorSpeed(-0.5);
        }
        else {
            testBench.setMotorSpeed(0);
        }
        telemetry.addData("Heading", testBench.getHeading(AngleUnit.DEGREES));

    }
}

