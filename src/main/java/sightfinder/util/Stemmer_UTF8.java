package sightfinder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class Stemmer_UTF8 {
  
  public Hashtable<String, String> stemmingRules = new Hashtable<String, String>();
  
  public int STEM_BOUNDARY = 1;

  public static Pattern vocals = Pattern.compile("[^аъоуеиюя]*[аъоуеиюя]");
  public static Pattern p = Pattern.compile("([а-я]+)\\s==>\\s([а-я]+)\\s([0-9]+)");
  
  
  private static File getStemmingRulesFile() {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      URL pipelineResource = classloader.getResource("stem_rules.txt");
      File pipelineFile = null;
      try {
          pipelineFile = new File(pipelineResource.toURI());
      } catch (URISyntaxException e) {
          e.printStackTrace();;
      }

      return pipelineFile;
  }

  public void loadStemmingRules() throws Exception {
	File rulesFile = getStemmingRulesFile();
    FileInputStream fis = new FileInputStream(rulesFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

    stemmingRules.clear();
    String s = null;
    while ((s = br.readLine()) != null) {
      Matcher m = p.matcher(s);
      if (m.matches()) {
        int j = m.groupCount();
        if (j == 3) {
          if (Integer.parseInt(m.group(3)) > STEM_BOUNDARY) {
            stemmingRules.put(m.group(1), m.group(2));
          }
        }
      }
    }
    
    br.close();
  }

  public String stem(String word) {
    Matcher m = vocals.matcher(word);
    if (!m.lookingAt()) {
      return word;
    }
    for (int i = m.end() + 1; i < word.length(); i++) {
      String suffix = word.substring(i);
      if ((suffix = (String) stemmingRules.get(suffix)) != null) {
        return word.substring(0, i) + suffix;
      }
    }
    return word;
  }
  
}
