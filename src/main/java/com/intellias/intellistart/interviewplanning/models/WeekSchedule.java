package com.intellias.intellistart.interviewplanning.models;

import java.util.ArrayList;
import java.util.List;




public class WeekSchedule {
    private  class DaySchedule {
        private List<TimeSlot> availableTimeSlots = new ArrayList<>();
    }


    private List<DaySchedule> weekSchedule = new ArrayList<>(5);
}
