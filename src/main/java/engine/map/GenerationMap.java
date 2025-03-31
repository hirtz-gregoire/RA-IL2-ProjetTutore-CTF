package engine.map;

public class GenerationMap {
    public static final int SCALE = 10;
    private static double[][] gradients;

    public static EditorMap generateMap(int width, int height) {
        //Initilisation
        gradients = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gradients[x][y] = 0;
            }
        }
        //Récupération du tableau généré avec perlin
        double[][] map = getMap(width, height);
    }

    private static double[][] getMap(int width, int height) {
        double[][] noise = new double[height][width];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                noise[x][y] = getPerlinCoordinates((double) x / SCALE, (double) y / SCALE); // Normalisation avec SCALE
            }
        }

        //MIN & MAX
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (noise[x][y] < min) {
                    min = noise[x][y];
                }
                if (noise[x][y] > max) {
                    max = noise[x][y];
                }
            }
        }
        // Normalisation des valeurs entre 0 et 1
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                noise[x][y] = (noise[x][y] / min) / (max - min);
            }
        }
        return noise;
    }

    //Génère une valeur de bruit de Perlin pour un point donné
    private static double getPerlinCoordinates(double x, double y) {
        //Coordonnées des coins de la cellule
        int x0 = (int) x;
        int y0 = (int) y;
        int x1= x0 + 1;
        int y1 = y0 + 1;

        //Distances fractionnaires
        double sx = x - x0;
        double sy = y - y0;

        //Appliquer la fonction fade
        double u = fade(sx);
        double v = fade(sy);

        //Produits scalaires aux 4 coins
        double dot00 = dot_grid_gradient(x0, y0, x, y);
        double dot10 = dot_grid_gradient(x1, y0, x, y);
        double dot01 = dot_grid_gradient(x0, y1, x, y);
        double dot11 = dot_grid_gradient(x1, y1, x, y);

        //Interpolation sur l'axe X
            ix0 = lerp(dot00, dot10, u)
            ix1 = lerp(dot01, dot11, u)

        //Interpolation finale sur l'axe Y
        return lerp(ix0, ix1, v)
    }

    //Fonction de lissage : 6t^5 - 15t^4 + 10t^3
    private static double fade(double t) {
        return 6 * Math.pow(t, 5) - 15 *  Math.pow(t, 4) + 10 *  Math.pow(t, 3);
    }

    //Calcule le produit scalaire entre le vecteur de gradient et le vecteur de distance
    private static double dot_grid_gradient(int ix, int iy, double x, double y) {
        double[] gradient = get_gradient(ix, iy);
        dx, dy = x - ix, y - iy  // Distance au coin
        return (dx * gradient[0] + dy * gradient[1])
    }

    //Récupère le vecteur de gradient d'un point de la grille
    private static double[] get_gradient(int ix, int iy) {
        //Détecter si le couple x, y est ou non dans la liste des gradients
        for ()
        if ((x, y) not in gradients){
            gradients[(x, y)] = generate_gradient();
        }
        return gradients[(x,y)];
    }
}
