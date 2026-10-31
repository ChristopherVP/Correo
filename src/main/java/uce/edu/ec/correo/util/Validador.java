package uce.edu.ec.correo.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

/**
 * Clase utilitaria para validar diferentes tipos de datos en el sistema de correo.
 *
 * Proporciona métodos estáticos para validar:
 * - Cédulas ecuatorianas (con verificación de dígito verificador)
 * - Correos electrónicos (con dominios permitidos)
 * - Números de teléfono
 * - Nombres y apellidos
 * - Contraseñas
 * - Fechas de nacimiento
 *
 * @author Sistema de Correo UCE
 * @version 1.0
 */
public class Validador {

    // ==================== CONSTANTES ====================

    /**
     * Lista de dominios de correo electrónico permitidos en el sistema.
     * Incluye proveedores populares y el dominio institucional de la UCE.
     */
    private static final List<String> DOMINIOS_PERMITIDOS = Arrays.asList(
            "@gmail.com", "@outlook.com", "@hotmail.com", "@live.com", "@msn.com",
            "@yahoo.com", "@yahoo.es", "@yahoo.mx",
            "@icloud.com", "@me.com", "@mac.com",
            "@proton.me", "@protonmail.com",
            "@zoho.com", "@zohomail.com",
            "@gmx.com", "@gmx.net",
            "@aol.com",
            "@uce.edu.ec"
    );

    /**
     * Array con los nombres de las provincias de Ecuador.
     * El índice corresponde al código de provincia en la cédula.
     * Índice 0: Desconocida
     * Índices 1-24: Provincias de Ecuador
     */
    private static final String[] PROVINCIAS = {
            "Desconocida", "Azuay", "Bolívar", "Cañar", "Carchi", "Cotopaxi",
            "Chimborazo", "El Oro", "Esmeraldas", "Guayas", "Imbabura", "Loja",
            "Los Ríos", "Manabí", "Morona Santiago", "Napo", "Pastaza",
            "Pichincha", "Tungurahua", "Zamora Chinchipe", "Galápagos",
            "Sucumbíos", "Orellana", "Santo Domingo", "Santa Elena"
    };

    // ==================== VALIDACIÓN DE CÉDULA ECUATORIANA ====================

    /**
     * Valida una cédula ecuatoriana verificando:
     * 1. Que no esté vacía
     * 2. Que tenga exactamente 10 dígitos
     * 3. Que solo contenga números
     * 4. Que el código de provincia sea válido (01-24)
     * 5. Que el tercer dígito sea menor a 6
     * 6. Que el dígito verificador sea correcto
     *
     * @param cedula Número de cédula a validar
     * @return true si la cédula es válida
     * @throws ValidationException si la cédula no cumple con los requisitos
     */
    public static boolean validarCedulaEcuatoriana(String cedula) throws ValidationException {
        // Verifica que la cédula no sea null o vacía
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new ValidationException("La cédula no puede estar vacía", "Cédula");
        }

        // Elimina espacios en blanco al inicio y final
        cedula = cedula.trim();

        // Verifica que tenga exactamente 10 dígitos
        if (cedula.length() != 10) {
            throw new ValidationException("La cédula debe tener exactamente 10 dígitos", "Cédula");
        }

        // Verifica que solo contenga números usando expresión regular
        if (!cedula.matches("\\d{10}")) {
            throw new ValidationException("La cédula solo debe contener números", "Cédula");
        }

        // Valida el código de provincia (primeros 2 dígitos)
        if (!validarCodigoProvincia(cedula)) {
            throw new ValidationException(
                    "Código de provincia inválido (primeros 2 dígitos deben ser entre 01 y 24)",
                    "Cédula");
        }

