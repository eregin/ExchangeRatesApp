package com.example.app;

import com.example.app.Entities.CurrencyEntity;
import com.example.app.Entities.PointGraph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HelloApplication extends Application {
    private static double min = Double.MAX_VALUE;
    private static double max = Double.MIN_VALUE;

    private static List<CurrencyEntity> currencyList;

    @Override public void start(Stage stage) {
        stage.setTitle("Line Chart Sample");
        getCurrencyList();
        XYChart.Series series1 = graphBuilder(currencyList.stream().filter(c -> c.getName().equals("US Dollar")).findFirst().get());
        XYChart.Series series2 = graphBuilder(currencyList.stream().filter(c -> c.getName().equals("Euro")).findFirst().get());
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(min - (max - min)/min, max + (max - min)/min, 1);
        xAxis.setLabel("Number of Month");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Exchange Rates");
        lineChart.setCreateSymbols(false);
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);

        Scene scene  = new Scene(lineChart,800,600);

        stage.setScene(scene);
        stage.show();
    }

    public static XYChart.Series graphBuilder(CurrencyEntity entity) {
        List<Double> currency = getCurrency(entity.getParentCode());
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName(entity.getName());
        //populating the series with data
        for (int i = 0; i < currency.size(); i++) {
            series.getData().add(new XYChart.Data(i, currency.get(i)));
        }
        double min_local = Collections.min(currency);
        double max_local = Collections.max(currency);
        if (min_local < min)
            min = min_local;
        if (max_local > max)
            max = max_local;
        return series;
    }

    public static List<Double> getCurrency(String currencyCode) {
        List<Double> list = new LinkedList<>();
        String str = getUrlContent("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=01/01/2022&date_req2=29/01/2022&VAL_NM_RQ=" + currencyCode);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            File temp = File.createTempFile("pattern", ".suffix");
            temp.deleteOnExit();
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(str);
            out.close();

            Document document = builder.parse(temp);
            document.getDocumentElement().normalize();

            Node rootNode = document.getFirstChild();
            NodeList rootChilds = rootNode.getChildNodes();
            for (int i = 0; i < rootChilds.getLength(); i++) {
                NodeList valute = rootChilds.item(i).getChildNodes();
                list.add(Double.parseDouble(valute.item(1).getTextContent().replace(',', '.')));
            }
        } catch (Exception exception) {
            System.out.println("Fuck");
        }
        return list;
    }

    private static void getCurrencyList() {
        currencyList = new ArrayList<>();
        String str = getUrlContent("http://www.cbr.ru/scripts/XML_val.asp?d=0");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            File temp = File.createTempFile("pattern", ".suffix");
            temp.deleteOnExit();
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(str);
            out.close();

            Document document = builder.parse(temp);
            document.getDocumentElement().normalize();

            Node rootNode = document.getFirstChild();
            NodeList rootChilds = rootNode.getChildNodes();
            for (int i = 0; i < rootChilds.getLength(); i++) {
                NodeList valute = rootChilds.item(i).getChildNodes();
                CurrencyEntity add = new CurrencyEntity();
                add.setId(valute.item(0).getTextContent());
                add.setName(valute.item(1).getTextContent());
                add.setNominal(Integer.parseInt(valute.item(2).getTextContent()));
                add.setParentCode(valute.item(3).getTextContent().trim());
                currencyList.add(add);
            }
        } catch (Exception exception) {
            System.out.println("Fuck");
        }
    }

    private static String getUrlContent(String urlAdress) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlAdress);
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line;
            while ( (line = reader.readLine()) != null ) {
                content.append(line + "\n");
            }
            reader.close();
        } catch (Exception ex) {
            System.out.println("ERROR");
        }
        return  content.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}