package interviewplanning.controllers;

import interviewplanning.dto.BookingRequestDto;
import interviewplanning.dto.InterviewerTimeSlotRequestDto;
import interviewplanning.dto.InterviewerTimeSlotResponseDto;
import interviewplanning.dto.UserDto;
import interviewplanning.dto.UserRequestDto;
import interviewplanning.models.Booking;
import interviewplanning.models.InterviewerTimeSlot;
import interviewplanning.models.User;
import interviewplanning.security.JwtUser;
import interviewplanning.services.BookingService;
import interviewplanning.services.CoordinatorService;
import interviewplanning.services.CoordinatorService.DayInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Coordinator controller.
 */
@RestController

public class CoordinatorController {

  private final CoordinatorService coordinatorService;
  private final BookingService bookingService;
  private final ModelMapper modelMapper;

  /**
   * Constructor.
   *
   * @param coordinatorService coordinator service
   * @param bookingService     booking service
   * @param modelMapper        model mapper
   */
  @Autowired
  public CoordinatorController(CoordinatorService coordinatorService,
                               BookingService bookingService, ModelMapper modelMapper) {
    this.coordinatorService = coordinatorService;
    this.bookingService = bookingService;
    this.modelMapper = modelMapper;
  }

  @DeleteMapping(path = "/bookings/{booking_id}")
  public void deleteBooking(@PathVariable("booking_id") UUID bookingId) {
    bookingService.deleteBooking(bookingId);
  }

  /**
   * Create booking endpoint.
   *
   * @param bookingDto Booking
   */
  @PostMapping(path = "/bookings")
  @ResponseStatus(code = HttpStatus.CREATED)
  public Booking createBooking(@Valid @RequestBody BookingRequestDto bookingDto) {
    Booking booking = mapToBooking(bookingDto);
    return bookingService.createBooking(booking.getInterviewerTimeSlotId(),
        booking.getCandidateTimeSlotId(),
        booking.getFrom(),
        booking.getTo(),
        booking.getSubject(),
        booking.getDescription());
  }

  @PostMapping(path = "/bookings/{booking_id}")
  public Booking updateBooking(@PathVariable("booking_id") UUID id,
                               @Valid @RequestBody BookingRequestDto bookingDto) {
    Booking booking = mapToBooking(bookingDto);
    return bookingService.updateBooking(id, booking);
  }

  @GetMapping(path = "/users/coordinators")
  public List<User> getCoordinators() {
    return coordinatorService.getCoordinators();
  }

  @GetMapping(path = "/users/interviewers")
  public List<User> getInterviewers() {
    return coordinatorService.getInterviewers();
  }

  /**
   * Revoke coordinator's role by his id.
   *
   * @param coordinatorId  coordinator's id
   * @param authentication authentication object to get email from
   */
  @DeleteMapping(path = "/users/coordinators/{coordinator_id}")
  public void revokeCoordinatorRole(@PathVariable("coordinator_id") UUID coordinatorId,
                                    Authentication authentication) {
    JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
    coordinatorService.revokeCoordinatorRole(coordinatorId, jwtUser.getEmail());
  }

  @DeleteMapping(path = "/users/interviewers/{interviewer_id}")
  public void revokeInterviewerRole(@PathVariable("interviewer_id") UUID interviewerId) {
    coordinatorService.revokeInterviewerRole(interviewerId);
  }

  @PostMapping(path = "/users/coordinators")
  @ResponseStatus(code = HttpStatus.CREATED)
  public UserDto grantCoordinatorRole(@Valid @RequestBody UserRequestDto coordinatorDto) {
    User coordinator = mapToUser(coordinatorDto);
    return mapToUserResponseDto(coordinatorService.grantCoordinatorRole(coordinator));
  }

  @PostMapping(path = "/users/interviewers")
  @ResponseStatus(code = HttpStatus.CREATED)
  public UserDto grantInterviewerRole(@Valid @RequestBody UserRequestDto interviewerDto) {
    User interviewer = mapToUser(interviewerDto);
    return mapToUserResponseDto(coordinatorService.grantInterviewerRole(interviewer));
  }

  /**
   * Update interviewer time slot by interviewer id, slot id.
   *
   * @param interviewerId interviewer id
   * @param slotId        slot id
   * @param timeSlotDto   time slot dto
   * @return interviewer time slot response dto
   */
  @PostMapping("/interviewers/{interviewer_id}/slots/{slot_id}")
  public InterviewerTimeSlotResponseDto updateInterviewerTimeSlot(
      @PathVariable("interviewer_id") UUID interviewerId,
      @PathVariable("slot_id") UUID slotId,
      @Valid @RequestBody InterviewerTimeSlotRequestDto timeSlotDto) {
    InterviewerTimeSlot timeSlot = mapToInterviewerTimeSlot(timeSlotDto);
    return mapToInterviewerTimeSlotResponseDto(
        coordinatorService.updateInterviewerTimeSlot(timeSlot, interviewerId, slotId));
  }

  @GetMapping(path = "/weeks/{week_num}/dashboard")
  public Map<String, DayInfo[]> getDashboard(@PathVariable("week_num") String weekNum) {
    return coordinatorService.getAllSlotsAndBookingsGroupedByDay(weekNum);
  }

  public InterviewerTimeSlot mapToInterviewerTimeSlot(InterviewerTimeSlotRequestDto dto) {
    return modelMapper.map(dto, InterviewerTimeSlot.class);
  }

  public User mapToUser(UserRequestDto userRequestDto) {
    return modelMapper.map(userRequestDto, User.class);
  }

  /**
   * Mapping to interviewer time slot response dto.
   *
   * @param timeSlot interviewer time slot
   * @return dto of interviewer time slot
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

  /**
   * Mapping to user response dto.
   *
   * @param user user
   * @return user dto
   */
  public UserDto mapToUserResponseDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .role(user.getRole())
        .build();
  }

  // Using model Mapper we get exception:
  //The destination property com.intellias.intellistart.interviewplanning.models.Booking.setId()
  // matches multiple source property hierarchies:

  //com.intellias.intellistart.interviewplanning.dto
  //.BookingRequestDto.getInterviewerTimeSlotId()
  //com.intellias.intellistart.interviewplanning
  //.dto.BookingRequestDto.getCandidateTimeSlotId()

  //  public Booking mapToBooking(BookingRequestDto bookingDto) {
  //    return modelMapper.map(bookingDto, Booking.class);
  //  }

  /**
   * mapping booking dto to booking model.
   *
   * @param bookingDto booking dto
   *
   * @return booking from bookingDTO
   */

  public Booking mapToBooking(BookingRequestDto bookingDto) {
    return Booking.builder()
        .candidateTimeSlotId(bookingDto.getCandidateTimeSlotId())
        .interviewerTimeSlotId(bookingDto.getInterviewerTimeSlotId())
        .from(bookingDto.getFrom())
        .to(bookingDto.getTo())
        .description(bookingDto.getDescription())
        .subject(bookingDto.getSubject())
        .build();
  }

}
