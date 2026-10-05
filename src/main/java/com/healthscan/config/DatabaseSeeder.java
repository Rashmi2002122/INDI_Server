package com.healthscan.config;

import com.healthscan.entity.FoodAliasEntity;
import com.healthscan.entity.FoodNutritionEntity;
import com.healthscan.entity.FoodPreparationEntity;
import com.healthscan.entity.FreshFoodEntity;
import com.healthscan.entity.PackagedProductEntity;
import com.healthscan.repository.FoodAliasRepository;
import com.healthscan.repository.FoodNutritionRepository;
import com.healthscan.repository.FoodPreparationRepository;
import com.healthscan.repository.FreshFoodRepository;
import com.healthscan.repository.PackagedProductRepository;
import com.healthscan.service.FoodNutritionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder {

    private final FreshFoodRepository freshFoodRepository;
    private final FoodNutritionRepository nutritionRepository;
    private final FoodAliasRepository aliasRepository;
    private final FoodPreparationRepository preparationRepository;
    private final PackagedProductRepository packagedProductRepository;

    public DatabaseSeeder(FreshFoodRepository freshFoodRepository,
                          FoodNutritionRepository nutritionRepository,
                          FoodAliasRepository aliasRepository,
                          FoodPreparationRepository preparationRepository,
                          PackagedProductRepository packagedProductRepository) {
        this.freshFoodRepository = freshFoodRepository;
        this.nutritionRepository = nutritionRepository;
        this.aliasRepository = aliasRepository;
        this.preparationRepository = preparationRepository;
        this.packagedProductRepository = packagedProductRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        seedPackagedDemoProducts();

        // Idempotent seeding check: if database already populated, do nothing
        if (freshFoodRepository.count() >= 30) {
            return;
        }

        seedFood("egg", "Egg", "Dairy & Eggs", "🥚", "1 large egg (50g)", "egg",
                true, false, true, true, 70.0, 6.3, 0.4, 4.8, 0.0, 0.4, 70.0, 186.0,
                List.of("egg", "eggs", "anda", "chicken egg"),
                List.of(new PrepData("raw", "Raw egg", 1.0, 1.0),
                        new PrepData("boiled", "Hard or soft boiled egg", 1.0, 1.0),
                        new PrepData("fried", "Pan fried egg with oil", 1.35, 1.8)));

        seedFood("boiled-egg", "Boiled Egg", "Dairy & Eggs", "🍳", "1 large boiled egg (50g)", "egg",
                true, false, true, true, 78.0, 6.3, 0.6, 5.3, 0.0, 0.5, 62.0, 186.0,
                List.of("boiled egg", "boiled eggs", "hard boiled egg"),
                List.of(new PrepData("raw", "Standard boiled", 1.0, 1.0)));

        seedFood("chicken", "Chicken", "Meat & Poultry", "🍗", "100g serving", "g",
                false, false, true, true, 215.0, 27.0, 0.0, 11.0, 0.0, 0.0, 75.0, 85.0,
                List.of("chicken", "poultry", "murgh"),
                List.of(new PrepData("grilled", "Grilled chicken", 1.0, 1.0),
                        new PrepData("boiled", "Boiled chicken", 1.0, 1.0),
                        new PrepData("fried", "Deep fried chicken", 1.4, 2.0)));

        seedFood("chicken-breast", "Chicken Breast", "Meat & Poultry", "🍗", "100g serving", "g",
                false, false, true, true, 165.0, 31.0, 0.0, 3.6, 0.0, 0.0, 74.0, 85.0,
                List.of("chicken breast", "boneless chicken", "grilled chicken breast"),
                List.of(new PrepData("grilled", "Grilled skinless breast", 1.0, 1.0),
                        new PrepData("boiled", "Boiled breast", 1.0, 1.0),
                        new PrepData("baked", "Oven roasted breast", 1.05, 1.1)));

        seedFood("paneer", "Paneer", "Dairy & Eggs", "🧀", "100g serving", "g",
                true, false, true, false, 265.0, 18.3, 1.2, 20.8, 0.0, 1.2, 18.0, 60.0,
                List.of("paneer", "cottage cheese", "indian cottage cheese"),
                List.of(new PrepData("raw", "Fresh raw paneer cubes", 1.0, 1.0),
                        new PrepData("grilled", "Grilled paneer tikka", 1.1, 1.2),
                        new PrepData("fried", "Deep fried paneer", 1.3, 1.6)));
    }

    private void seedFood(String slug, String name, String category, String emoji,
                          String servingSize, String servingUnit,
                          boolean vegetarian, boolean vegan, boolean glutenFree, boolean lactoseFree,
                          double calories, double protein, double carbs, double fat,
                          double fiber, double sugar, double sodium, double cholesterol,
                          List<String> aliases, List<PrepData> preps) {

        String normalizedName = FoodNutritionService.normalizeQuery(name);

        // Check if entity already exists to avoid duplicate inserts
        if (freshFoodRepository.findBySlug(slug).isPresent() ||
            nutritionRepository.findByNormalizedName(normalizedName).isPresent()) {
            return;
        }

        FreshFoodEntity food = new FreshFoodEntity();
        food.setSlug(slug);
        food.setName(name);
        food.setCommonName(name);
        food.setCategory(category);
        food.setEmoji(emoji);
        food.setVegetarian(vegetarian);
        food.setVegan(vegan);
        food.setGlutenFree(glutenFree);
        food.setLactoseFree(lactoseFree);

        FoodNutritionEntity nut = new FoodNutritionEntity();
        nut.setFood(food);
        nut.setFoodName(name);
        nut.setNormalizedName(normalizedName);
        nut.setCategory(category);
        nut.setServingSize(servingSize);
        nut.setServingUnit(servingUnit);
        nut.setServingDescription(servingSize);
        nut.setCalories(calories);
        nut.setProtein(protein);
        nut.setCarbohydrates(carbs);
        nut.setTotalFat(fat);
        nut.setFiber(fiber);
        nut.setSugar(sugar);
        nut.setSodium(sodium);
        nut.setCholesterol(cholesterol);
        nut.setSource("USDA Nutritional Reference (Development Data)");
        nut.setSourceUrl("https://fdc.nal.usda.gov/");
        nut.setVerified(false); // Clearly marked as development/test data per requirement 3

        food.setNutrition(nut);
        freshFoodRepository.save(food);

        if (aliases != null) {
            for (String a : aliases) {
                FoodAliasEntity aliasEntity = new FoodAliasEntity(food, a, "en");
                aliasRepository.save(aliasEntity);
            }
        }

        if (preps != null) {
            for (PrepData p : preps) {
                FoodPreparationEntity prepEntity = new FoodPreparationEntity(food, p.name, p.description, p.calorieMult, p.fatMult);
                preparationRepository.save(prepEntity);
            }
        }
    }

    private void seedPackagedDemoProducts() {
        try {
            if (packagedProductRepository.findByBarcode("8901058851234").isEmpty()) {
                PackagedProductEntity p = new PackagedProductEntity();
                p.setBarcode("8901058851234");
                p.setProductName("Amul Malai Fresh Paneer");
                p.setBrand("Amul");
                p.setQuantity("200g");
                p.setServingSize("100g");
                p.setCategories("Paneer & Dairy, Fresh Dairy");
                p.setIngredientsText("Pasteurized Toned Milk, Coagulant (Citric Acid). Contains no added preservatives or colors.");
                p.setAllergens("Milk");
                p.setDietCategory("VEGETARIAN");
                p.setEnergyKcal(289.0);
                p.setProtein(18.0);
                p.setCarbohydrates(2.0);
                p.setSugar(1.2);
                p.setFat(25.0);
                p.setSaturatedFat(15.0);
                p.setTransFat(0.0);
                p.setFiber(0.0);
                p.setSodium(45.0);
                String rawJson = "{\"code\":\"8901058851234\",\"product\":{\"product_name\":\"Amul Malai Fresh Paneer\",\"brands\":\"Amul\",\"quantity\":\"200g\",\"serving_size\":\"100g\",\"categories\":\"Paneer & Dairy\",\"ingredients_text\":\"Pasteurized Toned Milk, Coagulant (Citric Acid). Contains no added preservatives or colors.\",\"nutriments\":{\"energy-kcal_100g\":289,\"proteins_100g\":18,\"carbohydrates_100g\":2,\"sugars_100g\":1.2,\"fat_100g\":25,\"saturated-fat_100g\":15,\"sodium_100g\":0.045}},\"status\":1}";
                p.setRawJson(rawJson);
                packagedProductRepository.save(p);
                System.out.println("🌱 [SEEDER] Seeded demo product 8901058851234 (Amul Malai Fresh Paneer) into Aiven MySQL.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to seed demo packaged product: " + e.getMessage());
        }
    }

    private static class PrepData {
        String name;
        String description;
        double calorieMult;
        double fatMult;

        PrepData(String name, String description, double calorieMult, double fatMult) {
            this.name = name;
            this.description = description;
            this.calorieMult = calorieMult;
            this.fatMult = fatMult;
        }
    }
}
