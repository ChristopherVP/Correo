package uce.edu.ec.correo.util;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ControlIntentos {

    private record Estado(int fallos, Instant bloqueadoHasta) {
    }

    private final int maximoFallos;
    private final Duration duracionBloqueo;
    private final Map<String, Estado> estados = new HashMap<>();

    public ControlIntentos(int maximoFallos, Duration duracionBloqueo) {
        this.maximoFallos = maximoFallos;
        this.duracionBloqueo = duracionBloqueo;
    }

    public synchronized boolean estaBloqueado(String clave) {
        return segundosRestantes(clave) > 0;
    }

    public synchronized long segundosRestantes(String clave) {
        Estado estado = estados.get(clave);
        if (estado == null || estado.bloqueadoHasta() == null) {
            return 0;
        }
        long restantes = Duration.between(Instant.now(), estado.bloqueadoHasta()).toSeconds();
        if (restantes <= 0) {
            estados.remove(clave);
            return 0;
        }
        return restantes;
    }

    public synchronized void registrarFallo(String clave) {
        Estado actual = estados.getOrDefault(clave, new Estado(0, null));
        int fallos = actual.fallos() + 1;
        Instant bloqueo = fallos >= maximoFallos ? Instant.now().plus(duracionBloqueo) : null;
        estados.put(clave, new Estado(bloqueo == null ? fallos : 0, bloqueo));
    }

    public synchronized void reiniciar(String clave) {
        estados.remove(clave);
    }
}
