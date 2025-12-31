package Entities;

import Utilities.Security.PasswordHasher;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

// une classe permettant de stocker les informations de connexion d'un utilisateur
// afin qu'il puisse se connecter à son coffre fort.

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

    // prend en paramètre un mot de passe, et retourne un booléen en fonction de s'il est le bon mdp.
    public boolean connect(String password) {

        // sépare le passwordHash en son salt et hash
        String[] tmp = passwordHash.split("\\$");
        String salt = tmp[0];
        String hash = tmp[1];

        String connectionHash = "";

        // essaie d'obtenir un hash à partir du mot de passe en argument
        // si une exception survient, on dis que la connexion a échouée.
        //TODO: considérer un code d'erreur / des logs pour expliciter l'exception en question
        try {

            connectionHash = PasswordHasher.hashPasswordFromSalt(salt, password.toCharArray());

        } catch (Exception e) {

            return false;

        }

        // retourne la correspondance des mots de passe
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
