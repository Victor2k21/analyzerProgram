import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    final static int MAX_TEXT = 100;
    final static int TEXT_LENGTH = 100_000;
    final static int STRING_QUANTITY = 10_000;
    final static String TEXT_SAMPLE = "abcm";
    final static int SAMPLE_LENGTH = TEXT_SAMPLE.length();

    static MaxQueueArray[] maxQueue = new MaxQueueArray[SAMPLE_LENGTH];
    static int[] maxQuantity = new int[SAMPLE_LENGTH];
    static String[] maxString = new String[SAMPLE_LENGTH];


    public static void main(String[] args) throws InterruptedException {

        Runnable buildStrings = () -> {
            for (int i = 0; i < STRING_QUANTITY; i++) {
                try {
                    String s = generateText(TEXT_SAMPLE, TEXT_LENGTH);
                    for (int j = 0; j < SAMPLE_LENGTH; j++) {
                        maxQueue[j].getQueue().put(s);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        };

        for (int i = 0; i < SAMPLE_LENGTH; i++) {
            ArrayBlockingQueue<String> q = new ArrayBlockingQueue<>(MAX_TEXT);
            maxQueue[i] = new MaxQueueArray(q);
        }

        System.out.println("-------- Начали...");
        Thread t1 = new Thread(buildStrings);
        t1.start();

        System.out.println("--------- Запускаем потоки");
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < SAMPLE_LENGTH; i++) {
            Thread t = startThread(i);
            threads.add(t);
            t.start();
        }
        System.out.println("--------- Ждём завершения потоков");
        t1.join();
        for (int i = 0; i < SAMPLE_LENGTH; i++) threads.get(i).join();

        System.out.println("--------- Результат!");
        for (int i = 0; i < SAMPLE_LENGTH; i++) {
            System.out.println("Строка с наибольшим количеством символов " + TEXT_SAMPLE.charAt(i) + "(" + maxQuantity[i] + "):");
            System.out.println(maxString[i]);
        }
    }

    public static Thread startThread(int ind) {
        return new Thread(() -> {
            try {
                for (int i = 0; i < STRING_QUANTITY; i++) {
                    String str = maxQueue[ind].getQueue().take();
                    int calc = (int) str.chars().filter(c -> c == TEXT_SAMPLE.charAt(ind)).count();

                    if (maxQuantity[ind] >= calc) continue;
                    maxString[ind] = str;
                    maxQuantity[ind] = calc;
                }
            } catch (InterruptedException ignored) {
            }
        });
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}