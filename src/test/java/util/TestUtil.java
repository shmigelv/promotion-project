package util;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    public String getAuthToken(User user) {
        return securityService.login(new UserCredentialDTO(user.getUsername(), user.getPassword())).getToken();
    }

    public User createUser(UserCredentialDTO userCredential, Roles role) {
        return userService.saveUser(new User(userCredential.getUsername(), userCredential.getPassword(), role));
    }

    public User createUser(String username, String password, Roles role) {
        return userService.saveUser(new User(username, password, role));
    }

}
