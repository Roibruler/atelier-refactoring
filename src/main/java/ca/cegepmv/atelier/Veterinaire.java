package ca.cegepmv.atelier;

/**
 * Un membre du personnel vétérinaire de la clinique.
 */
public class Veterinaire {

    private final String nom;
    private final String specialite;

    public Veterinaire(String nom, String specialite) {
        this.nom = nom;
        this.specialite = specialite;
    }

    public String getNom() {
        return nom;
    }

    public String getSpecialite() {
        return specialite;
    }
}
