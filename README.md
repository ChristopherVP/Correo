# ✉️ EcuMail (Correo)

**A desktop email and group-chat system with a full administration console, written in Java (Swing), featuring Ecuadorian ID validation, local file persistence, PostgreSQL storage, audit logging and PDF/Excel report export.**

![Java](https://img.shields.io/badge/Java-24-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue)
![Status](https://img.shields.io/badge/Status-Educational%20project-green)

EcuMail is a university project (Universidad Central del Ecuador — package `uce.edu.ec.correo`) that simulates an email company. Users register with a validated Ecuadorian *cédula*, send and receive emails (To / CC / BCC, attachments, drafts, forwarding, starred and trash), manage a private contact book, chat in group conversations with administrator roles, and recover their password through security questions. A separate **administration console** lets administrators manage users, handle support requests and appeals, review an audit trail and export reports as **PDF** or **Excel**.

The whole domain follows an object-oriented design with clear layers: domain, GUI, persistence and utilities. Excel and PDF files are generated **without third-party libraries** — the only external dependency is the PostgreSQL JDBC driver.

---

## Table of Contents

1. [Features at a Glance](#-features-at-a-glance)
2. [Project Requirements](#-project-requirements)
3. [Installation and Setup](#-installation-and-setup)
4. [Running the Application](#-running-the-application)
5. [User Guide](#-user-guide)
6. [Administration Console](#-administration-console)
7. [Security](#-security)
8. [Validation Rules](#-validation-rules)
9. [Domain Model](#-domain-model)
10. [Persistence: Local File and PostgreSQL](#-persistence-local-file-and-postgresql)
11. [Database Schema](#-database-schema)
12. [Project Structure](#-project-structure)
13. [Architecture and Design Notes](#-architecture-and-design-notes)
14. [Known Limitations](#-known-limitations)
15. [Troubleshooting](#-troubleshooting)
16. [Author, Repository and License](#-author-repository-and-license)

---

## ✨ Features at a Glance

**Accounts**
- Registration with strict validation: Ecuadorian *cédula* (check-digit algorithm + province code), allowed e-mail domains, `09`-prefixed phone number, birth date, gender and password policy.
- The **province** is detected automatically from the cédula while typing.
- Login with show/hide password, personal information panel, **change password** and **delete my account** (both confirmed with the current password).
- **Password recovery** through security questions (two random questions among cédula, birth date and phone), with a temporary lock after repeated failures.
- **Suspended accounts** are blocked at login and can send an **appeal** from the login screen.

**Email**
- Compose with **To, CC and BCC (CCO)**, subject, body and **attachments** (images, videos, documents; **10 MB limit** per file).
- Recipients are checked against registered users before sending.
- Save as **draft**, send later, **forward** to another address, **star**, move to **trash** and delete **permanently**.
- Folders/filters: *All, Starred, Sent, Received, Drafts, Forwarded, Deleted*.
- Search by subject or sender; detail view with attachments you can **preview (images)** or **download**.
- BCC recipients are hidden from the rest; the sender sees them.

**Contacts**
- Private address book per user: add, edit, delete and search by e-mail or name; contact detail view; contact picker when composing and creating groups.

**Groups (group chat)**
- Create groups with name, description and members chosen from your contacts.
- **Group chat** with text and attached files, shared-files browser, member list and **system messages** (group created, member left, description changed).
- **Roles**: permanent creator, administrators and members. Admins can modify or delete the group and manage other administrators; regular members can leave.

**Support requests**
- Users can file **complaints, technical reports and improvement suggestions** from the *Centro de Solicitudes* and follow the status and answer of each one.

**Administration console**
- Dashboard with system statistics, user management (suspend, reactivate, delete with a reason), support-request handling, administrator management, audit log and **PDF / Excel report export**. See [Administration Console](#-administration-console).

**Interface and persistence**
- Animated **splash screen**, card-based navigation, collapsible sidebar menu, custom **vector icons drawn with Java2D** (no image files needed).
- Automatic saving to a local serialized file after every action, plus a full **PostgreSQL dump** when the application closes.
- Human-readable `usuarios_registrados.txt` report with masked sensitive data.

---

## 📋 Project Requirements

### Functional requirements

| ID | Requirement |
|----|-------------|
| FR-01 | Register users with cédula, first/last name, e-mail, phone, birth date, gender and password; reject duplicates (cédula and e-mail). |
| FR-02 | Validate the Ecuadorian cédula (10 digits, province code 01–24, third digit < 6, check digit) and show the province. |
| FR-03 | Restrict e-mail addresses to lowercase and to a list of allowed domains (Gmail, Outlook/Hotmail/Live/MSN, Yahoo, iCloud/Me/Mac, Proton, Zoho, GMX, AOL, UCE). |
| FR-04 | Authenticate users and administrators by e-mail and password; allow logout and account deletion (with password confirmation). |
| FR-05 | Let a logged-in user change their password following the password policy. |
| FR-06 | Recover a forgotten password by answering two security questions; lock recovery for 15 minutes after 3 failed attempts. |
| FR-07 | Compose e-mails with To, CC, BCC, subject, body and attachments up to 10 MB each; verify that recipients exist. |
| FR-08 | Save e-mails as drafts and send them later; send directly from the composer. |
| FR-09 | Forward an e-mail to a new recipient, keeping the original subject, sender and date. |
| FR-10 | Mark e-mails as starred; list by state (all, sent, received, drafts, forwarded, deleted, starred). |
| FR-11 | Soft-delete e-mails (trash) and delete them permanently. |
| FR-12 | Search e-mails by subject or sender and view full details with attachments (preview images, download files). |
| FR-13 | Manage a personal contact book (add, modify, delete, search); prevent adding yourself or duplicates. |
| FR-14 | Create groups from contacts with name and description; reject duplicate group names. |
| FR-15 | Group chat with text and file attachments, shared-files viewer, members list and automatic system messages. |
| FR-16 | Group roles: permanent creator, promotable/demotable administrators, members can leave; admins can modify or delete the group. |
| FR-17 | Let users file complaints, technical reports and recommendations and follow their status. |
| FR-18 | Let administrators suspend, reactivate and delete user accounts (a reason of at least 10 characters is mandatory). |
| FR-19 | Let suspended users submit an appeal that administrators can accept or reject. |
| FR-20 | Let administrators answer support requests (*Pending → In review → Resolved / Rejected*). |
| FR-21 | Let the main administrator create and delete collaborator administrators (temporary password, forced change at first login). |
| FR-22 | Record every sensitive administrative action in an audit log. |
| FR-23 | Export reports (daily, bi-weekly, monthly, yearly) as **PDF** or **Excel (.xlsx)**. |
| FR-24 | Persist all data automatically to `datos_sistema.dat` after each change and reload it at startup. |
| FR-25 | On exit (with confirmation), also store everything in PostgreSQL and regenerate `usuarios_registrados.txt`. |

### Non-functional requirements

| ID | Requirement |
|----|-------------|
| NFR-01 | **Desktop GUI** built with Java Swing and the operating system's native look and feel. |
| NFR-02 | **Object-oriented design** with a clear separation between domain, GUI, persistence and utilities. |
| NFR-03 | **Graceful failure**: if PostgreSQL is unreachable, the user is warned and data remains safe in the local file. |
| NFR-04 | **Transactional DB writes**: the PostgreSQL export runs in one transaction with rollback on error. |
| NFR-05 | **Input safety**: validation errors are raised as `ValidationException` with the offending field, and all SQL uses prepared statements. |
| NFR-06 | **Privacy**: administrators never see passwords; cédula, phone and e-mails in reports and audit logs are masked. |
| NFR-07 | **No extra libraries**: Excel (`.xlsx`) and PDF files are written by the project itself. |
| NFR-08 | **Language**: user interface and messages are in **Spanish**. |

### Software requirements

| Requirement | Details |
|-------------|---------|
| **JDK** | **Java 24** (`maven.compiler.source/target = 24` in `pom.xml`). |
| **Build tool** | **Apache Maven** 3.x (or an IDE with Maven support such as IntelliJ IDEA). |
| **Dependency** | `org.postgresql:postgresql:42.7.4` (JDBC driver), downloaded automatically by Maven. |
| **Database** *(optional)* | **PostgreSQL** 13+ with a database named `correo`. Without it, the app still works using the local file. |
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
2. Connect to `correo` and run the schema script (see [Database Schema](#-database-schema)):
   ```bash
   psql -U postgres -d correo -f crear_tablas.sql
   ```
3. Create a `db.properties` file in the working directory (the folder you run the app from):
   ```properties
   db.url=jdbc:postgresql://localhost:5432/correo
   db.user=postgres
   db.password=your_password_here
   ```

> ⚠️ `db.properties` contains credentials. Add it to `.gitignore` and **never commit it**.

### Optional: administrator account

On first start the system creates a **main administrator**. Its credentials come from environment variables:

| Variable | Meaning | Default |
|----------|---------|---------|
| `ECUMAIL_ADMIN_EMAIL` | Main administrator e-mail | `admin@ecumail.ec` |
| `ECUMAIL_ADMIN_PASSWORD` | Initial password | built-in default (change it) |

> ⚠️ Set your own values **before the first run** and change the password after logging in. The default password is meant for development only.

---

## ▶️ Running the Application

**From an IDE (recommended):** open the project in IntelliJ IDEA or NetBeans, wait for Maven to import and run the class
`uce.edu.ec.correo.Ejecutable.EjecucionCorreo`.

**From the command line:**

```bash
mvn compile exec:java -Dexec.mainClass="uce.edu.ec.correo.Ejecutable.EjecucionCorreo"
```

The app shows a splash screen and then the login window. On first run `datos_sistema.dat` is created in the working directory.

---

## 🧭 User Guide

1. **Register** — On the login screen choose *“¿No tienes cuenta? Regístrate”*. Fill in all fields (the province appears automatically from the cédula).
2. **Log in** with your e-mail and password. Forgot it? Use the recovery option and answer the two security questions.
3. **Sidebar menu** (hamburger button) with three sections:
   - **Correos:** *Redactar*, *Todos los Correos*, *Destacados*, *Enviados*, *Recibidos*, *Borradores*, *Reenviados*, *Eliminados*.
   - **Contactos:** *Agregar Contacto*, *Ver Todos los Contactos*.
   - **Grupos:** *Crear Grupo*, *Chats Grupales*.
4. **Write an e-mail:** *Redactar* → fill *Para* (and optionally *CC* / *CCO*), subject, message, attach files → **Guardar Borrador** or **Enviar**.
5. **Read / manage e-mails:** double-click a row to open *Detalle del Correo* → *Reenviar*, *Enviar Borrador*, *Eliminar*, *Eliminar Definitivamente*, *Ver* / *Descargar* attachments. Use the star column to mark as *Destacado*.
6. **Contacts:** add by e-mail + name; double-click a contact for details, where you can *Modificar Nombre*, *Eliminar Contacto* or *Enviar Correo*.
7. **Groups:** create a group and pick members from your contacts; open it from *Chats Grupales* to chat, share files (*Ver Archivos*), see *Ver Miembros* or — if you are an admin — *Modificar*, manage *Administradores* and *Eliminar Grupo*. Regular members can *Salir del Grupo*.
8. **Support requests:** open the *Centro de Solicitudes* to file a complaint, a technical report or an improvement suggestion and to read the administrators' answers.
9. **My account:** click the user button (top right) → *Mi Información Personal* → *Cambiar Contraseña* or *Eliminar Mi Cuenta*.
10. **Suspended account:** at login you will be told the account is suspended and shown the reason; you can submit **one appeal** while no other appeal is open.
11. **Exit:** closing the window asks for confirmation, then saves to the local file, PostgreSQL and `usuarios_registrados.txt`.

---

## 🛡 Administration Console

Administrators sign in from the **same login screen** with their administrator e-mail. There are two roles:

| Role | Permissions |
|------|-------------|
| **Main administrator** | Everything below, plus creating/deleting collaborator administrators, viewing the audit log, and the *General* and *Collaborator performance* reports. |
| **Collaborator** | Dashboard, user management, support requests and the *My work* report. |

**Tabs**

| Tab | What it does |
|-----|--------------|
| **Resumen** | System statistics: users, suspended users, administrators, sent e-mails, groups, open/total requests, and distributions by e-mail domain, province, gender, request type and status. |
| **Centro de Solicitudes y Mejoras** | List, filter and answer complaints, technical reports, suggestions and appeals (*Pending → In review → Resolved / Rejected*). |
| **Usuarios** | Masked overview of every user; **suspend** (with reason), **reactivate** or **delete** an account. A reason of at least 10 characters is required. |
| **Administradores** *(main admin)* | Create collaborators with a temporary password (they must change it at first login) or remove them. |
| **Auditoría** *(main admin)* | Chronological log of sensitive actions with the administrator, target and detail. |

**Reports** — *Exportar reportes* generates a multi-sheet document in **PDF** or **Excel (.xlsx)**:

| Report | Sheets | Available to |
|--------|--------|--------------|
| *My work* | Summary, handled requests, actions on accounts | All administrators |
| *General overview* | General summary, requests, user distribution, audit | Main administrator |
| *Collaborator performance* | Performance, accounts and reasons, handled requests | Main administrator |

Periods: **daily**, **bi-weekly** (1–15 and 16–end of month), **monthly** and **yearly**.

**Audited actions:** `LOGIN_ADMIN`, `LOGIN_ADMIN_FALLIDO`, `CAMBIO_PASSWORD_ADMIN`, `CREAR_ADMIN`, `ELIMINAR_ADMIN`, `SUSPENDER_USUARIO`, `REACTIVAR_USUARIO`, `ELIMINAR_USUARIO`, `ATENDER_SOLICITUD`, `EXPORTAR_REPORTE`.

---

## 🔐 Security

| Area | Implementation |
|------|----------------|
| **Administrator passwords** | Never stored in clear text: **PBKDF2-WithHmac-SHA256**, 120,000 iterations, 256-bit key and a random 16-byte salt per administrator. |
| **Forced password change** | New collaborators receive a temporary password and must change it at first login. |
| **Account recovery** | Two random security questions (cédula, birth date, phone); **3 failed attempts lock recovery for 15 minutes** per account. Administrator accounts cannot be recovered this way. |
| **Privacy by design** | Cédula, phone and e-mails are masked in the admin overview, audit log, reports and the readable user file. Administrators have no access to user passwords or message content. |
| **Accountability** | Every sensitive administrative action is written to the audit log with actor, target and detail. |
| **Mandatory justification** | Suspending or deleting a user requires a reason of at least 10 characters. |
| **SQL injection** | All database access uses `PreparedStatement`. |
| **Secrets outside the code** | Database credentials live in `db.properties`; administrator credentials come from environment variables. |

---

## ✅ Validation Rules

| Field | Rule |
|-------|------|
| **Cédula** | Exactly 10 digits, numeric only, province code 01–24, third digit < 6, valid check digit; must be unique. |
| **E-mail** | Lowercase only; format `^[a-z0-9+_.-]+@[a-z0-9.-]+\.[a-z]{2,}$`; domain must be one of `gmail.com, outlook.com, hotmail.com, live.com, msn.com, yahoo.com, yahoo.es, yahoo.mx, icloud.com, me.com, mac.com, proton.me, protonmail.com, zoho.com, zohomail.com, gmx.com, gmx.net, aol.com, uce.edu.ec`; must be unique. |
| **Name / last name** | Only letters (including accents and ñ) and spaces. |
| **Phone** | 10 digits starting with `09`. |
| **Birth date** | Format `dd/mm/aaaa`, real calendar date (leap years handled), not in the future, age between 1 and 120. |
| **Password** | 8–14 characters with at least one uppercase letter. |
| **Contacts** | Valid e-mail, cannot be yourself, no duplicates. |
| **Groups** | Unique name per user; the creator keeps a permanent administrator role (cannot be demoted). |
| **Attachments** | Maximum **10 MB** per file; groups: images (`jpg, jpeg, png, gif, bmp`), videos (`mp4, avi, mov, wmv, flv`), documents (`pdf, doc, docx, txt, xls, xlsx`), others. |
| **E-mail sending** | At least one valid, **registered** recipient in To/CC/BCC; only the owner can send their own message. |
| **Admin actions** | Suspension / deletion reason: minimum 10 characters. |

---

## 🧩 Domain Model

| Class | Responsibility |
|-------|----------------|
| `EmpresaCorreo` | Facade of the whole system: users, session, contacts, groups, e-mails, support requests, suspensions, password recovery, reports and file persistence. Serializable. |
| `Usuario` | Person with cédula, names, e-mail, password, phone, birth date, `Genero`, suspension data, plus their own lists of `Contacto`, `Grupo` and `Correo`. |
| `Correo` / `EstadoCorreo` | E-mail with To/CC/BCC, attachments, starred flag and forwarding metadata; states `TODOS, BORRADOR, ENVIADO, RECIBIDO, REENVIADO, ELIMINADO`. |
| `ArchivoAdjunto` | Attachment (name, bytes, size) with automatic type detection and human-readable size. |
| `Contacto` | Address-book entry (e-mail + name). |
| `Grupo` / `MensajeGrupo` | Group with members, administrators and messages; chat message with attachments and system-message flag. |
| `CentroAdministracion` | Aggregate of administrators, support requests and the audit log. |
| `Administrador` | Administrator account with salted PBKDF2 hash, main/collaborator role and pending-password-change flag. Immutable-style updates (`conNuevaPassword`). |
| `Solicitud` / `TipoSolicitud` / `EstadoSolicitud` | Support request (`QUEJA, REPORTE_TECNICO, RECOMENDACION, APELACION`) and its life cycle (`PENDIENTE, EN_REVISION, RESUELTA, RECHAZADA`). Immutable-style updates (`conRespuesta`, `anonimizada`). |
| `InfoSuspension` | Record with the suspension reason, date and related appeals. |
| `RegistroAuditoria` / `AccionAuditoria` | Audit entry and catalogue of audited actions. |
| `PreguntaSeguridad` | Enum of recovery questions (`CEDULA, FECHA_NACIMIENTO, TELEFONO`) with verification logic. |
| `GeneradorReportes` / `TipoReporte` / `PeriodoReporte` / `RangoFechas` / `HojaReporte` | Report engine: report types, periods, date ranges and the neutral sheet model written by the PDF and Excel writers. |
| `EstadisticasSistema` / `ResumenUsuario` | Read-only records for the dashboard and the user table. |
| `Validador` / `ValidationException` / `CuentaSuspendidaException` | Validation helpers and typed exceptions carrying message and field. |
| `SeguridadUtil` / `ReglasPassword` / `ControlIntentos` / `ConfiguracionAdmin` | Hashing and masking, password policy, attempt limiter with temporary lock, and admin configuration from environment variables. |
| `ExcelWriter` / `PdfWriter` | Dependency-free `.xlsx` and `.pdf` writers. |
| `IconoUtils` / `VisorDocumentos` / `EstiloUI` | Java2D icons, image preview and download, shared UI styling. |

**How mail delivery works:** a sent message is stored once in the sender's mailbox. A recipient's *Recibidos* view is computed by scanning all users' sent/forwarded messages whose To, CC or BCC contains the logged-in e-mail. This mirrors the database design (“one e-mail = one row, no per-user copies”).

---

## 💾 Persistence: Local File and PostgreSQL

| Mechanism | When | File / target |
|-----------|------|---------------|
| **Java serialization** | After every state-changing action and on exit; loaded at startup | `datos_sistema.dat` |
| **PostgreSQL export** | When the user confirms exit (`PersistenciaDB.guardarTodo`) | database `correo` |
| **Readable user report** | On exit | `usuarios_registrados.txt` (sensitive data masked) |

The PostgreSQL export runs inside **one transaction**: it first `TRUNCATE`s `usuarios, correos, grupos, administradores, solicitudes, auditoria_admin` (with `RESTART IDENTITY CASCADE`) and then re-inserts users → administrators → support requests → audit log → contacts, groups (members + messages + attachments) → e-mails (recipients, per-user state, attachments). On failure it rolls back and the app tells the user that data remains stored locally.

> The database is a **snapshot/backup of the in-memory system**, not the live source of truth: the application reads from `datos_sistema.dat`.

---

## 🗄 Database Schema

Run `crear_tablas.sql` against the `correo` database. The script is idempotent: it drops and recreates the 12 tables.

| Table | Purpose |
|-------|---------|
| `usuarios` | Users (`email` PK, unique `cedula`, names, password, phone, birth date, gender, suspension data). |
| `administradores` | Administrators (`email` PK, `password_hash`, `salt`, main-admin flag, pending-password-change flag, creator). |
| `solicitudes` | Support requests and appeals (type, subject, description, state, answer, handler, dates). |
| `auditoria_admin` | Audit log (date, administrator, action, target, detail). |
| `contactos` | Private address book per user. |
| `grupos` | Shared groups (`id`, `nombre`, `descripcion`, `creador_email`). |
| `grupo_miembros` | Members of a group with `es_admin` flag (PK `grupo_id, email`). |
| `mensajes_grupo` | Group chat messages, including system messages. |
| `correos` | One row per e-mail (sender, subject, body, date, state, forwarding metadata). |
| `correo_destinatarios` | Recipients with type `PARA`, `CC` or `CCO`. |
| `correo_usuario` | **Private per-user state** of an e-mail (starred, deleted). |
| `archivos_adjuntos` | Attachment bytes (`BYTEA`) linked to an e-mail **or** a group message. |

`CHECK` constraints restrict gender, request type/state, e-mail state and recipient type. Foreign keys use `ON DELETE CASCADE`, so truncating the parent tables also clears their children. Indexes cover the most frequent look-ups (recipient e-mail, group members, message date, audit date, request owner).

---

## 🧱 Project Structure

```text
Correo/
├── pom.xml                                  # Maven: Java 24 + PostgreSQL JDBC 42.7.4
├── crear_tablas.sql                         # PostgreSQL schema (12 tables)
├── db.properties                            # DB credentials (local only, do not commit)
├── .gitignore                               # target/, IDE files, db.properties, datos_sistema.dat, usuarios_registrados.txt
├── datos_sistema.dat                        # Serialized system data (auto-generated, not versioned)
├── usuarios_registrados.txt                 # Readable user report (auto-generated, not versioned)
└── src/main/
    ├── resources/META-INF/MANIFEST.MF       # Main-Class: uce.edu.ec.correo.Ejecutable.EjecucionCorreo
    └── java/uce/edu/ec/correo/
        ├── Ejecutable/
        │   └── EjecucionCorreo.java         # main(): look & feel + main window
        ├── dominio/                         # Business model (26 classes)
        │   ├── EmpresaCorreo.java           # System facade + file persistence
        │   ├── Usuario / Correo / Contacto / Grupo / MensajeGrupo / ArchivoAdjunto
        │   ├── CentroAdministracion / Administrador / Solicitud / RegistroAuditoria
        │   ├── GeneradorReportes / HojaReporte / RangoFechas / TipoReporte / PeriodoReporte
        │   ├── EstadisticasSistema / ResumenUsuario / InfoSuspension / PreguntaSeguridad
        │   └── Enums: Genero, EstadoCorreo, TipoSolicitud, EstadoSolicitud, AccionAuditoria, EstiloFecha
        ├── DB/
        │   ├── ConexionDB.java              # JDBC connection read from db.properties
        │   └── PersistenciaDB.java          # Full transactional export to PostgreSQL
        ├── gui/                             # Swing interface
        │   ├── VentanaPrincipal / SplashScreen / PanelLogin / PanelRegistro / PanelMenuPrincipal
        │   ├── PanelCorreos / PanelContactos / PanelGrupos / PanelInformacionUsuario
        │   ├── PanelAdmin + PanelAdminResumen / Solicitudes / Usuarios / Administradores / Auditoria
        │   ├── DialogoNuevoCorreo / DialogoDetalleCorreo / DialogoReenviarCorreo
        │   ├── DialogoContacto / DialogoDetalleContacto / DialogoSeleccionarContacto
        │   ├── DialogoGrupo / DialogoMiembrosGrupo / DialogoGestionAdmins
        │   ├── DialogoChatGrupal / DialogoArchivosChat
        │   ├── DialogoRecuperarPassword / DialogoApelacionSuspension
        │   ├── DialogoNuevaSolicitud / DialogoCentroSolicitudes / DialogoAtenderSolicitud
        │   ├── DialogoExportarReporte / DialogoCambiarPasswordAdmin
        │   └── CampoPassword / SelectorFecha
        └── util/
            ├── Validador / ValidationException / CuentaSuspendidaException
            ├── SeguridadUtil / ReglasPassword / ControlIntentos / ConfiguracionAdmin
            ├── ExcelWriter / PdfWriter
            └── IconoUtils / VisorDocumentos / EstiloUI
```

---

## 🏗 Architecture and Design Notes

- **Layered structure:** `dominio` (rules and data) → `DB` (persistence) → `gui` (Swing views) → `util` (validation, security, writers, icons).
- **Facade:** `EmpresaCorreo` is the single entry point used by every GUI component; it enforces “must be logged in” and “must be administrator / main administrator” checks and triggers automatic saving.
- **Immutable-style updates:** domain objects such as `Administrador` and `Solicitud` return new instances (`conNuevaPassword`, `conRespuesta`, `anonimizada`) instead of mutating state; reports and statistics are Java `record`s.
- **Soft delete:** deleting an e-mail only changes its state to `ELIMINADO`; permanent deletion removes it from the user's list.
- **Typed validation errors:** `ValidationException(message, field)` lets the GUI show precise errors; `CuentaSuspendidaException` drives the appeal flow.
- **Report pipeline:** `GeneradorReportes` builds neutral `HojaReporte` sheets; `PdfWriter` and `ExcelWriter` only render them, so a new format needs no change in the report logic.
- **No external UI assets or document libraries:** icons are drawn at runtime with Graphics2D; `.xlsx` is written as a ZIP of XML parts and `.pdf` is written directly.
- **Relational design:** e-mail content is stored once; per-user flags live in `correo_usuario`.

---

## ⚠️ Known Limitations

This is an **educational project**, not a production mail server.

- **Single-process “mail server”:** all users live in one local file; messages are not sent over a network (no SMTP/IMAP) and several machines cannot share the same data live.
- **Regular user passwords are stored in plain text** (in `datos_sistema.dat` and in the `usuarios.password` column). Administrator passwords are already hashed with PBKDF2; apply the same to users before any real use.
- **Local files may contain personal data** (names, cédula, phone). They are excluded from Git through `.gitignore`.
- **DB export overwrites** all exported tables (`TRUNCATE`) each time the app exits normally.
- E-mails are looked up by **subject** in several operations, so duplicate subjects may reference the first match.
- Attachments are held in memory (and stored as `BYTEA`), so files are limited to 10 MB.
- Password-recovery questions rely on data the user already registered (cédula, birth date, phone).

---

## 🛠 Troubleshooting

| Problem | Solution |
|---------|----------|
| `error: invalid target release: 24` | Install **JDK 24**, or lower `maven.compiler.source/target` in `pom.xml` to the JDK you have. |
| `ERROR: relation "usuarios" does not exist` | The tables are missing or have other names. Run `crear_tablas.sql` on the `correo` database. |
| `transaction aborted, commands will be ignored until end of transaction block` (SQL state 25P02) | An earlier statement failed inside the same transaction. Fix that first error (or run the script without `BEGIN`) and run it again. |
| `db.properties` not found | Create it in the folder you run the app from (see [Installation and Setup](#-installation-and-setup)). |
| “No se pudo guardar en la base de datos” on exit | PostgreSQL is not running, the database/tables do not exist, or the credentials in `db.properties` are wrong. Your data is still saved in `datos_sistema.dat`. |
| `No suitable driver found for jdbc:postgresql` | Run through Maven/IDE so the PostgreSQL JDBC dependency is on the classpath. |
| Cannot log in as administrator | Check `ECUMAIL_ADMIN_EMAIL` / `ECUMAIL_ADMIN_PASSWORD`; the main administrator is created on the first run only. |
| Recovery blocked | Wait 15 minutes after 3 failed attempts. |
| Cannot register: “Dominio de email no permitido” | Use one of the allowed domains listed in [Validation Rules](#-validation-rules). |
| Cédula rejected | Check the 10 digits, the province code (01–24) and the check digit. |
| Reset all data | Close the app and delete `datos_sistema.dat` (and `usuarios_registrados.txt`). |

---

## 👤 Author, Repository and License

- **Author:** Christopher Josué Villón Peralta
- **Repository:** <https://github.com/ChristopherVP/Correo>
- **Context:** university project (Universidad Central del Ecuador, package `uce.edu.ec`).
- **License:** *not specified yet* — add a `LICENSE` file (e.g., MIT) if you want others to reuse the code.
