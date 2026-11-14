package antofruch.final_project;

public class POI {

    private String name;
    private double latitude;
    private double longitude;
    private String description;

    public POI(final String name, final double latitude, final double longitude, final String description) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public double getDistance(final POI other) {
        final double lat1 = this.latitude;
        final double lon1 = this.longitude;
        final double lat2 = other.latitude;
        final double lon2 = other.longitude;

        //FIXME here
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);

        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // Earth's radius in kilometers
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        if (name != null && !name.trim().isEmpty()) {
            return name + " (" + latitude + ", " + longitude + ")" +
                    (description != null && !description.trim().isEmpty() ? " - " + description : "");
        }
        return "POI (" + latitude + ", " + longitude + ")" +
                (description != null && !description.trim().isEmpty() ? " - " + description : "");
    }
}
