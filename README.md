# ✉️ EcuMail (Correo)

**A desktop email and group-chat system written in Java (Swing), with Ecuadorian ID validation, local file persistence and PostgreSQL storage.**

EcuMail is a university project (Universidad Central del Ecuador — package `uce.edu.ec.correo`) that simulates an email company. Users register with a validated Ecuadorian *cédula*, send and receive emails (To / CC / BCC, attachments, drafts, forwarding, starred and trash), manage a private contact book and chat in shared group conversations with administrator roles and file sharing. The whole domain is built with object-oriented design (domain classes, GUI layer, utilities, persistence layer).

---

## Table of Contents

1. [Features at a Glance](#-features-at-a-glance)
2. [Project Requirements](#-project-requirements)
3. [Installation and Setup](#-installation-and-setup)
4. [Running the Application](#-running-the-application)
5. [User Guide](#-user-guide)
6. [Validation Rules](#-validation-rules)
7. [Domain Model](#-domain-model)
8. [Persistence: Local File and PostgreSQL](#-persistence-local-file-and-postgresql)
9. [Database Schema](#-database-schema)
10. [Project Structure](#-project-structure)
11. [Architecture and Design Notes](#-architecture-and-design-notes)
12. [Known Limitations and Security Notes](#-known-limitations-and-security-notes)
13. [Troubleshooting](#-troubleshooting)
14. [Author, Repository and License](#-author-repository-and-license)

---

## ✨ Features at a Glance

**Accounts**
- Registration with strict validation: Ecuadorian *cédula* (check digit algorithm + province code), allowed e-mail domains, 09-prefixed phone number, birth date, gender and password policy.
- The **province** is detected automatically from the cédula while typing.
- Login with show/hide password, personal information panel, **change password** and **delete my account** (both confirmed with the current password).

**Email**
- Compose with **To, CC and BCC (CCO)**, subject, body and **attachments** (images, videos, documents; **10 MB limit** per file).
- Save as **draft**, send later (“Enviar Borrador”), **forward** (“Reenviar”) to another address, **star** (“Destacados”), move to **trash** and delete **permanently**.
- Folders/filters: *All, Starred, Sent, Received, Drafts, Forwarded, Deleted*.
- Search by subject or sender; detail view with attachments you can **preview (images)** or **download**.
- BCC recipients are hidden from the rest; the sender sees them.

**Contacts**
- Private address book per user: add, edit, delete and search by e-mail or name; contact detail view; a contact picker is available when composing and creating groups; double-click a row to open its details.

**Groups (group chat)**
- Create groups with name, description and members chosen from your contacts.
- **Group chat** with text and attached files, shared-files browser, member list, and **system messages** (group created, member left, description changed).
- **Roles**: creator (permanent), administrators and members. Admins see the *Modificar* and *Eliminar Grupo* buttons and manage other administrators; non-admin members see *Salir del Grupo*.

**Interface and persistence**
- Animated **splash screen**, card-based navigation (login → register → main menu), collapsible sidebar menu, custom **vector icons drawn with Java2D** (no image files needed) and the EcuMail envelope app icon.
- Automatic saving to a local serialized file after every action, plus a full **PostgreSQL dump** when the application closes.
- Human-readable `usuarios_registrados.txt` report (passwords masked).

---

## 📋 Project Requirements

### Functional requirements

| ID | Requirement |
|----|-------------|
| FR-01 | Register users with cédula, first/last name, e-mail, phone, birth date, gender and password; reject duplicates (cédula and e-mail). |
| FR-02 | Validate the Ecuadorian cédula (10 digits, province code 01–24, third digit < 6, check digit) and show the province. |
| FR-03 | Restrict e-mail addresses to lowercase and to a list of allowed domains (Gmail, Outlook/Hotmail/Live/MSN, Yahoo, iCloud/Me/Mac, Proton, Zoho, GMX, AOL, UCE). |
| FR-04 | Authenticate users by e-mail and password; allow logout and account deletion (with password confirmation). |
| FR-05 | Let a logged-in user change their password following the password policy. |
| FR-06 | Compose e-mails with To, CC, BCC, subject, body and attachments up to 10 MB each. |
| FR-07 | Save e-mails as drafts and send them later; send directly from the composer. |
| FR-08 | Forward an e-mail to a new recipient, keeping the original subject, sender and date. |
| FR-09 | Mark e-mails as starred; list by state (all, sent, received, drafts, forwarded, deleted, starred). |
| FR-10 | Soft-delete e-mails (trash) and delete them permanently. |
| FR-11 | Search e-mails by subject or sender and view full details with attachments (preview images, download files). |
| FR-12 | Manage a personal contact book (add, modify, delete, search); prevent adding yourself or duplicates. |
| FR-13 | Create groups from contacts with name and description; reject duplicate group names. |
| FR-14 | Group chat with text and file attachments, shared-files viewer, members list and automatic system messages. |
| FR-15 | Group roles: permanent creator, promotable/demotable administrators, non-admin members can leave; admins can modify or delete the group. |
| FR-16 | Persist all data automatically to `datos_sistema.dat` after each change and reload it at startup. |
| FR-17 | On exit (with confirmation), also store everything in PostgreSQL and regenerate `usuarios_registrados.txt`. |

### Non-functional requirements

| ID | Requirement |
|----|-------------|
| NFR-01 | **Desktop GUI** built with Java Swing and the operating system’s native look and feel. |
| NFR-02 | **Object-oriented design** with a clear separation between domain, GUI, persistence and utilities. |
| NFR-03 | **Graceful failure**: if PostgreSQL is unreachable, the user is warned and data remains safe in the local file. |
| NFR-04 | **Transactional DB writes**: the PostgreSQL export runs in one transaction with rollback on error. |
| NFR-05 | **Input safety**: user-facing validation errors are raised as `ValidationException` with the offending field, and all SQL uses prepared statements. |
| NFR-06 | **Minimum window size** 900 × 600 (default 1000 × 700). |
| NFR-07 | **Language**: user interface and messages are in **Spanish**. |

### Software requirements

| Requirement | Details |
|-------------|---------|
| **JDK** | **Java 24** (`maven.compiler.source/target = 24` in `pom.xml`). |
| **Build tool** | **Apache Maven** 3.x (or an IDE with Maven support such as IntelliJ IDEA). |
| **Dependency** | `org.postgresql:postgresql:42.7.4` (JDBC driver), downloaded automatically by Maven. |
| **Database** *(optional)* | **PostgreSQL** server with a database named `correo`. Without it, the app still works using the local file. |
| **OS** | Any OS with a JDK and a graphical environment (Windows, Linux, macOS). |
| **Display** | Recommended ≥ 1000 × 700 px. |

---

## 🔧 Installation and Setup

```bash
# 1. Clone the repository
git clone https://github.com/ChristopherVP/Correo.git
cd Correo

# 2. Compile with Maven (downloads the PostgreSQL driver)
mvn clean compile
```

### Optional: PostgreSQL setup

1. Create the database:
   ```sql
   CREATE DATABASE correo;
   ```
2. Create the tables using the SQL script found in the comment block at the bottom of `src/main/java/uce/edu/ec/correo/DB/ConexionDB.java` (see [Database Schema](#-database-schema)).
3. Edit the connection constants in `ConexionDB.java`:
   ```java
   private static final String URL      = "jdbc:postgresql://localhost:5432/correo";
   private static final String USER     = "postgres";
   private static final String PASSWORD = "DB_Password";   // ← put your real password
   ```

> ⚠️ Do not commit real credentials. Consider reading them from environment variables.

---

## ▶️ Running the Application

**From an IDE (recommended):** open the project in IntelliJ IDEA, wait for Maven to import and run the class
`uce.edu.ec.correo.Ejecutable.EjecucionCorreo`.

**From the command line:**

```bash
mvn compile exec:java -Dexec.mainClass="uce.edu.ec.correo.Ejecutable.EjecucionCorreo"
```

The app shows a 3-second splash screen and then the login window. On first run `datos_sistema.dat` is created in the working directory.

---

## 🧭 User Guide

1. **Register** — On the login screen choose *“¿No tienes cuenta? Regístrate”*. Fill in all fields (the province appears automatically from the cédula).
2. **Log in** with your e-mail and password.
3. **Sidebar menu** (hamburger button) with three sections:
   - **Correos:** *Redactar*, *Todos los Correos*, *Destacados*, *Enviados*, *Recibidos*, *Borradores*, *Reenviados*, *Eliminados*.
   - **Contactos:** *Agregar Contacto*, *Ver Todos los Contactos*.
   - **Grupos:** *Crear Grupo*, *Chats Grupales*.
4. **Write an e-mail:** *Redactar* → fill *Para* (and optionally *CC* / *CCO*), subject, message, attach files → **Guardar Borrador** or **Enviar**.
5. **Read / manage e-mails:** double-click a row to open *Detalle del Correo* → *Reenviar*, *Enviar Borrador*, *Eliminar*, *Eliminar Definitivamente*, *Ver* / *Descargar* attachments. Use the star column to mark as *Destacado*.
6. **Contacts:** add by e-mail + name; double-click a contact for details, where you can *Modificar Nombre*, *Eliminar Contacto* or *Enviar Correo*.
7. **Groups:** create a group and pick members from your contacts; open it from *Chats Grupales* to chat, share files (*Ver Archivos*), see *Ver Miembros*, or — if you are an admin — *Modificar*, manage *Administradores* and *Eliminar Grupo*. Non-admin members can *Salir del Grupo*.
8. **My account:** click the user button (top right) → *Mi Información Personal* → *Cambiar Contraseña* or *Eliminar Mi Cuenta*.
9. **Exit:** closing the window asks for confirmation, then saves to the local file, PostgreSQL and `usuarios_registrados.txt`.

---

## ✅ Validation Rules

| Field | Rule |
|-------|------|
| **Cédula** | Exactly 10 digits, numeric only, province code 01–24, third digit < 6, valid check digit; must be unique. Province is derived from the first two digits. |
| **E-mail** | Lowercase only; format `^[a-z0-9+_.-]+@[a-z0-9.-]+\.[a-z]{2,}$`; domain must be one of `gmail.com, outlook.com, hotmail.com, live.com, msn.com, yahoo.com, yahoo.es, yahoo.mx, icloud.com, me.com, mac.com, proton.me, protonmail.com, zoho.com, zohomail.com, gmx.com, gmx.net, aol.com, uce.edu.ec`; must be unique. |
| **Name / last name** | Only letters (including accents and ñ) and spaces. |
| **Phone** | 10 digits starting with `09`. |
| **Birth date** | Format `dd/mm/aaaa`, real calendar date (leap years handled), not in the future, age between 1 and 120. |
| **Password** | 8–14 characters with at least one uppercase letter. |
| **Contacts** | Valid e-mail, cannot be yourself, no duplicates. |
| **Groups** | Unique name per user; the creator keeps a permanent administrator role (cannot be demoted). |
| **Attachments** | Maximum **10 MB** per file; supported groups: images (`jpg, jpeg, png, gif, bmp`), videos (`mp4, avi, mov, wmv, flv`), documents (`pdf, doc, docx, txt, xls, xlsx`), others. |
| **E-mail sending** | Subject and body required for the domain operation; at least one valid recipient in To/CC/BCC; only the owner can send their own message. |

---

## 🧩 Domain Model

| Class | Responsibility |
|-------|----------------|
| `EmpresaCorreo` | Facade/service of the whole system: holds all users and the logged-in user; implements registration, login, contacts, groups, e-mail operations and persistence. Serializable. |
| `Usuario` | Person with cédula, names, e-mail, password, phone, birth date, `Genero`, plus their own lists of `Contacto`, `Grupo` and `Correo`. |
| `Correo` | E-mail with subject, body, sender, To/CC/BCC lists, date, `EstadoCorreo`, attachments, starred flag and forwarding metadata; can clone itself for forwarding. |
| `EstadoCorreo` | Enum: `TODOS, BORRADOR, ENVIADO, RECIBIDO, REENVIADO, ELIMINADO`. |
| `ArchivoAdjunto` | Attachment (name, bytes, size) with automatic type detection (`IMAGEN, VIDEO, DOCUMENTO, OTRO`) and human-readable size. |
| `Contacto` | Address-book entry (e-mail + name). |
| `Grupo` | Group with name, description, creator, members, administrators and messages; helper methods to post system messages. |
| `MensajeGrupo` | Chat message (sender, content, date, attachments, system-message flag). |
| `Genero` | Enum: `MASCULINO, FEMENINO, OTRO`. |
| `Validador` / `ValidationException` | Static validation helpers and the typed exception carrying message and field. |
| `IconoUtils` | All icons drawn programmatically with Java2D. |
| `VisorDocumentos` | Image preview dialog and attachment download. |

**How mail delivery works:** a sent message is stored once in the sender’s mailbox. A recipient’s *Recibidos* view is computed by scanning all users’ sent/forwarded messages whose To, CC or BCC contains the logged-in e-mail. This mirrors the database design (“one e-mail = one row, no per-user copies”).

---

## 💾 Persistence: Local File and PostgreSQL

| Mechanism | When | File / target |
|-----------|------|---------------|
| **Java serialization** | After every state-changing action (`guardarDatosAutomaticamente`) and on exit; loaded at startup | `datos_sistema.dat` |
| **PostgreSQL export** | When the user confirms exit (`PersistenciaDB.guardarTodo`) | database `correo` |
| **Readable user report** | On exit | `usuarios_registrados.txt` (passwords masked with `*`) |

The PostgreSQL export runs inside **one transaction**: it first `TRUNCATE`s `usuarios, correos, grupos` (with `RESTART IDENTITY CASCADE`) and then re-inserts users → contacts, groups (members + messages + attachments) → e-mails (recipients, per-user state, attachments). On failure it rolls back and the app tells the user that data remains stored locally.

> The database is a **snapshot/backup of the in-memory system**, not the live source of truth: the application reads from `datos_sistema.dat`.

---

## 🗄 Database Schema

The SQL script is included as a comment in `DB/ConexionDB.java`. Summary:

| Table | Purpose |
|-------|---------|
| `usuarios` | Users (`email` PK, unique `cedula`, names, password, phone, birth date, gender). |
| `contactos` | Private address book per user (`usuario_email`, `email`, `nombre`). |
| `grupos` | Shared groups (`id`, `nombre`, `descripcion`, `creador_email`), unique per creator and name. |
| `grupo_miembros` | Members of a group with `es_admin` flag. |
| `mensajes_grupo` | Group chat messages, including system messages. |
| `correos` | One row per e-mail (sender, subject, body, date, state, forwarding metadata). |
| `correo_destinatarios` | Recipients with type `PARA`, `CC` or `CCO`. |
| `correo_usuario` | **Private per-user state** of an e-mail (starred, deleted). |
| `archivos_adjuntos` | Attachment bytes (`BYTEA`) linked to an e-mail **or** a group message. |

Indexes: `idx_dest_email` on `correo_destinatarios(email)` and `idx_miembros_email` on `grupo_miembros(email)`.

---

## 🧱 Project Structure

```text
Correo/
├── pom.xml                                  # Maven: Java 24 + PostgreSQL JDBC 42.7.4
├── .gitignore                               # target/, IDE files, datos_sistema.dat, usuarios_registrados.txt
├── datos_sistema.dat                        # Serialized system data (auto-generated, not versioned)
├── usuarios_registrados.txt                 # Readable user report (auto-generated, not versioned)
└── src/main/
    ├── resources/META-INF/MANIFEST.MF       # Main-Class: uce.edu.ec.correo.Ejecutable.EjecucionCorreo
    └── java/uce/edu/ec/correo/
        ├── Ejecutable/
        │   └── EjecucionCorreo.java         # main(): look & feel + main window
        ├── dominio/                         # Business model
        │   ├── EmpresaCorreo.java           # System facade + file persistence
        │   ├── Usuario.java
        │   ├── Correo.java
        │   ├── EstadoCorreo.java
        │   ├── ArchivoAdjunto.java
        │   ├── Contacto.java
        │   ├── Grupo.java
        │   ├── MensajeGrupo.java
        │   └── Genero.java
        ├── DB/
        │   ├── ConexionDB.java              # JDBC connection + SQL schema (in comments)
        │   └── PersistenciaDB.java          # Full export to PostgreSQL
        ├── gui/                             # Swing interface
        │   ├── VentanaPrincipal.java        # Frame, splash, CardLayout, exit/save logic
        │   ├── SplashScreen.java
        │   ├── PanelLogin.java
        │   ├── PanelRegistro.java
        │   ├── PanelMenuPrincipal.java      # Sidebar + content area
        │   ├── PanelCorreos.java            # Mail table, filters, search
        │   ├── PanelContactos.java
        │   ├── PanelGrupos.java
        │   ├── PanelInformacionUsuario.java # Profile, change password, delete account
        │   ├── DialogoNuevoCorreo.java      # Composer
        │   ├── DialogoDetalleCorreo.java
        │   ├── DialogoReenviarCorreo.java
        │   ├── DialogoContacto.java / DialogoDetalleContacto.java / DialogoSeleccionarContacto.java
        │   ├── DialogoGrupo.java / DialogoMiembrosGrupo.java / DialogoGestionAdmins.java
        │   └── DialogoChatGrupal.java / DialogoArchivosChat.java
        └── util/
            ├── Validador.java               # Cédula, e-mail, phone, name, date, password
            ├── ValidationException.java
            ├── IconoUtils.java              # Java2D vector icons
            └── VisorDocumentos.java         # Image viewer + download
```

---

## 🏗 Architecture and Design Notes

- **Layered structure:** `dominio` (rules and data) → `DB` (persistence) → `gui` (Swing views) → `util` (validation, icons, file viewer).
- **Facade:** `EmpresaCorreo` is the single entry point used by every GUI component; it enforces “must be logged in” checks and triggers automatic saving.
- **Soft delete:** deleting an e-mail only changes its state to `ELIMINADO`; permanent deletion removes it from the user’s list.
- **Immutability of ownership:** only the sender can send a draft; groups can only be written by members/admins.
- **Typed validation errors:** `ValidationException(message, field)` lets the GUI show precise errors.
- **No external UI assets:** icons are generated at runtime with Graphics2D.
- **Relational design:** e-mail content is stored once; per-user flags live in `correo_usuario`, which models private state (starred/deleted) without duplicating messages.

---

## ⚠️ Known Limitations and Security Notes

This is an **educational project**, not a production mail server.

- **Single-process “mail server”:** all users live in one local file; messages are not sent over a network (no SMTP/IMAP) and several machines cannot share the same data live.
- **Passwords are stored in plain text** (in `datos_sistema.dat` and in the `usuarios.password` column). Hash them (e.g., BCrypt/Argon2) before any real use. `usuarios_registrados.txt` only masks the length.
- **Credentials in source:** `ConexionDB.java` contains the DB user/password as constants (a placeholder is committed). Use environment variables or a config file kept out of Git.
- **Local files may contain personal data** (names, cédula, phone). They are excluded from Git through `.gitignore`.
- **DB export overwrites** `usuarios`, `correos` and `grupos` tables (`TRUNCATE`) each time the app exits normally.
- Emails are looked up by **subject** in several operations, so duplicate subjects may reference the first match.
- Attachments are held in memory (and stored as `BYTEA`), so very large files are limited to 10 MB.
- The `.idea/` and `target/` directories should not be committed (already ignored).

---

## 🛠 Troubleshooting

| Problem | Solution |
|---------|----------|
| `error: invalid target release: 24` | Install **JDK 24**, or lower `maven.compiler.source/target` in `pom.xml` to the JDK you have (the code uses mainstream Java features). |
| “No se pudo guardar en la base de datos” on exit | PostgreSQL is not running, the `correo` database/tables do not exist, or the credentials in `ConexionDB.java` are wrong. Your data is still saved in `datos_sistema.dat`. |
| `No suitable driver found for jdbc:postgresql` | Run through Maven/IDE so the PostgreSQL JDBC dependency is on the classpath. |
| Cannot register: “Dominio de email no permitido” | Use one of the allowed domains listed in [Validation Rules](#-validation-rules). |
| Cédula rejected | Check the 10 digits, the province code (01–24) and the check digit; use a valid Ecuadorian ID. |
| Reset all data | Close the app and delete `datos_sistema.dat` (and `usuarios_registrados.txt`). |

---

## 👤 Author, Repository and License

- **Author:** Christopher Josué Villón Peralta 
- **Repository:** <https://github.com/ChristopherVP/Correo>
- **Context:** university project (Universidad Central del Ecuador, package `uce.edu.ec`).
- **License:** *not specified yet* — add a `LICENSE` file (e.g., MIT) if you want others to reuse the code.
