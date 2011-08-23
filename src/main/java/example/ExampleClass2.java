package example;

/**
 * @author azotcsit
 *         Date: 18.05.11
 *         Time: 14:07
 */
public class ExampleClass2 {
    private int k;

    public void doSomething(int count) {
        int i = 0;
        while (i < count) {
            i = k;
        }

        System.out.println(i);
        System.out.println(count);
    }

}
