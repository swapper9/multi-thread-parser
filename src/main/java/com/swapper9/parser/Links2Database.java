package com.swapper9.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Links2Database {

    static private Document parsePage(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static void main(String[] args) {
        String startUrl = "https://habr.com/hubs/design/";
        Document doc = parsePage(startUrl);
        Elements links = doc.select("a[href]");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/parser", "root", "root");
            connection.setAutoCommit(false);
            String sql = "INSERT INTO parsed_pages (links) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            for (Element link : links) {
                String absLink = link.attr("abs:href");
                statement.setString (1, absLink);
                statement.execute();
            }
            connection.commit();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





