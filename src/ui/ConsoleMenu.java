package ui;

import exception.ValidationException;
import model.Grade;
import model.GradeStatus;
import service.GradeService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final GradeService service = new GradeService();

    public void start() {
        while (true) {
            System.out.println("\n=== ЭЛЕКТРОННЫЙ ДНЕВНИК ===");
            System.out.println("1. Выставить оценку");
            System.out.println("2. Показать все оценки");
            System.out.println("3. Показать оценки ученика");
            System.out.println("4. Поиск по предмету");
            System.out.println("5. Фильтрация и сортировка");
            System.out.println("6. Изменить статус оценки");
            System.out.println("7. Статистика");
            System.out.println("8. Экспорт данных в CSV");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> addGrade();
                    case "2" -> printList(service.getAllGrades());
                    case "3" -> getStudentGrades();
                    case "4" -> searchGrades();
                    case "5" -> filterAndSort();
                    case "6" -> updateStatus();
                    case "7" -> printStatistics();
                    case "8" -> exportData();
                    case "0" -> {
                        System.out.println("Выход из программы...");
                        return;
                    }
                    default -> System.out.println("Неверный ввод. Попробуйте снова.");
                }
            } catch (ValidationException e) {
                System.out.println("Ошибка бизнес-логики: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Системная ошибка: " + e.getMessage());
            }
        }
    }

    private void addGrade() {
        System.out.print("Введите ID ученика (целое число): ");
        int studentId = readInt();
        System.out.print("Введите предмет: ");
        String subject = scanner.nextLine();
        System.out.print("Введите оценку (1-5): ");
        int value = readInt();
        System.out.print("Введите дату (ГГГГ-ММ-ДД): ");
        LocalDate date = readDate();

        service.addGrade(studentId, subject, value, date);
        System.out.println("Оценка успешно добавлена!");
    }

    private void getStudentGrades() {
        System.out.print("Введите ID ученика: ");
        int studentId = readInt();
        printList(service.getStudentGrades(studentId));
    }

    private void searchGrades() {
        System.out.print("Введите часть названия предмета: ");
        String query = scanner.nextLine();
        printList(service.searchGrades(query));
    }

    private void filterAndSort() {
        System.out.println("1. Фильтр по статусу");
        System.out.println("2. Сортировка по дате (сначала новые)");
        System.out.print("Выбор: ");
        String ch = scanner.nextLine();
        if (ch.equals("1")) {
            System.out.print("Введите статус (ACTIVE, CORRECTED, DELETED): ");
            try {
                GradeStatus status = GradeStatus.valueOf(scanner.nextLine().toUpperCase());
                printList(service.filterGrades(null, status));
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный статус.");
            }
        } else if (ch.equals("2")) {
            printList(service.getSortedGrades());
        } else {
            System.out.println("Неверный выбор.");
        }
    }

    private void updateStatus() {
        System.out.print("Введите ID оценки: ");
        int id = readInt();
        System.out.print("Введите новый статус (ACTIVE, CORRECTED, DELETED): ");
        try {
            GradeStatus status = GradeStatus.valueOf(scanner.nextLine().toUpperCase());
            service.updateGradeStatus(id, status);
            System.out.println("Статус обновлен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус.");
        }
    }

    private void printStatistics() {
        Map<String, ? extends Number> stats = service.getStatistics();
        System.out.println("\n--- СТАТИСТИКА ---");
        stats.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    private void exportData() {
        System.out.print("Введите имя файла (например, grades.csv): ");
        String filename = scanner.nextLine();
        service.exportToCsv(filename);
    }

    private void printList(List<Grade> list) {
        if (list.isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Ошибка: введите целое число. Попробуйте снова: ");
            }
        }
    }

    private LocalDate readDate() {
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.print("Ошибка: неверный формат даты. Введите ГГГГ-ММ-ДД: ");
            }
        }
    }
}