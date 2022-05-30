package main.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

@Component
public class Lemmatization {

  private LuceneMorphology luceneMorphology;

  public Lemmatization() throws IOException {
    luceneMorphology = new RussianLuceneMorphology();
  }

  public HashMap<String, Integer> analysisText(String text) {
    HashMap<String, Integer> listLemmas = new HashMap<>();
    text = text.toLowerCase(Locale.ROOT).trim();
    String[] arrayWords = text.split(" ");
    for (String arrayWord : arrayWords) {
      if (arrayWord.length() <= 3) {
        continue;
      }
      List<String> lemma = luceneMorphology.getNormalForms(arrayWord);
      lemma.forEach(l -> {
        if (!isServicePart(l)) {
          listLemmas.merge(l, 1, Integer::sum);
        }
      });
    }
    return listLemmas;
  }

  public List<String> getLemmas(String text) {
    List<String> lemmasList = new ArrayList<>();
    text = text.toLowerCase(Locale.ROOT).trim();
    String[] arrayWords = text.split(" ");
    for (String word : arrayWords) {
      if (word.length() <= 3) {
        continue;
      }
      luceneMorphology.getNormalForms(word).forEach(normalForm ->{
        if(!isServicePart(normalForm)){
          lemmasList.add(normalForm);
        }
      });
    }
    return lemmasList;
  }

  private boolean isServicePart(String word) {
    List<String> lemma = luceneMorphology.getMorphInfo(word);
    for (String l : lemma) {
      if (l.contains("ПРЕДЛ")
          || l.contains("СОЮЗ")
          || l.contains("МЕЖД")
          || l.contains("МС")
          || l.contains("ЧАСТ")
          || l.length() <= 3) {
        return true;
      }
    }
    return false;
  }

  public LuceneMorphology getLuceneMorphology() {
    return luceneMorphology;
  }
}
