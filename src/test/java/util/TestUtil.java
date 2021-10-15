package util;

import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.dto.UserDTO;
import com.shmigel.promotionproject.repository.AdminRepository;
import com.shmigel.promotionproject.repository.InstructorRepository;
import com.shmigel.promotionproject.repository.StudentRepository;
import com.shmigel.promotionproject.repository.UserRepository;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public static final String DEFAULT_PASSWORD = "pass";

    public String getAuthToken(User user) {
        return securityService.login(new UserCredentialDTO(user.getUsername(), DEFAULT_PASSWORD)).getToken();
    }

    public Student createTestStudent() {
        Student student = studentRepository.save(new Student("student", encode(DEFAULT_PASSWORD)));
        userRepository.updateUserRole(student.getId(), Roles.ROLE_STUDENT.name());
        return student;
    }

    public Instructor createTestInstructor() {
        Instructor instructor = instructorRepository.save(new Instructor("instructor", encode(DEFAULT_PASSWORD)));
        userRepository.updateUserRole(instructor.getId(), Roles.ROLE_INSTRUCTOR.name());
        return instructor;
    }

    public Admin createTestAdmin() {
        Admin admin = adminRepository.save(new Admin("admin", encode(DEFAULT_PASSWORD)));
        userRepository.updateUserRole(admin.getId(), Roles.ROLE_ADMIN.name());
        return admin;
    }

    private String encode(String input) {
        return passwordEncoder.encode(input);
    }

}
