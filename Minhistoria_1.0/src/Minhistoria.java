import extensions.File;
import extensions.CSVFile;

public class Minhistoria extends Program {

    final String NEWLINE = "\n";
    final String NOM_REPERTOIRE = "ressources/jeu";
    final int IDX_QUESTION = 0;
    final int IDX_REPONSE = 1;

    //Affiche le menu principal du jeu.
    void afficherMenuPrincipal () {
        clearScreen();
        File menuPrincipal = newFile("ressources/menus/menu_principal.txt");
        while (ready(menuPrincipal)) {
            println(readLine(menuPrincipal));
        }
        String choixUtilisateur = "";
        do {
            choixUtilisateur = readString();
            if (equals(choixUtilisateur,"1")) {
                clearScreen();
                return;
            } else if (equals(choixUtilisateur,"2")) {
                afficherInstructions();
            } else if (equals(choixUtilisateur,"3")) {
                afficherScores();
            } else if (equals(choixUtilisateur,"4")) {
                println("A bientôt !");
                System.exit(0);
            } else {
                println("Saisie incorrecte");
            }
        } while (!equals(choixUtilisateur,"1") && !equals(choixUtilisateur,"2") && !equals(choixUtilisateur,"3") && !equals(choixUtilisateur,"4"));
    }

    //Affiche le menu des instructions pour le jeu
    void afficherInstructions() {
        clearScreen();
        File menuInstructions = newFile("ressources/menus/instructions.txt");
        while (ready(menuInstructions)) {
            println(readLine(menuInstructions));
        }
        String choixUtilisateur = "";
        do {
            choixUtilisateur = readString();
            if (equals(choixUtilisateur,"1")) {
                afficherMenuPrincipal();   
            } else {
                println("Saisie incorrecte");
            }   
        } while (!equals(choixUtilisateur,"1"));
    }

    //Affiche le menu des derniers scores enregistrés
    void afficherScores() {
        clearScreen();
        File menuScoreHaut = newFile("ressources/menus/Scores_Haut.txt");
        while (ready(menuScoreHaut)) {
            println(readLine(menuScoreHaut));
        }
        CSVFile fichier = loadCSV("ressources/scores.csv");
        // Si le fichier est vide, affiche un message indiquant qu'aucun score n'est enregistré
        if (rowCount(fichier) == 0) {
            println("| Aucun score enregistré                                              |" );
        // Si le fichier contient plus de 5 scores, affiche les 5 derniers scores
        } else if (rowCount(fichier) > 5){
            for (int indice = rowCount(fichier)-5; indice < rowCount(fichier); indice = indice + 1) {
                println("| "+getCell(fichier,indice,0)+" | "+getCell(fichier,indice,1)+" | "+getCell(fichier,indice,2)+" | "+getCell(fichier,indice,3)+" | "+getCell(fichier,indice,4));            
            }
        // Si le fichier contient moins de 5 scores, affiche tous les scores
        } else {
            for (int indice = 0; indice < rowCount(fichier); indice = indice + 1) {
                println("| "+getCell(fichier,indice,0)+" | "+getCell(fichier,indice,1)+" | "+getCell(fichier,indice,2)+" | "+getCell(fichier,indice,3)+" | "+getCell(fichier,indice,4));
            }
        }
        File menuScoreBas = newFile("ressources/menus/Scores_Bas.txt");
        while (ready(menuScoreBas)) {
            println(readLine(menuScoreBas));
        }
        String choixUtilisateur = "";
        do {
            choixUtilisateur = readString();
            if (equals(choixUtilisateur,"1")) {
                afficherMenuPrincipal();
            } else {
                println("Saisie incorrecte");
            }   
        } while (!equals(choixUtilisateur,"1" ));
    }

    // Permet de convertir une chaine de caractères en entier
    int conversionStringEnInt(String chaine) {
        int result = 0;
        for (int i = 0; i < length(chaine); i++) {
            char c = charAt(chaine, i);
            result = result * 10 + (c - '0');
        }
        return result;
    }

    void testConversionStringEnInt() {
        assertEquals(123, conversionStringEnInt("123"));
        assertEquals(0, conversionStringEnInt("0"));
        assertEquals(1, conversionStringEnInt("1"));
    }

