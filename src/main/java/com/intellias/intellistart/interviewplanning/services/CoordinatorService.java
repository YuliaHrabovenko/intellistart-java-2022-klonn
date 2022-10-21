package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidResourceException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
   * Create booking.
   *
   * @param interviewerSlotId   interviewer time slot id
   * @param candidateTimeSlotId candidate time slot id
   * @param from                start time
   * @param to                  end time
   * @param subject             subject of booking
   * @param description         decriptions of booking
   * @return Booking object if success
   */
  public Booking createBooking(UUID interviewerSlotId,
                               UUID candidateTimeSlotId,
                               LocalTime from,
                               LocalTime to,
                               String subject,
                               String description) {
    boolean existInterviewerTimeSlot = interviewerTimeSlotRepository.existsById(interviewerSlotId);
    if (!existInterviewerTimeSlot) {
      throw new IllegalStateException(
          "Interviewer Time slot with id " + interviewerSlotId + " does not exists");
    }

    boolean existCandidateTimeSlot = candidateTimeSlotRepository.existsById(candidateTimeSlotId);
    if (!existCandidateTimeSlot) {
      throw new IllegalStateException(
          "Candidate Time slot with id " + candidateTimeSlotId + " does not exists");
    }

    if (subject.length() > 255) {
      throw new IllegalStateException("Subject is incorrect");
    }

    if (description.length() > 4000) {
      throw new IllegalStateException("Description is incorrect");
    }

    Booking booking = new Booking(from,
        to, // not sure if it will be working
        interviewerSlotId,
        candidateTimeSlotId,
        subject,
        description);

    return bookingRepository.save(booking);
  }

  /**
   * Update booking.
   *
   * @param booking   Booking object
   * @param bookingId booking id
   * @return Booking object if success
   */
  public Booking updateBooking(Booking booking, UUID bookingId) { // need to be fixed

    Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(
        () -> new IllegalStateException(
            "booking with id" + bookingId + " does not exists"));

    existingBooking.setId(booking.getId());
    existingBooking.setCandidateTimeSlotId(booking.getCandidateTimeSlotId());
    existingBooking.setInterviewerTimeSlotId(booking.getInterviewerTimeSlotId());
    existingBooking.setFrom(booking.getFrom());
    existingBooking.setTo(booking.getTo());
    existingBooking.setDescription(booking.getDescription());
    existingBooking.setSubject(booking.getSubject());
    return bookingRepository.save(existingBooking);
  }

  /**
   * Delete booking.
   *
   * @param bookingId booking id
   */
  public void deleteBooking(UUID bookingId) {
    boolean exists = bookingRepository.existsById(bookingId);
    if (!exists) {
      throw new IllegalStateException(
          "Booking with id " + bookingId + " does not exists");
    }
    bookingRepository.deleteById(bookingId);
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
      throw new InvalidResourceException(
          "Coordinator with email " + coordinator.getEmail() + " already exists");
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
      throw new InvalidResourceException(
          "Interviewer with email " + interviewer.getEmail() + " already exists");
    }
    interviewer.setRole(UserRole.INTERVIEWER);
    return coordinatorRepository.save(interviewer);
  }

  /**
   * Revoke coordinator role.
   *
   * @param coordinatorId id of the user
   * @return user object if success
   */
  public User revokeCoordinatorRole(UUID coordinatorId) {
    User coordinator = coordinatorRepository.findById(coordinatorId)
        .orElseThrow(() -> new IllegalStateException(
            "coordinator with id " + coordinatorId + " does not exists"));
    if (coordinator.getRole() == UserRole.COORDINATOR) {
      coordinator.setRole(null);
      return coordinatorRepository.save(coordinator);
      // don't really sure what role to set
      // and also don`t know how to prevent coordinator to revoke himself
    }
    return coordinator; // do not sure if it`s ok
  }

  /**
   * Revoke interviewer role.
   *
   * @param interviewerId id of the user
   * @return user object if success
   */
  public User revokeInterviewerRole(UUID interviewerId) {
    User interviewer = coordinatorRepository.findById(interviewerId)
        .orElseThrow(() -> new IllegalStateException(
            "coordinator with id " + interviewerId + " does not exists"));
    if (interviewer.getRole() == UserRole.INTERVIEWER) {
      interviewer.setRole(UserRole.COORDINATOR);
      return coordinatorRepository.save(interviewer);
    }
    return interviewer; // do not sure if it`s ok
  }

  /**
   * Get coordinators.
   *
   * @return all the users with role COORDINATOR
   */
  public List<User> getCoordinators() {
    return coordinatorRepository.findByRole(UserRole.COORDINATOR);
  }

  /**
   * Get interviewers.
   *
   * @return all the users with role INTERVIEWER
   */
  public List<User> getInterviewers() {
    return coordinatorRepository.findByRole(UserRole.INTERVIEWER);
  }
}
