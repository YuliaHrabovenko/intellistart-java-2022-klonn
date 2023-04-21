package interviewplanning.controllers;

import interviewplanning.dto.InterviewerBookingLimitDto;
import interviewplanning.dto.InterviewerTimeSlotRequestDto;
import interviewplanning.dto.InterviewerTimeSlotResponseDto;
import interviewplanning.models.InterviewerBookingLimit;
import interviewplanning.models.InterviewerTimeSlot;
import interviewplanning.services.InterviewerService;
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

  @GetMapping("/interviewers/{interviewer_id}/booking-limits")
  public List<InterviewerBookingLimit> getLimits(
      @PathVariable("interviewer_id") UUID interviewerId) {
    return interviewerService.getBookingLimitsByInterviewerId(interviewerId);
  }

  @PostMapping("/interviewers/{interviewer_id}/booking-limits")
  @ResponseStatus(code = HttpStatus.CREATED)
  public InterviewerBookingLimit createInterviewerBookingLimit(
      @PathVariable("interviewer_id") UUID interviewerId,
      @Valid @RequestBody InterviewerBookingLimitDto dto) {
    InterviewerBookingLimit limit = mapToInterviewerBookingLimit(dto);
    return interviewerService.setNextWeekInterviewerBookingLimit(limit, interviewerId);
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
      @PathVariable("interviewer_id") UUID interviewerId,
      @Valid @RequestBody InterviewerTimeSlotRequestDto timeSlotDto) {
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
      @PathVariable("interviewer_id") UUID interviewerId,
      @PathVariable("slot_id") UUID slotId,
      @Valid @RequestBody InterviewerTimeSlotRequestDto timeSlotDto) {
    InterviewerTimeSlot timeSlot = mapToInterviewerTimeSlot(timeSlotDto);

    return mapToInterviewerTimeSlotResponseDto(
        interviewerService.updateSlotForNextWeek(timeSlot, interviewerId, slotId));
  }

  @GetMapping("/weeks/current/interviewers/{interviewer_id}/slots")
  @ResponseStatus(code = HttpStatus.OK)
  public List<InterviewerTimeSlot> getCurrentWeekSlots(@PathVariable("interviewer_id")
                                                       UUID interviewerId) {
    return interviewerService.getWeekTimeSlotsByInterviewerId(interviewerId, true);
  }


  @GetMapping("/weeks/next/interviewers/{interviewer_id}/slots")
  @ResponseStatus(code = HttpStatus.OK)
  public List<InterviewerTimeSlot> getNextWeekSlots(@PathVariable("interviewer_id")
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

  public InterviewerBookingLimit mapToInterviewerBookingLimit(InterviewerBookingLimitDto dto) {
    return modelMapper.map(dto, InterviewerBookingLimit.class);
  }
}
