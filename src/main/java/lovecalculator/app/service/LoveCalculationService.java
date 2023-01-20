package lovecalculator.app.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lovecalculator.app.model.Result;

@Service
public class LoveCalculationService {
  private static final String LOVE_CALCULATOR_URL = "https://love-calculator.p.rapidapi.com/getPercentage";
  private static final String LOVE_CALCULATOR_API = System.getenv("LOVE_CALCULATOR_API_KEY");
  public Optional<Result> getResult(String fname, String sname) throws IOException{
    // Create Url
   String loveCalculatorUrl =  UriComponentsBuilder.fromUriString(LOVE_CALCULATOR_URL)
   .queryParam("fname", fname)
   .queryParam("sname", sname)
   .toUriString();

  //  Create Headers
  final HttpHeaders headers = new HttpHeaders();
  headers.set("X-RapidAPI-Key", LOVE_CALCULATOR_API);
  headers.set("X-RapidAPI-Host", "love-calculator.p.rapidapi.com");

  // Create Entity with header
  final HttpEntity<String> entity = new HttpEntity<String>(headers);
  
  // Create template
   RestTemplate restTemplate = new RestTemplate();
   ResponseEntity<String> response = restTemplate.exchange(loveCalculatorUrl,HttpMethod.GET,entity,String.class);

   Result loveResult = Result.create(response.getBody());

   if (loveResult!=null) {
    return Optional.of(loveResult);
  }

   return Optional.empty();
  }

  @Autowired
  RedisTemplate<String, Result> redisTemplate;

  public int saveToRedis(final Result result){
    String key = result.getFname()+result.getSname();
    redisTemplate.opsForValue().set(key, result);
    Result retrievedFromRedis = redisTemplate.opsForValue().get(key);
    if(retrievedFromRedis==null){
      return 0;
    }
    return 1;
  }
  public List<Result> getAllFromRedis(){
    List<Result> allCalculations = new LinkedList<Result>();
    Set<String> keys = redisTemplate.keys("*");
    for(String key:keys){
      allCalculations.add(redisTemplate.opsForValue().get(key));
    }
    return allCalculations;
  }

  public Result getSingleResult(String id){
    Result result = redisTemplate.opsForValue().get(id);
    return result;
  }
}
