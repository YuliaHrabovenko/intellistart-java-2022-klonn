package interviewplanning.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interviewplanning.exceptions.AuthException;
import interviewplanning.exceptions.NotFoundException;
import interviewplanning.exceptions.ValidationException;
import interviewplanning.models.Booking;
import interviewplanning.models.CandidateTimeSlot;
import interviewplanning.models.InterviewerTimeSlot;
import interviewplanning.models.User;
import interviewplanning.models.UserRole;
import interviewplanning.repositories.BookingRepository;
import interviewplanning.repositories.CandidateTimeSlotRepository;
import interviewplanning.repositories.InterviewerTimeSlotRepository;
import interviewplanning.repositories.UserRepository;
import interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Coordinator business logic.
 */
@Service
public class CoordinatorService {
  private final UserRepository coordinatorRepository;
  private final BookingRepository bookingRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final InterviewerService interviewerService;

  /**
   * Constructor.
   *
   * @param coordinatorRepository         coordinator repository
   * @param bookingRepository             booking repository
   * @param candidateTimeSlotRepository   candidate time slot repository
   * @param interviewerTimeSlotRepository interviewer time slot repository
   * @param interviewerService            interviewer service
   */
  @Autowired
  public CoordinatorService(UserRepository coordinatorRepository,
                            BookingRepository bookingRepository,
                            CandidateTimeSlotRepository candidateTimeSlotRepository,
                            InterviewerTimeSlotRepository interviewerTimeSlotRepository,
                            InterviewerService interviewerService) {
    this.coordinatorRepository = coordinatorRepository;
    this.bookingRepository = bookingRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.interviewerService = interviewerService;
  }

  /**
   * Update interviewer's slot for the current or next week.
   *
   * @param interviewerTimeSlot interviewer time slot object
   * @param interviewerId       interviewer id
   * @param slotId              slot id
   * @return interviewer time slot object if success
   */
  public InterviewerTimeSlot updateInterviewerTimeSlot(InterviewerTimeSlot interviewerTimeSlot,
                                                       UUID interviewerId,
                                                       UUID slotId) {
    WeekUtil.validateIsCurrentOrNextWeekNumber(interviewerTimeSlot.getWeekNum());
    return interviewerService.updateSlot(interviewerTimeSlot, interviewerId, slotId);
  }

  /**
   * Grant the user coordinator role.
   *
   * @param coordinator user object
   * @return user object if success
   */
  public User grantCoordinatorRole(User coordinator) {
    Optional<User> user = coordinatorRepository.findByEmail(coordinator.getEmail());
    if (user.isPresent()) {
      throw new ValidationException(ValidationException.USER_EMAIL_EXISTS);
    }
    coordinator.setRole(UserRole.COORDINATOR);
    return coordinatorRepository.save(coordinator);
  }

  /**
   * Grant the user interviewer role.
   *
   * @param interviewer user object
   * @return user object if success
   */
  public User grantInterviewerRole(User interviewer) {
    Optional<User> user = coordinatorRepository.findByEmail(interviewer.getEmail());
    if (user.isPresent()) {
      throw new ValidationException(ValidationException.USER_EMAIL_EXISTS);
    }
    interviewer.setRole(UserRole.INTERVIEWER);
    return coordinatorRepository.save(interviewer);
  }

