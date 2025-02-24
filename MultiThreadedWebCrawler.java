//question.no.6.b

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedWebCrawler {

    // Thread-safe queue for URLs to crawl
    private ConcurrentLinkedQueue<String> urlQueue = new ConcurrentLinkedQueue<>();
    // Thread-safe map to store crawled data
    private ConcurrentHashMap<String, String> crawledData = new ConcurrentHashMap<>();
    // Executor service for managing threads
    private ExecutorService executorService;

    public MultiThreadedWebCrawler(int numThreads) {
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    // Task to crawl a URL
    class CrawlTask implements Callable<String> {
        private String url;

        public CrawlTask(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            return fetchWebPage(url);
        }

        private String fetchWebPage(String url) {
            StringBuilder content = new StringBuilder();
            try {
                @SuppressWarnings("deprecation")
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                // Store crawled data
                crawledData.put(url, content.toString());
            } catch (IOException e) {
                System.err.println("Error fetching URL: " + url + " - " + e.getMessage());
            }
            return content.toString();
        }
    }

    // Add URLs to the queue
    public void addUrls(List<String> newUrls) {
        for (String newUrl : newUrls) {
            urlQueue.add(newUrl);
        }
    }

    // Start the crawling process
    public void startCrawling() {
        while (!urlQueue.isEmpty()) {
            String url = urlQueue.poll();
            if (url != null) {
                executorService.submit(new CrawlTask(url));
            }
        }
        // Shutdown executor after tasks are completed
        executorService.shutdown();
    }

    // Main method to run the crawler
    public static void main(String[] args) {
        MultiThreadedWebCrawler crawler = new MultiThreadedWebCrawler(10); // 10 threads
        // Add initial URLs to crawl
        crawler.addUrls(List.of("https://google.com", "https://youtube.com"));
        // Start crawling
        crawler.startCrawling();
        // Wait for termination
        try {
            while (!crawler.executorService.isTerminated()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Output crawled data
        crawler.crawledData.forEach((url, content) -> {
            System.out.println("Crawled URL: " + url);
            System.out.println("Content Length: " + content.length());
        });
    }
}