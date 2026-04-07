// Version: 2.1 (refactored)

abstract class Room {
    private int numberOfBeds;
    private double size;
    private double pricePerNight;

    public Room(int numberOfBeds, double size, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.pricePerNight = pricePerNight;
    }

    public int getNumberOfBeds() { return numberOfBeds; }
    public double getSize() { return size; }
    public double getPricePerNight() { return pricePerNight; }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println(getRoomType() + ":");
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + (int) size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500.0); }

    @Override
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500.0); }

    @Override
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000.0); }

    @Override
    public String getRoomType() { return "Suite Room"; }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom  = new SuiteRoom();

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable  = 2;

        System.out.println("Hotel Room Initialization");

        singleRoom.displayDetails();
        System.out.println("Available: " + singleAvailable);

        System.out.println();
        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailable);

        System.out.println();
        suiteRoom.displayDetails();
        System.out.println("Available: " + suiteAvailable);
    }
}
