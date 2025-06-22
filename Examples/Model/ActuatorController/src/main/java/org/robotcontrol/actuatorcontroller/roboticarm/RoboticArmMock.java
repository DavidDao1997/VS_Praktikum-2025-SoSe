package org.robotcontrol.actuatorcontroller.roboticarm;

import org.cads.vs.roboticArm.hal.ICaDSRoboticArm;

public class RoboticArmMock implements ICaDSRoboticArm {

    @Override
    public int getBackForthPercentage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBackForthPercentage'");
    }

    @Override
    public int getLeftRightPercentage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLeftRightPercentage'");
    }

    @Override
    public int getOpenClosePercentage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOpenClosePercentage'");
    }

    @Override
    public int getUpDownPercentage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUpDownPercentage'");
    }

    @Override
    public boolean heartbeat() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'heartbeat'");
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void setBackForthPercentageTo(int arg0) {
        logMethodCall(arg0);
    }

    @Override
    public void setLeftRightPercentageTo(int arg0) {
        logMethodCall(arg0);
    }

    @Override
    public void setOpenClosePercentageTo(int arg0) {
        logMethodCall(arg0);
    }

    @Override
    public void setUpDownPercentageTo(int arg0) {
        logMethodCall(arg0);
    }

    @Override
    public void teardown() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'teardown'");
    }

    @Override
    public void waitUntilInitIsFinished() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'waitUntilInitIsFinished'");
    }
    
    private void logMethodCall(Object... args) {
    // Get the method name dynamically
    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

    // Build argument string
    StringBuilder argStr = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
        argStr.append("arg").append(i).append(" = ").append(args[i]);
        if (i < args.length - 1) {
            argStr.append(", ");
        }
    }

    // Print formatted log
    System.out.printf("Method: %s, Arguments: %s%n", methodName, argStr);
}
}
