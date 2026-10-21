package dao;

import model.Student;
import java.util.List;

public interface StudentDao {
    List<Student> findAll();
}