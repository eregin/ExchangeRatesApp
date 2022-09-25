package com.example.app;

import com.example.app.Entities.CurrencyEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MoneyController {
    ///////////////////// JavaFX ////////////////////////
    @FXML
    public DatePicker dateTo;
    @FXML
    public DatePicker dateFrom;
    @FXML
    public ComboBox<String> currencyChoice;
    @FXML
    public Button startPlot;
    @FXML
    public LineChart<Number,Number> chartExchangeRates;
    @FXML
    public NumberAxis xAxis;
    @FXML
    public NumberAxis yAxis;
    @FXML
    public ImageView back;

    ///////////////////// Logic variable /////////////////////
    private Map<String, CurrencyEntity> currencyBox;
    private CurrencyEntity chosenCurrency;
    DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String maxDate;
    private String minDate;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    ///////////////////// Logic /////////////////////////////

    @FXML
    void initialize() {
        currencyBox = new HashMap<>();
        getCurrencyList();
        currencyChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chosenCurrency = currencyBox.get(newValue);
        });
    }
    @FXML
    public void goBack() {
        System.out.println("ssss");
    }
    @FXML
    public void setDateTo(ActionEvent actionEvent) {
        maxDate = formater.format(dateTo.getValue());
    }
    @FXML
    public void setDateFrom(ActionEvent actionEvent) {
        minDate = formater.format(dateFrom.getValue());
    }

    @FXML
    public void startPloting(ActionEvent actionEvent) {
        XYChart.Series<Number, Number> series = graphBuilder();
        //defining the axes
        xAxis = (NumberAxis) chartExchangeRates.getXAxis();
        yAxis = (NumberAxis) chartExchangeRates.getYAxis();
        xAxis.setLowerBound(min - (max - min)/min);
        xAxis.setLowerBound(max + (max - min)/min);
        //creating the chart
        chartExchangeRates.setCreateSymbols(false);
        chartExchangeRates.getData().add(series);
    }

    public XYChart.Series<Number, Number> graphBuilder() {
        List<Double> currency = getCurrency(chosenCurrency.getParentCode());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(chosenCurrency.getName());
        for (int i = 0; i < currency.size(); i++) {
            series.getData().add(new XYChart.Data<Number, Number>(i * 1.0, currency.get(i)));
        }
        double min_local = Collections.min(currency);
        double max_local = Collections.max(currency);
        if (min_local < min)
            min = min_local;
        if (max_local > max)
            max = max_local;
        return series;
    }

    public List<Double> getCurrency(String currencyCode) {
        List<Double> list = new LinkedList<>();
        String str = Main.getUrlContent(
                String.format("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=%s&date_req2=%s&VAL_NM_RQ=%s",
                        minDate, maxDate, currencyCode)
        );

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

    private void getCurrencyList() {
        String str = Main.getUrlContent("http://www.cbr.ru/scripts/XML_val.asp?d=0");

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
                CurrencyEntity toAdd = new CurrencyEntity();
                toAdd.setId(valute.item(0).getTextContent());
                toAdd.setName(valute.item(1).getTextContent());
                toAdd.setNominal(Integer.parseInt(valute.item(2).getTextContent()));
                toAdd.setParentCode(valute.item(3).getTextContent().trim());
                currencyBox.put(toAdd.getName(), toAdd);
                currencyChoice.getItems().add(toAdd.getName());
            }
        } catch (Exception exception) {
            System.out.println("Fuck");
        }
    }


}
