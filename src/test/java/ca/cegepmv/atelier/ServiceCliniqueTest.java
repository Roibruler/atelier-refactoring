package ca.cegepmv.atelier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Atelier — Réusinage (refactoring)
 * =================================
 *
 * Cette suite de tests est VOLONTAIREMENT INCOMPLÈTE. Les 4 tests ci-dessous passent
 * déjà et couvrent seulement une partie du comportement de {@link ServiceClinique}.
 *
 * Avant de réusiner quoi que ce soit (voir README, Partie 3), complétez cette classe
 * (Partie 2) avec des tests de CARACTÉRISATION qui documentent le comportement ACTUEL
 * du code (pas celui que vous jugeriez "idéal") pour :
 *
 *   1. genererFacture() — le cas VACCIN, le cas où le rabais de fidélité s'applique
 *      (2 animaux ou plus), et le cas de pénalité de solde négatif.
 *   2. calculerRemiseFidelite() — les 3 branches non couvertes (0.15, 0.10, 0.05).
 *   3. estimerCoutAnnuel() — au moins un cas avec plusieurs visites de types différents.
 *   4. calculerDureeRendezVous() — au moins les 4 types (CONSULTATION, VACCIN,
 *      CHIRURGIE, type inconnu).
 *   5. genererRapportPourVeterinaire() — au moins un cas normal.
 *   6. planifierVisite() — le cas avec rappel par courriel activé ET désactivé
 *      (vérifiez le contenu de getJournalRappels()).
 *
 * N'ajoutez PAS de test pour un comportement que vous jugez incorrect (ex. le tarif
 * silencieux à 0$ pour un type inconnu) sans d'abord relire l'avertissement dans
 * ServiceClinique — ce comportement est un piège intentionnel de l'atelier.
 */
class ServiceCliniqueTest {

    private ServiceClinique service;
    private Proprietaire julie;

    @BeforeEach
    void setUp() {
        service = new ServiceClinique();
        // Arrange commun : propriétaire avec un seul animal, solde à jour, cliente depuis 1 an
                julie = new Proprietaire("Julie", "Tremblay",
                new Adresse("123 rue des Lilas", "Longueuil", "J4G 1A1"), "julie.tremblay@example.com", "450-555-0100", 0.0, 1);
        julie.ajouterAnimal(new Animal(1, "Rex", "CHIEN", LocalDate.of(2020, 3, 15)));
    }

    // ------------------------------------------------------------------
    // Tests fournis (déjà complets, à titre d'exemple — NE PAS MODIFIER)
    // ------------------------------------------------------------------

    @Test
    void genererFacture_uneConsultationSansUrgence_calculeLeBonSousTotal() {
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(julie, List.of(consultation));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $60.00 — Total: $60.00", facture);
    }

    @Test
    void genererFacture_chirurgieUrgente_appliqueLeMultiplicateurDurgence() {
        // Arrange
        Visite chirurgieUrgente = new Visite(LocalDate.now(), "CHIRURGIE", true, "Dr Gagnon", "Urgence");

        // Act
        String facture = service.genererFacture(julie, List.of(chirurgieUrgente));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $375.00 — Total: $375.00", facture);
    }

