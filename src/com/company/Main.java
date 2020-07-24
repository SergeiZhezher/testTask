package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;


//
// Conditions task - https://drive.google.com/file/d/1ezFeqMEedmRBDaft6DepsHxmGgmGEEOz/view

public class Main {

    private static String inputFile = "src\\com\\company\\read.txt";
    private static String outputFile = "src\\com\\company\\write.txt";

    private static Map<Integer, Integer> bid = new HashMap<>();
    private static Map<Integer, Integer> ask = new HashMap<>();
    private static Map<Integer, Integer> spread = new HashMap<>();

    public static void main(String[] args) {

        long startedAt = currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get(inputFile))) {

            stream.forEach(n -> {

                if (n.startsWith("u") && n.endsWith("bid")) updateBid(n);
                else if (n.startsWith("u") && n.endsWith("ask")) updateAsk(n);
                else if (n.startsWith("u") && n.endsWith("spread")) updateSpread(n);

                else if (n.startsWith("q") && n.endsWith("best_bid")) getBestBid();
                else if (n.startsWith("q") && n.endsWith("best_ask")) getBestAsk();

                else if (n.startsWith("q") && n.contains("size")) getSize(n);
                else if (n.startsWith("o") && n.contains("buy")) buy(n);
                else if (n.startsWith("o") && n.contains("sell")) sell(n);

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("time: " + (currentTimeMillis() - startedAt) + " ms");
    }

     private static void updateBid(String s) {
        bid.put(getPriceOrSize(s, 0), getPriceOrSize(s, 1));
    }

     private static void updateAsk(String s) {
        ask.put(getPriceOrSize(s, 0), getPriceOrSize(s, 1));
    }

     private static void updateSpread(String s) {
        spread.put(getPriceOrSize(s, 0), getPriceOrSize(s, 1));
    }

     private static void getBestBid() {
        int price =  Collections.max(bid.keySet());
        writer(price + " " + bid.get(price) + "\n");
    }

     private static void getBestAsk() {
        int price =  Collections.min(ask.keySet());
        writer(price + " " + ask.get(price) + "\n");
    }

     private static void getSize(String s) {
        int price = Integer.parseInt(s.split(",")[2]);

        if (bid.get(price) != null) writer(bid.get(price) + "\n");
        else if (ask.get(price) != null)  writer(ask.get(price) + "\n");
        else writer(spread.get(price) + "\n");
    }

     private static void buy(String s) {
        removeSize(s, "ask");
    }

     private static void sell(String s) {
        removeSize(s, "bid");
    }

     private static int getPriceOrSize(String s, int index) {
        return  Integer.parseInt(s.replaceAll("[^\\d]", " ").trim().split(" ")[index]);
    }

     private static void removeSize(String s, String type) {
        int price;
        int reduceSize = getPriceOrSize(s, 0);

        if (type.equals("bid")) {
            price =  Collections.max(bid.keySet());
            bid.put(price, bid.get(price) - reduceSize);
        } else {
            price =  Collections.min(ask.keySet());
            ask.put(price, ask.get(price) - reduceSize);
        }
    }

     private static void writer(String s) {
        try {
            Files.write(Paths.get(outputFile), s.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}