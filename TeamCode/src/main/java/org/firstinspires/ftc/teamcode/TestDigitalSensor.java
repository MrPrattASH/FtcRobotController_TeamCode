package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestDigitalSensor extends TestItem{
    private DigitalChannel digitalChannel;

    public TestDigitalSensor(String description, DigitalChannel channel) {
        super(description);
        this.digitalChannel = channel;
    }

    @Override
    public void run(boolean on, Telemetry telemetry) {
        telemetry.addData("Sensor State (inverted)", !digitalChannel.getState());
    }
}
