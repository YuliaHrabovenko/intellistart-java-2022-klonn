package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.Week;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Week Controller.
 */
@RestController
public class WeekController {

  @Autowired
  private WeekService weekService;

  @GetMapping("weeks/current")
  public Week getCurrentWeek() {
    return weekService.getCurrentWeekNumber();
  }

  @GetMapping("weeks/next")
  public Week getNextWeek() {
    return weekService.getNextWeekNumber();
  }
}
