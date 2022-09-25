package com.example.app;

import com.example.app.Entities.WeatherPoint;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class GoodMorningController {

    private Map<String, Image[]> images;

    private List<WeatherPoint> weather;

    private String apiKey = "51bde7a9ff0c968dd9dbac176a98d494";

    @FXML
    private ImageView image1;
    @FXML
    private ImageView image2;
    @FXML
    private ImageView image3;
    @FXML
    private ImageView image4;
    @FXML
    private ImageView image5;
    @FXML
    private Button refresh;
    @FXML
    private Label weatherDescription1;
    @FXML
    private Label weatherDescription2;
    @FXML
    private Label weatherDescription3;
    @FXML
    private Label weatherDescription4;
    @FXML
    private Label weatherDescription5;

    @FXML
    void refreshData(ActionEvent event) {
        List<Label> labels = new ArrayList<>();
        labels.add(weatherDescription1);
        labels.add(weatherDescription2);
        labels.add(weatherDescription3);
        labels.add(weatherDescription4);
        labels.add(weatherDescription5);
        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(image1);
        imageViews.add(image2);
        imageViews.add(image3);
        imageViews.add(image4);
        imageViews.add(image5);
        getWeatherData();
        for (int i = 0; i < 5; i++) {
            WeatherPoint wp = weather.get(i);
            labels.get(i).setText(
                    String.format("%s-%s\n%s\nwind %s\ntemperature %s\nfeels like %s\npressure %s\nhumidity %s\n%s",
                            wp.getFrom(), wp.getTo(),
                            wp.getWeatherName(),
                            wp.getWindInfo(),
                            wp.getTemperature(),
                            wp.getFeels_like(),
                            wp.getPressure(),
                            wp.getHumidity(),
                            wp.getCloudInfo())
            );
        }
    }
    
    @FXML
    void initialize() {
        weather = new ArrayList<>();
        images = new HashMap();
        Image[] sample = new Image[2];
        ////////////////////////////////////////////////////////////////////////
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/sun.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/sun.png"));
        images.put("clear sky", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/couded_sun.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/dark-clouded_sun.png"));
        images.put("few clouds", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/double-clouded_sun.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/double-dark-clouded_sun.png"));
        images.put("scattered clouds", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/cloud.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/cloud.png"));
        images.put("broken clouds", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/double-cloud.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/double-dark-cloud.png"));
        images.put("overcast clouds", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/cloud_lighting.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/cloud_lighting.png"));
        images.put("thunderstorm", sample);
        images.put("tornado", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/rain_sun.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/big-rain_sun.png"));
        images.put("light intensity drizzle", sample);
        images.put("drizzle", sample);
        images.put("heavy intensity drizzle", sample);
        images.put("light intensity drizzle rain", sample);
        images.put("drizzle rain", sample);
        images.put("heavy intensity drizzle rain", sample);
        images.put("shower rain and drizzle", sample);
        images.put("heavy shower rain and drizzle", sample);
        images.put("shower drizzle", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/rain.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/big-rain.png"));
        images.put("light rain", sample);
        images.put("moderate rain", sample);
        images.put("heavy intensity rain", sample);
        images.put("very heavy rain", sample);
        images.put("extreme rain", sample);
        images.put("freezing rain", sample);
        images.put("light intensity shower rain", sample);
        images.put("shower rain", sample);
        images.put("heavy intensity shower rain", sample);
        images.put("ragged shower rain", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/snow.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/big-snow.png"));
        images.put("light snow", sample);
        images.put("snow", sample);
        images.put("heavy snow", sample);
        images.put("light shower snow", sample);
        images.put("shower snow", sample);
        images.put("heavy shower snow", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/snow-rain.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/big-snow-rain.png"));
        images.put("sleet", sample);
        images.put("light shower sleet", sample);
        images.put("shower sleet", sample);
        images.put("light rain and snow", sample);
        sample = new Image[2];
        sample[0] = new Image(getClass().getResourceAsStream("images/weather/fog.png"));
        sample[1] = new Image(getClass().getResourceAsStream("images/weather/fog.png"));
        images.put("mist", sample);
        images.put("smoke", sample);
        images.put("haze", sample);
        images.put("sand", sample);
        images.put("fog", sample);
        images.put("dust", sample);
        images.put("squalls", sample);
    }


    private void getWeatherData() {
        String str = Main.getUrlContent(
                String.format("https://api.openweathermap.org/data/2.5/forecast?mode=xml&q=Moscow&appid=%s", apiKey)
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
                String forecast = rootChilds.item(i).getNodeName();
                if (forecast.equals("forecast")) {
                    NodeList weatherData = rootChilds.item(i).getChildNodes();
                    for (int j = 0; j < 5; j++) {
                        WeatherPoint wp = new WeatherPoint();
                        wp.setFrom(weatherData.item(j).getAttributes().item(0).getTextContent());
                        wp.setTo(weatherData.item(j).getAttributes().item(1).getTextContent());
                        NodeList timeWPoint = weatherData.item(j).getChildNodes();
                        for(int k = 0; k < timeWPoint.getLength(); k++) {
                            String property = timeWPoint.item(k).getNodeName();
                            switch (property) {
                                case "symbol" :
                                    wp.setWeatherName(timeWPoint.item(k).getAttributes().getNamedItem("name").getTextContent());
                                    break;
                                case "windDirection" :
                                    wp.setWindInfo(timeWPoint.item(k).getAttributes().getNamedItem("name").getTextContent());
                                    break;
                                case "windSpeed" :
                                    wp.setWindInfo(
                                        String.format("%s %s%s %s",
                                        wp.getWindInfo(),
                                            timeWPoint.item(k).getAttributes().getNamedItem("mps").getTextContent(),
                                            timeWPoint.item(k).getAttributes().getNamedItem("unit").getTextContent(),
                                            timeWPoint.item(k).getAttributes().getNamedItem("name").getTextContent()
                                        )
                                    );
                                    break;
                                case "temperature" :
                                    wp.setTemperature(
                                        String.format("%s %s",
                                                timeWPoint.item(k).getAttributes().getNamedItem("value").getTextContent(),
                                                timeWPoint.item(k).getAttributes().getNamedItem("unit").getTextContent()
                                        )
                                    );
                                    break;
                                case "feels_like" :
                                    wp.setFeels_like(
                                        String.format("%s %s",
                                                timeWPoint.item(k).getAttributes().getNamedItem("value").getTextContent(),
                                                timeWPoint.item(k).getAttributes().getNamedItem("unit").getTextContent()
                                        )
                                    );
                                    break;
                                case "pressure" :
                                    wp.setPressure(
                                        String.format("%s%s",
                                                timeWPoint.item(k).getAttributes().getNamedItem("value").getTextContent(),
                                                timeWPoint.item(k).getAttributes().getNamedItem("unit").getTextContent()
                                        )
                                    );
                                    break;
                                case "humidity" :
                                    wp.setHumidity(
                                            String.format("%s%s",
                                                    timeWPoint.item(k).getAttributes().getNamedItem("value").getTextContent(),
                                                    timeWPoint.item(k).getAttributes().getNamedItem("unit").getTextContent()
                                            )
                                    );
                                    break;
                                case "clouds" :
                                    wp.setCloudInfo(
                                            String.format("%s %s%s",
                                                    timeWPoint.item(k).getAttributes().getNamedItem("value").getTextContent(),
                                                    timeWPoint.item(k).getAttributes().getNamedItem("all").getTextContent(),
                                                    timeWPoint.item(k).getAttributes().getNamedItem("unit").getTextContent()
                                            )
                                    );
                            }
                        }
                        weather.add(wp);
                    }
                }
            }
        } catch (Exception exception) {
            System.out.println("Fuck");
        }
    }

}
