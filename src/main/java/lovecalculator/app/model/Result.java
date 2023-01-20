package lovecalculator.app.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Result implements Serializable{
  private String fname;
  private String sname;
  private String percentage;
  private String resultDescription;
  private String compatibility;

  // #region Getters and Setters
  
  public String getFname() {
    return fname;
  }
  public void setFname(String fname) {
    this.fname = fname;
  }
  public String getSname() {
    return sname;
  }
  public void setSname(String sname) {
    this.sname = sname;
  }
  public String getPercentage() {
    return percentage;
  }
  public void setPercentage(String percentage) {
    this.percentage = percentage;
  }
  public String getResultDescription() {
    return resultDescription;
  }
  public void setResultDescription(String resultDescription) {
    this.resultDescription = resultDescription;
  }
  public String getCompatibility() {
    return compatibility;
  }
  public void setCompatibility(String compatibility) {
    this.compatibility = compatibility;
  }
  // #endregion
  

  // Methods
  public static Result create(String json) throws IOException{
    Result loveResult = new Result();
    try(InputStream is = new ByteArrayInputStream(json.getBytes())){
      JsonReader jsonReader = Json.createReader(is);
      JsonObject jsonObj = jsonReader.readObject();

      String fNameDecoded = URLDecoder.decode(jsonObj.getString("fname"), "UTF-8") ;
      String SNameDecoded = URLDecoder.decode(jsonObj.getString("sname"), "UTF-8") ;

      loveResult.setFname(fNameDecoded);
      loveResult.setSname(SNameDecoded);
      loveResult.setPercentage(jsonObj.getString("percentage"));
      loveResult.setResultDescription(jsonObj.getString("result"));
      return loveResult;
    }
  }

  
}
