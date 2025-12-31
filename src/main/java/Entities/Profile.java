package Entities;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * La classe Profile est l'entité JPA contenant les informations liées aux comptes enregistrés.
 *
 * C'est la propriétaire des relations avec les autres entités dans la base de donnée.
 */
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="SERVICE")
    private String service;

    @Column(name="USERNAME")
    private String username;

    @Column(name="PASSWORD")
    private String encrypted_password;

    @Column(name="URL")
    private String url;

    @ManyToOne
    @JoinColumn(name="OWNER")
    private UserProfile owner;

    @ManyToOne
    @JoinColumn(name="CATEGORY")
    private Category category;

    public Profile() {}

    public Profile(String service, String username, String encrypted_password, String url) {
        this.service = service;
        this.username = username;
        this.encrypted_password = encrypted_password;
        this.url = url;
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

    public void setEncrypted_password(String encrypted_password) {
        this.encrypted_password = encrypted_password;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setOwner(UserProfile owner) {
        this.owner = owner;
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
                ", url='" + url + '\'' +
                ", owner=" + ownerRep +
                ", category=" + categoryRep +
                '}';
    }

}
