package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Week Controller.
 */
@RestController
public class WeekController {

  @GetMapping(value = "weeks/current", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, String> getCurrentWeek() {
    return Collections.singletonMap("weekNum", WeekUtil.getCurrentWeekNumber());
  }

  @GetMapping(value = "weeks/next", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, String> getNextWeek() {
    return Collections.singletonMap("weekNum", WeekUtil.getNextWeekNumber());
  }
}
