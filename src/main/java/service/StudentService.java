package service;

import dao.StudentDao;
import dao.StudentDaoImpl;
import model.Student;

import java.util.List;

public class StudentService {
    private final StudentDao dao = new StudentDaoImpl();

    public List<Student> getAllStudents() {
        return dao.findAll();
    }
}