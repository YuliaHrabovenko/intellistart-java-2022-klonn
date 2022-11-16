package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.security.JwtUser;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
public class CandidateController {

  private final CandidateService candidateService;

  @Autowired
  public CandidateController(CandidateService candidateService) {
    this.candidateService = candidateService;
  }

  /**
   * Endpoint to create candidate's slot.
   *
   * @param candidateTimeSlot slot
   * @return created slot
   */
  @PostMapping("/candidates/current/slots")
  @ResponseStatus(code = HttpStatus.CREATED)
  public CandidateTimeSlot createCandidateSlot(
      @Valid @RequestBody CandidateTimeSlot candidateTimeSlot, Authentication authentication) {
    JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
    candidateTimeSlot.setName(jwtUser.getUsername());
    candidateTimeSlot.setEmail(jwtUser.getEmail());
    return candidateService.createSlot(candidateTimeSlot);
  }

  @PostMapping("/candidates/current/slots/{slot_id}")
  public CandidateTimeSlot updateCandidateSlot(@PathVariable("slot_id") UUID slotId,
                                               @Valid @RequestBody
                                               CandidateTimeSlot candidateTimeSlot) {
    return candidateService.updateSlot(candidateTimeSlot, slotId);
  }

  @GetMapping("/candidates/current/slots")
  public List<CandidateTimeSlot> getCandidateSlots(Authentication authentication) {
    JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
    return candidateService.getSlotsByCandidateEmail(jwtUser.getEmail());
  }

}
