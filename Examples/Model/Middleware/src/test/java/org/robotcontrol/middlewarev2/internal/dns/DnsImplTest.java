package org.robotcontrol.middlewarev2.internal.dns;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.rpc.RawRpcClientImpl;

public class DnsImplTest {

    private DnsImpl dns;

    @Before
    public void setUp() {
        dns = new DnsImpl();
    }

    @Test
    public void testCall_register_storesInRegistry() {
        RpcValue[] args = {
            new RpcValue.StringValue("MyService"),
            new RpcValue.StringValue("doThing"),
            new RpcValue.StringValue("127.0.0.1:1234")
        };

        dns.call("register", args);

        // Check internal state via reflection or direct test if possible
        // We verify indirectly by resolving it
        // For simplicity, just call resolve and ensure the correct data is passed
        try (MockedConstruction<RawRpcClientImpl> mocked = mockConstruction(RawRpcClientImpl.class,
        (mock, context) -> {
            // No-op
        })) {

            dns.call("resolve", args);

            RawRpcClientImpl client = mocked.constructed().get(0);
            verify(client, times(1)).invoke(
                eq("receiveResolution"),
                eq(new RpcValue.StringValue("MyService")),
                eq(new RpcValue.StringValue("doThing")),
                eq(new RpcValue.StringValue("127.0.0.1:1234"))
            );
        }
    }

    @Test
    public void testCall_resolve_sendsCorrectRpcCall() {
        // Pre-register the entry
        dns.register("PingService", "ping", "192.168.1.10:8888");

        try (MockedConstruction<RawRpcClientImpl> mocked = mockConstruction(RawRpcClientImpl.class,
        (mock, context) -> {
            // no-op
        })) {

            dns.call("resolve", new RpcValue[]{
                new RpcValue.StringValue("PingService"),
                new RpcValue.StringValue("ping"),
                new RpcValue.StringValue("localhost:9999")
            });

            RawRpcClientImpl client = mocked.constructed().get(0);
            verify(client, times(1)).invoke(
                eq("receiveResolution"),
                eq(new RpcValue.StringValue("PingService")),
                eq(new RpcValue.StringValue("ping")),
                eq(new RpcValue.StringValue("192.168.1.10:8888"))
            );
        }
    }

    @Test(expected = RuntimeException.class)
    public void testCall_withUnknownFunction_throwsException() {
        dns.call("notAFunction", new RpcValue[0]);
    }

    @Test(expected = RuntimeException.class)
    public void testCall_register_withWrongArgsCount_throwsException() {
        dns.call("register", new RpcValue[]{ new RpcValue.StringValue("onlyOneArg") });
    }
    
}
