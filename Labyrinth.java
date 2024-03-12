import java.util.ArrayList;
import java.util.Scanner;
public class Labyrinth {

    public static int taillePlateau; // Impair
    public static int[][] labyrinth;
    public static String ANSI_WHITE_BACKGROUND = "\u001B[40m";
    public static String ANSI_RESET = "\u001B[0m";
    public static ArrayList<int[]> murACasser = new ArrayList<>();
    public static Scanner sc = new Scanner(System.in);

    public static void main(String args[]) throws InterruptedException {
        int choixArretProgramme;
        int choix = 0;
        do {
            System.out.println("Que souhaitez vous faire ?\n1. Créer un labyrinth\n2. Quittez le programme");
            choixArretProgramme=sc.nextInt();

            if(choixArretProgramme==1) {
                do {
                    System.out.println("Quelle taille de labyrinth souhaitez vous ? (valeur impair supérieur ou égal à 5) hauteur puis largeur");
                    choix = sc.nextInt();
                } while (choix % 2 == 0 || choix < 5);
                taillePlateau = choix;
                labyrinth = new int[taillePlateau][taillePlateau];
                do {
                    System.out.println("Souhaitez vous voir le processus de construction du labyrinth ?\n1. Oui\n2. Non");
                    choix = sc.nextInt();
                } while (choix < 1 || choix > 2);

                if (choix == 1) {
		    do{
                    	System.out.println("Quelle vitesse d'affichage souhaitez vous ?\n1. Lente\n2. Normal\n3. Rapide\n4. Extrêmement rapide");
                    	choix = sc.nextInt();
		    }while(choix < 1 || choix > 4);
                    if (choix == 1)
                        choix = 1000;
                    else if (choix == 2)
                        choix = 500;
                    else if (choix == 3)
                        choix = 50;
		    else
			choix = 30;
                } else {
                    choix = 0;
                }

                // On initialise les valeurs du tableau pour ensuite crée les chemins.
                créationBaseLabyrinth();
                // On enregistre dans une liste les coordonnées de tous les murs qu'on peut casser.
                murACasserRemplissage();

                // Temps que toutes les parcelles du labyrinth ne sont pas de la même couleur (valeur).
                while(!finish()){
                    casseUnMur();
                    if(choix!=0) {
                        afficherLabyrinthEnCreation();
                        System.out.println();
                        Thread.sleep(choix);
                        nettoyageTerminal();
                    }
                }
		        afficherLabyrinthEnCreation();
                System.out.println();
                // On remet tout le labyrinth à la valeur "0" à part les murs.
                labyrinth[1][1]=0;
                fusionZone(1,1);

		do{
			System.out.println("Souhaitez vous afficher la résolution instantanément ?\n1. Oui\n2. Non");
			choix = sc.nextInt();
		}while(choix<1 || choix>2);
		if(choix == 1)
			choix = 0;
		else
			choix = 30;
                // On rempli le labyrinth avec des valeurs qui indique la longueur du chemin
                resolutionLabyrinth(2, taillePlateau-2, taillePlateau-1);

                // On trace le chemin le plus court
                tracageDuMeilleurChemin(labyrinth[1][0], 1, 0, choix);
                labyrinth[taillePlateau-2][taillePlateau-1]=0;

                afficherLabyrinthEnResolution();
            }
        }while(choixArretProgramme!=2);
    }

    /**
     * Initialisation de tous les murs.
     */
    public static void créationBaseLabyrinth(){
        for(int i = 0 ; i < labyrinth.length ; i+=2){
            for(int j = 0 ; j < labyrinth.length ; j++){
                labyrinth[i][j]=1;
            }
        }
        int cpt=2;
        for(int i = 1 ; i < labyrinth.length-1 ; i+=2){
            for(int j = 0 ; j < labyrinth.length ; j+=2){
                labyrinth[i][j]=1;
            }
            for(int j = 1 ; j < labyrinth.length-1 ; j+=2){
                labyrinth[i][j]=cpt++;
            }
        }
        labyrinth[1][0]=2;
        labyrinth[taillePlateau-2][taillePlateau-1]=cpt-1;
    }

