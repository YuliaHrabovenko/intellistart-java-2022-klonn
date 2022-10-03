package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// todo: think how can we switch weekId
public class TimeSlot {

    private static int TOTAL_ID = 1;
    private int currentId;
    private int weekId = 1;
    BookingStatus status;
    private LocalTime minValueForStart =  LocalTime.of(8,0);
    private LocalTime maxValueForEnd =  LocalTime.of(22,0);
    private LocalTime from;
    private LocalTime to;
    private DayOfWeek day;

    public TimeSlot(DayOfWeek day, LocalTime from, LocalTime to) {
        this.day = day;
        this.from = from;
        this.to = to;
        this.currentId = TOTAL_ID++;
    }
}
