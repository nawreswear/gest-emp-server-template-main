package tn.iset.m2glnt.server.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Data
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Size(max = 20)
    private String type;

    @NotBlank
    @Size(max = 20)
    private String nom;

    @NotBlank
    @Size(max = 20)
    private String prenom;

    @NotNull(message = "Tel cannot be null")
    private Integer tel;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(unique = true)
    private String email;


    @NotBlank
    @Size(max = 120)
    private String password;

    @Min(value = 1000, message = "Postal code must be at least 1000")
    @Max(value = 9999, message = "Postal code must be at most 9999")
    private Integer codePostal;

    @Size(max = 20)
    private String pays;

    @Size(max = 20)
    private String ville;

    @Digits(integer = 8, fraction = 0, message = "CIN must contain exactly 8 digits")
    @Min(value = 10000000, message = "CIN must contain exactly 8 digits")
    @Max(value = 99999999, message = "CIN must contain exactly 8 digits")
    private String cin;


    private double longitude;
    private double latitude;

    @Column(length = 1000)
    private String photo;

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void getPhoto(String photo) {
    }
    public User() {
    }
    public User(Long id) {
        this.id=id;
    }
}
