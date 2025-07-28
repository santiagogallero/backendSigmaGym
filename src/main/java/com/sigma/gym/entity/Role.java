package com.sigma.gym.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ej: "OWNER", "TRAINER", "MEMBER"


    private int priority; // 1 = más poder (OWNER), 2 = TRAINER, 3 = MEMBER
    // opcional: permisos si querés escalar
    // @ElementCollection
    // private List<String> permissions;

}
