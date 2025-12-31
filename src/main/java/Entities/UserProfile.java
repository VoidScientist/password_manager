package Entities;

import Utilities.Security.PasswordHasher;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

/** Une classe permettant de stocker les informations de connexion d'un utilisateur
* afin qu'il puisse se connecter à son coffre fort.
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
    private Set<Profile> profiles;

    @OneToMany(mappedBy="owner")
    private Set<Category> categories;

    public UserProfile(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public UserProfile() {
        this.username = "";
        this.passwordHash = "";
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
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
        return "UserProfile{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }

}
