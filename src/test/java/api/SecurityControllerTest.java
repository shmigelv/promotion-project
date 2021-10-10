package api;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.repository.UserRepository;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import util.ApiTestConfiguration;

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

        assertTrue(userService.existsByUsername(userCredential.getUsername()));
    }

    @Test
    void userCantRegister_whenUsernameAlreadyExists() throws Exception {
        //GIVEN credentials of existing user
        User user = userService.saveUser("user1", "pass2");

        //WHEN user tries to register with that credentials
        ResultActions resultActions = mockMvc.perform(post("/security/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(new UserCredentialDTO(user.getUsername(), user.getPassword()))));

        //THEN new user is not register
        Collection<User> users = userRepository.findAllByUsername(user.getUsername());

        assertEquals(1, users.size());
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void userCanLogin() throws Exception {
        //GIVEN existing user
        User existingUser = userService.saveUser("user1", "pass2");

        //WHEN user tries to login with that credentials
        ResultActions resultActions = mockMvc.perform(post("/security/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(new UserCredentialDTO(existingUser.getUsername(), existingUser.getPassword()))));

        //THEN user receives token information
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());
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
        User user = new User("user1", "pass2", Roles.ROLE_STUDENT);
        userService.saveUser(user);

        JwtDTO jwt = securityService.login(new UserCredentialDTO(user.getUsername(), user.getPassword()));

        //WHEN user tries to get his information
        ResultActions resultActions = mockMvc.perform(post("/security/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt.getToken()));

        //THEN user receives his account information
        resultActions.andExpect(status().isOk());
    }

}