    // Permet de choisir le fichier CSV correspondant à la période qui va être utilisée pour la partie
    String choixPeriode (String nomRepertoire){
        clearScreen();
        String[] fichiers = getAllFilesFromDirectory(nomRepertoire);
        println("Périodes historiques : " + NEWLINE);
        String categorie = "";
        for (int indice = 0; indice < length(fichiers); indice = indice + 1) {
            categorie = substring(fichiers[indice],0, length(fichiers[indice])-4); // Permet de supprimer l'extension .csv du nom du fichier
            print((indice+1)+". "+categorie+" | ");
        }
        println(NEWLINE);
        int numeroFichier = 0;
        do {
            println("Quel période souhaitez vous réviser ?");
            String choix = readString();
            if (estNumerique(choix)) {
                numeroFichier = conversionStringEnInt(choix);
            } else {
                println("Saisie invalide...");
            }
        } while (numeroFichier < 1 || numeroFichier > length(fichiers));
        String periodeChoisie = fichiers[numeroFichier-1];
        categorie = substring(periodeChoisie, 0, length(periodeChoisie)-4);
        println(NEWLINE + "Vous avez choisi " + categorie);
        return periodeChoisie;
    }

    //Permet de créer un nouvelle question et d'enregistrer sa réponse à partir des données passées en paramètre
    Questions newQuestions(String question, String[] reponses) {
        Questions questions = new Questions();
        questions.question = question;
        questions.reponses = reponses;
        return questions;
    }

    void testNewQuestions() {
        Questions questions = newQuestions("question", new String[]{"reponse1", "reponse2", "reponse3"});
        assertEquals("question", questions.question);
        assertEquals("reponse1", questions.reponses[0]);
        assertEquals("reponse2", questions.reponses[1]);
        assertEquals("reponse3", questions.reponses[2]);
    }

    // Charge les questions et réponses présentes dans le fichier CSV et retourne un tableau qui les contient
    Questions[] load(String nomFichier) {
        CSVFile fichier = loadCSV(nomFichier);
        Questions[] questions = new Questions[rowCount(fichier)];
        int nbReponses = columnCount(fichier) - 1;
        for (int indice = 0; indice < length(questions); indice = indice + 1) {
            String question = getCell(fichier,indice, IDX_QUESTION);
            String[] reponses = new String[nbReponses];
            for (int i = 0; i < nbReponses; i++) {
                reponses[i] = getCell(fichier, indice, IDX_REPONSE + i);
            }
            questions[indice] = newQuestions(question, reponses);
        }
        return questions;
    }

    void testLoad() {
        Questions[] questions = load("ressources/jeu/moyen/Histoire_de_France.csv");
        assertEquals("Quelle bataille fût remportée par la France en 1515 ?", questions[8].question);
        assertEquals("Marignan", questions[8].reponses[0]);
        assertEquals("la bataille de Marignan", questions[8].reponses[1]);
        assertEquals("bataille de Marignan", questions[8].reponses[2]);
    }

    // Permet d'afficher toutes les questions d'une période donnée, utilisé à des fins de déboguage
    void afficherQuestions (Questions[] tab) {
        for (int indice = 0; indice < length(tab); indice = indice + 1 ) {
            println(tab[indice].question+" "+tab[indice].reponses[0]);
        }
    }

    boolean estNumerique (String chaine) {
        for (int indice = 0; indice < length(chaine); indice = indice + 1) {
            if (charAt(chaine,indice) < '0' || charAt(chaine,indice) > '9') {
                return false;
            }
        }
        return true;
    }

    void testEstNumerique() {
        assertEquals(true, estNumerique("123"));
        assertEquals(false, estNumerique("abc"));
        assertEquals(false, estNumerique("123abc"));
    }

    // Permet de sauvegarder le pseudo et le score de l'utilisateur dans un fichier CSV contenant les scores de toutes les precedentes parties
    void save (String username, int pourcentage, String mode, String periode, String difficulte, String destination) {
        CSVFile fichier = loadCSV("ressources/scores.csv");
        String[][] scores = new String[rowCount(fichier)+1][5];
        String points = "";
        for (int indice = 0; indice < length(scores,1); indice = indice + 1) {
            if (indice < length(scores,1)-1) {
                scores[indice][0] = getCell(fichier, indice, 0); // Pseudo
                scores[indice][1] = getCell(fichier, indice,1); // Mode
                scores[indice][2] = getCell(fichier, indice,2); // Période
                scores[indice][3] = getCell(fichier, indice,3); // Difficulté
                scores[indice][4] = points + getCell(fichier, indice,4); // Score
            } else {
                scores[indice][0] = username;
                scores[indice][1] = mode;
                scores[indice][2] = periode;
                scores[indice][3] = difficulte;
                scores[indice][4] = pourcentage+"%";
            }
        }
        saveCSV(scores, destination);
    }

    // Pose la question passé en paramètre et retourne un boolean qui indique si la réponse est correcte ou non. 
    boolean poserQuestion(Questions question) {
        println("Appuyez sur la touche 'q' pour quitter le jeu");
        println(question.question);
        String reponseUser = readString();
        reponseUser = toLowerCase(reponseUser);
        if (equals(reponseUser, "q")) {
            println("A une prochaine fois");
            System.exit(0);
        }
        else {
            for (int indice = 0; indice < length(question.reponses); indice = indice + 1) {
                if (equals(reponseUser, toLowerCase(question.reponses[indice]))) {
                    println("Bravo !");
                    return true;
                }
            }
            println("Dommage, les réponses possibles étaient :");      
            println(question.reponses[0]);
            delay(1000);
            return false;
        }
        return false;
    }

