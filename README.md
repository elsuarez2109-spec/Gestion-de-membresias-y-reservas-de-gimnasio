# Gestión de membresías y reservas de gimnasio

Un sistema para administrar miembros, membresías y reservas de clases en un gimnasio. Lo hicimos en Java, sin frameworks ni servidor web: se descarga, se ejecuta y funciona en tu computador.

Tiene dos formas de usarlo, las dos sobre el mismo núcleo: una interfaz gráfica y una versión por consola.


## ¿Qué puede hacer?

- **Usuarios con roles.** Registro e inicio de sesión para administradores, instructores y clientes. Cada rol solo puede hacer lo que le corresponde.
- **Planes de membresía.** Básico, Pro, Grupal (familiar) y Estudiante, cada uno con su precio y sus permisos.
- **Beneficios adicionales.** Sauna, Spa, Entrenador personal e Invitado. Se suman al plan y el costo total se calcula solo.
- **Clases grupales con cupo limitado.** El administrador las programa y los clientes con membresía válida reservan su lugar.
- **Lista de espera.** Si la clase está llena, el cliente entra a la cola. Cuando alguien cancela, el cupo pasa automáticamente al primero y se le avisa.
- **Infracciones y bloqueo.** Cancelar tarde o faltar sin avisar genera una infracción. Al acumular el límite, el cliente queda bloqueado para reservar.

## ¿Qué se queda fuera?

Rutinas de entrenamiento, inventario, pasarelas de pago reales y cualquier recurso físico. Los pagos son simulados, para no depender de servicios externos.

---

## Cómo ejecutarlo

### Lo que necesitas
- **Java 17 o superior** (`java -version` te dice cuál tienes).
- **Maven**, solo si quieres compilarlo tú. Si descargas el `.jar` ya hecho, no lo necesitas.

### Opción A: descargar el ejecutable
1. Entra a la pestaña **Actions** de este repositorio y abre la última ejecución.
2. Baja a **Artifacts** y descarga `gimnasio-jar`.
3. Descomprime el zip y ejecuta:

```bash
java -jar gimnasio-1.0-SNAPSHOT.jar
```

### Opción B: compilarlo
```bash
git clone https://github.com/elsuarez2109-spec/Gestion-de-membresias-y-reservas-de-gimnasio.git
cd Gestion-de-membresias-y-reservas-de-gimnasio
mvn clean package
java -jar target/gimnasio-1.0-SNAPSHOT.jar
```

### Versión por consola
```bash
java -cp target/gimnasio-1.0-SNAPSHOT.jar com.gimnasio.ui.consola.ConsolaApp
```

### Primer ingreso
Al arrancar se crea una cuenta de administrador para que puedas empezar:

| Correo | Contraseña |
|---|---|
| `admin@gym.com` | `admin123` |

Desde ahí puedes crear instructores, programar clases y asignar planes. Los clientes pueden registrarse solos desde la pantalla de inicio.

> Los datos viven en memoria: al cerrar el programa se pierden. Es a propósito, para mantenerlo simple y sin dependencias.

---

## Cómo está armado

Es un monolito modular: un solo proyecto, con los dominios separados por paquetes.

```
com.gimnasio
├── usuarios         Usuarios, roles, contraseñas y sesiones
├── facturacion      Planes, beneficios, políticas de cobro y membresías
├── reservas         Clases, reservas, lista de espera e infracciones
├── notificaciones   Avisos por consola y por interfaz gráfica
├── fachada          GimnasioFacade: la única puerta de entrada para la UI
└── ui
    ├── consola      Versión por consola
    └── gui          Versión con ventanas (Swing)
```

La regla clave: **las interfaces solo hablan con la fachada**. Así la consola y la GUI comparten exactamente la misma lógica.

### Patrones de diseño que usamos

| Patrón | Dónde | Para qué |
|---|---|---|
| **Decorator** | Beneficios sobre los planes | Sumar costo, descripción y permisos sin crear una clase por cada combinación |
| **Factory** | Usuarios y planes | Crear objetos según el tipo, sin `switch` que haya que tocar cada vez |
| **Observer** | Cupos de las clases | Avisar a la lista de espera cuando se libera un lugar |
| **Strategy** | Políticas de cobro | Cambiar cómo se penaliza sin tocar el resto del código |
| **Facade** | `GimnasioFacade` | Dar una entrada simple y controlada por roles |

Agregar un plan nuevo, un beneficio o una política es escribir una clase nueva y registrarla, sin modificar las existentes.

### Seguridad
- Las contraseñas se guardan con **PBKDF2** y una sal aleatoria por usuario. Nunca en texto plano.
- Cada acción pasa por un **token de sesión** que expira.
- Los permisos se validan en la fachada según el rol.

---

## Documentación

Todo el diseño está en la **https://github.com/elsuarez2109-spec/Gestion-de-membresias-y-reservas-de-gimnasio/wiki** del repositorio: alcance, propuesta, requisitos funcionales y no funcionales, y descripción de los patrones.

## Uso de inteligencia artificial

Usamos IA como apoyo durante el desarrollo.


- Interfaces y fachada — trabajo conjunto

[Curso · Universidad · Docente · Semestre]
