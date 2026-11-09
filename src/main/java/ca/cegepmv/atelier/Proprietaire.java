package ca.cegepmv.atelier;

import java.util.ArrayList;
import java.util.List;

/**
 * Propriétaire d'un ou plusieurs animaux, cliente ou client de la clinique.
 */
public class Proprietaire {

    private final String prenom;
    private final String nom;
    private final Adresse adresse;
    private final String email;
    private final String telephone;
    private final double solde;
    private final int anneesClient;
    private final List<Animal> animaux = new ArrayList<>();

    public Proprietaire(String prenom, String nom, Adresse adresse,
                         String email, String telephone, double solde, int anneesClient) {
        this.prenom = prenom;
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
        this.solde = solde;
        this.anneesClient = anneesClient;
    }

    public String resumeContact() {
        return "Client: " + prenom + " " + nom + "\n"
                + "Adresse: " + adresse.rue() + ", " + adresse.ville() + " " + adresse.codePostal() + "\n"
                + "Contact: " + email + " / " + telephone;
    }

    public void ajouterAnimal(Animal animal) {
        animaux.add(animal);
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public double getSolde() {
        return solde;
    }

    public int getAnneesClient() {
        return anneesClient;
    }

    public List<Animal> getAnimaux() {
        return animaux;
    }
}