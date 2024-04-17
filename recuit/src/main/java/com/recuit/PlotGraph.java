package com.recuit;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class PlotGraph extends JFrame {

    public PlotGraph(String title) {
        super(title);

        // Read data from file
        List<int[]> points = new ArrayList<>();
        List<int[]> edges = new ArrayList<>();
        List<String> edgeLabels = new ArrayList<>();
        String objective = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/RESULTS/matrixBigObstacles.txt"));
            String line = reader.readLine();
            objective = (line.split("=")[0]);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && edges.isEmpty()) {
                    points.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
                } else if (parts.length == 4) {
                    edges.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]), Integer.parseInt(parts[3])});
                    edgeLabels.add(parts[0] + "," + parts[1] + " to " + parts[2] + "," + parts[3]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Count occurrences of each point
        Map<String, Integer> pointCounter = new HashMap<>();
        for (int[] point : points) {
            String key = point[0] + "," + point[1];
            pointCounter.put(key, pointCounter.getOrDefault(key, 0) + 1);
        }

        // Create dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Edges");

        // Generate random colors for edges
        Random rand = new Random();
        List<Color> edgeColors = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            edgeColors.add(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        }

        // Plot edges with labels and random colors
        for (int i = 0; i < edges.size(); i++) {
            int[] edge = edges.get(i);
            String label = edgeLabels.get(i);
            Color color = edgeColors.get(i);
            XYSeries edgeSeries = new XYSeries(label); // Create a new series for each edge
            edgeSeries.add(edge[0], edge[1]);
            edgeSeries.add(edge[2], edge[3]);
            
            // Check if a series with the same key already exists
            boolean seriesExists = false;
            for (int j = 0; j < dataset.getSeriesCount(); j++) {
                if (dataset.getSeriesKey(j).equals(label)) {
                    seriesExists = true;
                    break;
                }
            }
            
            // Add the series to the dataset if it doesn't already exist
            if (!seriesExists) {
                dataset.addSeries(edgeSeries);
            }
        }


        // Plot points
        for (int[] point : points) {
            int x = point[0];
            int y = point[1];
            if (pointCounter.get(x + "," + y) > 1) {
                series.add(x, y);
            } else {
                series.add(x, y);
            }
        }

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                title+"; objective="+objective,
                "X",
                "Y",
                dataset
        );

        // Customize the chart
        chart.setBackgroundPaint(Color.white);

        // Create a panel to display the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        
        final PlotGraph example = new PlotGraph("Line Chart Example");
        example.setSize(800, 600);
        example.setLocationRelativeTo(null);
        example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        example.setVisible(true);
        
        TimerTask task = new TimerTask() {
        public void run() {
            example.setVisible(false);
        }
        };
        Timer timer = new Timer("Timer");
        
        long delay = 2000;
        timer.schedule(task, delay);

    }
}
