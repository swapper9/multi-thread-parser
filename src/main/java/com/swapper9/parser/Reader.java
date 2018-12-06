package com.swapper9.parser;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class Reader {

    private BlockingQueue<String> queue;

    public Reader(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public Reader() {
    }

    public Document parsePage(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public void saveFiles(BlockingQueue<String> queue) {
        Document doc = null;
        try {
            try {
                if(queue.peek()!=null){
                    doc = Jsoup.connect(queue.take()).get();
                    System.out.println("Queue size: " + queue.size());
                } else System.exit(0);
            } catch (InterruptedException | UnsupportedMimeTypeException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String file = doc.outerHtml();
        String filename = "filename"+(MultiThread.fileCount != 0 ? MultiThread.fileCount : "-0");
        try(FileWriter fw = new FileWriter("D:/wiki/"+filename+".html")) {
            fw.write(file);
            System.out.println("Файл "+filename+" записан потоком" + Thread.currentThread().getName());
            MultiThread.fileCount--;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
