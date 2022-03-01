import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import org.jsoup.select.Elements;


public class CustomRecursiveTask extends RecursiveTask<List<String>> {

    private String URL;

    public CustomRecursiveTask(String URL) {
        this.URL = URL;
    }

    @Override
    protected List<String> compute() {
        List<CustomRecursiveTask> subTasks = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add(URL);
        try {
            Thread.sleep(150);
            Document doc = Jsoup.connect(URL).get();
            Elements links = doc.select("body").select("a");
            for (Element element : links) {
                String link = element.attr("abs:href");
                if (link.contains(URL) && !list.contains(link) && !link.contains("pdf") && !link.contains("?") && !link.contains("#")) {
                    list.add(link);
                    CustomRecursiveTask task = new CustomRecursiveTask(link);
                    task.fork();
                    subTasks.add(task);
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        addResultsFromTasks(list, subTasks);
        return list;
    }

    private void addResultsFromTasks(List<String> list, List<CustomRecursiveTask> subTasks) {
        for (CustomRecursiveTask item : subTasks) {
            list.addAll(item.join());
        }
    }
}

