package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Grade;
import model.GradeStatus;
import model.Student;
import service.StudentService;

public class GradeDialogController {

    @FXML private ComboBox<Student> studentCombo;
    @FXML private TextField subjectField;
    @FXML private Spinner<Integer> valueSpinner;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<GradeStatus> statusCombo;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private Grade grade;
    private boolean isSaved = false;
    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        statusCombo.getItems().setAll(GradeStatus.values());
        
        // Загружаем учеников в ComboBox
        studentCombo.getItems().setAll(studentService.getAllStudents());
        
        // Настраиваем отображение ФИО в выпадающем списке (чтобы не выводилось "model.Student@1a2b")
        studentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLastName() + " " + item.getFirstName());
                }
            }
        });
        // И в самом выбранном поле тоже показываем ФИО
        studentCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLastName() + " " + item.getFirstName());
                }
            }
        });
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
        if (grade != null && grade.getId() != 0) {
            subjectField.setText(grade.getSubject());
            valueSpinner.getValueFactory().setValue(grade.getValue());
            datePicker.setValue(grade.getDate());
            statusCombo.setValue(grade.getStatus());
            
            // Выбираем ученика по ID
            for (Student s : studentCombo.getItems()) {
                if (s.getId() == grade.getStudentId()) {
                    studentCombo.setValue(s);
                    break;
                }
            }
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (subjectField.getText() == null || subjectField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Введите название предмета!");
            return;
        }
        if (studentCombo.getValue() == null) {
            showAlert("Ошибка", "Выберите ученика!");
            return;
        }
        if (datePicker.getValue() == null) {
            showAlert("Ошибка", "Выберите дату!");
            return;
        }

        grade.setStudentId(studentCombo.getValue().getId()); // Берем ID из выбранного ученика
        grade.setSubject(subjectField.getText());
        grade.setValue(valueSpinner.getValue());
        grade.setDate(datePicker.getValue());
        grade.setStatus(statusCombo.getValue());

        isSaved = true;
        closeWindow();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        isSaved = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}