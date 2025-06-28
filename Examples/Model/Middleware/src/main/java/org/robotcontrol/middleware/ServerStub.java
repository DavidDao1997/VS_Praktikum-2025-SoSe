package org.robotcontrol.middleware;

import java.lang.reflect.Method;

public class ServerStub {
    
    public void call(String fnName, RpcValue... args) {
        Method method = findMethodByNameAndArgs(fnName, args);
        if (method == null) {
            System.err.println("Method not found: " + fnName);
            return;
        }
        Object[] unwrappedArgs = RpcUtils.unwrap(args);
        try {
            method.invoke(this, unwrappedArgs);
        } catch (Exception e) {
            System.err.println("Invocation failed: " + e);
        }
    }

    /**
     * Finds a method by name and matching parameter count & compatible parameter types.
     */
    private Method findMethodByNameAndArgs(String fnName, RpcValue[] args) {
        Method[] methods = this.getClass().getMethods();
        outer:
        for (Method method : methods) {
            if (!method.getName().equals(fnName)) {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            // Check if unwrapped RpcValue types are compatible with method parameters
            for (int i = 0; i < paramTypes.length; i++) {
                Object unwrapped = RpcUtils.unwrap(args[i]);
                if (unwrapped != null && !isAssignable(paramTypes[i], unwrapped.getClass())) {
                    continue outer;
                }
            }
            return method;
        }
        return null;
    }

    /**
     * Checks if `from` type can be assigned to `to` type considering primitives and wrappers.
     */
    private boolean isAssignable(Class<?> to, Class<?> from) {
        if (to.isAssignableFrom(from)) {
            return true;
        }
        if (to.isPrimitive()) {
            // Handle boxing conversions
            if (to == int.class && from == Integer.class) return true;
            if (to == boolean.class && from == Boolean.class) return true;
            if (to == long.class && from == Long.class) return true;
            if (to == double.class && from == Double.class) return true;
            if (to == float.class && from == Float.class) return true;
            if (to == char.class && from == Character.class) return true;
            if (to == byte.class && from == Byte.class) return true;
            if (to == short.class && from == Short.class) return true;
        }
        return false;
    }
}
