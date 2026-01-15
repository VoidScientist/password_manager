package Entities;

import jakarta.persistence.*;

import java.util.*;

/**
 * La classe Category est l'entité JPA contenant les infos liées aux catégories.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"NAME", "OWNER"})
})
public class Category {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String desc;

    @ManyToOne
    @JoinColumn(name="OWNER")
    private UserProfile owner;

    @OneToMany(mappedBy = "category")
    private final Set<Profile> profiles = new HashSet<>();

    public Category(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public Category() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public UserProfile getOwner() {
        return owner;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setOwner(UserProfile owner) {
        this.owner = owner;
    }

    public void addProfile(Profile profile) throws IllegalArgumentException {

        boolean success = this.profiles.add(profile);
        if (success) {
            profile.setCategory(this);
        } else {
            throw new IllegalArgumentException("Failed to add profile");
        }

    }

    public void removeProfile(Profile profile) throws IllegalArgumentException {

         boolean success = this.profiles.remove(profile);
         if (success) {
             profile.setCategory(null);
         } else {
             throw new IllegalArgumentException("Failed to remove profile");
         }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(getName(), category.getName()) && Objects.equals(owner, category.owner);
    }

    @Override
    public String toString() {

        String ownerRep = owner != null ? owner.getUuid() : null;
        List<String> profileReps = new ArrayList<>();

        for (Profile profile : profiles) {
            profileReps.add("\"" + profile.getService() + "\"");
        }

        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", owner=" + ownerRep +
                ", profiles=[" + String.join(", ", profileReps) +
                "]}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), owner);
    }

}
