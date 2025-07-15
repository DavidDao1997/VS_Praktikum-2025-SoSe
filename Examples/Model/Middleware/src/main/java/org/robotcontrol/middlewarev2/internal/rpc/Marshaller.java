package org.robotcontrol.middlewarev2.internal.rpc;

import java.util.ArrayList;
import java.util.List;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class Marshaller {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String marshal(String functionName, RpcValue... values) {
        return marshal(null, functionName, values);
    }

    public static String marshal(Long timestamp, String functionName, RpcValue... values) {
        ObjectNode root = objectMapper.createObjectNode();

        if (timestamp != null) {
            root.put("timeStamp", timestamp);
        } else {
            root.put("timeStamp", 0);
        }
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
            JsonNode timestampNode = root.get("timeStamp");
            Long timestamp = null;
            if (timestampNode != null) {
                timestamp = timestampNode.asLong();
                if (timestamp == 0) {
                    timestamp = null;
                }
            } 
            String functionName = root.get("function").asText();
            JsonNode valuesNode = root.get("params");

            List<RpcValue> values = parseValues(valuesNode);

            return new RpcRequest(timestamp, functionName, values);
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
        if (node.isNumber()) {
            return new RpcValue.LongValue(node.longValue());
        } else if (node.isBoolean()) {
            return new RpcValue.BoolValue(node.booleanValue());
        } else if (node.isTextual()) {
            return new RpcValue.StringValue(node.textValue());
        } else if (node.isArray()) {
            // Check if the array length is 32 and all elements are int 0-255 -> treat as Bitmap256Value
            if (node.size() == 32 && allElementsAreByteValues(node)) {
                byte[] bytes = new byte[32];
                for (int i = 0; i < 32; i++) {
                    int val = node.get(i).intValue();
                    if (val < 0 || val > 255) {
                        throw new IllegalArgumentException("Bitmap256Value array element out of byte range: " + val);
                    }
                    bytes[i] = (byte) val;
                }
                return new RpcValue.Bitmap256Value(bytes);
            } else {
                // Otherwise, parse as generic ListValue
                List<RpcValue> nested = parseValues(node);
                return new RpcValue.ListValue(nested);
            }
        } else {
            throw new IllegalArgumentException("Unsupported JSON type: " + node);
        }
    }

    private static JsonNode serializeValue(RpcValue val) {
        if (val instanceof RpcValue.LongValue) {
            RpcValue.LongValue lv = (RpcValue.LongValue) val;
            return new LongNode(lv.getValue());
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

    private static boolean allElementsAreByteValues(JsonNode node) {
        for (JsonNode element : node) {
            if (!element.isInt()) {
                return false;
            }
            int val = element.intValue();
            if (val < 0 || val > 255) {
                return false;
            }
        }
        return true;
    }
}
