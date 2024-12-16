package com.lms.domain.projection;
import java.util.List;
import java.util.Set;

public interface CourseProjection {
    Long getId();
    String getName();
    String getDescription();
    Set<String> getInstructorsFullNames();
}
