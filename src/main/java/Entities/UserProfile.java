package Entities;

import jakarta.persistence.*;

import java.util.*;

/**
 * La classe UserProfile est l'entité JPA concernant le mot de passe maître d'un utilisateur
 * pour accéder à ses mots de passe.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 */
@Entity
public class UserProfile {

    @Id
    @Column(name="UUID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(name="USERNAME", unique = true)
    private String username;

    @Column(name="PASSWORD_HASH")
    private String passwordHash;

    @OneToMany(mappedBy="owner", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private final Set<Profile> profiles = new HashSet<>();

    @OneToMany(mappedBy="owner",  cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private final Set<Category> categories = new HashSet<>();

    public UserProfile(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public UserProfile() {}

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) throws IllegalArgumentException {

        boolean success = this.categories.add(category);
        if (success) {
            category.setOwner(this);
            for (Profile profile : category.getProfiles()) {
                this.addProfile(profile);
            }
        } else {
            throw new IllegalArgumentException("Failed to insert category");
        }

    }

    public void removeCategory(Category category) throws IllegalArgumentException {

        boolean success = this.categories.remove(category);
        if (!success) {
            throw new IllegalArgumentException("No such category in user categories");
        }

    }

    public void addProfile(Profile profile) throws IllegalArgumentException {

        boolean success = this.profiles.add(profile);
        if (success) {
            profile.setOwner(this);
        } else  {
            throw new IllegalArgumentException("Failed to add profile");
        }

    }

    public void removeProfile(Profile profile) throws IllegalArgumentException {

        boolean success = this.profiles.remove(profile);
        if (!success) {
            throw new IllegalArgumentException("No such profile in user profiles");
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(getUuid(), that.getUuid()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPasswordHash(), that.getPasswordHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getUsername(), getPasswordHash());
    }

    @Override
    public String toString() {

        List<String> profileReps = new ArrayList<>();
        List<String> categoriesReps = new ArrayList<>();

        for (Profile profile : profiles) {
            profileReps.add(profile.getService());
        }

        for (Category category : categories) {
            categoriesReps.add(category.getName());
        }

        return "UserProfile{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", profiles=[" + String.join(", ", profileReps) +
                "], categories=[" + String.join(",", categoriesReps) +
                "]}";

    }

}
