import Entities.Category;
import Entities.Profile;
import Managers.ServiceManager;
import Services.DataService;
import Utilities.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.Objects;

public class DataServiceTests {

    private static String USERNAME_TEST = "TESTDATASERVICE";
    private static String PASSWORD_TEST = "TESTPASSWORD";

    @BeforeAll
    public static void setup() throws Exception {
        ServiceManager.init(Config.getTestPersistenceUnit());

        ServiceManager.getUserService().register(USERNAME_TEST, PASSWORD_TEST.toCharArray());

        ServiceManager.getUserService().login(USERNAME_TEST, PASSWORD_TEST.toCharArray());

    }

    @Test
    public void createCategoryTest() {

        ServiceManager.getDataService().createCategory("TEST_CAT_MEOW", "HELLO WORLD I LOVE TESTING");

    }

    @Test
    public void createDuplicateCategoryTest() {

        boolean flag = false;

        DataService data = ServiceManager.getDataService();

        data.createCategory("DUPLICATE", "HELLO WORLD I LOVE TESTING");

        try {
            data.createCategory("DUPLICATE", "DIFFERENT DESCS BUT SHOULD CRASH");
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            flag = true;
        }

        assert flag;

    }

    @Test
    public void createAndRemoveCategoryTest() {

        DataService data = ServiceManager.getDataService();

        Category created = data.createCategory("AMTOBEKILLED", "NOOOOOOO");

        data.removeCategory(created);

    }

    @Test
    public void createThenUpdateCategoryTest() {

        DataService data = ServiceManager.getDataService();

        Category created = data.createCategory("AMTOBEMODIFIED", "NOOOOOOO");

        data.updateCategory(created, "IAMPLEASEDTOHAVEBEENUPDATED", "IM LYING");

        assert Objects.equals(created.getName(), "IAMPLEASEDTOHAVEBEENUPDATED");
        assert Objects.equals(created.getDesc(), "IM LYING");

    }

    @Test
    public void createProfile() {

        DataService data = ServiceManager.getDataService();

        Profile profil = data.createProfile("AMAZON", "test", "eloooo@gmail.com", "genericsarelolz", "amazon.com");

        assert profil != null;

    }

    @Test
    public void createProfileWithCategory() {

        DataService data = ServiceManager.getDataService();

        Profile profil = data.createProfile("AMAZON", "test", "eloooo@gmail.com", "genericsarelolz", "amazon.com");

        Category cat = data.createCategory("Shopping", "i love to shop");

        data.attachProfileToCategory(profil, cat);

        data.saveProfile(profil);

    }

}
