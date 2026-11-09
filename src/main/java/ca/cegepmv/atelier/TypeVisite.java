package ca.cegepmv.atelier;

public enum TypeVisite {
    CONSULTATION(60.0, 20),
    VACCIN(35.0, 10),
    CHIRURGIE(250.0, 90),
    INCONNU(0.0, 30);

    private final double tarifBase;
    private final int dureeMinutes;

    TypeVisite(double tarifBase, int dureeMinutes) {
        this.tarifBase = tarifBase;
        this.dureeMinutes = dureeMinutes;
    }

    public static TypeVisite depuis(String code) {
        for (TypeVisite type : values()) {
            if (code.equals(type.name())) {
                return type;
            }
        }
        return INCONNU;
    }

    public double tarifBase() {
        return tarifBase;
    }

    public int dureeMinutes() {
        return dureeMinutes;
    }
}