  /**
   * Revoke coordinator role.
   *
   * @param coordinatorId id of the user
   */
  public void revokeCoordinatorRole(UUID coordinatorId, String email) {
    User coordinator = coordinatorRepository.findById(coordinatorId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.COORDINATOR_NOT_FOUND));
    if (coordinator.getRole() != UserRole.COORDINATOR) {
      throw new AuthException(AuthException.ACCESS_UNAUTHORIZED);
    }
    if (coordinator.getEmail().equals(email)) {
      throw new ValidationException(ValidationException.COORDINATOR_CAN_NOT_BE_REVOKED);
    }
    coordinatorRepository.deleteById(coordinatorId);
  }

  /**
   * Revoke interviewer role.
   *
   * @param interviewerId id of the user
   */
  public void revokeInterviewerRole(UUID interviewerId) {
    User coordinator = coordinatorRepository.findById(interviewerId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.INTERVIEWER_NOT_FOUND));
    if (coordinator.getRole() != UserRole.INTERVIEWER) {
      throw new AuthException(AuthException.ACCESS_UNAUTHORIZED);
    }
    coordinatorRepository.deleteById(interviewerId);
  }

  /**
   * Get granted coordinators.
   *
   * @return all the users with role COORDINATOR
   */
  public List<User> getCoordinators() {
    return coordinatorRepository.findByRole(UserRole.COORDINATOR);
  }

  /**
   * Get granted interviewers.
   *
   * @return all the users with role INTERVIEWER
   */
  public List<User> getInterviewers() {
    return coordinatorRepository.findByRole(UserRole.INTERVIEWER);
  }

  public Map<DayOfWeek, List<InterviewerTimeSlot>> getInterviewerSlotsByDayOfWeek(String weekNum) {
    return interviewerTimeSlotRepository.findByWeekNum(weekNum).stream()
        .collect(Collectors.groupingBy(InterviewerTimeSlot::getDayOfWeek));
  }

  /**
   * Get candidate slots by date.
   *
   * @param firstDateOfWeek first date of week
   * @return map of slots by date
   */
  public Map<LocalDate, List<CandidateTimeSlot>> getCandidateSlotsByDate(
      LocalDate firstDateOfWeek) {
    return candidateTimeSlotRepository.findByDateBetween(firstDateOfWeek,
        firstDateOfWeek.plusDays(4L)).stream().collect(
        Collectors.groupingBy(CandidateTimeSlot::getDate));
  }

  /**
   * Get dates of week.
   *
   * @param firstDateOfWeek first date of a week
   * @return array of dates
   */
  public LocalDate[] getWeekDates(LocalDate firstDateOfWeek) {
    LocalDate[] dates = new LocalDate[7];
    dates[0] = firstDateOfWeek;
    long count = 1L;
    for (int i = 1; i < 7; i++) {
      dates[i] = firstDateOfWeek.plusDays(count);
      count++;
    }
    return dates;
  }

  /**
   * Get all both interviewers' and candidates' slots and bookings.
   *
   * @param weekNum week number
   * @return map of slots and bookings by days
   */
  public Map<String, DayInfo[]> getAllSlotsAndBookingsGroupedByDay(String weekNum) {
    int year = Integer.parseInt(weekNum.substring(0, 4));
    int weekNumInt = Integer.parseInt(weekNum.substring(4));
    LocalDate firstDateOfWeek = WeekUtil.getFirstDateOfWeekByYearWeekNum(year, weekNumInt);

    DayOfWeek[] dayOfWeeks = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

    LocalDate[] dates = getWeekDates(firstDateOfWeek);

    Map<DayOfWeek, List<InterviewerTimeSlot>> interviewerSlotsByDayOfWeek =
        getInterviewerSlotsByDayOfWeek(weekNum);

    Map<LocalDate, List<CandidateTimeSlot>> candidateSlotsByDate =
        getCandidateSlotsByDate(firstDateOfWeek);

    DayInfo[] daysInfo = {
        new DayInfo(), new DayInfo(), new DayInfo(), new DayInfo(),
        new DayInfo(), new DayInfo(), new DayInfo()
    };
    for (int i = 0; i < daysInfo.length; i++) {
      daysInfo[i].setDayOfWeek(dayOfWeeks[i]);
      daysInfo[i].setDate(dates[i]);
      List<InterviewerTimeSlot> interviewerTimeSlots =
          interviewerSlotsByDayOfWeek.get(dayOfWeeks[i]);
      if (interviewerTimeSlots != null) {
        List<Booking> bookings = interviewerTimeSlots.stream().map(
            InterviewerTimeSlot::getBookingList).flatMap(List::stream).collect(Collectors.toList());
        daysInfo[i].setInterviewerTimeSlots(interviewerTimeSlots);
        if (!bookings.isEmpty()) {
          daysInfo[i].setBookings(
              bookings.stream().collect(Collectors.toMap(Booking::getId, b -> b)));
        }
      }
      List<CandidateTimeSlot> candidateTimeSlots = candidateSlotsByDate.get(dates[i]);
      if (candidateTimeSlots != null) {
        daysInfo[i].setCandidateTimeSlots(candidateSlotsByDate.get(dates[i]));
      }
    }
    Map<String, DayInfo[]> resultMap = new LinkedHashMap<>();
    resultMap.put("days", daysInfo);
    return resultMap;
  }

  /**
   * Static inner class for dashboard data serialization.
   */
  @Setter
  @Getter
  @NoArgsConstructor
  public static class DayInfo {
    @JsonProperty("dayOfWeek")
    private DayOfWeek dayOfWeek;
    @JsonProperty("date")
    private LocalDate date;
    @JsonIgnoreProperties("dayOfWeek")
    @JsonProperty("interviewerTimeSlots")
    private List<InterviewerTimeSlot> interviewerTimeSlots = new ArrayList<>();
    @JsonIgnoreProperties("date")
    @JsonProperty("candidateTimeSlots")
    private List<CandidateTimeSlot> candidateTimeSlots = new ArrayList<>();
    @JsonProperty("bookings")
    private Map<UUID, Booking> bookings = new HashMap<>();
  }
}
