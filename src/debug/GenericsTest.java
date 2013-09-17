/**
 * @author Kazuki Nishiura
 */
public class GenericsTest<T> {
    Class<T> c;

    public boolean applicable(Object o) {
        o.getClass();
        c.isAssignableFrom(o.getClass());
        return false;
    }
}
