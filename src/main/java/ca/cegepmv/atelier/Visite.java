package ca.cegepmv.atelier;

import java.time.LocalDate;

/**
 * Une visite d'un animal à la clinique.
 *
 * {@code type} est volontairement une chaîne libre ("CONSULTATION", "VACCIN", "CHIRURGIE")
 * plutôt qu'un type dédié — c'est le point de départ du réusinage "Replace Conditional with
 * Polymorphism" proposé dans l'atelier.
 */
public class Visite {

    private final LocalDate date;
    private final String type;
    private final boolean urgence;
    private final String nomVeterinaire;
    private final String notes;

    public Visite(LocalDate date, String type, boolean urgence, String nomVeterinaire, String notes) {
        this.date = date;
        this.type = type;
        this.urgence = urgence;
        this.nomVeterinaire = nomVeterinaire;
        this.notes = notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public boolean isUrgence() {
        return urgence;
    }

    public String getNomVeterinaire() {
        return nomVeterinaire;
    }

    public String getNotes() {
        return notes;
    }
}
