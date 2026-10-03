package com.healthscan.config;

import com.healthscan.entity.*;
import com.healthscan.repository.*;
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
        seedPackagedProducts();

        // Idempotent seeding check for fresh food
        if (freshFoodRepository.count() >= 5) {
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

    private void seedPackagedProducts() {
        if (packagedProductRepository.count() > 0) {
            return;
        }

        System.out.println("🌱 Seeding initial Indian packaged products into Aiven MySQL DB...");

        savePackaged("8901058000052", "Amul Taaza Toned Milk", "Amul", "500 ml", "200 ml", "Dairy, Milk",
                "Toned Milk, Vitamin A, Vitamin D", "Milk", "VEGETARIAN", 59.0, 3.1, 4.7, 4.7, 3.2, 2.0, 0.0, 0.0, 50.0);

        savePackaged("8901063011111", "Parle-G Glucose Biscuits", "Parle", "100 g", "30 g", "Snacks, Biscuits",
                "Wheat Flour, Sugar, Refined Palm Oil, Invert Sugar Syrup, Raising Agents, Salt, Milk Solids", "Wheat, Milk", "VEGETARIAN", 464.0, 6.5, 78.2, 26.3, 14.3, 6.8, 0.0, 2.1, 280.0);

        savePackaged("8901058852304", "Maggi 2-Minute Noodles", "Nestle", "70 g", "70 g", "Noodles, Fast Food",
                "Wheat Flour, Palm Oil, Salt, Wheat Gluten, Mineral, Garlic Powder, Onion Powder, Spices", "Wheat", "VEGETARIAN", 427.0, 8.0, 63.5, 2.2, 15.7, 6.8, 0.1, 3.6, 820.0);

        savePackaged("8901030000010", "Tata Salt Iodized", "Tata", "1 kg", "1 g", "Spices, Salt",
                "Edible Common Salt, Potassium Iodate, Anticaking Agent (INS 536)", "None", "VEGETARIAN", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 38700.0);

        savePackaged("8901491000015", "Kurkure Masala Munch", "PepsiCo", "90 g", "30 g", "Snacks, Chips",
                "Rice Meal, Corn Meal, Gram Meal, Edible Vegetable Oil, Seasoning (Spices, Salt, Sugar, Mango Powder)", "None", "VEGETARIAN", 558.0, 5.8, 54.0, 1.5, 35.6, 15.3, 0.1, 1.8, 870.0);

        System.out.println("✅ Successfully seeded initial Indian packaged products into Aiven MySQL!");
    }

    private void savePackaged(String barcode, String name, String brand, String qty, String serving,
                              String cat, String ing, String allergens, String diet,
                              double kcal, double protein, double carbs, double sugar, double fat,
                              double satFat, double transFat, double fiber, double sodium) {

        PackagedProductEntity p = new PackagedProductEntity();
        p.setBarcode(barcode);
        p.setProductName(name);
        p.setBrand(brand);
        p.setQuantity(qty);
        p.setServingSize(serving);
        p.setCategories(cat);
        p.setIngredientsText(ing);
        p.setAllergens(allergens);
        p.setDietCategory(diet);
        p.setEnergyKcal(kcal);
        p.setProtein(protein);
        p.setCarbohydrates(carbs);
        p.setSugar(sugar);
        p.setFat(fat);
        p.setSaturatedFat(satFat);
        p.setTransFat(transFat);
        p.setFiber(fiber);
        p.setSodium(sodium);

        String json = String.format("{\"code\":\"%s\",\"status\":1,\"product\":{\"product_name\":\"%s\",\"brands\":\"%s\",\"quantity\":\"%s\",\"serving_size\":\"%s\",\"ingredients_text\":\"%s\",\"nutriments\":{\"energy-kcal_100g\":%.1f,\"proteins_100g\":%.1f,\"carbohydrates_100g\":%.1f,\"sugars_100g\":%.1f,\"fat_100g\":%.1f,\"sodium_100g\":%.1f}}}",
                barcode, name, brand, qty, serving, ing.replace("\"", "'"), kcal, protein, carbs, sugar, fat, sodium);
        p.setRawJson(json);

        packagedProductRepository.save(p);
    }

    private void seedFood(String slug, String name, String category, String emoji,
                          String servingSize, String servingUnit,
                          boolean vegetarian, boolean vegan, boolean glutenFree, boolean lactoseFree,
                          double calories, double protein, double carbs, double fat,
                          double fiber, double sugar, double sodium, double cholesterol,
                          List<String> aliases, List<PrepData> preps) {

        String normalizedName = FoodNutritionService.normalizeQuery(name);

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
        nut.setVerified(false);

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
