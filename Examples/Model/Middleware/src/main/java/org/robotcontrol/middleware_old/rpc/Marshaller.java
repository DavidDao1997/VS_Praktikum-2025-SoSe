package org.robotcontrol.middleware.rpc;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class Marshaller {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String marshal(String functionName, RpcValue... values) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("function", functionName);

        ArrayNode valueArray = objectMapper.createArrayNode();
        for (RpcValue val : values) {
            valueArray.add(serializeValue(val));
        }

        root.set("params", valueArray);

        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize RPC payload", e);
        }
    }

    // ✅ New: Unmarshal from JSON string to RpcRequest
    public static RpcRequest unmarshal(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String functionName = root.get("function").asText();
            JsonNode valuesNode = root.get("params");

            List<RpcValue> values = parseValues(valuesNode);

            return new RpcRequest(functionName, values);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize RPC request", e);
        }
    }

    private static List<RpcValue> parseValues(JsonNode arrayNode) {
        List<RpcValue> values = new ArrayList<>();
        if (!arrayNode.isArray()) {
            System.out.println("OH NO");
            return values;
        }

        for (JsonNode node : arrayNode) {
            values.add(parseSingleValue(node));
        }

        return values;
    }

    private static RpcValue parseSingleValue(JsonNode node) {
        if (node.isInt()) {
            return new RpcValue.IntValue(node.intValue());
        } else if (node.isBoolean()) {
            return new RpcValue.BoolValue(node.booleanValue());
        } else if (node.isTextual()) {
            return new RpcValue.StringValue(node.textValue());
        } else if (node.isArray()) {
            List<RpcValue> nested = parseValues(node);
            return new RpcValue.ListValue(nested);
        } else {
            throw new IllegalArgumentException("Unsupported JSON type: " + node);
        }
    }

    private static JsonNode serializeValue(RpcValue val) {
        if (val instanceof RpcValue.IntValue) {
            RpcValue.IntValue iv = (RpcValue.IntValue) val;
            return new IntNode(iv.getValue());
        } else if (val instanceof RpcValue.StringValue) {
            RpcValue.StringValue sv = (RpcValue.StringValue) val;
            return new TextNode(sv.getValue());
        } else if (val instanceof RpcValue.BoolValue) {
            RpcValue.BoolValue bv = (RpcValue.BoolValue) val;
            return BooleanNode.valueOf(bv.getValue());
        } else if (val instanceof RpcValue.ListValue) {
            RpcValue.ListValue lv = (RpcValue.ListValue) val;
            ArrayNode array = objectMapper.createArrayNode();
            for (RpcValue item : lv.getValues()) {
                array.add(serializeValue(item));
            }
            return array;
        } else if (val instanceof RpcValue.Bitmap256Value) {
            RpcValue.Bitmap256Value bav = (RpcValue.Bitmap256Value) val;
            ArrayNode array = objectMapper.createArrayNode();
            for (byte b : bav.getBytes()) {
                array.add(b & 0xFF); // convert signed byte to unsigned int
            }
            return array;
        } else {
            throw new IllegalArgumentException("Unknown RpcValue type: " + val);
        }
    }
}
