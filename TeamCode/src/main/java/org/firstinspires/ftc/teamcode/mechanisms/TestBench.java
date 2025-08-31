package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.TestCRServo;
import org.firstinspires.ftc.teamcode.TestDigitalSensor;
import org.firstinspires.ftc.teamcode.TestItem;
import org.firstinspires.ftc.teamcode.TestMotor;
import org.firstinspires.ftc.teamcode.TestServo;

import java.util.ArrayList;

public class TestBench {
    private DigitalChannel touchSensor;
    private DcMotor motor;
    private double ticksPerRev;
    private Servo servoPos;
    private CRServo servoRot;

    private NormalizedColorSensor colorSensor;
    private DistanceSensor rangeSensor;

    private DistanceSensor distanceSensor;

    private IMU imu;


    public void init(HardwareMap hwMap){
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);
        motor = hwMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        servoPos = hwMap.get(Servo.class, "servo_pos");
        servoRot = hwMap.get(CRServo.class, "servo_rot");

        distanceSensor = hwMap.get(DistanceSensor.class, "sensor_distance");

        colorSensor = hwMap.get(NormalizedColorSensor.class,"sensor_color_distance");
        colorSensor.setGain(4f);
        rangeSensor = hwMap.get(DistanceSensor.class, "sensor_color_distance");

        imu = hwMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(RevOrientation));
    }

    public boolean isTouchSensorPressed() {
        return !touchSensor.getState();
    }

    public boolean isTouchSensorReleased() {
        return touchSensor.getState();
    }

    public void setMotorSpeed(double speed){
        motor.setPower(speed);
    }

    public double getTicksPerRotation() {
        return motor.getCurrentPosition() / ticksPerRev;
    }

    public void setMotorBrake(String brakeType){
        if (brakeType.equals("Brake")){
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        else if (brakeType.equals("Float")){
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }

    public void setServoPos(double position) {
        servoPos.setPosition(position);
    }

    public void setServoRot(double power) {
        servoRot.setPower(power);
    }

   public NormalizedRGBA getColorReading () {
        return colorSensor.getNormalizedColors();
   }

    public double getRange(DistanceUnit du) {
        return rangeSensor.getDistance(du);
    }

    public double getDistance(DistanceUnit du) {
        return distanceSensor.getDistance(du);
    }
    public double getHeading(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getYaw(angleUnit);
    }

    public ArrayList<TestItem> getTests() {
        ArrayList<TestItem> tests = new ArrayList<>();
        tests.add(new TestMotor("TB Motor", 0.5, motor));
        tests.add(new TestDigitalSensor("Touch Sensor", touchSensor));
        tests.add(new TestServo("Pos Servo", 1.0, 0.0, servoPos));
        tests.add(new TestCRServo("Rot Servo", 1.0, 0.0, servoRot));
        return tests;
    }
}
