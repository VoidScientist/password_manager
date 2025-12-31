package Entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Category {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="NAME")
    private String name;

    @Column(name="DESC")
    private String desc;

    @ManyToOne
    @JoinColumn(name="OWNER")
    private UserProfile owner;

    @OneToMany(mappedBy = "category")
    private List<Profile> profiles;

    public Category(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public Category() {
        this.name = "";
        this.desc = "";
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return getId() == category.getId() && Objects.equals(getName(), category.getName()) && Objects.equals(getDesc(), category.getDesc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDesc());
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

}
