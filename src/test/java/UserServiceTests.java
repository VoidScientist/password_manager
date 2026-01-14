import Services.SessionManager;
import Services.UserService;
import Utilities.Config;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserServiceTests {

    private static final String USERNAME_LOGIN_TEST = "BIG_NEWB";
    private static final char[] PASSWORD_LOGIN_TEST = "thisisapassword".toCharArray();

    private static EntityManagerFactory emf;
    private static UserService userService;


    @BeforeAll
    public static void init() throws Exception {

        emf = Persistence.createEntityManagerFactory(Config.getTestPersistenceUnit());
        userService = new UserService(emf);

        userService.register(USERNAME_LOGIN_TEST, PASSWORD_LOGIN_TEST.clone());
        SessionManager.disconnect();

    }

    @Test
    public void registerTest() throws Exception {

        userService.register("TestUser987", "password".toCharArray());
        SessionManager.disconnect();

    }

    @Test
    public void duplicateErrorHandlingRegisterTest() throws Exception {

        boolean flag = false;

        try {
            userService.register("Dupli_kate", "password".toCharArray());
            SessionManager.disconnect();
            userService.register("Dupli_kate", "password".toCharArray());
            SessionManager.disconnect();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            flag = true;

        }

        assert flag;

    }

    @Test
    public void loginCorrectPasswordTest() throws Exception {

        userService.login(USERNAME_LOGIN_TEST, PASSWORD_LOGIN_TEST.clone());
        SessionManager.disconnect();

    }

    @Test
    public void loginWrongPasswordTest() throws Exception {

        boolean flag = false;

        try {
            userService.login(USERNAME_LOGIN_TEST, "ahahahnotrightpasswordamhacker".toCharArray());
            SessionManager.disconnect();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            flag = true;
        }

        SessionManager.disconnect();
        assert flag;

    }

    @Test
    public void loginWrongUsernameTest() throws Exception {

        boolean flag = false;

        try {
            userService.login("hiidontexist", PASSWORD_LOGIN_TEST.clone());
            SessionManager.disconnect();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            flag = true;
        }

        SessionManager.disconnect();
        assert flag;

    }

    @Test
    public void registerInvalidUsernameTest() throws Exception {

        boolean flag = false;

        try {
            userService.register("SELECT * FROM userprofile;", PASSWORD_LOGIN_TEST.clone());
            SessionManager.disconnect();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            flag = true;
        }

        SessionManager.disconnect();
        assert flag;

    }

}
