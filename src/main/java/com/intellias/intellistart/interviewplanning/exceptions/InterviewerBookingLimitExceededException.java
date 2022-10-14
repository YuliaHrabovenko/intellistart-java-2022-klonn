package com.intellias.intellistart.interviewplanning.exceptions;

public class InterviewerBookingLimitExceededException  extends RuntimeException{
    public InterviewerBookingLimitExceededException(){
      super("Interviewer booking limit was exceeded");
    }
}
