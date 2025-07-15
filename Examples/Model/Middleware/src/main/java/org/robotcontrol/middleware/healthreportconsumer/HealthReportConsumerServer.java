package org.robotcontrol.middleware.healthreportconsumer;

import java.lang.Thread.State;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.HealthReportConsumer;
import org.robotcontrol.middleware.idl.StateService;
import org.robotcontrol.middleware.rpc.RpcValue;

public class HealthReportConsumerServer implements ServerStub_I{

    private final StateService healthreportconsumer;

    public HealthReportConsumerServer(StateService stateService){
        this.healthreportconsumer = stateService;
    }



    @Override
    public void call(String fnName, RpcValue... args) {
     
    }
}

