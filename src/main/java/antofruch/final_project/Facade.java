package antofruch.final_project;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.MyTrees.KDimTree;
import antofruch.final_project.MultiDimTrees.MyTrees.QuadTree;
import antofruch.final_project.MultiDimTrees.MyTrees.QuadTreeKdTreeMix;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Facade {

    static final int MIN_LAT = -90;
    static final int MAX_LAT = 90;
    static final int MIN_LNG = -180;
    static final int MAX_LNG = 180;
    //SINGLETON
    private static Facade instance = null;
    public static Facade getInstance(){
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    private Facade(){
        System.out.println("Facade has been instantiated");
    }


    MultiDimTree<POI> tree;


    private int comparePOI(final POI a, final POI b, final int axis){
        switch (axis) {
            case 0:
                return Double.compare(a.getLatitude(), b.getLatitude());
            case 1:
                return Double.compare(a.getLongitude(), b.getLongitude());
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }
    private POI findPOImidpoints(final POI a, final POI b, final int axis){
        switch (axis) {
            case 0:
                return new POI("", (a.getLatitude() + b.getLatitude()) / 2, a.getLongitude(), "");
            case 1:
                return new POI("", a.getLatitude(), (a.getLongitude() + b.getLongitude()) / 2, "");
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }

    public void createTree(final TreeType type,
                           final Double minLat,
                           final double maxLat,
                           final double minLng,
                           final double maxLng,
                           final int max_subdivision_depth) {
        POI min = new POI("", minLat, minLng, "");
        POI max = new POI("", maxLat, maxLng, "");

        switch (type) {
            case QuadTree:
                tree = new QuadTree<>(min, max, max_subdivision_depth, this::comparePOI, this::findPOImidpoints);
                break;
            case MixedTree:
                tree = new QuadTreeKdTreeMix<>(min, max, max_subdivision_depth, this::comparePOI, this::findPOImidpoints);
                break;
            case KdTree:
                tree = new KDimTree<>(2, this::comparePOI);
                break;
            default:
                throw new IllegalArgumentException("Invalid tree type");
        }
    }

    public void addPOI(final String name, final double lat, final double lon, final String desc ){
        tree.insert(new POI(name, lat, lon, desc));
    }

    public void generateRandomData(final int N){
        Random random = new Random();

        for (int i=0; i<N; ++i){
            addPOI(
                "Generated",
                random.nextDouble() * (MAX_LAT - MIN_LAT) + MIN_LAT,
                random.nextDouble() * (MAX_LNG - MIN_LNG) + MIN_LNG,
                "This is a description"
            );
        }
    }

    // Import - Export
    public void importCsv(final File file){
        if (file == null) {
            throw new IllegalArgumentException("Aucun fichier fourni (file == null).");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Le fichier n'existe pas ou n'est pas un fichier valide : " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new SecurityException("Impossible de lire le fichier (droits insuffisants) : " + file.getAbsolutePath());
        }
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Le fichier doit avoir l'extension .csv : " + file.getName());
        }

        int lineNumber = 0;
        int addedCount = 0;
        int skippedCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Ignorer l'en-tête
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Ignorer les lignes vides ou uniquement composées d'espaces
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Sécurité : imposer exactement 4 colonnes séparées par ';'
                // limit = -1 conserve les colonnes vides éventuelles en fin de ligne (ex: "Nom;48.7;19.1;")
                String[] columns = line.split(";", -1);
                if (columns.length != 4) {
                    System.err.printf("Line %d ignored : %d column(s) found, 4 expected.%n",
                            lineNumber, columns.length);
                    skippedCount++;
                    continue;
                }

                String name = columns[0].trim();
                String latRaw = columns[1].trim().replace(',', '.');
                String lonRaw = columns[2].trim().replace(',', '.');
                String description = columns[3].trim();

                // Sécurité : le nom du POI ne doit pas être vide
                if (name.isEmpty()) {
                    System.err.printf("Line %d ignored : the name (column 1) is empty.%n", lineNumber);
                    skippedCount++;
                    continue;
                }

                try {
                    double lat = Double.parseDouble(latRaw);
                    double lon = Double.parseDouble(lonRaw);

                    // Sécurité : vérifier que lat et lon sont des nombres valides et dans les bornes géographiques
                    if (!Double.isFinite(lat) || !Double.isFinite(lon)
                            || lat < -90.0 || lat > 90.0
                            || lon < -180.0 || lon > 180.0) {
                        System.err.printf("Line %d ignored : coordinates out of bounds (lat=%s, lon=%s).%n",
                                lineNumber, latRaw, lonRaw);
                        skippedCount++;
                        continue;
                    }

                    Facade.getInstance().addPOI(name, lat, lon, description);
                    addedCount++;

                } catch (NumberFormatException e) {
                    System.err.printf("Line %d ignored : invalid latitude or longitude ('%s', '%s').%n",
                            lineNumber, latRaw, lonRaw);
                    skippedCount++;
                }
            }

            System.out.printf("Import succeeded : %d POI added, %d line(s) skipped.%n",
                    addedCount, skippedCount);

        } catch (IOException e) {
            throw new RuntimeException("Read error : " + file.getName(), e);
        }
    }
    public void exportCsv(File file){
        // Récupérez votre liste de POI depuis la Facade (adaptez le nom de la méthode si besoin)
        List<POI> pois = findPOIRange(MIN_LAT, MAX_LAT, MIN_LNG, MAX_LNG);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // 1. Ligne d'en-tête (4 colonnes)
            writer.write("name;lat;lon;description");
            writer.newLine();

            // 2. Une ligne par POI
            for (POI poi : pois) {
                String safeName = sanitizeCsvField(poi.getName());
                String safeDesc = sanitizeCsvField(poi.getDescription());

                // Point décimal standard pour éviter les ambiguïtés avec ';'
                String line = String.join(";",
                        safeName,
                        Double.toString(poi.getLatitude()),
                        Double.toString(poi.getLongitude()),
                        safeDesc
                );

                writer.write(line);
                writer.newLine();
            }

            System.out.println("Export CSV succeeded : " + file.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Write error : " + file.getName(), e);
        }
    }

    private String sanitizeCsvField(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace(";", ",")
                .replace("\r", " ")
                .replace("\n", " ")
                .trim();
    }

    // ===== USE CASES =====
    public List<POI> findPOIRange(
            final double minLat,
            final double maxLat,
            final double minLng,
            final double maxLng){
        POI min = new POI("", minLat, minLng, "");
        POI max = new POI("", maxLat, maxLng, "");

        return tree.find(min, max);
    }

    public List<POI> findAllPOI(){
        return findPOIRange(MIN_LAT, MAX_LAT, MIN_LNG, MAX_LNG);
    }

    public List<POI> findNearPOI(final POI point, final double radius){
        List<POI> range = findPOIRange(
                point.getLatitude() - radius,
                point.getLatitude() + radius,
                point.getLongitude() - radius,
                point.getLongitude() + radius
        );


        List<POI> result = new ArrayList<>();
        for (final POI p : range) {
            if (point.getDistance(p) <= radius) {
                result.add(p);
            }
        }
        return result;
    }

    public void createNewPOI(final String name, final double lat, final double lng, final String description){
        POI poi = new POI(name, lat, lng, description);
        tree.insert(poi);
    }

    public void editPOI(final POI point,
                        final String name,
                        final String description){
        point.setName(name);
        point.setDescription(description);
    }

    public List<POI> findPOIbyName(final String name,
                                   final double minLat,
                                   final double maxLat,
                                   final double minLng,
                                   final double maxLng){
        List<POI> list = findPOIRange(minLat, maxLat, minLng, maxLng);
        List<POI> result = new ArrayList<>();
        for (final POI p : list) {
            if (p.getName().equals(name)) {
                result.add(p);
            }
        }
        return result;
    }
}
