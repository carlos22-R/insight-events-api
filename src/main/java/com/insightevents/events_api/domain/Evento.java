package com.insightevents.events_api.domain;

import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento: entidad central del sistema (accidente, reporte de clima, etc.).
 * Se clasifica en una Categoria, tiene prioridad y un estado que evoluciona.
 * Usa borrado logico (campo {@code activo}) por ser un sistema de auditoria.
 */
@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo legible autogenerado (ej. EVT-000123). Unico. */
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String titulo;

    /** Texto largo -> se mapea a TEXT en Postgres. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    /** Enum guardado como texto (STRING) en vez de numero: legible y estable. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEvento estado;

    @Column(length = 150)
    private String fuente;

    /**
     * Categoria a la que pertenece. LAZY: no se carga hasta usarla.
     * FK con ON DELETE RESTRICT (definida en la migracion): no se puede
     * borrar una categoria que tenga eventos.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /** Borrado logico: true = visible, false = eliminado. */
    @Column(nullable = false)
    private boolean activo = true;
}
