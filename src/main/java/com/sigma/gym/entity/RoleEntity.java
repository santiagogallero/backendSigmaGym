package com.sigma.gym.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ej: "OWNER", "TRAINER", "MEMBER"

    // ⚠️ Evitamos usar 'id' que podría ser null al momento de crear el Set
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleEntity)) return false;
        RoleEntity that = (RoleEntity) o;
        return name.equals(that.name); // Comparamos por nombre
    }
        @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    private int priority; // 1 = más poder (OWNER), 2 = TRAINER, 3 = MEMBER
    // opcional: permisos si querés escalar
    // @ElementCollection
    // private List<String> permissions;

}
