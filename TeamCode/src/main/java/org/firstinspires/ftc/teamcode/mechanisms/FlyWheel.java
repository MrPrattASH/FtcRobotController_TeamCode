package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    DcMotor flyWheel;

    public void init(HardwareMap hwMap) {
        flyWheel = hwMap.get(DcMotor.class, "lift_motor");
        flyWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void setPower(double power) {
        flyWheel.setPower(power);
    }

}
