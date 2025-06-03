package pe.getsemani.mikhipu.assistance.specification;

import org.springframework.data.jpa.domain.Specification;
import pe.getsemani.mikhipu.assistance.dto.AssistanceReportFilterDTO;
import pe.getsemani.mikhipu.assistance.entity.AssistanceRecord;

public class AssistanceRecordStatisticsSpecification {

    public static Specification<AssistanceRecord> buildFromFilter(AssistanceReportFilterDTO filter) {
        return Specification
                .where(AssistanceRecordSpecification.hasStudentId(filter.getStudentId()))
                .and(AssistanceRecordSpecification.hasEntryStatus(filter.getEntryStatus()))
                .and(AssistanceRecordSpecification.hasExitStatus(filter.getExitStatus()))
                .and(AssistanceRecordSpecification.dateBetween(filter.getStartDate(), filter.getEndDate()))
                .and(AssistanceRecordSpecification.hasGrade(filter.getGrade()))
                .and(AssistanceRecordSpecification.hasSection(filter.getSection()))
                .and(AssistanceRecordSpecification.hasSchoolLevel(filter.getSchoolLevel()))
                .and(AssistanceRecordSpecification.hasCourseId(filter.getCourseId()));
    }
}