    /**
     * Casse un mur après avoir vérifié qu'il ne crée pas de boucle entre une même zone.
     */
    public static void casseUnMur(){
        int random = murACasser.size();
        int choix = (int)(Math.random()*random);
        int[] mur = murACasser.get(choix);
        boolean casser=false;

        while(!casser) {
            for (int i = -1; i <= 1; i+=2) {
                if (coordonneeEstSurPlateau(mur[0]+i, mur[1]) && coordonneeEstSurPlateau(mur[0]-i, mur[1]) && labyrinth[mur[0]+i][mur[1]]!=labyrinth[mur[0]-i][mur[1]]) {
                    labyrinth[mur[0]][mur[1]] = labyrinth[mur[0] + i][mur[1]];
                    fusionZone(mur[0], mur[1]);
                    murACasser.remove(choix);
                    casser=true;
                }
                else if (coordonneeEstSurPlateau(mur[0], mur[1]+i) && coordonneeEstSurPlateau(mur[0], mur[1]-i) && labyrinth[mur[0]][mur[1]+i]!=labyrinth[mur[0]][mur[1]-i]) {
                    labyrinth[mur[0]][mur[1]] = labyrinth[mur[0]][mur[1] + i];
                    fusionZone(mur[0], mur[1]);
                    murACasser.remove(choix);
                    casser=true;
                }
                else{
                    random = murACasser.size();
                    choix = (int)(Math.random()*random);
                    mur = murACasser.get(choix);
                }
            }
        }
    }

    /**
     * @param ligne
     * @param colonne
     * Fusion des 2 zones qui sont désormais reliés après avoir cassé un mur
     */
    public static void fusionZone(int ligne, int colonne){
        for(int i = -1 ; i <= 1 ; i+=2){
            if(coordonneeEstSurPlateau(ligne+i, colonne) && labyrinth[ligne+i][colonne]!=1 && labyrinth[ligne+i][colonne]!=labyrinth[ligne][colonne]){
                labyrinth[ligne+i][colonne]=labyrinth[ligne][colonne];
                fusionZone(ligne+i,colonne);
            }
            if(coordonneeEstSurPlateau(ligne, colonne+i) && labyrinth[ligne][colonne+i]!=1 && labyrinth[ligne][colonne+i]!=labyrinth[ligne][colonne]){
                labyrinth[ligne][colonne+i]=labyrinth[ligne][colonne];
                fusionZone(ligne,colonne+i);
            }
        }
    }

    /**
     * @param l
     * @param c
     * @return
     * Vérification coordonnées valides
     */
    public static boolean coordonneeEstSurPlateau(int l, int c){
        return l>=0 && l<taillePlateau && c>=0 && c<taillePlateau;
    }

    /**
     * @return
     * Vérification que toutes les cases ont été connectées lors de la génération du labyrinth
     */
    public static boolean finish(){
        int chiffreFin;
        chiffreFin = labyrinth[1][1];
        for(int i = 1 ; i < taillePlateau-1 ; i++){
            for(int j = 1 ; j < taillePlateau-1 ; j++){
                if(chiffreFin!=labyrinth[i][j] && labyrinth[i][j]!=1)
                    return false;
            }
        }
        return true;
    }

    /**
     * Casse un mur qui relie 2 parcelle différente (pas de boucle crée)
     */
    public static void murACasserRemplissage(){
        for(int i = 2 ; i < labyrinth.length-2 ; i+=2)
            for(int j = 1 ; j < labyrinth[i].length-1 ; j+=2) {
                int[] tab1 = {i,j};
                murACasser.add(tab1);
                int[] tab2 = {j,i};
                murACasser.add(tab2);
            }
    }

