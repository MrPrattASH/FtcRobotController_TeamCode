package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class ArrayOpMode extends OpMode {

    int[] numbers = {0,1,2,3,4,5};
    int numIndex;
    double delaySeconds = 0.5;
    double nextTime;



    @Override
    public void init() {
        numIndex = 0;
    }

    @Override
    public void loop() {
        if (nextTime < getRuntime()) {
            numIndex++;
            if (numIndex >= numbers.length) {
                numIndex = numbers.length -1;
            }
            nextTime = getRuntime() + delaySeconds;
        }
        telemetry.addData("Array location", numbers[numIndex]);
    }
}
