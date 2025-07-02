package org.robotcontrol.middleware;

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
}
