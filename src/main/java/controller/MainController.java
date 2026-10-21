package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Grade;
import model.GradeStatus;
import service.GradeService;

public class MainController {

    @FXML private TextField searchField;
    @FXML private ComboBox<GradeStatus> statusFilter;
    @FXML private Button resetFiltersBtn;

    @FXML private TableView<Grade> gradesTable;
    @FXML private TableColumn<Grade, Integer> idColumn;
    @FXML private TableColumn<Grade, Integer> studentColumn;
    @FXML private TableColumn<Grade, String> subjectColumn;
    @FXML private TableColumn<Grade, Integer> valueColumn;
    @FXML private TableColumn<Grade, LocalDate> dateColumn;
    @FXML private TableColumn<Grade, GradeStatus> statusColumn;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button statsBtn;
    @FXML private Button exportBtn;

    private final GradeService service = new GradeService();
    private ObservableList<Grade> masterData = FXCollections.observableArrayList();
    private FilteredList<Grade> filteredData;

    @FXML
    public void initialize() {
        statsBtn.setOnAction(e -> showStatistics());
        exportBtn.setOnAction(e -> exportToCsv());
        // 1. Настраиваем колонки
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Загружаем данные из БД
        loadData();

        // 3. Настраиваем фильтры
        statusFilter.getItems().setAll(GradeStatus.values());
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        
        resetFiltersBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.getSelectionModel().clearSelection();
            applyFilters();
        });

        // 4. Обработчики кнопок
        addBtn.setOnAction(e -> openDialog(null));
                deleteBtn.setOnAction(e -> {
            Grade selected = gradesTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Ошибка", "Выберите запись для удаления!");
                return;
            }
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText("Удаление оценки");
            alert.setContentText("Вы уверены, что хотите удалить запись ID: " + selected.getId() + "?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        service.deleteGrade(selected.getId()); 
                        loadData(); // Обновляем таблицу
                    } catch (Exception ex) {
                        showError("Ошибка удаления", ex.getMessage());
                    }
                }
            });
        });
        editBtn.setOnAction(e -> {
            Grade selected = gradesTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Ошибка", "Выберите запись для редактирования!");
                return;
            }
            openDialog(selected);
        });
        
                deleteBtn.setOnAction(e -> {
            Grade selected = gradesTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Ошибка", "Выберите запись для удаления!");
                return;
            }
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText("Удаление оценки");
            alert.setContentText("Вы уверены, что хотите удалить запись ID: " + selected.getId() + "?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Вызываем сервис для удаления
                        service.deleteGrade(selected.getId()); 
                        loadData(); // Обновляем таблицу, чтобы удаленная запись исчезла
                    } catch (Exception ex) {
                        showError("Ошибка удаления", ex.getMessage());
                    }
                }
            });
        });
    }
    
    private void loadData() {
        try {
            masterData.setAll(service.getAllGrades());
            filteredData = new FilteredList<>(masterData, p -> true);
            gradesTable.setItems(filteredData);
        } catch (Exception e) {
            showError("Ошибка загрузки данных", e.getMessage());
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText();
        GradeStatus selectedStatus = statusFilter.getValue();

        filteredData.setPredicate(grade -> {
            if (searchText != null && !searchText.isEmpty()) {
                if (!grade.getSubject().toLowerCase().contains(searchText.toLowerCase())) {
                    return false;
                }
            }
            if (selectedStatus != null && grade.getStatus() != selectedStatus) {
                return false;
            }
            return true;
        });
    }

            private void openDialog(Grade grade) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/grade-dialog.fxml"));
            Parent root = loader.load();

            GradeDialogController dialogController = loader.getController();
            
            // Если grade == null, значит мы создаем новую
            // Если grade != null, значит редактируем существующую
            Grade gradeToEdit = (grade != null) ? grade : new Grade();
            dialogController.setGrade(gradeToEdit);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(grade == null ? "Новая оценка" : "Редактирование");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(gradesTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (dialogController.isSaved()) {
                try {
                    if (gradeToEdit.getId() == 0) {
                        // Новая запись — вызываем addGrade
                        service.addGrade(
                                gradeToEdit.getStudentId(),
                                gradeToEdit.getSubject(),
                                gradeToEdit.getValue(),
                                gradeToEdit.getDate()
                        );
                    } else {
                        // Редактирование — нужно добавить метод updateGrade в сервис!
                        service.updateGrade(gradeToEdit);
                    }
                    
                    // Перезагружаем данные из БД в таблицу
                    loadData();
                    
                } catch (Exception ex) {
                    showError("Ошибка сохранения", ex.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
        private void showStatistics() {
        try {
            Map<String, ? extends Number> stats = service.getStatistics();
            
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, ? extends Number> entry : stats.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Статистика");
            alert.setHeaderText("Показатели системы");
            alert.setContentText(sb.toString());
            alert.showAndWait();
            
        } catch (Exception ex) {
            showError("Ошибка", "Не удалось загрузить статистику: " + ex.getMessage());
        }
    }
        private void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные в CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("grades_export.csv");
        
        File file = fileChooser.showSaveDialog(gradesTable.getScene().getWindow());
        
        if (file != null) {
            try {
                service.exportToCsv(file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно экспортированы в:\n" + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                showError("Ошибка экспорта", ex.getMessage());
            }
        }
    }
}