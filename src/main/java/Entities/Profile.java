package Entities;

import Utilities.Security.Encryption.PasswordEncrypter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>La classe Profile est l'entité JPA contenant les informations liées aux comptes enregistrés.</p>
 *
 * <p>C'est la propriétaire des relations avec les autres entités dans la base de donnée.</p>
 *
 * <p>Elle contient:</p>
 * <ul>
 *     <li>id</li>
 *     <li>creationDate</li>
 *     <li>service</li>
 *     <li>username</li>
 *     <li>email</li>
 *     <li>encrypted_password</li>
 *     <li>password</li>
 *     <li>url</li>
 *     <li>owner</li>
 *     <li>category</li>
 * </ul>
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 */
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="CREATED", nullable=false, updatable=false)
    private LocalDateTime creationDate;

    @Column(name="SERVICE")
    private String service;

    @Column(name="USERNAME")
    private String username;

    @Column(name="EMAIL")
    private String email;

    @Column(name="PASSWORD")
    private String encrypted_password;

    @Transient
    private String password;

    @Column(name="URL")
    private String url;

    @ManyToOne
    @JoinColumn(name="OWNER")
    private UserProfile owner;

    @ManyToOne
    @JoinColumn(name="CATEGORY")
    private Category category;

    public Profile() {}

    public Profile(String service, String username, String password, String url) {
        this.service = service;
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @PrePersist
    private void prePersist() {
        this.creationDate = LocalDateTime.now();

        if (this.owner != null) {
            password = PasswordEncrypter.encrypt(this.getPassword(), this.owner.getPasswordHash());
        }

    }

    @PreUpdate
    private void encryptPassword() {
        if (this.owner != null) {
            this.encrypted_password = PasswordEncrypter.encrypt(this.getPassword(), this.owner.getPasswordHash());
        }
    }

    public int getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getUsername() {
        return username;
    }

    public String getEncrypted_password() {
        return encrypted_password;
    }

    public String getUrl() {
        return url;
    }

    public UserProfile getOwner() {
        return owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setOwner(UserProfile owner) {
        this.owner = owner;
    }

    public String getPassword() {
        if (password != null) return this.password;

        if (this.owner != null) {
            this.password = PasswordEncrypter.decrypt(encrypted_password, this.owner.getPasswordHash());
        }

        return this.password;
    }

    public void setPassword(String password) {
       this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(getService(), profile.getService()) && Objects.equals(getUsername(), profile.getUsername()) && Objects.equals(getEncrypted_password(), profile.getEncrypted_password()) && Objects.equals(getUrl(), profile.getUrl()) && Objects.equals(getOwner(), profile.getOwner()) && Objects.equals(getCategory(), profile.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getService(), getUsername(), getEncrypted_password(), getUrl(), getOwner(), getCategory());
    }

    @Override
    public String toString() {

        String ownerRep = owner != null ? owner.getUuid() : null;
        String categoryRep = category != null ? category.getName() : null;

        return "Profile{" +
                "id=" + id +
                ", service='" + service + '\'' +
                ", username='" + username + '\'' +
                ", encrypted_password='" + encrypted_password + '\'' +
                ", password ='" + getPassword() + '\'' +
                ", url='" + url + '\'' +
                ", owner=" + ownerRep +
                ", category=" + categoryRep +
                '}';
    }

}