    // Permet à l'utilisateur de choisir son pseudo
    String choixPseudo() {
        println("Veuillez saisir un pseudo");
        String user = readString();
        return user;
    }

    //Affiche un message personnalisé en fonction du résultat du joueur
    void affichageFinPartie(Questions[] questions, String user, int score, int pourcentage) {
        if (score>length(questions)/2) {
            println("Félicitations "+user+" tu as remporté un total de "+score+" points, soit "+pourcentage+"% de bonnes réponses, tu es un expert en histoire !");
        } else {
            println(user+", tu as obtenu "+score+" points, soit "+pourcentage+"% de bonnes réponses, n'hésite pas à réviser tes cours d'histoire !");
        }
    }

    int aleatoire (int borneMin, int borneMax) {
        int alea = (int)(borneMin+(random() *  (borneMax-borneMin)));
        return alea;
    }

    void testAleatoire() {
        assertEquals(0, aleatoire(0,1));
        assertEquals(1, aleatoire(1,2));
        assertEquals(2, aleatoire(2,3));
    }

    void permutation (Questions[] tab, int indice1, int indice2) {
        Questions tmp = tab[indice1];
        tab[indice1] = tab[indice2];
        tab[indice2] = tmp;
    }

    void testPermutation() {
        Questions[] tab = new Questions[2];
        tab[0] = newQuestions("question1", new String[]{"reponse1", "reponse2", "reponse3"});
        tab[1] = newQuestions("question2", new String[]{"reponse1", "reponse2", "reponse3"});
        permutation(tab, 0, 1);
        assertEquals("question2", tab[0].question);
        assertEquals("question1", tab[1].question);
    }

    String choixdifficulte() {
        clearScreen();
        File menuDifficulte = newFile("ressources/menus/difficulte.txt");
        while (ready(menuDifficulte)) {
            println(readLine(menuDifficulte));
        }
        String choix = "";
        do {
            choix = readString();
            if (equals(choix,"1")) {
                return "normal";
            } else if (equals(choix,"2")) {
                return "difficile";
            } else {
                println("Saisie incorrecte");
            }
        } while (!equals(choix,"1") || !equals(choix,"2"));
        return "";
    }

    String choixModeDeJeu (String dossier) {
        clearScreen();
        String[] fichiers = getAllFilesFromDirectory(dossier);
        println("Choisis un mode de jeu : " + NEWLINE);
        for (int indice = 0; indice < length(fichiers); indice = indice + 1) {
            print((indice+1)+". "+fichiers[indice]+" | ");
        }
        println();
        int numeroFichier = 0;
        do {
            println("Quel mode de jeu choisissez-vous ?");
            String choix = readString();
            if (estNumerique(choix)) {
                numeroFichier = conversionStringEnInt(choix);
            } else {
                println("Ce n'est pas un entier..");
            }
        } while (numeroFichier < 1 || numeroFichier > length(fichiers));
        String modeChoisi = fichiers[numeroFichier-1];
        println(NEWLINE + "Vous avez choisi " + modeChoisi);
        return modeChoisi;
    }
    
    // Fonction principale
    void algorithm() {
        afficherMenuPrincipal();
        String mode = choixModeDeJeu(NOM_REPERTOIRE);
        String difficulte = choixdifficulte();
        String fichier = choixPeriode(NOM_REPERTOIRE+"/"+mode+"/"+difficulte);
        Questions[] questions = load(NOM_REPERTOIRE+"/"+mode+"/"+difficulte+"/"+fichier);
        int questionCourante = 0;
        int score = 0;
        int borneMax = length(questions);
        String user = choixPseudo();
        while (borneMax != 0) { // Boucle de jeu, se termine lorsque toutes les questions ont été posées.
            clearScreen();
            questionCourante = aleatoire(0, borneMax);
            println("Vous avez actuellement "+score+" points");
            boolean estReponseValide = poserQuestion(questions[questionCourante]);
            if (estReponseValide) {
                score = score + 1;
            }
            borneMax = borneMax-1;
            permutation(questions, borneMax, questionCourante);
            delay(2000);
        }
        clearScreen();
        int pourcentage = (score*100)/length(questions);
        affichageFinPartie(questions, user, score, pourcentage);
        save(user, pourcentage, mode, substring(fichier,0,length(fichier)-4), difficulte, "ressources/scores.csv");
    }
}