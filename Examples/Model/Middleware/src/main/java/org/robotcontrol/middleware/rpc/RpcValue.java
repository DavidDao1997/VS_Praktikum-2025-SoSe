package org.robotcontrol.middleware.rpc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface RpcValue {

    class IntValue implements RpcValue {
        private final int value;

        public IntValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IntValue)) return false;
            IntValue intValue = (IntValue) o;
            return value == intValue.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "IntValue(" + value + ")";
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Bitmap256Value)) return false;
            Bitmap256Value that = (Bitmap256Value) o;
            return Arrays.equals(bytes, that.bytes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }

        @Override
        public String toString() {
            return "Bitmap256Value(0x" + toBigInteger().toString(16).toUpperCase() + ")";
        }
    }

}
