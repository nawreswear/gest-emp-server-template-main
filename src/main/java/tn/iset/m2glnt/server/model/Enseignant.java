package tn.iset.m2glnt.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Enseignant extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Enseignant(Long id, @Size(max = 20) String type, @NotBlank @Size(max = 20) String nom,
                      @NotBlank @Size(max = 20) String prenom, @NotNull(message = "Tel cannot be null") Integer tel,
                      @NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(max = 120) String password,
                      @Min(value = 1000, message = "Postal code must be at least 1000")
                      @Max(value = 9999, message = "Postal code must be at most 9999") Integer codePostal,
                      @Size(max = 20) String pays, @Size(max = 20) String ville,
                      @Digits(integer = 8, fraction = 0, message = "CIN must contain exactly 8 digits")
                      @Min(value = 10000000, message = "CIN must contain exactly 8 digits")
                      @Max(value = 99999999, message = "CIN must contain exactly 8 digits") String cin,
                      double longitude, double latitude, String photo) {
        super(id, type, nom, prenom, tel, email, password, codePostal, pays, ville, cin, longitude, latitude, photo);
    }

    public Enseignant(Long id){
        super(id);
    }

    @OneToMany(mappedBy = "enseignant")
    private List<CalendarSlot> cours;

}
