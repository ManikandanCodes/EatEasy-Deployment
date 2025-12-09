package com.example.backend.config;

import com.example.backend.model.User;
import com.example.backend.model.Restaurant;
import com.example.backend.model.MenuCategory;
import com.example.backend.model.MenuItem;
import com.example.backend.repository.MenuCategoryRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class DataSeeder implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RestaurantRepository restaurantRepository;
        private final MenuCategoryRepository menuCategoryRepository;
        private final MenuItemRepository menuItemRepository;
        private final JdbcTemplate jdbcTemplate;

        public DataSeeder(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        RestaurantRepository restaurantRepository,
                        MenuCategoryRepository menuCategoryRepository,
                        MenuItemRepository menuItemRepository,
                        JdbcTemplate jdbcTemplate) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.restaurantRepository = restaurantRepository;
                this.menuCategoryRepository = menuCategoryRepository;
                this.menuItemRepository = menuItemRepository;
                this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public void run(String... args) throws Exception {

                System.out.println("Running Data Seeder...");

                try {
                        System.out.println("Normalizing roles in DB...");
                        jdbcTemplate.update(
                                        "UPDATE users SET role = 'RESTAURANT_OWNER' WHERE role = 'ROLE_RESTAURANT_OWNER'");
                        jdbcTemplate.update("UPDATE users SET role = 'ADMIN' WHERE role = 'ROLE_ADMIN'");
                        System.out.println("Roles normalized.");
                } catch (Exception e) {
                        System.err.println("Could not normalize roles: " + e.getMessage());
                }

                User admin = userRepository.findByEmail("admin@eateasy.com").orElse(null);

                if (admin == null) {
                        admin = new User();
                        admin.setName("Admin User");
                        admin.setEmail("admin@eateasy.com");
                        admin.setPhone("9999999999");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRole(User.Role.ADMIN);
                        userRepository.save(admin);

                        System.out.println("Admin user created");
                } else {

                        if (admin.getRole() != User.Role.ADMIN) {
                                admin.setRole(User.Role.ADMIN);
                                userRepository.save(admin);
                                System.out.println("Admin role updated to ADMIN");
                        }
                        System.out.println("Admin user already exists");
                }

                User customer = userRepository.findByEmail("user@eateasy.com").orElse(null);

                if (customer == null) {
                        customer = new User();
                        customer.setName("Standard User");
                        customer.setEmail("user@eateasy.com");
                        customer.setPhone("9876543210");
                        customer.setPassword(passwordEncoder.encode("user123"));
                        customer.setRole(User.Role.CUSTOMER);
                        userRepository.save(customer);
                        System.out.println("Standard customer created");
                } else {
                        System.out.println("Standard customer already exists");
                }

                User owner = userRepository.findByEmail("iammanikandan.engineer@gmail.com").orElse(null);

                if (owner == null) {
                        owner = new User();
                        owner.setName("Manikandan");
                        owner.setEmail("iammanikandan.engineer@gmail.com");
                        owner.setPhone("1234567890");
                        owner.setPassword(passwordEncoder.encode("17072001"));
                        owner.setRole(User.Role.RESTAURANT_OWNER);
                        userRepository.save(owner);

                        System.out.println("Owner user created");
                } else {
                        boolean changed = false;
                        if (owner.getRole() != User.Role.RESTAURANT_OWNER) {
                                owner.setRole(User.Role.RESTAURANT_OWNER);
                                changed = true;
                        }

                        owner.setPassword(passwordEncoder.encode("17072001"));
                        changed = true;

                        if (changed) {
                                userRepository.save(owner);
                                System.out.println("Owner updated (Role/Password)");
                        }
                        System.out.println("Owner user already exists");
                }

                if (owner != null && restaurantRepository.findByOwnerId(owner.getId()).isEmpty()) {

                        Restaurant r = new Restaurant();
                        r.setName("Manikandan's Kitchen");
                        r.setDescription("Authentic South Indian Cuisine");
                        r.setAddress("123, Main Street, Chennai");
                        r.setCuisines("South Indian");
                        r.setImageUrl("https://images.unsplash.com/photo-1589302168068-964664d93dc0");
                        r.setOwner(owner);
                        r.setStatus(Restaurant.ApprovalStatus.APPROVED);
                        r.setRating(4.5);
                        r.setOpeningHours("9 AM - 10 PM");

                        restaurantRepository.save(r);

                        System.out.println("Restaurant created");
                } else {
                        System.out.println("Restaurant already exists for owner");
                        Restaurant existing = restaurantRepository.findByOwnerId(owner.getId()).get(0);
                        boolean updated = false;
                        if (existing.getRating() == 0.0) {
                                existing.setRating(4.5);
                                updated = true;
                        }
                        if (existing.getOpeningHours() == null || existing.getOpeningHours().isEmpty()) {
                                existing.setOpeningHours("9 AM - 10 PM");
                                updated = true;
                        }
                        if (updated) {
                                restaurantRepository.save(existing);
                                System.out.println("Updated existing restaurant details (Rating/Hours)");
                        }
                }

                createOwnerAndRestaurant("Chettinad Chef", "owner1@eateasy.com", "owner1",
                                "Chettinad Spice", "Authentic Chettinad flavors with spicy curries.",
                                "45, Heritage Road, Karaikudi, Tamil Nadu", "Chettinad, South Indian",
                                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4");

                createOwnerAndRestaurant("Madurai Master", "owner2@eateasy.com", "owner2",
                                "Spice Haven", "Famous for Jigarthanda and spicy non-veg.",
                                "12, Temple Street, Madurai, Tamil Nadu", "South Indian, Non-Veg",
                                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5");

                createOwnerAndRestaurant("Luigi", "owner3@eateasy.com", "owner3",
                                "Italiano Delight", "Wood-fired pizzas and creamy pastas.",
                                "88, OMR, Chennai, Tamil Nadu", "Italian, Pizza, Pasta",
                                "https://images.unsplash.com/photo-1559339352-11d035aa65de");

                createOwnerAndRestaurant("Chen", "owner4@eateasy.com", "owner4",
                                "Dragon Wok", "Best Chinese food in town.",
                                "22, RS Puram, Coimbatore, Tamil Nadu", "Chinese, Asian",
                                "https://images.unsplash.com/photo-1560624052-449f5ddf0c31");

                createOwnerAndRestaurant("Singh", "owner5@eateasy.com", "owner5",
                                "Tandoori Flames", "Rich North Indian gravies and kebabs.",
                                "56, Anna Nagar, Chennai, Tamil Nadu", "North Indian, Tandoori",
                                "https://images.unsplash.com/photo-1585518419759-7fe2e0fbf8a6");

                createOwnerAndRestaurant("Colonel Sanders", "owner6@eateasy.com", "owner6",
                                "KFC", "Finger Lickin' Good crispy chicken and burgers.",
                                "15, GST Road, West Tambaram, Chennai, Tamil Nadu", "American, Fast Food, Chicken",
                                "https://images.unsplash.com/photo-1513639776629-7b61b0ca4909");

                createOwnerAndRestaurant("Chengalpattu Chief", "owner7@eateasy.com", "owner7",
                                "SS Hyderabad Biryani", "Authentic Dum Biryani and Kebabs.",
                                "88, GST Road, Chengalpattu, Tamil Nadu", "Biryani, Mughlai",
                                "https://images.unsplash.com/photo-1631452180519-c014fe946bc7");

                createOwnerAndRestaurant("Thiruvallur Taste", "owner8@eateasy.com", "owner8",
                                "Dindigul Thalappakatti", "Legendary Seeraga Samba Biryani.",
                                "10, M.G. Road, Thiruvallur, Tamil Nadu", "Biryani, South Indian",
                                "https://images.unsplash.com/photo-1552566626-52f8b828add9");

                createOwnerAndRestaurant("Hsb Manager", "owner9@eateasy.com", "owner9",
                                "Hotel Saravana Bhavan", "World famous vegetarian South Indian food.",
                                "19, Usman Road, T. Nagar, Chennai, Tamil Nadu", "South Indian, Vegetarian",
                                "https://images.unsplash.com/photo-1592861956120-e524fc739696");

                createOwnerAndRestaurant("Domino's Manager", "owner10@eateasy.com", "owner10",
                                "Domino's Pizza", "Hot and fresh pizzas delivered fast.",
                                "42, 100 Feet Road, Velachery, Chennai, Tamil Nadu", "Pizza, Fast Food",
                                "https://images.unsplash.com/photo-1590846406792-0adc7f938f1d");

                createOwnerAndRestaurant("A2B Manager", "owner11@eateasy.com", "owner11",
                                "Adyar Ananda Bhavan", "Pure vegetarian sweets and savories.",
                                "5, LB Road, Adyar, Chennai, Tamil Nadu", "Sweets, South Indian, Vegetarian",
                                "https://images.unsplash.com/photo-1554118811-1e0d58224f24");
        }

        private void createOwnerAndRestaurant(String name, String email, String password, String restaurantName,
                        String description, String address, String cuisines, String imageUrl) {
                User owner = userRepository.findByEmail(email).orElse(null);

                if (owner == null) {
                        owner = new User();
                        owner.setName(name);
                        owner.setEmail(email);
                        owner.setPhone("9876543210");
                        owner.setPassword(passwordEncoder.encode(password));
                        owner.setRole(User.Role.RESTAURANT_OWNER);
                        userRepository.save(owner);
                        System.out.println("Owner created: " + email);
                } else {

                        if (owner.getRole() != User.Role.RESTAURANT_OWNER) {
                                owner.setRole(User.Role.RESTAURANT_OWNER);
                                userRepository.save(owner);
                        }

                        owner.setPassword(passwordEncoder.encode(password));
                        userRepository.save(owner);
                        System.out.println("Owner already exists (Password reset): " + email);
                }

                Restaurant r = restaurantRepository.findByOwnerId(owner.getId()).stream().findFirst().orElse(null);
                if (r == null) {
                        r = new Restaurant();
                        r.setOwner(owner);
                        r.setName(restaurantName);
                        r.setDescription(description);
                        r.setAddress(address);
                        r.setCuisines(cuisines);
                        r.setImageUrl(imageUrl);
                        r.setStatus(Restaurant.ApprovalStatus.APPROVED);
                        r.setRating(4.5);
                        r.setOpen(true);
                        r.setOpeningHours("9 AM - 10 PM");
                        r.setPhone("9876543210");

                        restaurantRepository.save(r);
                        seedMenu(r);
                        System.out.println("Created Restaurant: " + restaurantName);
                } else {

                        if (r.getOpeningHours() == null || r.getOpeningHours().isEmpty()) {
                                r.setOpeningHours("9 AM - 10 PM");
                                restaurantRepository.save(r);
                                System.out.println("Updated opening hours for existing restaurant: " + restaurantName);
                        }

                        seedMenu(r);
                        System.out.println(
                                        "Restaurant already exists: " + restaurantName
                                                        + " - Skipping to preserve user changes");
                }
        }

        private void seedMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding menu for: " + r.getName());

                        MenuCategory starters = new MenuCategory();
                        starters.setName("Starters");
                        starters.setRestaurant(r);
                        menuCategoryRepository.save(starters);

                        MenuCategory mainCourse = new MenuCategory();
                        mainCourse.setName("Main Course");
                        mainCourse.setRestaurant(r);
                        menuCategoryRepository.save(mainCourse);

                        MenuCategory desserts = new MenuCategory();
                        desserts.setName("Desserts");
                        desserts.setRestaurant(r);
                        menuCategoryRepository.save(desserts);

                        String cuisine = r.getCuisines().toLowerCase();

                        if (cuisine.contains("south indian")) {
                                createItem(starters, "Gobi 65", "Crispy fried cauliflower", 150, true,
                                                "https://images.unsplash.com/photo-1601050690597-df0568f70950");
                                createItem(mainCourse, "Masala Dosa", "Crispy crepe with potato filling", 120, true,
                                                "https://images.unsplash.com/photo-1589301760014-d929645e3b6c");
                                createItem(mainCourse, "Idli Sambar", "Steamed rice cakes with lentil stew", 80, true,
                                                "https://images.unsplash.com/photo-1589301760014-d929645e3b6c");
                                createItem(desserts, "Gulab Jamun", "Sweet milk dumplings", 60, true,
                                                "https://images.unsplash.com/photo-1589301760014-d929645e3b6c");
                        } else if (cuisine.contains("pizza") || cuisine.contains("italian")) {
                                createItem(starters, "Garlic Bread", "Toasted bread with garlic butter", 120, true,
                                                "https://images.unsplash.com/photo-1573140247632-f84660f67627");
                                createItem(mainCourse, "Margherita Pizza", "Classic cheese and tomato pizza", 250, true,
                                                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002");
                                createItem(mainCourse, "Pasta Alfredo", "Creamy white sauce pasta", 280, true,
                                                "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb");
                                createItem(desserts, "Tiramisu", "Coffee flavored Italian dessert", 200, true,
                                                "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9");
                        } else if (cuisine.contains("chinese") || cuisine.contains("asian")) {
                                createItem(starters, "Spring Rolls", "Crispy vegetable rolls", 140, true,
                                                "https://images.unsplash.com/photo-1544510808-91bcbee1df55");
                                createItem(mainCourse, "Veg Hakka Noodles", "Stir fried noodles with veggies", 180,
                                                true,
                                                "https://images.unsplash.com/photo-1585032226651-759b368d7246");
                                createItem(mainCourse, "Manchurian", "Veg balls in spicy sauce", 190, true,
                                                "https://images.unsplash.com/photo-1525755662778-989d0524087e");
                                createItem(desserts, "Honey Noodles", "Fried noodles with honey and sesame", 120, true,
                                                "https://images.unsplash.com/photo-1563805042-7684c019e1cb");
                        } else if (cuisine.contains("biryani")) {
                                createItem(starters, "Chicken 65", "Spicy fried chicken", 200, false,
                                                "https://images.unsplash.com/photo-1601050690597-df0568f70950");
                                createItem(mainCourse, "Chicken Biryani", "Aromatic rice with chicken", 250, false,
                                                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8");
                                createItem(mainCourse, "Mutton Biryani", "Aromatic rice with mutton", 350, false,
                                                "https://images.unsplash.com/photo-1633945274405-b6c8069047b0");
                                createItem(desserts, "Bread Halwa", "Sweet bread pudding", 80, true,
                                                "https://images.unsplash.com/photo-1589301760014-d929645e3b6c");
                        } else if (cuisine.contains("north indian")) {
                                createItem(starters, "Paneer Tikka", "Grilled cottage cheese", 220, true,
                                                "https://images.unsplash.com/photo-1567188040754-b93d80d31941");
                                createItem(mainCourse, "Butter Chicken", "Chicken in creamy tomato sauce", 300, false,
                                                "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db");
                                createItem(mainCourse, "Naan", "Tandoori flatbread", 40, true,
                                                "https://images.unsplash.com/photo-1601050690597-df0568f70950");
                                createItem(desserts, "Rasmalai", "Soft cheese patties in milk", 100, true,
                                                "https://images.unsplash.com/photo-1589301760014-d929645e3b6c");
                        } else {
                                // Default / Fast Food
                                createItem(starters, "French Fries", "Crispy potato fries", 100, true,
                                                "https://images.unsplash.com/photo-1573080496987-a199f8cd4058");
                                createItem(mainCourse, "Burger", "Veggie burger with cheese", 150, true,
                                                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd");
                                createItem(mainCourse, "Sandwich", "Grilled vegetable sandwich", 120, true,
                                                "https://images.unsplash.com/photo-1528735602780-2552fd46c7af");
                                createItem(desserts, "Ice Cream", "Vanilla scoop", 80, true,
                                                "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f");
                        }
                }
        }

        private void createItem(MenuCategory category, String name, String desc, double price, boolean veg,
                        String img) {
                MenuItem item = new MenuItem();
                item.setName(name);
                item.setDescription(desc);
                item.setPrice(price);
                item.setVeg(veg);
                item.setImageUrl(img);
                item.setCategory(category);
                item.setBestSeller(false);
                item.setOutOfStock(false);
                menuItemRepository.save(item);
        }
}
