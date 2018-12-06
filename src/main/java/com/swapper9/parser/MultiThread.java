package com.swapper9.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.*;

public class MultiThread {
    static volatile int fileCount = 0;

    public static void main(String[] args) {
        String startUrl = "https://stackoverflow.com/questions/12759676/java-jsoup-using-threads-not-working";
        Reader reader = new Reader();
        Document doc = reader.parsePage(startUrl);
        Elements links = doc.select("a[href]");
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(links.size());
        for (Element link : links) {
            String absLink = link.attr("abs:href");

            try {
                queue.put(absLink);
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i < queue.size(); i++) {
            executor.submit(() -> reader.saveFiles(queue));

            if (queue.size() < 20) {
                try {
                    System.out.println("Выключаем executor...");
                    executor.shutdown();
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Задания прерваны...");
                } finally {
                    if (!executor.isTerminated()) {
                        System.err.println("Отмена незаконченных заданий...");
                    }
                    executor.shutdownNow();
                    System.out.println("Работа остановлена.");
                }
                break;
            }
        }

    }
}




