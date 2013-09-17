/**
 * @author Kazuki Nishiura
 */
public class ReplaceTest {

    public static void main(String[] args) {
        System.out.println("abcabc".replaceAll("abc", "x"));
        System.out.println("abcabc".replaceAll("[abc]", "x"));
        System.out.println("abcabc".replaceAll("abc|[abc]", "x"));
        System.out.println("abcabc".replaceAll("[abc]|abc", "x"));
    }
}
