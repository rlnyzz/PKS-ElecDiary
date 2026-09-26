package service;

import exception.ValidationException;
import model.Grade;
import model.GradeStatus;
import repository.GradeRepository;
import repository.GradeRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GradeService {
    private final GradeRepository repository = new GradeRepositoryImpl();

    public void addGrade(int studentId, String subject, int value, LocalDate date) {
        if (value < 1 || value > 5) {
            throw new ValidationException("Оценка должна быть от 1 до 5.");
        }
        if (studentId <= 0) {
            throw new ValidationException("Неверный ID ученика.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата оценки не может быть в будущем.");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new ValidationException("Название предмета не может быть пустым.");
        }

        Grade grade = new Grade(0, studentId, subject, value, date, GradeStatus.ACTIVE);
        repository.save(grade);
    }

    public void updateGradeStatus(int gradeId, GradeStatus newStatus) {
        Grade grade = repository.findById(gradeId)
                .orElseThrow(() -> new ValidationException("Оценка с таким ID не найдена."));

        if (grade.getStatus() == GradeStatus.DELETED && newStatus == GradeStatus.ACTIVE) {
            throw new ValidationException("Нельзя восстановить удаленную оценку.");
        }

        Grade updated = new Grade(grade.getId(), grade.getStudentId(), grade.getSubject(),
                grade.getValue(), grade.getDate(), newStatus);
        repository.update(updated);
    }

    public List<Grade> getAllGrades() {
        return repository.findAll();
    }

    public List<Grade> getStudentGrades(int studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<Grade> getSortedGrades() {
        return repository.findAllSortedByDate();
    }

    public List<Grade> filterGrades(String subject, GradeStatus status) {
        return repository.findWithFilters(subject, status);
    }

    public List<Grade> searchGrades(String query) {
        return repository.searchBySubject(query);
    }

    public Map<String, ? extends Number> getStatistics() {
        List<Grade> all = repository.findAll();
        long total = all.size();
        long active = all.stream().filter(g -> g.getStatus() == GradeStatus.ACTIVE).count();
        long corrected = all.stream().filter(g -> g.getStatus() == GradeStatus.CORRECTED).count();
        long deleted = all.stream().filter(g -> g.getStatus() == GradeStatus.DELETED).count();

        double average = all.stream()
                .filter(g -> g.getStatus() == GradeStatus.ACTIVE)
                .mapToInt(Grade::getValue)
                .average().orElse(0.0);

        return Map.of(
                "Всего оценок", total,
                "Активных", active,
                "Исправленных", corrected,
                "Удаленных", deleted,
                "Средний балл (активные)", Math.round(average * 100.0) / 100.0
        );
    }

    public void exportToCsv(String filePath) {
        List<Grade> grades = repository.findAll();
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
            writer.println("ID,StudentID,Subject,Value,Date,Status");
            for (Grade g : grades) {
                writer.printf("%d,%d,%s,%d,%s,%s%n",
                        g.getId(), g.getStudentId(), g.getSubject(),
                        g.getValue(), g.getDate(), g.getStatus());
            }
            System.out.println("Данные успешно экспортированы в " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка экспорта: " + e.getMessage());
        }
    }
}