public class FeetMeasurementEquality {

    // Inner class
    static class Feet {
        private final double value;

        // Constructor
        public Feet(double value) {
            this.value = value;
        }

        // Override equals method
        @Override
        public boolean equals(Object obj) {

            // Same reference
            if (this == obj) return true;

            // Null and type check
            if (obj == null || getClass() != obj.getClass()) return false;

            // Cast
            Feet other = (Feet) obj;

            // Compare using Double.compare
            return Double.compare(this.value, other.value) == 0;
        }
    }

    public static void main(String[] args) {

        Feet f1 = new Feet(1.0);
        Feet f2 = new Feet(1.0);
        Feet f3 = new Feet(2.0);

        // Same value
        System.out.println("1.0 ft == 1.0 ft : " + f1.equals(f2));

        // Different value
        System.out.println("1.0 ft == 2.0 ft : " + f1.equals(f3));

        // Null comparison
        System.out.println("1.0 ft == null : " + f1.equals(null));

        // Same reference
        System.out.println("Same reference : " + f1.equals(f1));

        // Different type
        System.out.println("Compare with String : " + f1.equals("abc"));
    }
}