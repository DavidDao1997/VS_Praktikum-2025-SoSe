package org.robotcontrol.middleware.ui;

import java.util.List;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.UI;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UIServer implements ServerStub_I {
    private UI ui;

    @Override
    public void call(String fnName, RpcValue... args) {
        switch (fnName) {
            case "updateView":// Annahme: Die Roboter sind durch Kommas getrennt (falls das Format es
                              // erfordert)
                forwardToUi(args);
                break;
            default:
                System.out.printf("%s.call(fnName: %s, ...): Unimplemented method called", getClass().getSimpleName(),
                        fnName);
                break;

        }
    }

    @SuppressWarnings("unchecked")
    private void forwardToUi(RpcValue... args) {
        // --- unwrap & convert the first argument to primitive byte[] -------------
        Object arg0 = RpcUtils.unwrap(args[0]);

        List<?> list = (List<?>) arg0; // elements may be Byte, Integer, etc.
        byte[] payload = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            payload[i] = ((Number) list.get(i)).byteValue();
        }

        // --- unwrap the remaining arguments --------------------------------------
        int number = ((Number) RpcUtils.unwrap(args[1])).intValue();
        boolean flag1 = (Boolean) RpcUtils.unwrap(args[2]);
        boolean flag2 = (Boolean) RpcUtils.unwrap(args[3]);

        System.out.println("FOOOOOOOOOOOOOOOOOOO");
        System.out.println(payload);
        System.out.println("BAAARRRR");
        // --- delegate to the UI ---------------------------------------------------
        ui.updateView(payload, number, flag1, flag2);
    }

}
