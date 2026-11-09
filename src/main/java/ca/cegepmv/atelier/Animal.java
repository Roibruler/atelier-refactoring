package ca.cegepmv.atelier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Un animal appartenant à un {@link Proprietaire}, avec son historique de visites.
 */
public class Animal {

    private final int id;
    private final String nom;
    private final String espece;
    private final LocalDate dateNaissance;
    private final List<Visite> visites = new ArrayList<>();

    public Animal(int id, String nom, String espece, LocalDate dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.espece = espece;
        this.dateNaissance = dateNaissance;
    }

    public void ajouterVisite(Visite visite) {
        visites.add(visite);
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEspece() {
        return espece;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public List<Visite> getVisites() {
        return visites;
    }
}
