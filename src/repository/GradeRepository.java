package repository;

import model.Grade;
import model.GradeStatus;

import java.util.List;
import java.util.Optional;

public interface GradeRepository {
    void save(Grade grade);
    Optional<Grade> findById(int id);
    List<Grade> findAll();
    void update(Grade grade);
    void deleteById(int id);
    List<Grade> findByStudentId(int studentId);
    List<Grade> findWithFilters(String subject, GradeStatus status);
    List<Grade> findAllSortedByDate();
    List<Grade> searchBySubject(String query);
}