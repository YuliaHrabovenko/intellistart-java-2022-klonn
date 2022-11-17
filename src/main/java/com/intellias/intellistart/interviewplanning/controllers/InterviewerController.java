package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.dto.InterviewerTimeSlotRequestDto;
import com.intellias.intellistart.interviewplanning.dto.InterviewerTimeSlotResponseDto;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Interviewer controller.
 */
@RestController
public class InterviewerController {
  private final InterviewerService interviewerService;
  private final ModelMapper modelMapper;

  @Autowired
  public InterviewerController(InterviewerService interviewerService, ModelMapper modelMapper) {
    this.interviewerService = interviewerService;
    this.modelMapper = modelMapper;
  }

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
  }

  /**
   * Create interviewer time slot.
   *
   * @param interviewerId interviewer id
   * @param timeSlotDto   time slot dto
   * @return dto response of interviewer time slot
   */
  @PostMapping(path = "/interviewers/{interviewer_id}/slots")
  @ResponseStatus(code = HttpStatus.CREATED)
  public InterviewerTimeSlotResponseDto createInterviewerTimeSlot(
      @Valid @PathVariable("interviewer_id") UUID interviewerId,
      @RequestBody InterviewerTimeSlotRequestDto timeSlotDto) {
    InterviewerTimeSlot timeSlot = mapToInterviewerTimeSlot(timeSlotDto);

    return mapToInterviewerTimeSlotResponseDto(
        interviewerService.createSlot(timeSlot, interviewerId));
  }

  /**
   * Update interviewer time slot.
   *
   * @param interviewerId interviewer id
   * @param slotId        slot id
   * @param timeSlotDto   request dto of time slot
   * @return response dto of interviewer time slot
   */
  @PostMapping("/interviewers/{interviewer_id}/next-week-slots/{slot_id}")
  public InterviewerTimeSlotResponseDto updateInterviewerTimeSlot(
      @Valid @PathVariable("interviewer_id") UUID interviewerId,
      @PathVariable("slot_id") UUID slotId,
      @RequestBody InterviewerTimeSlotRequestDto timeSlotDto) {
    InterviewerTimeSlot timeSlot = mapToInterviewerTimeSlot(timeSlotDto);

    return mapToInterviewerTimeSlotResponseDto(
        interviewerService.updateSlotForNextWeek(timeSlot, interviewerId, slotId));
  }

  @GetMapping("/weeks/current/interviewers/{interviewerId}/slots")
  @ResponseStatus(code = HttpStatus.OK)
  public List<InterviewerTimeSlot> getCurrentWeekSlots(@PathVariable("interviewerId")
                                                       UUID interviewerId) {
    return interviewerService.getWeekTimeSlotsByInterviewerId(interviewerId, true);
  }


  @GetMapping("/weeks/next/interviewers/{interviewerId}/slots")
  @ResponseStatus(code = HttpStatus.OK)
  public List<InterviewerTimeSlot> getNextWeekSlots(@PathVariable("interviewerId")
                                                    UUID interviewerId) {
    return interviewerService.getWeekTimeSlotsByInterviewerId(interviewerId, false);
  }

  public InterviewerTimeSlot mapToInterviewerTimeSlot(InterviewerTimeSlotRequestDto dto) {
    return modelMapper.map(dto, InterviewerTimeSlot.class);
  }

  /**
   * Mapping to interviewer time slot response dto.
   *
   * @param timeSlot time slot
   * @return dto response of interviewer time slot
   */
  public InterviewerTimeSlotResponseDto mapToInterviewerTimeSlotResponseDto(
      InterviewerTimeSlot timeSlot) {
    return InterviewerTimeSlotResponseDto.builder()
        .id(timeSlot.getId())
        .weekNum(timeSlot.getWeekNum())
        .day(timeSlot.getDayOfWeek())
        .from(timeSlot.getFrom())
        .to(timeSlot.getTo())
        .build();
  }
}
