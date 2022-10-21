package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Candidate controller.
 */
@RestController
//@RequestMapping("/candidates/current/slots")
public class CandidateController {

  @Autowired
  private CandidateService candidateService;

  @Autowired
  private CoordinatorService coordinatorService;

  @PostMapping("/candidates/current/slots")
  @ResponseStatus(code = HttpStatus.CREATED)
  public CandidateTimeSlot createCandidateSlot(@RequestBody CandidateTimeSlot candidateTimeSlot) {
    return candidateService.createSlot(candidateTimeSlot);
  }

  @PostMapping("/candidates/current/slots/{slot_id}")
  public CandidateTimeSlot updateCandidateSlot(@PathVariable("slot_id") UUID slotId,
                                               @RequestBody CandidateTimeSlot candidateTimeSlot) {
    return candidateService.updateSlot(candidateTimeSlot, slotId);
  }
  //  @GetMapping("/candidates/current/slots")
  //  public List<CandidateTimeSlot> getCandidateSlots(
  //      @PathVariable("candidate_id") UUID candidateId) {
  //    return candidateService.getSlotsByCandidateId(candidateId);
  //  }

  @DeleteMapping("/candidates/current/slots/{slot_id}")
  public void deleteCandidateSlotById(@PathVariable("slot_id") UUID slotId) {
    candidateService.deleteSlot(slotId);
  }
}
