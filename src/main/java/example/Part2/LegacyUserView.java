package example.Part2;

public class LegacyUserView {
    record City(String name) {}
    record Address(City city, String street) {}
    record User(String name, Address address) {}

    public static void main(String[] args) {
        User user1 = new User("Dev", new Address(new City("Seoul"), "Gangnam"));
        User user2 = new User("Junior", new Address(null, "Bundang")); // City is null
        User user3 = new User("Ghost", null); // Address is null
        User user4 = null; // User is null

        System.out.println(getUserCityName(user1)); // Seoul
        System.out.println(getUserCityName(user2)); // Unknown
        System.out.println(getUserCityName(user3)); // Unknown
        System.out.println(getUserCityName(user4)); // Unknown
    }

    public static String getUserCityName(User user) {
        // Dirty Null Checks -----------------------
        if (user != null) {
            Address address = user.address();
            if (address != null) {
                City city = address.city();
                if (city != null) {
                    return city.name();
                }
            }
        }
        return "Unknown";
        // -----------------------------------------
    }
}
