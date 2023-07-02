import java.util.concurrent.ArrayBlockingQueue;

public class MaxQueueArray {

    protected ArrayBlockingQueue<String> queue;

    public MaxQueueArray(ArrayBlockingQueue<String> queue) {
        this.queue = queue;
    }

    public ArrayBlockingQueue<String> getQueue() {
        return this.queue;
    }
}
