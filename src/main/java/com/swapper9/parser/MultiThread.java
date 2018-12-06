package com.swapper9.parser;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

public class MultiThread {
    private static volatile int fileCount = 0;
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    static private Document parsePage(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    static private void saveFiles() {
        Document doc = null;
        try {
            try {
                if (queue.size()>1){
                    doc = Jsoup.connect(queue.take()).get();
                    System.out.println("Queue size: " + queue.size());
                } else System.exit(1);
            } catch (InterruptedException | UnsupportedMimeTypeException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String file = doc.outerHtml();
        String filename = "filename"+(fileCount != 0 ? fileCount : "-0");
        try(FileWriter fw = new FileWriter("D:/wiki/"+filename+".html")) {
            fw.write(file);
            System.out.println("Файл "+filename+" записан потоком " + Thread.currentThread().getName());
            fileCount--;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String startUrl = "https://habr.com/hubs/design/";
        Document doc = parsePage(startUrl);
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String absLink = link.attr("abs:href");
            queue.offer(absLink);
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < links.size(); i++) {
            if (queue.size()>1) {
                executor.execute(() -> saveFiles());
            } else {
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
            }
        }
    }
}




