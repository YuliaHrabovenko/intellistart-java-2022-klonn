package interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Not found exception class.
 */
@Getter
@Setter
public class NotFoundException extends AbstractCommonException {

  public static final Detail CANDIDATE_SLOT_NOT_FOUND =
      new Detail(404, "candidate_slot_not_found", "Candidate time slot with this ID doesn't exist");
  public static final Detail INTERVIEWER_SLOT_NOT_FOUND =
      new Detail(404, "interviewer_slot_not_found",
          "Interviewer time slot with this ID doesn't exist");
  public static final Detail INTERVIEWER_NOT_FOUND =
      new Detail(404, "interviewer_not_found", "Interviewer with this ID doesn't exist");
  public static final Detail COORDINATOR_NOT_FOUND =
      new Detail(404, "coordinator_not_found", "Coordinator with with ID doesn't exists");
  public static final Detail BOOKING_NOT_FOUND =
      new Detail(404, "booking_not_found", "Booking with this ID doesn't exist");
  public static final Detail USER_NOT_FOUND =
      new Detail(404, "user_not_found", "User with this email doesn't exist");

  public NotFoundException(Detail detail) {
    super(detail);
  }
}
