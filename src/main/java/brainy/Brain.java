package brainy;

/**
 * Created by iters on 3/14/17.
 */
public class Brain {
    public static void main(String[] args) throws Exception {
        // parser started
        Thread parser = new Thread(new RunnableParser());
        parser.start();
    }
}