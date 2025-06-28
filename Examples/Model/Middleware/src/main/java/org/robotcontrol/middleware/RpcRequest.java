package org.robotcontrol.middleware;

import java.util.List;

public record RpcRequest(String function, List<RpcValue> values) {}