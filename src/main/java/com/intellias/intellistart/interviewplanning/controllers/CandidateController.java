package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.dto.CandidateTimeSlotRequestDto;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.security.JwtUser;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
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
  private final ModelMapper modelMapper;

  @Autowired
  public CandidateController(CandidateService candidateService, ModelMapper modelMapper) {
    this.candidateService = candidateService;
    this.modelMapper = modelMapper;
  }

  /**
   * Endpoint to create candidate's slot.
   *
   * @param candidateTimeSlotDto slot
   * @return created slot
   */

  @PostMapping("/candidates/current/slots")
  @ResponseStatus(code = HttpStatus.CREATED)
  public CandidateTimeSlot createCandidateSlot(
      @Valid @RequestBody CandidateTimeSlotRequestDto candidateTimeSlotDto,
      Authentication authentication) {
    CandidateTimeSlot timeSlot = mapToCandidateTimeSlot(candidateTimeSlotDto);
    JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
    timeSlot.setEmail(jwtUser.getEmail());
    timeSlot.setName(jwtUser.getUsername());
    return candidateService.createSlot(timeSlot);
  }

  @PostMapping("/candidates/current/slots/{slot_id}")
  public CandidateTimeSlot updateCandidateSlot(@PathVariable("slot_id") UUID slotId,
                                               @Valid @RequestBody
                                               CandidateTimeSlotRequestDto candidateTimeSlotDto) {
    CandidateTimeSlot timeSlot = mapToCandidateTimeSlot(candidateTimeSlotDto);
    return candidateService.updateSlot(timeSlot, slotId);
  }

  @GetMapping("/candidates/current/slots")
  public List<CandidateTimeSlot> getCandidateSlots(Authentication authentication) {
    JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
    return candidateService.getSlotsByCandidateEmail(jwtUser.getEmail());
  }

  public CandidateTimeSlot mapToCandidateTimeSlot(
      CandidateTimeSlotRequestDto candidateTimeSlotDto) {
    return modelMapper.map(candidateTimeSlotDto, CandidateTimeSlot.class);
  }
}
