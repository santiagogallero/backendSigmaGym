package com.sigma.gym.entity;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;

    @Column(nullable = false)
    private Integer priority; // 1 = más poder (OWNER/ADMIN), 2 = TRAINER/STAFF, 3 = MEMBER

    public enum RoleName {
        OWNER(1),
        ADMIN(1),
        TRAINER(2),
        STAFF(2), 
        MEMBER(3);

        private final int defaultPriority;

        RoleName(int defaultPriority) {
            this.defaultPriority = defaultPriority;
        }

        public int getDefaultPriority() {
            return defaultPriority;
        }
    }

    // ⚠️ Evitamos usar 'id' que podría ser null al momento de crear el Set
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleEntity)) return false;
        RoleEntity that = (RoleEntity) o;
        return name != null && name.equals(that.name); // Comparamos por nombre
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
