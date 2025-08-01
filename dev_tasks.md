
# 🛠️ Tareas pendientes y mejoras - backendSigmaGym

## 🔧 1. Servicios (Service y ServiceImpl)
- Implementar los servicios para:
  - UserServiceImpl
  - WorkoutPlanServiceImpl
  - RoutineServiceImpl
  - ExerciseServiceImpl
  - MembershipTypeServiceImpl
  - ProgressServiceImpl
- Usar correctamente los Mapper y Repository dentro de los servicios.

## 🌐 2. Controladores (@RestController)
- Crear endpoints REST para:
  - Autenticación de usuarios (/auth)
  - Gestión de planes (/plans)
  - Rutinas (/routines)
  - Progreso (/progress)
  - Asignación de membresías
- Manejar respuestas con DTOs.
- Agregar control de errores con @ExceptionHandler o @ControllerAdvice.

## 🧱 3. Modelo de dominio (model)
- Completar los modelos vacíos o incompletos.
- Asegurar que no dependan de JPA.
- Incluir atributos relevantes como id y referencias por Long.

## 📤 4. DTOs
- Completar y estandarizar DTOs.
- Agregar validaciones: @NotNull, @Size, etc.
- Separar por propósito si es necesario: CreateDTO, UpdateDTO, ResponseDTO.

## 🔁 5. Mappers
- Unificar nombres de métodos:
  - toDto, toModel, toEntity, toDtoListFromDomain, etc.
- Asegurar que manejen null y soporten listas correctamente.
- Evitar confusiones entre DTO, Entity y Model.

## 🛡️ 6. Seguridad
- Renombrar controller de jugador a user si corresponde.
- Verificar rutas públicas/seguras en filtro JWT.
- Implementar control por roles (OWNER, TRAINER, MEMBER).

## ⚠️ 7. Entidades
- Usar @JsonIgnore en relaciones bidireccionales.
- Inicializar listas para evitar NullPointerException.
- Verificar claves foráneas y nombres claros en @JoinColumn.

## 🧪 8. Extras a futuro
- Agregar tests con @SpringBootTest o @DataJpaTest.
- Escribir README.md más detallado.
- Documentación automática con Swagger/OpenAPI.
- Endpoint de salud (/health o /status) si se despliega.

---

✅ Recomendación final:
Terminá primero los servicios + controladores de usuarios, planes y rutinas. Una vez listos, ya podés conectar el frontend o mostrarlo al cliente.