    @Test
    void genererFacture_sansVisite_lanceUneException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.genererFacture(julie, List.of()));
    }

    @Test
    void calculerRemiseFidelite_unSeulAnimalEtPeuAnciennete_retourneZero() {
        // Act
        double remise = service.calculerRemiseFidelite(julie);

        // Assert
        assertEquals(0.0, remise);
    }

    // ------------------------------------------------------------------
    // À COMPLÉTER — voir README, Partie 2 (tests de caractérisation)
    // ------------------------------------------------------------------

        @Test
    void genererFacture_unVaccinSansUrgence_factureLeTarifDeBase() {
        Visite vaccin = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Rappel annuel");

        String facture = service.genererFacture(julie, List.of(vaccin));

        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Total: $35.00", facture);
    }

    @Test
    void genererFacture_proprietaireAvecDeuxAnimaux_appliqueEtAfficheLaRemiseDeDixPourcent() {
        julie.ajouterAnimal(new Animal(2, "Mimi", "CHAT", LocalDate.of(2021, 6, 1)));
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Visite de routine");

        String facture = service.genererFacture(julie, List.of(consultation));

        assertEquals("Facture pour Julie Tremblay — Sous-total: $60.00 — Remise fidélité: -10% — Total: $54.00",
                facture);
    }

    @Test
    void genererFacture_soldeNegatif_ajouteUnePenaliteDeCinqPourcent() {
        Proprietaire marc = new Proprietaire("Marc", "Lavoie",
                new Adresse("45 boul. Taschereau", "Brossard", "J4Z 2B2"), "marc.lavoie@example.com", "450-555-0200", -50.0, 1);
        marc.ajouterAnimal(new Animal(3, "Bijou", "CHIEN", LocalDate.of(2019, 1, 10)));
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Visite de routine");

        String facture = service.genererFacture(marc, List.of(consultation));

        assertEquals("Facture pour Marc Lavoie — Sous-total: $60.00 — Total: $63.00", facture);
    }

    @Test
    void calculerRemiseFidelite_plusieursAnimauxEtTroisAnsOuPlus_retourneQuinzePourcent() {
        Proprietaire proprietaire = proprietaireAvec(2, 3);

        double remise = service.calculerRemiseFidelite(proprietaire);

        assertEquals(0.15, remise);
    }

    @Test
    void calculerRemiseFidelite_plusieursAnimauxEtMoinsDeTroisAns_retourneDixPourcent() {
        Proprietaire proprietaire = proprietaireAvec(2, 2);

        double remise = service.calculerRemiseFidelite(proprietaire);

        assertEquals(0.10, remise);
    }

    @Test
    void calculerRemiseFidelite_unSeulAnimalEtCinqAnsOuPlus_retourneCinqPourcent() {
        Proprietaire proprietaire = proprietaireAvec(1, 5);

        double remise = service.calculerRemiseFidelite(proprietaire);

        assertEquals(0.05, remise);
    }

    @Test
    void estimerCoutAnnuel_plusieursTypesDeVisites_multiplieLesTarifsDeBaseParDouzeSansMajorationDurgence() {
        List<Visite> visites = List.of(
                new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", ""),
                new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", ""),
                new Visite(LocalDate.now(), "CHIRURGIE", true, "Dr Gagnon", ""));

        double coutAnnuel = service.estimerCoutAnnuel(visites);

        assertEquals(4140.0, coutAnnuel);
    }

    @Test
    void calculerDureeRendezVous_chaqueType_retourneLaDureeActuelleEtTrenteMinutesPourUnTypeInconnu() {
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "");
        Visite vaccin = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "");
        Visite chirurgie = new Visite(LocalDate.now(), "CHIRURGIE", false, "Dr Gagnon", "");
        Visite inconnue = new Visite(LocalDate.now(), "CHIRURGEI", false, "Dr Gagnon", "");

        assertEquals(20, service.calculerDureeRendezVous(consultation));
        assertEquals(10, service.calculerDureeRendezVous(vaccin));
        assertEquals(90, service.calculerDureeRendezVous(chirurgie));
        assertEquals(30, service.calculerDureeRendezVous(inconnue));
    }

    @Test
    void genererRapportPourVeterinaire_casNormal_resumeLesCoordonneesEtLeNombreDeVisites() {
        Veterinaire gagnon = new Veterinaire("Gagnon", "Chirurgie");
        List<Visite> visites = List.of(
                new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", ""),
                new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", ""));

        String rapport = service.genererRapportPourVeterinaire(gagnon, julie, visites);

        assertEquals("Rapport du Dr Gagnon (Chirurgie)\n"
                + "Client: Julie Tremblay\n"
                + "Adresse: 123 rue des Lilas, Longueuil J4G 1A1\n"
                + "Contact: julie.tremblay@example.com / 450-555-0100\n"
                + "Nombre de visites: 2", rapport);
    }

    @Test
    void planifierVisite_avecRappelEmail_creeLaVisiteEtAjouteUnRappelAuJournal() {
        LocalDate date = LocalDate.of(2026, 3, 10);

        Visite visite = service.planifierVisite(
                new DemandeVisite(1, date, "VACCIN", false, "Dr Gagnon", "Rappel annuel", true));

        assertEquals(date, visite.getDate());
        assertEquals("VACCIN", visite.getType());
        assertFalse(visite.isUrgence());
        assertEquals("Dr Gagnon", visite.getNomVeterinaire());
        assertEquals("Rappel annuel", visite.getNotes());
        assertEquals(List.of("Rappel envoyé pour la visite du 2026-03-10 (animal #1)"), service.getJournalRappels());
    }

    @Test
    void planifierVisite_sansRappelEmail_creeLaVisiteSansToucherAuJournal() {
        LocalDate date = LocalDate.of(2026, 3, 10);

        Visite visite = service.planifierVisite(
                new DemandeVisite(1, date, "CHIRURGIE", true, "Dr Gagnon", "Fracture", false));

        assertEquals("CHIRURGIE", visite.getType());
        assertTrue(visite.isUrgence());
        assertTrue(service.getJournalRappels().isEmpty());
    }

    private Proprietaire proprietaireAvec(int nombreAnimaux, int anneesClient) {
        Proprietaire proprietaire = new Proprietaire("Luc", "Bergeron",
                new Adresse("8 rue Principale", "Chambly", "J3L 1A1"), "luc.bergeron@example.com", "450-555-0300", 0.0, anneesClient);
        for (int i = 1; i <= nombreAnimaux; i++) {
            proprietaire.ajouterAnimal(new Animal(i, "Animal" + i, "CHIEN", LocalDate.of(2020, 1, i)));
        }
        return proprietaire;
    }
}