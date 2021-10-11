package api;

import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.repository.UserRepository;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import util.ApiTestConfiguration;
import util.TestUtil;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.JacksonUtil.serialize;

@ApiTestConfiguration
public class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void userCanRegister() throws Exception {
        //GIVEN valid new user credentials
        UserCredentialDTO userCredential = new UserCredentialDTO("user1", "pass1");

        // WHEN user tries to register
        ResultActions resultActions = mockMvc.perform(post("/security/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(userCredential)));

        // THEN new user will be created
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("user1"));

        User createdUser = userService.getUserByUsername(userCredential.getUsername());
        assertEquals("user1", createdUser.getUsername());
        assertTrue(passwordEncoder.matches("pass1", createdUser.getPassword()));
    }

    @Test
    void userCantRegister_whenUsernameAlreadyExists() throws Exception {
        //GIVEN credentials of existing user
        Student user = testUtil.createTestStudent();

        //WHEN user tries to register with that credentials
        ResultActions resultActions = mockMvc.perform(post("/security/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(new UserCredentialDTO(user.getUsername(), "pass"))));

        //THEN new user is not register
        Collection<User> users = userRepository.findAllByUsername(user.getUsername());

        assertEquals(1, users.size());
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void userCanLogin() throws Exception {
        //GIVEN existing user
        Student existingUser = testUtil.createTestStudent();

        //WHEN user tries to login with that credentials
        ResultActions resultActions = mockMvc.perform(post("/security/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(new UserCredentialDTO(existingUser.getUsername(), "pass"))));

        //THEN user receives token information
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());
    }

    @Test
    void userCantLogin_whenPasswordIsIncorrect() throws Exception {
        //GIVEN existing user
        Student existingUser = testUtil.createTestStudent();

        //WHEN user tries to login with that credentials
        ResultActions resultActions = mockMvc.perform(post("/security/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(new UserCredentialDTO(existingUser.getUsername(), "!"))));

        //THEN user receives token information
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Can't find user for given credentials"));
    }

    @Test
    void userCantLogin_whenUserDoesntExist() throws Exception {
        //GIVEN non-existing credentials
        UserCredentialDTO nonExistingCredentials = new UserCredentialDTO("user1", "pass2");

        //WHEN user logins with non-existing credentials
        ResultActions resultActions = mockMvc.perform(post("/security/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(nonExistingCredentials)));

        //THEN user receives error response
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    void loggedUserCanSeeHisInformation_whenValidTokenProvided() throws Exception {
        //GIVEN jwt token for existing user
        Student user = testUtil.createTestStudent();
        JwtDTO jwt = securityService.login(new UserCredentialDTO(user.getUsername(), "pass"));

        //WHEN user tries to get his information
        ResultActions resultActions = mockMvc.perform(post("/security/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt.getToken()));

        //THEN user receives his account information
        resultActions.andExpect(status().isOk());
    }

}
