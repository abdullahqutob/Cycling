package testing;

import java.lang.reflect.Method;

public class TestCase {
    public int runner() {
        // Setup
        String currentClassName = this.getClass().getName();
        String headerHeaderAndFooter = new String(new char[currentClassName.length() + 8]).replace("\0", "#");
        String headerName = "### " + currentClassName + " ###";

        System.out.println(headerHeaderAndFooter);
        System.out.println(headerName);
        System.out.println(headerHeaderAndFooter);

        // Run all tests
        boolean errorOccurred = false;
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("test")) {
                System.out.println("  " + method.getName() + " testing...");
                try {
                    Object ret = method.invoke(this);
                    if ( ret == null) {
                        System.out.println("->" + method.getName() + " " + ANSI_TEXT_GREEN_SUCCESS);
                    } else {
                        System.out.println("  " + method.getName() + " exited with error: " + ret);
                        System.out.println("X>" + method.getName() + " " + ANSI_TEXT_RED_FAIL);
                    }
                }
                catch (java.lang.IllegalAccessException e) {
                    errorOccurred = true;
                    System.out.println("Couldn't call your test method. Access denied. Did you make it public?");
                    System.out.println("Error message: " + e.getMessage());
                    System.out.println("X>" + method.getName() + " " + ANSI_TEXT_RED_FAIL);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    errorOccurred = true;
                    System.out.println("  " + method.getName() + " exited with exception: " + e.getTargetException().getMessage());
                    e.getTargetException().printStackTrace();
                    System.out.println("X>" + method.getName() + " " + ANSI_TEXT_RED_FAIL);
                } finally {
                    System.out.println("---");
                }
            }
        }

        // Shutdown
        System.out.println();

        return errorOccurred ? 1 : 0;
    }

    final private int STACK_TRACE_MAGIC_NUMBER = 2;

    private static final String ANSI_COLOUR_RESET = "\u001B[0m";
    private static final String ANSI_COLOUR_RED = "\u001B[31m";
    private static final String ANSI_COLOUR_GREEN = "\u001B[32m";

    private static final String ANSI_TEXT_RED_FAIL = ANSI_COLOUR_RED + "FAIL" + ANSI_COLOUR_RESET;
    private static final String ANSI_TEXT_GREEN_SUCCESS = ANSI_COLOUR_GREEN + "SUCCESS" + ANSI_COLOUR_RESET;

    protected void assertEqual(Object one, Object two) throws AssertError {
        if (one != two) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            String className = stackTraceElements[STACK_TRACE_MAGIC_NUMBER].getClassName();
            String methodName = stackTraceElements[STACK_TRACE_MAGIC_NUMBER].getMethodName();
            int lineNumber = stackTraceElements[STACK_TRACE_MAGIC_NUMBER].getLineNumber();

            String message = String.format(
                    "%s is not equal to %s in assertEqual on line %d in method %s in class %s.",
                    one, two, lineNumber, methodName, className
            );

            throw new AssertError(message);
        }
    }

    protected void assertNotEqual(Object one, Object two) throws AssertError {
        if (one == two) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            String className = stackTraceElements[STACK_TRACE_MAGIC_NUMBER].getClassName();
            String methodName = stackTraceElements[STACK_TRACE_MAGIC_NUMBER].getMethodName();
            int lineNumber = stackTraceElements[STACK_TRACE_MAGIC_NUMBER].getLineNumber();

            String message = String.format(
                    "%s is equal to %s in assertNotEqual on line %d in method %s in class %s.",
                    one, two, lineNumber, methodName, className
            );

            throw new AssertError(message);
        }
    }

    protected static class AssertError extends Exception {
        /**
         * Constructs an instance of the exception with default message
         */
        public AssertError() {
            super("An assertion error occurred");
        }

        /**
         * Constructs an instance of the exception containing the message argument
         *
         * @param message message containing details regarding the exception cause
         */
        public AssertError(String message) {
            super(message);
        }
    }
}