package model;

import java.time.LocalDate;

public class Grade {
    private int id;
    private int studentId;
    private String subject;
    private int value;
    private LocalDate date;
    private GradeStatus status;

    public Grade(int id, int studentId, String subject, int value, LocalDate date, GradeStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.subject = subject;
        this.value = value;
        this.date = date;
        this.status = status;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getSubject() { return subject; }
    public int getValue() { return value; }
    public LocalDate getDate() { return date; }
    public GradeStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("ID: %d | Ученик ID: %d | %s: %d | Дата: %s | Статус: %s",
                id, studentId, subject, value, date, status);
    }
}