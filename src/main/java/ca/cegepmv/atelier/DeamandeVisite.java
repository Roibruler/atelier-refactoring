package ca.cegepmv.atelier;

import java.time.LocalDate;

public record DemandeVisite(int idAnimal, LocalDate date, String type, boolean urgence,
                            String nomVeterinaire, String notes, boolean envoyerRappelEmail) {
}