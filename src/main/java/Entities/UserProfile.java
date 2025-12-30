package Entities;

import java.util.Objects;

public class UserProfile {

    private String uuid;
    private String username;
    private String passwordHash;

    public UserProfile(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public boolean connect(String password) {

        return true;

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
