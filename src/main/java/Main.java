import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {

    private static String dataFile = "data/file";
    private static String URL = "https://lenta.ru/";

    public static void main(String[] args) throws IOException {

        ForkJoinPool pool = new ForkJoinPool();
        CustomRecursiveTask customRecursiveTask = new CustomRecursiveTask(URL);

        pool.execute(customRecursiveTask);

        do {
            System.out.printf("******************************************\n");
            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
            System.out.printf("Main: Active Threads: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n", pool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n", pool.getStealCount());
            System.out.printf("******************************************\n");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ((!customRecursiveTask.isDone()));

        pool.shutdown();
        List<String> results = customRecursiveTask.join();

        System.out.printf("Общее количество ссылок: %d.\n", results.size());

        writeToFile(results, dataFile);
    }


    private static void writeToFile(List<String> results, String dataFile) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(dataFile);
        for (String link : results) {
            StringBuilder str = new StringBuilder();
            int test1 = countSlash(link) - countSlash(URL);
            for (int i = 0; i < test1; i++) {
                str.append("\t");
            }
            printWriter.write(str + link + "\n");
        }
        printWriter.flush();
        printWriter.close();
    }

    private static int countSlash(String string) {
        String findStr = "/";
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = string.indexOf(findStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }
}




