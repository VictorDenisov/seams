package example;

/**
 * @author azotcsit
 *         Date: 18.05.11
 *         Time: 14:07
 */
public class ExampleClass {
    private String someString;

    public ExampleClass(String someString) {
        this.someString = someString;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public void doSomething(int count) {
        int[] someArray = new int[count];
        int i = 0;
        while (i < count) {
            someArray[i] = i;
        }

        boolean b = true;
        if (b) {
            someArray = somePrivateMethod();
        } else {
            someArray = someProtectedMethod();
        }

        b = false;

        System.out.println(b);
        System.out.println(count);
    }

    private int[] somePrivateMethod() {
        return new int[0];
    }

    protected int[] someProtectedMethod() {
        return new int[0];
    }
}
