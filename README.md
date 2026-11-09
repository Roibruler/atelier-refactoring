## Atelier — Réusinage (refactoring) et code smells

Petit projet Java autonome (Maven, JDK 21, JUnit 6) — aucune dépendance à Spring ni à une
base de données. `ServiceClinique` fonctionne correctement (tous les tests fournis passent),
mais elle est **volontairement truffée de plusieurs code smells** du catalogue vu en semaine 6
(Fowler). Votre travail : les repérer, verrouiller leur comportement actuel avec des tests, puis
les corriger un à la fois, par petites étapes toujours vertes.

⚠️ **Cet atelier n'est pas trivial.** Contrairement à un simple remplissage de `// TODO`, une
bonne partie du travail consiste à **écrire vous-mêmes** les tests manquants, sans qu'on vous
donne le code à l'avance — exactement comme dans une vraie base de code legacy.

### Structure

```
atelier-refactoring/
├── pom.xml
└── src/
    ├── main/java/ca/cegepmv/atelier/
    │   ├── Proprietaire.java               ← contient un "data clump" (rue/ville/codePostal)
    │   ├── Animal.java
    │   ├── Visite.java                     ← type de visite en String libre
    │   ├── Veterinaire.java
    │   └── ServiceClinique.java            ← la classe à réusiner (6 méthodes, 6 smells)
    └── test/java/ca/cegepmv/atelier/
        └── ServiceCliniqueTest.java        ← 4 tests fournis + 10 tests à écrire (TODO)
```

---

### Étape 0 — Confirmer l'état de départ

```bash
./mvnw test
```

Vous devriez obtenir **4 tests, 0 échec**. Notez ce nombre — c'est votre point de référence pour
la suite (voir les notes de cours de la semaine 6, section 2 : la suite de tests comme condition
préalable au réusinage).

---

### Partie 1 — Identifier les code smells

Ouvrez `ServiceClinique.java` (les commentaires Javadoc donnent des indices, mais lisez aussi le
code lui-même) et `Proprietaire.java`. Trouvez
**au moins six** *code smells* distincts, parmi : méthode trop longue, duplication de code,
liste de paramètres trop longue, obsession des primitifs, *feature envy*, *data clump*,
conditions répétées. Ne corrigez rien encore.

---

### Partie 2 — Écrire les tests de caractérisation manquants

`ServiceCliniqueTest.java` contient 10 emplacements marqués `// TODO N —` décrivant un
comportement à couvrir, **sans vous donner le code du test**. Pour chacun :

1. Lisez attentivement le code de la méthode concernée dans `ServiceClinique`.
2. Déduisez le résultat exact attendu pour le scénario décrit (ne devinez pas — calculez ou
   tracez le code à la main si nécessaire).
3. Écrivez le test en suivant le patron Arrange-Act-Assert, au même niveau de rigueur que les 4
   tests déjà fournis.

> ⚠️ Le but n'est PAS de tester le comportement que vous jugez « correct », mais celui que le
> code produit **réellement aujourd'hui** — c'est la définition même d'un test de
> caractérisation (semaine 6, section 2). Si un résultat vous semble étrange, testez-le quand
> même tel quel

Une fois les 10 tests écrits, relancez `./mvnw test` : vous devriez obtenir **15 tests, 0 échec**
avant de passer à la suite (le dernier point, sur `planifierVisite`, demande deux tests distincts
— rappel activé et désactivé — d'où 15 plutôt que 14). Ne continuez pas si un seul test échoue.

---

### Partie 3 — Appliquer le catalogue de réusinages

Appliquez, **un réusinage à la fois**, en relançant `./mvnw test` après chacun et en committant
dès que c'est vert (voir semaine 6, section 6) :

1. **Extract Method** sur `genererFacture` — séparez la validation, le calcul du sous-total, le
   calcul du rabais/de la pénalité, et le formatage, en méthodes privées distinctes.
2. **Éliminer la duplication** entre `genererFacture`, `estimerCoutAnnuel` et
   `calculerDureeRendezVous` — les trois font un aiguillage sur `visite.getType()` (la règle des
   trois s'applique). Solution minimale : une méthode privée partagée (ex.
   `tarifBasePour(String type)`). Pour aller plus loin (facultatif, plus difficile) :
   **Replace Conditional with Polymorphism** avec un type `TypeVisite` qui connaît lui-même son
   tarif et sa durée.
3. **Introduce Parameter Object** sur `planifierVisite` (7 paramètres) — regroupez-les dans un
   `record DemandeVisite`.
4. **Extract Class** sur `Proprietaire` — extrayez `rue`/`ville`/`codePostal` dans un
   `record Adresse`, et mettez à jour tous les appelants.
5. **Corriger le *Feature Envy*** de `genererRapportPourVeterinaire` — cette méthode s'intéresse
   presque exclusivement aux données de `Proprietaire` : déplacez la construction du bloc
   adresse/contact directement sur `Proprietaire` (ex. une méthode `resumeContact()`), et
   appelez-la depuis `ServiceClinique`.
6. **Guard Clauses** sur `calculerRemiseFidelite` — éliminez l'imbriquement à 4 niveaux avec des
   sorties anticipées, **sans changer aucun des 4 résultats possibles** (0.0, 0.05, 0.10, 0.15).

À la fin de chaque réusinage, vérifiez que le **nombre de tests et leurs résultats** restent
identiques à ceux notés à la fin de la Partie 2 — sinon, vous avez changé un comportement
observable, ce qui n'est plus un réusinage.

---

### Partie 4 — Valider avec le pipeline CI

Complétez les `TODO` du fichier `.github/workflows/compilation_maven.yml` (voir les notes de
cours de la semaine 5 — GitHub Actions) pour que le pipeline exécute réellement votre suite de
tests, pas seulement la compilation. Poussez votre travail et confirmez que le pipeline est
**vert**.

---

### Partie 5 (bonus, plus difficile) — Couverture et JaCoCo

1. Ajoutez le plugin JaCoCo à votre `pom.xml` (voir semaine 5) et générez le rapport
   (`target/site/jacoco/index.html`) **avant** d'avoir écrit vos tests de la Partie 2 — notez la
   couverture de `ServiceClinique`.
2. Régénérez le rapport une fois vos 10 tests écrits — la couverture devrait avoir nettement
   augmenté.
3. Publiez ce rapport comme artefact dans votre pipeline (TODO 2-3 du workflow).
4. Identifiez, dans le rapport, une branche encore non couverte (s'il en reste une) et discutez
   en équipe si elle vaut la peine d'être testée avant d'aller plus loin.

---