    /**
     * Affichage du labyrinth lors de sa génération
     */
    public static void afficherLabyrinthEnCreation(){
        for (int i = 0 ; i < labyrinth.length ; i++){
            for(int j = 0 ; j < labyrinth.length ; j++){
                if(labyrinth[i][j]==1){
                    System.out.print(ANSI_WHITE_BACKGROUND+"   ");
                }
                else{
                    if(labyrinth[i][j]%6==0)
                        System.out.print("\u001B[41m"+"   "); // couleurs
                    else if(labyrinth[i][j]%5==0)
                        System.out.print("\u001B[42m"+"   ");
                    else if(labyrinth[i][j]%4==0)
                        System.out.print("\u001B[43m"+"   ");
                    else if(labyrinth[i][j]%3==0)
                        System.out.print("\u001B[44m"+"   ");
                    else if(labyrinth[i][j]%2==0)
                        System.out.print("\u001B[45m"+"   ");
                    else
                        System.out.print("\u001B[46m"+"   ");

                }
                System.out.print(ANSI_RESET);
            }
            System.out.println();
        }
    }

    /**
     * Affichage du tracage du meilleur chemin de résolution du labyrinth
     */
    public static void afficherLabyrinthEnResolution(){
        for (int i = 0 ; i < labyrinth.length ; i++){
            for(int j = 0 ; j < labyrinth.length ; j++){
                if(labyrinth[i][j]==1){
                    System.out.print(ANSI_WHITE_BACKGROUND+"   ");
                }
                else if(labyrinth[i][j]==0){
                    System.out.print("\u001B[42m"+"   ");
                }
                else{
                    System.out.print("   ");
                }
                System.out.print(ANSI_RESET);
            }
            System.out.println();
        }
    }

    /**
     * @param longueurChemin
     * @param ligne
     * @param colonne
     * Algorithme BFS sur toutes les cases du labyrinth
     */
    public static void resolutionLabyrinth(int longueurChemin, int ligne, int colonne){
        labyrinth[ligne][colonne]=longueurChemin;
        for(int i = -1 ; i <= 1 ; i+=2){
            if(coordonneeEstSurPlateau(ligne+i, colonne) && labyrinth[ligne+i][colonne]==0)
                resolutionLabyrinth(longueurChemin+1, ligne+i, colonne);
            if(coordonneeEstSurPlateau(ligne, colonne+i) && labyrinth[ligne][colonne+i]==0)
                resolutionLabyrinth(longueurChemin+1, ligne, colonne+i);
        }
    }

    /**
     *
     * @param longueurChemin
     * @param ligne
     * @param colonne
     * @param vitesseAffichage
     * @throws InterruptedException
     * Remonte le meilleur chemin depuis le départ le trace (remplace les cases du chemins par 0)
     */
    public static void tracageDuMeilleurChemin(int longueurChemin, int ligne, int colonne, int vitesseAffichage) throws InterruptedException {
	    Thread.sleep(vitesseAffichage);
        for(int i = -1 ; i <= 1 ; i+=2){
            if(coordonneeEstSurPlateau(ligne+i, colonne) && labyrinth[ligne+i][colonne]>1 && labyrinth[ligne+i][colonne]==longueurChemin-1) {
                labyrinth[ligne][colonne]=0;
                tracageDuMeilleurChemin(longueurChemin - 1, (ligne + i), colonne, vitesseAffichage);
        }
        if(coordonneeEstSurPlateau(ligne, colonne+i) && labyrinth[ligne][colonne+i]>1 && labyrinth[ligne][colonne+i]==longueurChemin-1) {
            labyrinth[ligne][colonne]=0;
		if(vitesseAffichage!=0){
		nettoyageTerminal();
		afficherLabyrinthEnResolution();
		}
                tracageDuMeilleurChemin(longueurChemin - 1, ligne, (colonne + i), vitesseAffichage);
            }
        }
    }

    /**
     * Clear le terminal entre chaque affichage pour un rendu plus fluide
     */
    public static void nettoyageTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush(); 
    }
}
