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
        return studentRepository.save(new Student("student", encode(DEFAULT_PASSWORD)));
    }

    public Instructor createTestInstructor() {
        return instructorRepository.save(new Instructor("instructor", encode(DEFAULT_PASSWORD)));
    }

    public Admin createTestAdmin() {
        return adminRepository.save(new Admin("admin", encode(DEFAULT_PASSWORD)));
    }

    private String encode(String input) {
        return passwordEncoder.encode(input);
    }

}
