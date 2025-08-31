package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Used to test wiring of items
 *
 */
abstract public class TestItem {
    private String description;

    protected TestItem(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     *
     * @param on - suggested to use a gamepad input to trigger in main OpMode Loop
     * @param telemetry
     */
    abstract public void run(boolean on, Telemetry telemetry);
}
