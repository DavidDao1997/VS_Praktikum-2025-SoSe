package org.robotcontrol.middlewarev2.idl.types;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.val;

public interface RpcValue {

    class LongValue implements RpcValue {
        private final long value;

        public LongValue(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LongValue)) return false;
            LongValue longValue = (LongValue) o;
            return value == longValue.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "longValue(" + value + ")";
        }
    }

    class StringValue implements RpcValue {
        private final String value;

        public StringValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StringValue)) return false;
            StringValue that = (StringValue) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "StringValue(" + value + ")";
        }
    }

    class BoolValue implements RpcValue {
        private final boolean value;

        public BoolValue(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BoolValue)) return false;
            BoolValue boolValue = (BoolValue) o;
            return value == boolValue.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "BoolValue(" + value + ")";
        }
    }

    class ListValue implements RpcValue {
        private final List<RpcValue> values;

        public ListValue(List<RpcValue> values) {
            this.values = values;
        }

        public List<RpcValue> getValues() {
            return values;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ListValue)) return false;
            ListValue listValue = (ListValue) o;
            return Objects.equals(values, listValue.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values);
        }

        @Override
        public String toString() {
            return "ListValue(" + values + ")";
        }
    }

    class Bitmap256Value implements RpcValue {
        private static final int BYTE_LENGTH = 32; // 256 bits
        private final byte[] bytes;

        public Bitmap256Value(byte[] bytes) {
            if (bytes.length != BYTE_LENGTH) {
                throw new IllegalArgumentException("Bitmap256Value must be 32 bytes (256 bits)");
            }
            this.bytes = Arrays.copyOf(bytes, BYTE_LENGTH);
        }

        public byte[] getBytes() {
            return Arrays.copyOf(bytes, BYTE_LENGTH);
        }

        public BigInteger toBigInteger() {
            return new BigInteger(1, bytes); // unsigned
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Bitmap256Value that = (Bitmap256Value) obj;
            return Arrays.equals(this.bytes, that.bytes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }

        @Override
        public String toString() {
            return Arrays.toString(bytes);
        }
    }

    public static Object[] unwrap(RpcValue[] rpcArgs) {
        Object[] result = new Object[rpcArgs.length];
        for (int i = 0; i < rpcArgs.length; i++) {
            result[i] = unwrap(rpcArgs[i]);
        }
        return result;
    }

    public static Object unwrap(RpcValue rpcValue) {
        if (rpcValue instanceof RpcValue.LongValue) {
            RpcValue.LongValue iv = (RpcValue.LongValue) rpcValue;
            return iv.getValue();
        } else if (rpcValue instanceof RpcValue.StringValue) {
            RpcValue.StringValue sv = (RpcValue.StringValue) rpcValue;
            return sv.getValue();
        } else if (rpcValue instanceof RpcValue.BoolValue) {
            RpcValue.BoolValue bv = (RpcValue.BoolValue) rpcValue;
            return bv.getValue();
        } else if (rpcValue instanceof RpcValue.Bitmap256Value) {
            RpcValue.Bitmap256Value bv = (RpcValue.Bitmap256Value) rpcValue;
            return bv.getBytes(); 
        } else if (rpcValue instanceof RpcValue.ListValue) {
            RpcValue.ListValue lv = (RpcValue.ListValue) rpcValue;
            List<RpcValue> values = lv.getValues();
            List<Object> unwrapped = new ArrayList<>();
            for (RpcValue val : values) {
                unwrapped.add(unwrap(val));
            }
            return unwrapped;
        } else {
            throw new IllegalArgumentException("Unknown RpcValue type: " + rpcValue);
        }
    }
}
