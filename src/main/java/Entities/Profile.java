package Entities;

import jakarta.persistence.*;

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

}