        // Obtiene el tercer dígito de la cédula
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));

        // El tercer dígito debe ser menor a 6 (0-5)
        if (tercerDigito >= 6) {
            throw new ValidationException("El tercer dígito de la cédula debe ser menor a 6", "Cédula");
        }

        // Valida el dígito verificador usando el algoritmo módulo 10
        if (!validarDigitoVerificador(cedula)) {
            throw new ValidationException("Dígito verificador de la cédula es inválido", "Cédula");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    /**
     * Valida que el código de provincia (primeros 2 dígitos) esté entre 01 y 24
     *
     * @param cedula Número de cédula completo
     * @return true si el código de provincia es válido
     */
    public static boolean validarCodigoProvincia(String cedula) {
        // Extrae los primeros 2 dígitos y los convierte a entero
        int codigoProvincia = Integer.parseInt(cedula.substring(0, 2));

        // Verifica que esté en el rango válido (1-24)
        return codigoProvincia >= 1 && codigoProvincia <= 24;
    }

    /**
     * Valida el dígito verificador de la cédula usando el algoritmo módulo 10.
     *
     * Algoritmo:
     * 1. Multiplica cada uno de los primeros 9 dígitos por 2,1,2,1,2,1,2,1,2
     * 2. Si el resultado es mayor o igual a 10, se le resta 9
     * 3. Suma todos los resultados
     * 4. Calcula el módulo 10 de la suma
     * 5. Si el módulo es 0, el verificador debe ser 0, sino debe ser (10 - módulo)
     *
     * @param cedula Número de cédula completo
     * @return true si el dígito verificador es correcto
     */
    public static boolean validarDigitoVerificador(String cedula) {
        // Coeficientes para multiplicar cada dígito (alternando 2 y 1)
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};

        // Variable para acumular la suma
        int suma = 0;

        // Itera sobre los primeros 9 dígitos de la cédula
        for (int i = 0; i < 9; i++) {
            // Convierte el carácter a su valor numérico
            int digito = Character.getNumericValue(cedula.charAt(i));

            // Multiplica el dígito por su coeficiente correspondiente
            int resultado = digito * coeficientes[i];

            // Si el resultado es mayor o igual a 10, resta 9
            if (resultado >= 10) {
                resultado -= 9;
            }

            // Suma el resultado al acumulador
            suma += resultado;
        }

        // Calcula el módulo 10 de la suma total
        int modulo = suma % 10;

        // Calcula el dígito verificador esperado
        // Si módulo es 0, el verificador debe ser 0
        // Si no, el verificador debe ser (10 - módulo)
        int verificadorEsperado = (modulo == 0) ? 0 : (10 - modulo);

        // Obtiene el décimo dígito (verificador real)
        int verificadorReal = Character.getNumericValue(cedula.charAt(9));

        // Compara el verificador esperado con el real
        return verificadorEsperado == verificadorReal;
    }

    /**
     * Obtiene el nombre de la provincia basándose en los primeros 2 dígitos de la cédula
     *
     * @param cedula Número de cédula
     * @return Nombre de la provincia o "Desconocida" si es inválida
     */
    public static String obtenerProvinciaPorCedula(String cedula) {
        // Verifica que la cédula tenga al menos 2 caracteres
        if (cedula == null || cedula.length() < 2) {
            return "Desconocida";
        }

        try {
            // Extrae y convierte los primeros 2 dígitos
            int codigoProvincia = Integer.parseInt(cedula.substring(0, 2));

            // Verifica que el código esté en el rango válido
            if (codigoProvincia >= 1 && codigoProvincia <= 24) {
                // Retorna el nombre de la provincia del array
                return PROVINCIAS[codigoProvincia];
            }
        } catch (NumberFormatException e) {
            // Si ocurre error al convertir a número, retorna "Desconocida"
            return "Desconocida";
        }

        // Si el código no está en rango, retorna "Desconocida"
        return "Desconocida";
    }

    // ==================== VALIDACIÓN DE EMAIL ====================

    /**
     * Valida un correo electrónico verificando:
     * 1. Que no esté vacío
     * 2. Que no contenga mayúsculas (solo minúsculas permitidas)
     * 3. Que tenga formato válido (usuario@dominio.extensión)
     * 4. Que el dominio esté en la lista de dominios permitidos
     *
     * @param email Dirección de correo a validar
     * @return true si el email es válido
     * @throws ValidationException si el email no cumple con los requisitos
     */
    public static boolean validarEmail(String email) throws ValidationException {
        // Verifica que el email no sea null o vacío
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("El email no puede estar vacío", "Email");
        }

        // Elimina espacios en blanco
        email = email.trim();

        // VALIDACIÓN CRÍTICA: No permite mayúsculas en el email
        if (!email.equals(email.toLowerCase())) {
            throw new ValidationException(
                    "El email no puede contener letras mayúsculas.\n" +
                            "Por favor, use solo letras minúsculas.",
                    "Email");
        }

        // Expresión regular para validar formato de email
        // ^[a-z0-9+_.-]+ : Parte local (antes del @)
        // @ : Arroba obligatoria
        // [a-z0-9.-]+ : Dominio
        // \\.[a-z]{2,}$ : Punto y extensión (mínimo 2 caracteres)
        String regex = "^[a-z0-9+_.-]+@[a-z0-9.-]+\\.[a-z]{2,}$";

        // Verifica que el email cumpla con el formato
        if (!Pattern.matches(regex, email)) {
            throw new ValidationException("Formato de email inválido", "Email");
        }

        // Valida que el dominio esté en la lista de permitidos
        boolean dominioValido = false;

        // Recorre la lista de dominios permitidos
        for (String dominio : DOMINIOS_PERMITIDOS) {
            // Verifica si el email termina con el dominio
            if (email.endsWith(dominio)) {
                dominioValido = true;
                break; // Sale del bucle si encuentra un dominio válido
            }
        }

        // Si no se encontró un dominio válido, lanza excepción
        if (!dominioValido) {
            // Construye mensaje con lista de dominios permitidos
            StringBuilder dominiosPermitidos = new StringBuilder("Dominios permitidos:\n");
            dominiosPermitidos.append("• Gmail: @gmail.com\n");
            dominiosPermitidos.append("• Outlook: @outlook.com, @hotmail.com, @live.com, @msn.com\n");
            dominiosPermitidos.append("• Yahoo: @yahoo.com, @yahoo.es, @yahoo.mx\n");
            dominiosPermitidos.append("• Apple: @icloud.com, @me.com, @mac.com\n");
            dominiosPermitidos.append("• Proton: @proton.me, @protonmail.com\n");
            dominiosPermitidos.append("• Zoho: @zoho.com, @zohomail.com\n");
            dominiosPermitidos.append("• GMX: @gmx.com, @gmx.net\n");
            dominiosPermitidos.append("• AOL: @aol.com\n");
            dominiosPermitidos.append("• UCE: @uce.edu.ec");

            throw new ValidationException(
                    "Dominio de email no permitido.\n" + dominiosPermitidos.toString(),
                    "Email");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    // ==================== VALIDACIÓN DE TELÉFONO ====================

    /**
     * Valida un número de teléfono celular ecuatoriano verificando:
     * 1. Que no esté vacío
     * 2. Que tenga exactamente 10 dígitos
     * 3. Que comience con "09"
     *
     * @param telefono Número de teléfono a validar
     * @return true si el teléfono es válido
     * @throws ValidationException si el teléfono no cumple con los requisitos
     */
    public static boolean validarTelefono(String telefono) throws ValidationException {
        // Verifica que el teléfono no sea null o vacío
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new ValidationException("El teléfono no puede estar vacío", "Teléfono");
        }

        // Elimina espacios en blanco
        telefono = telefono.trim();

        // Verifica que tenga exactamente 10 dígitos numéricos
        if (!telefono.matches("\\d{10}")) {
            throw new ValidationException(
                    "El teléfono debe tener exactamente 10 dígitos numéricos",
                    "Teléfono");
        }

        // Verifica que comience con "09" (celulares en Ecuador)
        if (!telefono.startsWith("09")) {
            throw new ValidationException("El teléfono debe comenzar con 09", "Teléfono");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    // ==================== VALIDACIÓN DE NOMBRES ====================

    /**
     * Valida un nombre o apellido verificando:
     * 1. Que no esté vacío
     * 2. Que solo contenga letras (incluyendo acentos y ñ) y espacios
     *
     * @param nombre Nombre o apellido a validar
     * @return true si el nombre es válido
     * @throws ValidationException si el nombre no cumple con los requisitos
     */
    public static boolean validarNombre(String nombre) throws ValidationException {
        // Verifica que el nombre no sea null o vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre no puede estar vacío", "Nombre");
        }

        // Verifica que solo contenga letras (con acentos), ñ y espacios
        // [a-zA-Z] : Letras sin acentos
        // áéíóúÁÉÍÓÚ : Letras con acentos
        // ñÑ : Letra ñ mayúscula y minúscula
        // (espacio) : Espacios permitidos
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new ValidationException(
                    "El nombre solo puede contener letras y espacios",
                    "Nombre");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    // ==================== VALIDACIÓN DE CAMPOS NO VACÍOS ====================

    /**
     * Valida que un campo de texto no esté vacío
     *
     * @param campo Valor del campo a validar
     * @param nombreCampo Nombre descriptivo del campo
     * @return true si el campo no está vacío
     * @throws ValidationException si el campo está vacío
     */
    public static boolean validarCampoNoVacio(String campo, String nombreCampo) throws ValidationException {
        // Verifica que el campo no sea null o vacío después de quitar espacios
        if (campo == null || campo.trim().isEmpty()) {
            throw new ValidationException(
                    "El campo " + nombreCampo + " no puede estar vacío",
                    nombreCampo);
        }

        // Si el campo tiene contenido, retorna true
        return true;
    }

    // ==================== VALIDACIÓN DE FECHA DE NACIMIENTO ====================

    /**
     * Valida una fecha de nacimiento verificando:
     * 1. Que no sea null
     * 2. Que no sea fecha futura
     * 3. Que la edad sea al menos 1 año
     * 4. Que la edad no sea mayor a 120 años
     *
     * @param fechaNacimiento Fecha de nacimiento a validar
     * @return true si la fecha es válida
     * @throws ValidationException si la fecha no cumple con los requisitos
     */
    public static boolean validarFechaNacimiento(LocalDate fechaNacimiento) throws ValidationException {
        // Verifica que la fecha no sea null
        if (fechaNacimiento == null) {
            throw new ValidationException(
                    "La fecha de nacimiento no puede estar vacía",
                    "Fecha Nacimiento");
        }

        // Obtiene la fecha actual
        LocalDate hoy = LocalDate.now();

        // Verifica que la fecha no sea futura
        if (fechaNacimiento.isAfter(hoy)) {
            throw new ValidationException(
                    "La fecha de nacimiento no puede ser futura",
                    "Fecha Nacimiento");
        }

        // Calcula la edad en años
        int edad = Period.between(fechaNacimiento, hoy).getYears();

        // Verifica que la edad sea al menos 1 año
        if (edad < 1) {
            throw new ValidationException(
                    "La persona debe tener al menos 1 año",
                    "Fecha Nacimiento");
        }

        // Verifica que la edad no sea mayor a 120 años
        if (edad > 120) {
            throw new ValidationException(
                    "La edad no puede ser mayor a 120 años",
                    "Fecha Nacimiento");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    /**
     * Valida el formato de una fecha en texto (dd/mm/aaaa)
     *
     * Verifica:
     * 1. Que no esté vacía
     * 2. Que tenga formato dd/mm/aaaa
     * 3. Que el mes esté entre 1 y 12
     * 4. Que el día sea válido para el mes y año dados
     * 5. Consideraciones especiales para años bisiestos
     *
     * @param fechaTexto Fecha en formato de texto dd/mm/aaaa
     * @return true si el formato es válido
     * @throws ValidationException si la fecha no cumple con los requisitos
     */
    public static boolean validarFormatoFecha(String fechaTexto) throws ValidationException {
        // Verifica que la fecha no sea null o vacía
        if (fechaTexto == null || fechaTexto.trim().isEmpty()) {
            throw new ValidationException("Debe ingresar una fecha", "Fecha");
        }

        // Verifica que tenga el formato dd/mm/aaaa usando expresión regular
        // \\d{2} : Exactamente 2 dígitos
        // / : Barra diagonal
        // \\d{4} : Exactamente 4 dígitos
        if (!fechaTexto.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new ValidationException(
                    "Formato de fecha inválido.\nUse: dd/mm/aaaa (ejemplo: 15/03/2000)",
                    "Fecha");
        }

        // Divide la fecha en partes usando "/" como separador
        String[] partes = fechaTexto.split("/");

        // Convierte cada parte a número entero
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int anio = Integer.parseInt(partes[2]);

        // Valida que el mes esté entre 1 y 12
        if (mes < 1 || mes > 12) {
            throw new ValidationException(
                    "Mes inválido: " + mes + "\nEl mes debe estar entre 1 y 12",
                    "Fecha");
        }

        // Obtiene la cantidad de días del mes
        int diasDelMes = obtenerDiasDelMes(mes, anio);

        // Valida que el día sea válido para el mes
        if (dia < 1 || dia > diasDelMes) {
            // Obtiene el nombre del mes para el mensaje de error
            String nombreMes = obtenerNombreMes(mes);

            // Construye mensaje de error
            String mensaje = "Día inválido para " + nombreMes + " " + anio + "\n" +
                    nombreMes + " tiene " + diasDelMes + " días";

            // Mensaje especial para febrero 29 en años no bisiestos
            if (mes == 2 && dia == 29 && !esBisiesto(anio)) {
                mensaje += "\n\n" + anio + " NO es año bisiesto.\n" +
                        "Febrero solo tiene 28 días en años no bisiestos.";
            }

            throw new ValidationException(mensaje, "Fecha");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    /**
     * Obtiene la cantidad de días de un mes específico en un año dado
     *
     * @param mes Número del mes (1-12)
     * @param anio Año para considerar si es bisiesto
     * @return Cantidad de días del mes
     */
    public static int obtenerDiasDelMes(int mes, int anio) {
        // Evalúa el mes y retorna los días correspondientes
        switch (mes) {
            // Meses con 31 días
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;

            // Meses con 30 días
            case 4: case 6: case 9: case 11:
                return 30;

            // Febrero: 29 días si es bisiesto, 28 si no
            case 2:
                return esBisiesto(anio) ? 29 : 28;

            // Mes inválido
            default:
                return 0;
        }
    }

    /**
     * Determina si un año es bisiesto según las reglas:
     * - Divisible entre 400: SÍ es bisiesto
     * - Divisible entre 100: NO es bisiesto
     * - Divisible entre 4: SÍ es bisiesto
     * - Otros casos: NO es bisiesto
     *
     * @param anio Año a verificar
     * @return true si el año es bisiesto
     */
    public static boolean esBisiesto(int anio) {
        // Si es divisible entre 400, es bisiesto
        if (anio % 400 == 0) {
            return true;
        }

        // Si es divisible entre 100, NO es bisiesto
        if (anio % 100 == 0) {
            return false;
        }

        // Si es divisible entre 4, es bisiesto
        return anio % 4 == 0;
    }

    /**
     * Obtiene el nombre del mes en español
     *
     * @param mes Número del mes (1-12)
     * @return Nombre del mes o "Mes desconocido" si es inválido
     */
    public static String obtenerNombreMes(int mes) {
        // Array con los nombres de los meses
        // Índice 0 está vacío para que el índice coincida con el número del mes
        String[] meses = {
                "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        // Verifica que el mes esté en rango válido
        if (mes >= 1 && mes <= 12) {
            return meses[mes];
        }

        // Retorna texto por defecto para mes inválido
        return "Mes desconocido";
    }

    // ==================== VALIDACIÓN DE CONTRASEÑA ====================

    /**
     * Valida una contraseña verificando:
     * 1. Que no esté vacía
     * 2. Que tenga entre 8 y 14 caracteres
     * 3. Que contenga al menos una letra mayúscula
     *
     * @param password Contraseña a validar
     * @return true si la contraseña es válida
     * @throws ValidationException si la contraseña no cumple con los requisitos
     */
    public static boolean validarPassword(String password) throws ValidationException {
        // Verifica que la contraseña no sea null o vacía
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("La contraseña no puede estar vacía", "Password");
        }

        // Verifica que tenga al menos 8 caracteres
        if (password.length() < 8) {
            throw new ValidationException(
                    "La contraseña debe tener entre 8 y 14 caracteres",
                    "Password");
        }

        // Verifica que no exceda 14 caracteres
        if (password.length() > 14) {
            throw new ValidationException(
                    "La contraseña debe tener entre 8 y 14 caracteres",
                    "Password");
        }

        // Variable para verificar si hay al menos una mayúscula
        boolean tieneMayuscula = false;

        // Recorre cada carácter de la contraseña
        for (char c : password.toCharArray()) {
            // Verifica si el carácter es mayúscula
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
                break; // Sale del bucle si encuentra una mayúscula
            }
        }

        // Si no tiene mayúsculas, lanza excepción
        if (!tieneMayuscula) {
            throw new ValidationException(
                    "La contraseña debe contener al menos una letra mayúscula",
                    "Password");
        }

        // Si pasó todas las validaciones, retorna true
        return true;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Calcula la edad de una persona basándose en su fecha de nacimiento
     *
     * @param fechaNacimiento Fecha de nacimiento
     * @return Edad en años o 0 si la fecha es null
     */
    public static int calcularEdad(LocalDate fechaNacimiento) {
        // Si la fecha es null, retorna 0
        if (fechaNacimiento == null) {
            return 0;
        }

        // Calcula el período entre la fecha de nacimiento y hoy
        // y obtiene los años de diferencia
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}