package Entities;

import jakarta.persistence.*;

import java.util.*;

/**
 * La classe UserProfile est l'entité JPA concernant le mot de passe maître d'un utilisateur
 * pour accéder à ses mots de passe.
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

    @OneToMany(mappedBy="owner")
    private final Set<Profile> profiles = new HashSet<>();

    @OneToMany(mappedBy="owner")
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

    public boolean addCategory(Category category) {

        boolean success = this.categories.add(category);
        if (success) {
            category.setOwner(this);
            category.getProfiles().forEach(this::addProfile);
        }
        return success;

    }

    public boolean removeCategory(Category category) {

        boolean success = this.categories.remove(category);
        if (success) {
            category.setOwner(null);
            category.getProfiles().forEach(this::removeProfile);
        }
        return success;

    }

    public boolean addProfile(Profile profile) {

        boolean success = this.profiles.add(profile);
        if (success) {
            profile.setOwner(this);
        }
        return success;

    }

    public boolean removeProfile(Profile profile) {

        boolean success = this.profiles.remove(profile);
        if (success) {
            profile.setOwner(null);
        }
        return success;

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
