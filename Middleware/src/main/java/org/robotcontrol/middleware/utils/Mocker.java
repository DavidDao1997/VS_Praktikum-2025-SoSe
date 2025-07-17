package org.robotcontrol.middleware.utils;

import java.lang.reflect.*;
import java.util.Arrays;


/**
 * Utility class for creating mock implementations of Java interfaces.
 * <p>
 * The mock implementation logs the name of each method invoked along with its arguments,
 * and returns default values (e.g., {@code null}, {@code 0}, {@code false}) as appropriate
 * for the return type of the method.
 * <p>
 * This is useful for testing, logging, or simply tracing method calls without
 * implementing the full logic of an interface.
 *
 * <h2>Example Interface</h2>
 * <pre>{@code
 * public interface Service {
 *     void doSomething(String input);
 *     int compute(int a, int b);
 * }
 * }</pre>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * public class Main {
 *     public static void main(String[] args) {
 *         Service mock = Mocker.mock(Service.class);
 *
 *         mock.doSomething("hello");
 *         int result = mock.compute(3, 5);
 *         System.out.println("Result: " + result);
 *     }
 * }
 * }</pre>
 *
 * <h2>Output:</h2>
 * <pre>
 * Called method: doSomething
 *   Arg 0: hello
 * Called method: compute
 *   Arg 0: 3
 *   Arg 1: 5
 * Result: 0
 * </pre>
 */
public class Mocker {
    @SuppressWarnings("unchecked")
    public static <T> T mock(Class<T> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName() + " is not an interface");
        }

        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new LoggingInvocationHandler()
        );
    }

    private static class LoggingInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Called method: " + method.getName());
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    String displayValue;

                    if (arg != null && arg.getClass().isArray()) {
                        // Handle primitive and object arrays correctly
                        if (arg instanceof Object[])
                            displayValue = Arrays.toString((Object[]) arg);
                        else if (arg instanceof int[])
                            displayValue = Arrays.toString((int[]) arg);
                        else if (arg instanceof boolean[])
                            displayValue = Arrays.toString((boolean[]) arg);
                        else if (arg instanceof byte[])
                            displayValue = Arrays.toString((byte[]) arg);
                        else if (arg instanceof char[])
                            displayValue = Arrays.toString((char[]) arg);
                        else if (arg instanceof double[])
                            displayValue = Arrays.toString((double[]) arg);
                        else if (arg instanceof float[])
                            displayValue = Arrays.toString((float[]) arg);
                        else if (arg instanceof long[])
                            displayValue = Arrays.toString((long[]) arg);
                        else if (arg instanceof short[])
                            displayValue = Arrays.toString((short[]) arg);
                        else
                            displayValue = "Unknown array type";
                    } else {
                        displayValue = String.valueOf(arg);
                    }

                    System.out.printf("  Arg %d: %s%n", i, displayValue);
                }
            } else {
                System.out.println("  No arguments");
            }

            // Return dummy values depending on return type
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(void.class)) {
                return null;
            } else if (returnType.isPrimitive()) {
                if (returnType.equals(boolean.class)) return false;
                if (returnType.equals(char.class)) return '\0';
                return 0; // for byte, short, int, long, float, double
            }
            return null;
        }
    }

}