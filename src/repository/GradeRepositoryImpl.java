package repository;

import db.DatabaseConnection;
import model.Grade;
import model.GradeStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradeRepositoryImpl implements GradeRepository {

    @Override
    public void save(Grade grade) {
        String sql = "INSERT INTO grades (student_id, subject, grade_value, grade_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, grade.getStudentId());
            stmt.setString(2, grade.getSubject());
            stmt.setInt(3, grade.getValue());
            stmt.setDate(4, Date.valueOf(grade.getDate()));
            stmt.setString(5, grade.getStatus().name());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении оценки: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Grade> findById(int id) {
        String sql = "SELECT * FROM grades WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска оценки по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Grade> findAll() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка оценок", e);
        }
        return grades;
    }

    @Override
    public void update(Grade grade) {
        String sql = "UPDATE grades SET subject = ?, grade_value = ?, grade_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, grade.getSubject());
            stmt.setInt(2, grade.getValue());
            stmt.setDate(3, Date.valueOf(grade.getDate()));
            stmt.setString(4, grade.getStatus().name());
            stmt.setInt(5, grade.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления оценки", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления оценки", e);
        }
    }

    @Override
    public List<Grade> findByStudentId(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска оценок ученика", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findWithFilters(String subject, GradeStatus status) {
        List<Grade> grades = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM grades WHERE 1=1");
        if (subject != null && !subject.isEmpty()) sql.append(" AND subject = ?");
        if (status != null) sql.append(" AND status = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (subject != null && !subject.isEmpty()) stmt.setString(paramIndex++, subject);
            if (status != null) stmt.setString(paramIndex++, status.name());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка фильтрации", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findAllSortedByDate() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades ORDER BY grade_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сортировки", e);
        }
        return grades;
    }

    @Override
    public List<Grade> searchBySubject(String query) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE subject ILIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска", e);
        }
        return grades;
    }

    private Grade mapRowToGrade(ResultSet rs) throws SQLException {
        return new Grade(
                rs.getInt("id"),
                rs.getInt("student_id"),
                rs.getString("subject"),
                rs.getInt("grade_value"),
                rs.getDate("grade_date").toLocalDate(),
                GradeStatus.valueOf(rs.getString("status"))
        );
    }
}