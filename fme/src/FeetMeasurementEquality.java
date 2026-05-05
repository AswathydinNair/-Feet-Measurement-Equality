public class FeetMeasurementEquality {

    // Enum for units
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),                 // 1 yard = 3 feet
        CENTIMETER(0.0328084);     // 1 cm = 0.0328084 feet

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }
    }

    // Single Quantity class (DRY applied)
    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {

            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }

            this.value = value;
            this.unit = unit;
        }

        // Convert to base unit (feet)
        private double toFeet() {
            return unit.toFeet(value);
        }

        @Override
        public boolean equals(Object obj) {

            // Same reference
            if (this == obj) return true;

            // Null + type check
            if (obj == null || getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;

            // Use tolerance for floating comparison
            double epsilon = 0.0001;
            return Math.abs(this.toFeet() - other.toFeet()) < epsilon;
        }
    }

    public static void main(String[] args) {

        // Same unit equality
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.FEET);

        System.out.println("1 ft == 1 ft : " + q1.equals(q2));

        // Cross-unit equality
        QuantityLength q3 = new QuantityLength(12.0, LengthUnit.INCH);
        System.out.println("1 ft == 12 inch : " + q1.equals(q3));

        // Yard comparison
        QuantityLength q4 = new QuantityLength(1.0, LengthUnit.YARD);
        QuantityLength q5 = new QuantityLength(3.0, LengthUnit.FEET);
        System.out.println("1 yard == 3 ft : " + q4.equals(q5));

        // Centimeter comparison
        QuantityLength q6 = new QuantityLength(2.54, LengthUnit.CENTIMETER);
        QuantityLength q7 = new QuantityLength(1.0, LengthUnit.INCH);
        System.out.println("2.54 cm == 1 inch : " + q6.equals(q7));

        // Different values
        QuantityLength q8 = new QuantityLength(2.0, LengthUnit.FEET);
        System.out.println("1 ft == 2 ft : " + q1.equals(q8));

        // Edge cases
        System.out.println("Compare with null : " + q1.equals(null));
        System.out.println("Same reference : " + q1.equals(q1));

        // Invalid unit test
        try {
            QuantityLength invalid = new QuantityLength(5.0, null);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}