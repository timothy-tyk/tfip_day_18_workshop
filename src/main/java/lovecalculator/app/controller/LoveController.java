package lovecalculator.app.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import lovecalculator.app.model.Result;
import lovecalculator.app.service.LoveCalculationService;

@Controller
@RequestMapping(path = "/calculate")
public class LoveController {

  @Autowired
  LoveCalculationService loveCalcService;
  
  @GetMapping()
  public String showCalculationForm(Model model){
  
    return "calculate";
  }

  @PostMapping("/result")
  public String getCalculation(Model model, @RequestParam(required = true) String fname, @RequestParam(required = true) String sname, HttpServletResponse httpResponse) throws IOException{
   Optional <Result> loveResult = loveCalcService.getResult(fname, sname);
   if(Integer.parseInt(loveResult.get().getPercentage())==0){
     httpResponse.setStatus(418);
     return "teapot";
   }
   if(Integer.parseInt(loveResult.get().getPercentage())>=75){
     loveResult.get().setCompatibility("COMPATIBLE");
     httpResponse.setStatus(201);
    }
    if(Integer.parseInt(loveResult.get().getPercentage())<75){
      loveResult.get().setCompatibility("NOT COMPATIBLE");
      httpResponse.setStatus(201);
    }    
    // Save result to Redis
    int res = loveCalcService.saveToRedis(loveResult.get());

    model.addAttribute("result", loveResult.get());
    return "result";
  }

  @GetMapping("/result/{id}")
  public String getResult(Model model, @PathVariable String id){
    Result result = loveCalcService.getSingleResult(id);
    model.addAttribute("result", result);
    return "result";
  }
  
  @GetMapping("/list")
  public String getListOfCalculations(Model model){
    List<Result> allResults = loveCalcService.getAllFromRedis();
    model.addAttribute("results", allResults);
    return "list";
  }
}
