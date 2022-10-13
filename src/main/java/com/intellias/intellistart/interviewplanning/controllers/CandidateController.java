package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  CandidateTimeSlotRepository candidateTimeSlotRepository;

  @PostMapping("/candidates/{candidate_id}/slots")
  @ResponseStatus(code = HttpStatus.CREATED)
  public CandidateTimeSlot createCandidateSlot(@RequestBody CandidateTimeSlot candidateTimeSlot) {
    return candidateService.createSlot(candidateTimeSlot);
  }

  @PostMapping("/candidates/{candidate_id}/slots/{slot_id}")
  public CandidateTimeSlot updateCandidateSlot(@PathVariable("slot_id") UUID slotId,
                                               @RequestBody CandidateTimeSlot candidateTimeSlot) {
    return candidateService.updateSlot(candidateTimeSlot, slotId);
  }

  @GetMapping("/candidates/{candidate_id}/slots")
  public List<CandidateTimeSlot> getCandidateSlots(@PathVariable("candidate_id") UUID candidateId) {
    return candidateService.getSlotsByCandidateId(candidateId);
  }

  @DeleteMapping("/candidates/{candidate_id}/slots/{slot_id}")
  public void deleteCandidateSlotById(@PathVariable("slot_id") UUID slotId) {
    candidateService.deleteSlot(slotId);
  }

}
