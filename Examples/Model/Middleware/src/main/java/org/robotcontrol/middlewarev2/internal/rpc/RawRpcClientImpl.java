package org.robotcontrol.middlewarev2.internal.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.rpc.Invokable;

public class RawRpcClientImpl implements Invokable, InvokableWithTimestamp {
    private final Logger logger = new Logger("RawRpcClientImpl");
    private InetAddress ipAddr;
    private Integer port;

    public RawRpcClientImpl(String socket) {
        String[] socketParts = socket.split(":", 2);
        try {
            this.ipAddr = InetAddress.getByName(socketParts[0]);
            this.port = Integer.parseInt(socketParts[1]);
        } catch (UnknownHostException e) {
            logger.error("UnknownHostException in constructor, rethrowing: %s", e);
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            logger.error("IndexOutOfBoundsException in constructor, rethrowing: %s", e);
            throw e;
        }
    }

    @Override
    public void invoke(Long timestamp, String fnName, RpcValue... args) {
        byte[] msg = Marshaller.marshal(timestamp, fnName, args).getBytes();
        
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ipAddr, port);
            socket.send(sendPacket);
            socket.close();
        } catch (IOException e) {
            logger.info("IOException in invoke, failing silently: %s", e);
        }
    }

    public void invoke(String fnName, RpcValue... args) {
        byte[] msg = Marshaller.marshal(fnName, args).getBytes();

        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ipAddr, port);
            socket.send(sendPacket);
            socket.close();
        } catch (IOException e) {
            logger.info("IOException in invoke, failing silently: %s", e);
        }
    }

}
