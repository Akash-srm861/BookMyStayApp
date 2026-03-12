/**
 * BookMyStayApp - Hotel Booking Management System.
 *
 * <p>
 * UC1: Application Entry and Welcome Message
 * <p>
 * UC2: Basic Room Types and Static Availability
 *
 * @author Akash
 * @version 2.0
 */
public class BookMyStayApp {

    // =========================================================================
    // UC2 — Room Domain Model (Abstract Class + Inheritance + Polymorphism)
    // =========================================================================

    /**
     * Abstract base class for all hotel room types.
     * Encapsulates shared attributes and enforces structure
     * via the abstract {@code displayAmenities()} method.
     */
    static abstract class Room {
        protected String roomType;
        protected int numberOfBeds;
        protected double pricePerNight;
        protected String sizeDescription;
        protected String amenities;

        public Room(String roomType, int numberOfBeds, double pricePerNight,
                String sizeDescription, String amenities) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.pricePerNight = pricePerNight;
            this.sizeDescription = sizeDescription;
            this.amenities = amenities;
        }

        public String getRoomType() {
            return roomType;
        }

        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }

        public String getSizeDescription() {
            return sizeDescription;
        }

        /** Displays amenities specific to this room type. */
        public abstract void displayAmenities();

        /** Prints a formatted summary of this room's core details. */
        public void displayRoomDetails() {
            System.out.println("  Room Type   : " + roomType);
            System.out.println("  Beds        : " + numberOfBeds);
            System.out.println("  Size        : " + sizeDescription);
            System.out.printf("  Price/Night : $%.2f%n", pricePerNight);
            displayAmenities();
        }
    }

    /** Single Room — compact, budget-friendly option (1 bed). */
    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 80.00, "Small (200 sq ft)",
                    "Wi-Fi, TV, Mini-Fridge");
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities   : " + amenities);
        }
    }

    /** Double Room — suitable for couples or small families (2 beds). */
    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 140.00, "Medium (350 sq ft)",
                    "Wi-Fi, TV, Mini-Fridge, Work Desk");
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities   : " + amenities);
        }
    }

    /** Suite Room — premium, spacious accommodation (3 beds). */
    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 280.00, "Large (600 sq ft)",
                    "Wi-Fi, TV, Mini-Bar, Jacuzzi, Living Area, Balcony");
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities   : " + amenities);
        }
    }

    // =========================================================================
    // UC1 — Application Entry Point (main)
    // =========================================================================

    /**
     * Application entry point.
     * UC1: Prints welcome message.
     * UC2: Creates room objects and displays catalog with static availability.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        // UC1: Welcome message
        System.out.println("============================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking Management System v2.0");
        System.out.println("============================================");

        // UC2: Static availability variables (intentional limitation)
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // UC2: Polymorphic room references
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };
        int[] avail = { singleAvailable, doubleAvailable, suiteAvailable };

        System.out.println("\n[UC2] Room Type Catalog:");
        for (int i = 0; i < rooms.length; i++) {
            System.out.println();
            rooms[i].displayRoomDetails();
            System.out.println("  Available   : " + avail[i] + " rooms");
        }

        System.out.println("\n============================================");
        System.out.println("  NOTE: Availability stored in variables —");
        System.out.println("  Centralized HashMap introduced in UC3.");
        System.out.println("============================================");
    }
}
