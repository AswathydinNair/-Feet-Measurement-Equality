public class FeetMeasurementEquality {

    // Feet Class
    static class Feet {
        private final double value;

        public Feet(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Feet other = (Feet) obj;
            return Double.compare(this.value, other.value) == 0;
        }
    }

    // Inches Class
    static class Inches {
        private final double value;

        public Inches(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Inches other = (Inches) obj;
            return Double.compare(this.value, other.value) == 0;
        }
    }

    // Feet comparison method
    public static boolean compareFeet(double a, double b) {
        return new Feet(a).equals(new Feet(b));
    }

    // Inches comparison method
    public static boolean compareInches(double a, double b) {
        return new Inches(a).equals(new Inches(b));
    }

    public static void main(String[] args) {

        // Feet checks
        System.out.println("UC2 Feet Equality:");
        System.out.println("1.0 ft == 1.0 ft : " + compareFeet(1.0, 1.0));
        System.out.println("1.0 ft == 2.0 ft : " + compareFeet(1.0, 2.0));

        // Inches checks
        System.out.println("\nUC2 Inches Equality:");
        System.out.println("1.0 inch == 1.0 inch : " + compareInches(1.0, 1.0));
        System.out.println("1.0 inch == 2.0 inch : " + compareInches(1.0, 2.0));

        // Edge cases
        Feet f = new Feet(1.0);
        System.out.println("\nEdge Cases:");
        System.out.println("Feet vs null : " + f.equals(null));
        System.out.println("Feet vs String : " + f.equals("abc"));
    }
}