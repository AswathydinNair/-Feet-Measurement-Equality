public class FeetMeasurementEquality {

    // Enum for units
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0);

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

            // Compare after conversion
            return Double.compare(this.toFeet(), other.toFeet()) == 0;
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

        // Different values
        QuantityLength q4 = new QuantityLength(2.0, LengthUnit.FEET);

        System.out.println("1 ft == 2 ft : " + q1.equals(q4));

        // Same unit (inch)
        QuantityLength q5 = new QuantityLength(1.0, LengthUnit.INCH);
        QuantityLength q6 = new QuantityLength(1.0, LengthUnit.INCH);

        System.out.println("1 inch == 1 inch : " + q5.equals(q6));

        // Edge cases
        System.out.println("Compare with null : " + q1.equals(null));
        System.out.println("Same reference : " + q1.equals(q1));

        // Invalid unit test (will throw exception)
        try {
            QuantityLength invalid = new QuantityLength(5.0, null);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}