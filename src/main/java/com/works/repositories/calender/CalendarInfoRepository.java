package com.works.repositories.calender;


import com.works.entities.calender.CalendarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarInfoRepository extends JpaRepository<CalendarInfo, Integer> {
}
