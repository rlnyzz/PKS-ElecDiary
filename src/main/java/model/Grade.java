package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Grade {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final StringProperty subject = new SimpleStringProperty();
    private final IntegerProperty value = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<GradeStatus> status = new SimpleObjectProperty<>();

    // Конструктор пустой (нужен для TableView)
    public Grade() {}

    // Конструктор полный (для загрузки из БД)
    public Grade(int id, int studentId, String subject, int value, LocalDate date, GradeStatus status) {
        this.id.set(id);
        this.studentId.set(studentId);
        this.subject.set(subject);
        this.value.set(value);
        this.date.set(date);
        this.status.set(status);
    }

    // Обязательные методы Property() для JavaFX
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty studentIdProperty() { return studentId; }
    public StringProperty subjectProperty() { return subject; }
    public IntegerProperty valueProperty() { return value; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public ObjectProperty<GradeStatus> statusProperty() { return status; }

    // Геттеры и сеттеры (для DAO)
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public int getStudentId() { return studentId.get(); }
    public void setStudentId(int studentId) { this.studentId.set(studentId); }

    public String getSubject() { return subject.get(); }
    public void setSubject(String subject) { this.subject.set(subject); }

    public int getValue() { return value.get(); }
    public void setValue(int value) { this.value.set(value); }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate date) { this.date.set(date); }

    public GradeStatus getStatus() { return status.get(); }
    public void setStatus(GradeStatus status) { this.status.set(status); }
}