package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TelemetryMechanism {

    private Telemetry telemetry;

    public void init(Telemetry driverTelemetry) {
        telemetry = driverTelemetry; // local variable telemetry IS the telemetry object from our DS
    }

    public void printInitialized() {
        telemetry.addData("Telemetry Mechanism", "Initialized");
        telemetry.addData("This Statement was printed", "From the Mechanism Class");
    }
}
