package com.lms.domain.projection;
import java.util.List;

public interface CourseProjection {
    Long getId();
    String getName();
    String getDescription();
    List<String> getInstructorsFullNames();
}
