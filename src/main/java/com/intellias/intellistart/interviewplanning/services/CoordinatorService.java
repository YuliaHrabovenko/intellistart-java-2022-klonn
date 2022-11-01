package com.intellias.intellistart.interviewplanning.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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

  /**
   * Constructor.
   *
   * @param coordinatorRepository         coordinator repository
   * @param bookingRepository             booking repository
   * @param candidateTimeSlotRepository   candidate time slot repository
   * @param interviewerTimeSlotRepository interviewer time slot repository
   */
  @Autowired
  public CoordinatorService(UserRepository coordinatorRepository,
                            BookingRepository bookingRepository,
                            CandidateTimeSlotRepository candidateTimeSlotRepository,
                            InterviewerTimeSlotRepository interviewerTimeSlotRepository) {
    this.coordinatorRepository = coordinatorRepository;
    this.bookingRepository = bookingRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;

  }

  /**
   * Update interviewer time slot.
   *
   * @param interviewerTimeSlot interviewer time slot object
   * @param interviewerId       interviewer id
   * @return interviewer time slot object if success
   */
  public InterviewerTimeSlot updateInterviewerTimeSlot(InterviewerTimeSlot interviewerTimeSlot,
                                                       UUID interviewerId) {
    InterviewerTimeSlot existingInterviewerTimeSlot =
        interviewerTimeSlotRepository.findById(interviewerId).orElseThrow(
            () -> new IllegalStateException(
                "interviewer timeslot with id" + interviewerTimeSlot.getId() + " does not exists"));
    List<Booking> bookings = bookingRepository.findAll();
    for (Booking booking : bookings) {
      if (booking.getCandidateTimeSlotId().equals(interviewerId)) {
        throw new IllegalStateException("this slot is already booked");
      }
    }
    existingInterviewerTimeSlot.setId(interviewerTimeSlot.getId());
    existingInterviewerTimeSlot.setFrom(interviewerTimeSlot.getFrom());
    existingInterviewerTimeSlot.setTo(interviewerTimeSlot.getTo());
    existingInterviewerTimeSlot.setInterviewerId(interviewerTimeSlot.getInterviewerId());
    return interviewerTimeSlotRepository.save(existingInterviewerTimeSlot);
  }

  /**
   * Get candidates slots.
   *
   * @return all candidates slots
   */

  public List<CandidateTimeSlot> getCandidatesSlots() {
    return candidateTimeSlotRepository.findAll();
  }

  /**
   * Get interviewers slots.
   *
   * @return all interviewers slots
   */
  public List<InterviewerTimeSlot> getInterviewersSlots() {
    return interviewerTimeSlotRepository.findAll();
  }

  /**
   * Grant the user coordinator role.
   *
   * @param coordinator user object
   * @return user object if success
   */
  public User grantCoordinatorRole(User coordinator) {
    Optional<User> user = coordinatorRepository.findUserByEmail(coordinator.getEmail());
    if (user.isPresent()) {
      throw new ValidationException(ExceptionMessage.USER_EMAIL_EXISTS.getMessage());
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
    Optional<User> user = coordinatorRepository.findUserByEmail(interviewer.getEmail());
    if (user.isPresent()) {
      throw new ValidationException(ExceptionMessage.USER_EMAIL_EXISTS.getMessage());
    }
    interviewer.setRole(UserRole.INTERVIEWER);
    return coordinatorRepository.save(interviewer);
  }

  /**
   * Revoke coordinator role.
   *
   * @param coordinatorId id of the user
   */
  public void revokeCoordinatorRole(UUID coordinatorId) {
    User coordinator = coordinatorRepository.findById(coordinatorId)
        .orElseThrow(
            () -> new NotFoundException(ExceptionMessage.COORDINATOR_NOT_FOUND.getMessage()));
    if (coordinator.getRole() != UserRole.COORDINATOR) {
      throw new ValidationException(ExceptionMessage.NOT_COORDINATOR.getMessage());
      //don`t know how to prevent coordinator to revoke himself
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
            () -> new NotFoundException(ExceptionMessage.INTERVIEWER_NOT_FOUND.getMessage()));
    if (coordinator.getRole() != UserRole.INTERVIEWER) {
      throw new ValidationException(ExceptionMessage.NOT_INTERVIEWER.getMessage());
      //don`t know how to prevent coordinator to revoke himself
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
    return interviewerTimeSlotRepository.findInterviewerTimeSlotsByWeekNum(weekNum).stream()
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
    return candidateTimeSlotRepository.findCandidateTimeSlotsByDateBetween(firstDateOfWeek,
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
    LocalDate[] dates = new LocalDate[5];
    dates[0] = firstDateOfWeek;
    long count = 1L;
    for (int i = 1; i < 5; i++) {
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
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};

    LocalDate[] dates = getWeekDates(firstDateOfWeek);

    Map<DayOfWeek, List<InterviewerTimeSlot>> interviewerSlotsByDayOfWeek =
        getInterviewerSlotsByDayOfWeek(weekNum);

    Map<LocalDate, List<CandidateTimeSlot>> candidateSlotsByDate =
        getCandidateSlotsByDate(firstDateOfWeek);

    DayInfo[] daysInfo = {
        new DayInfo(), new DayInfo(), new DayInfo(), new DayInfo(), new DayInfo()
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
  @ToString
  public static class DayInfo {
    private DayOfWeek dayOfWeek;
    private LocalDate date;
    @JsonIgnoreProperties("dayOfWeek")
    private List<InterviewerTimeSlot> interviewerTimeSlots = new ArrayList<>();
    @JsonIgnoreProperties("date")
    private List<CandidateTimeSlot> candidateTimeSlots = new ArrayList<>();
    private Map<UUID, Booking> bookings = new LinkedHashMap<>();
  }
}
