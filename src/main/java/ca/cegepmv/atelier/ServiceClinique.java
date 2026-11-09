package ca.cegepmv.atelier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Atelier — Réusinage (refactoring)
 * =================================
 *
 * Cette classe gère la facturation, la planification et les rapports de la clinique.
 * Elle fonctionne correctement — tous les tests fournis passent — mais elle est
 * volontairement truffée de plusieurs "code smells" du catalogue vu en semaine 6 :
 *
 *  - {@link #genererFacture} : méthode trop longue (Long Method) qui mélange
 *    validation, calcul et formatage.
 *  - {@link #genererFacture}, {@link #estimerCoutAnnuel} et {@link #calculerDureeRendezVous} :
 *    aiguillage répété sur {@code visite.getType()} (Repeated Conditionals) — la même
 *    logique de tarif de base est dupliquée (Duplicated Code) entre les deux premières.
 *  - {@link #planifierVisite} : liste de paramètres trop longue (Long Parameter List).
 *  - {@link #genererRapportPourVeterinaire} : s'intéresse presque exclusivement aux
 *    données de {@link Proprietaire} plutôt qu'aux siennes (Feature Envy).
 *  - {@link #calculerRemiseFidelite} : imbriquement profond de conditions qui gagnerait
 *    à utiliser des clauses de garde (Guard Clauses).
 *  - Le "data clump" rue/ville/codePostal vit dans {@link Proprietaire}, pas ici, mais
 *    {@link #genererRapportPourVeterinaire} en dépend directement.
 *
 * ⚠️ RAPPEL DU COURS : ne réusinez jamais du code non couvert par un test. Certaines
 * méthodes ci-dessous n'ont AUCUN test dans {@code ServiceCliniqueTest} — c'est
 * intentionnel : votre première tâche (voir README, Partie 2) est d'écrire des tests
 * de caractérisation qui documentent leur comportement ACTUEL, avant d'y toucher.
 *
 * ⚠️ PIÈGE INTENTIONNEL : un type de visite inconnu (mal orthographié, par exemple)
 * retombe silencieusement sur un tarif de 0.0$ et une durée de 30 minutes, sans jamais
 * lancer d'exception. Cela ressemble à un bogue — ce n'en est pas un que vous devez
 * corriger cette semaine. Réusiner ne change JAMAIS le comportement observable, même
 * un comportement qui semble discutable. 
 */
public class ServiceClinique {

    private final List<String> journalRappels = new ArrayList<>();

    /**
     * Génère le texte de facture pour un propriétaire, à partir de la liste de visites
     * à facturer.
     *
     * Règles métier actuelles (à préserver EXACTEMENT après réusinage) :
     * - Tarif de base : CONSULTATION = 60$, VACCIN = 35$, CHIRURGIE = 250$, tout autre
     *   type = 0$ (silencieusement).
     * - Une visite urgente coûte 1.5x son tarif de base.
     * - Un propriétaire avec PLUS D'UN animal (strictement) obtient 10% de rabais sur
     *   le sous-total.
     * - Un solde de compte négatif ajoute une pénalité de 5% sur le total (après rabais).
     */
    public String genererFacture(Proprietaire proprietaire, List<Visite> visites) {
        validerDemandeFacture(proprietaire, visites);
        double sousTotal = calculerSousTotal(visites);
        double remise = tauxRemiseFacture(proprietaire);
        double total = appliquerPenaliteSolde(proprietaire, sousTotal - (sousTotal * remise));
        return formaterFacture(proprietaire, sousTotal, remise, total);
    }

    private void validerDemandeFacture(Proprietaire proprietaire, List<Visite> visites) {
        if (proprietaire == null || visites == null || visites.isEmpty()) {
            throw new IllegalArgumentException("Le propriétaire et au moins une visite sont requis");
        }
    }

    private double calculerSousTotal(List<Visite> visites) {
        double sousTotal = 0.0;
        for (Visite visite : visites) {
            double tarifBase = visite.getTypeVisite().tarifBase();
            double tarif = visite.isUrgence() ? tarifBase * 1.5 : tarifBase;
            sousTotal += tarif;
        }
        return sousTotal;
    }

    private double tauxRemiseFacture(Proprietaire proprietaire) {
        return proprietaire.getAnimaux().size() > 1 ? 0.10 : 0.0;
    }

    private double appliquerPenaliteSolde(Proprietaire proprietaire, double total) {
        if (proprietaire.getSolde() < 0) {
            return total + (total * 0.05);
        }
        return total;
    }

    private String formaterFacture(Proprietaire proprietaire, double sousTotal, double remise, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("Facture pour ").append(proprietaire.getPrenom()).append(" ").append(proprietaire.getNom());
        sb.append(" — Sous-total: $").append(String.format(Locale.US, "%.2f", sousTotal));
        if (remise > 0) {
            sb.append(" — Remise fidélité: -").append((int) Math.round(remise * 100)).append("%");
        }
        sb.append(" — Total: $").append(String.format(Locale.US, "%.2f", total));
        return sb.toString();
    }

    /**
     * Projette un coût annuel approximatif à partir d'un échantillon de visites d'un
     * mois type (on multiplie simplement par 12 — projection volontairement naïve).
     */
    public double estimerCoutAnnuel(List<Visite> visites) {
        if (visites == null || visites.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Visite visite : visites) {
            double tarifBase = visite.getTypeVisite().tarifBase();
            total += tarifBase;
        }
        return total * 12;
    }

    /**
     * Retourne la durée estimée (en minutes) d'un rendez-vous selon le type de visite.
     */
    public int calculerDureeRendezVous(Visite visite) {
        return visite.getTypeVisite().dureeMinutes();
    }

    /**
     * Construit un rapport texte à l'intention d'un vétérinaire, résumant les
     * coordonnées du propriétaire et le nombre de visites.
     */
    public String genererRapportPourVeterinaire(Veterinaire veterinaire, Proprietaire proprietaire,
                                                 List<Visite> visites) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport du Dr ").append(veterinaire.getNom())
          .append(" (").append(veterinaire.getSpecialite()).append(")\n");
        sb.append(proprietaire.resumeContact()).append("\n");
        sb.append("Nombre de visites: ").append(visites.size());
        return sb.toString();
    }

    /**
     * Planifie une nouvelle visite pour un animal.
     */
    public Visite planifierVisite(DemandeVisite demande) {
        if (demande.type() == null || demande.type().isBlank()) {
            throw new IllegalArgumentException("Le type de visite est requis");
        }
        Visite visite = new Visite(demande.date(), demande.type(), demande.urgence(),
                demande.nomVeterinaire(), demande.notes());
        if (demande.envoyerRappelEmail()) {
            journalRappels.add("Rappel envoyé pour la visite du " + demande.date()
                    + " (animal #" + demande.idAnimal() + ")");
        }
        return visite;
    }

    /**
     * Calcule le taux de remise de fidélité applicable à un propriétaire.
     *
     * Règles métier actuelles (à préserver EXACTEMENT après réusinage) :
     * - Propriétaire null → 0.0
     * - Solde négatif → 0.0 (aucune remise si le compte n'est pas à jour)
     * - Plus d'un animal ET client depuis 3 ans ou plus → 0.15
     * - Plus d'un animal ET client depuis moins de 3 ans → 0.10
     * - Un seul animal ET client depuis 5 ans ou plus → 0.05
     * - Un seul animal ET client depuis moins de 5 ans → 0.0
     */
    public double calculerRemiseFidelite(Proprietaire proprietaire) {
        if (proprietaire == null || proprietaire.getSolde() < 0) {
            return 0.0;
        }
        if (proprietaire.getAnimaux().size() > 1) {
            return proprietaire.getAnneesClient() >= 3 ? 0.15 : 0.10;
        }
        return proprietaire.getAnneesClient() >= 5 ? 0.05 : 0.0;
    }

    public List<String> getJournalRappels() {
        return journalRappels;
    }
}