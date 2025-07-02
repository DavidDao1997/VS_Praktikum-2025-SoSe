package org.robotcontrol.middleware.moveadapter;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.MoveAdapter;
import org.robotcontrol.middleware.idl.MoveAdapter.RobotDirection;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoveAdapterServer implements ServerStub_I {
    private MoveAdapter moveAdapter;

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "move":
                moveAdapter.move(
                    (RobotDirection) RpcUtils.unwrap(args[0])
                );
                break;
            default:
                System.out.printf("%s.call(fnName: %s, ...): Unimplemented method called", getClass().getSimpleName(), fnName);
                break;
        }
    }
    
}
