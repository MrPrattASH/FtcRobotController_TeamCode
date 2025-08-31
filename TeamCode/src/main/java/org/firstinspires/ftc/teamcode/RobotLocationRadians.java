package org.firstinspires.ftc.teamcode;

public class RobotLocationRadians {
    double angleRadians;
    double x;
    double y;

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void changeY(double value) {
        this.y += value;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void changeX(double value){
        this.x += value;
    }

    public RobotLocationRadians(double angleDegrees){
        this.angleRadians = Math.toRadians(angleDegrees);

    }


    public double getHeading(){
        double angle = this.angleRadians;
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        while(angle < Math.PI){
            angle += 2 * Math.PI;
        }
        return Math.toDegrees(angle);
    }

    @Override
    public String toString(){
        return "RobotLocationRadians: angle (" + angleRadians + ")";
    }



    public void turn(double angleChangeDegrees){
        angleRadians += Math.toRadians(angleChangeDegrees);
    }

    public double getAngle(){
        return angleRadians;
    }
    public void setAngle(double angleDegrees){
        this.angleRadians = Math.toRadians(angleDegrees);
    }
}
