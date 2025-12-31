package Entities;

import Utilities.Security.PasswordHasher;
import jakarta.persistence.*;

import java.util.Objects;

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

    public UserProfile(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public UserProfile() {
        this.username = "";
        this.passwordHash = "";
    }

    public boolean connect(String password) {

        String[] tmp = passwordHash.split("\\$");
        String salt = tmp[0];
        String hash = tmp[1];

        String connectionHash = "";

        try {

            connectionHash = PasswordHasher.hashPasswordFromSalt(salt, password.toCharArray());

        } catch (Exception e) {

            return false;

        }

        return connectionHash.equals(hash);

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
