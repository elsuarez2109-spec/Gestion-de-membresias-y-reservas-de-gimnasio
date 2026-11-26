package com.gimnasio.facturacion;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

public class MembresiaService {

    private record Membresia(Plan plan, LocalDateTime vence) {}

    private final Map<String, Membresia> membresias = new ConcurrentHashMap<>();
    private final Map<String, UnaryOperator<Plan>> beneficios = new LinkedHashMap<>();
    private final FacturacionService facturacion;
    private final Clock reloj;

    public MembresiaService(FacturacionService facturacion, Clock reloj) {
        if (facturacion == null || reloj == null) {
            throw new IllegalArgumentException("Dependencias obligatorias");
        }
        this.facturacion = facturacion;
        this.reloj = reloj;
        registrarBeneficio("SAUNA", SaunaDecorator::new);
        registrarBeneficio("SPA", SpaDecorator::new);
        registrarBeneficio("ENTRENADOR", EntrenadorPersonalDecorator::new);
        registrarBeneficio("INVITADO", InvitadoDecorator::new);
    }

    public void registrarBeneficio(String nombre, UnaryOperator<Plan> decorador) {
        beneficios.put(nombre.trim().toUpperCase(), decorador);
    }

    public List<String> beneficiosDisponibles() {
        return new ArrayList<>(beneficios.keySet());
    }
    public ResultadoCobro asignar(String clienteId, String tipoPlan, int miembros,
                                  List<String> nombresBeneficios, int meses) {
        Plan plan = PlanFactory.crear(tipoPlan, miembros);
        for (String nombre : nombresBeneficios) {
            UnaryOperator<Plan> decorador = beneficios.get(nombre.trim().toUpperCase());
            if (decorador == null) {
                throw new IllegalArgumentException("Beneficio desconocido: " + nombre);
            }
            plan = decorador.apply(plan);
        }
        ResultadoCobro factura = facturacion.calcular(new ContextoCobro(plan, meses));
        membresias.put(clienteId,
                new Membresia(plan, LocalDateTime.now(reloj).plusMonths(meses)));
        return factura;
    }

    public boolean tieneMembresiaVigente(String clienteId) {
        Membresia m = membresias.get(clienteId);
        return m != null && LocalDateTime.now(reloj).isBefore(m.vence());
    }

    public Optional<Plan> planDe(String clienteId) {
        Membresia m = membresias.get(clienteId);
        return m == null ? Optional.empty() : Optional.of(m.plan());
    }
}