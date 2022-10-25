package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Interviewer controller.
 */
@RestController
public class InterviewerController {
  @Autowired
  private InterviewerService interviewerService;

  @GetMapping("/interviewers/booking-limits/{interviewerId}")
  public List<InterviewerBookingLimit> getLimits(
      @PathVariable("interviewerId") UUID interviewerId) {
    return interviewerService.getBookingLimitsByInterviewerId(interviewerId);
  }

  @PostMapping("/interviewers/booking-limits")
  @ResponseStatus(code = HttpStatus.CREATED)
  public InterviewerBookingLimit createInterviewerBookingLimit(
      @Valid @RequestBody InterviewerBookingLimit interviewerBookingLimit) {
    return interviewerService.setNextWeekInterviewerBookingLimit(interviewerBookingLimit);
 * Interviewer Controller
 */

@RestController
public class InterviewerController {
  @Autowired
  InterviewerService interviewerService;

  @GetMapping("/weeks/current/interviewers/{interviewerId}/slots")
  @ResponseStatus(code = HttpStatus.OK)
  public List<InterviewerTimeSlot> getCurrentWeekSlots(@PathVariable("interviewer_id")
                                                             UUID interviewerId){
    return interviewerService.getWeekTimeSlotsByInterviewerId(interviewerId, true);
  }


  @GetMapping("/weeks/next/interviewers/{interviewerId}/slots")
  @ResponseStatus(code = HttpStatus.OK)
  public List<InterviewerTimeSlot> getNextWeekSlots(@PathVariable("interviewer_id")
                                                           UUID interviewerId){
    return interviewerService.getWeekTimeSlotsByInterviewerId(interviewerId, false);
  }
}
