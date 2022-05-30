package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import main.service.Lemmatization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLemmatization {

  private Lemmatization lemmatization;

  @BeforeEach
  public void setUp() throws IOException {
    lemmatization = new Lemmatization();

  }

  @Test
  public void analysisTextTest(){
    String text = "Повторное появление леопарда в Осетии позволяет предположить "
        + "что леопард постоянно обитает в некоторых районах Северного Кавказа";
    HashMap<String, Integer> actual = lemmatization.analysisText(text);
    HashMap<String,Integer> expected = new HashMap<>();
    expected.put("обитать", 1);
    expected.put("появление", 1);
    expected.put("постоянно", 1);
    expected.put("позволять", 1);
    expected.put("предположить", 1);
    expected.put("постоянный", 1);
    expected.put("северный", 1);
    expected.put("район", 1);
    expected.put("кавказ", 1);
    expected.put("осетия", 1);
    expected.put("леопард", 2);
    expected.put("повторный", 1);
    assertEquals(expected, actual);
  }

  @Test
  public void getLemmasTest(){
    String text = "Повторное появление леопарда в Осетии позволяет предположить "
        + "что леопард постоянно обитает в некоторых районах Северного Кавказа";
    List<String> actual = lemmatization.getLemmas(text);
    List<String> expected = new ArrayList<>();
    expected.add("повторный");
    expected.add("появление");
    expected.add("леопард");
    expected.add("осетия");
    expected.add("позволять");
    expected.add("предположить");
    expected.add("леопард");
    expected.add("постоянно");
    expected.add("постоянный");
    expected.add("обитать");
    expected.add("район");
    expected.add("северный");
    expected.add("кавказ");
    assertEquals(expected, actual);
  }

}
