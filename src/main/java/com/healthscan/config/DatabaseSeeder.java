package com.healthscan.config;

import com.healthscan.entity.PackagedProductEntity;
import com.healthscan.entity.RecipeEntity;
import com.healthscan.repository.PackagedProductRepository;
import com.healthscan.repository.RecipeRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder {

    private final RecipeRepository recipeRepository;
    private final PackagedProductRepository packagedProductRepository;

    public DatabaseSeeder(RecipeRepository recipeRepository,
                          PackagedProductRepository packagedProductRepository) {
        this.recipeRepository = recipeRepository;
        this.packagedProductRepository = packagedProductRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        seedPackagedDemoProducts();
        seedRecipes();
    }

    private void seedRecipes() {
        try {
            if (recipeRepository.count() >= 15) {
                System.out.println("⚡ [SEEDER] Aiven MySQL recipes table already populated (" + recipeRepository.count() + " recipes).");
                return;
            }

            List<RecipeEntity> recipes = List.of(
                // BREAKFAST (5:00 - 10:59)
                new RecipeEntity(
                    "bf-veg-paneer-bhurji",
                    "High-Protein Paneer Bhurji with Multigrain Toast",
                    "breakfast",
                    "veg,eggetarian",
                    "muscle_gain,fat_loss,maintenance,high_energy",
                    "dairy,gluten",
                    "Packed with 26g of fresh vegetarian protein and complex carbs to ignite your morning metabolism.",
                    "150g fresh low-fat paneer (crumbled)\n1 small onion & 1 medium tomato (finely chopped)\n1 green chilli & 1/2 tsp grated ginger\n1/2 tsp turmeric powder & 1/2 tsp garam masala\n1 tsp cold-pressed mustard oil or olive oil\n1 slice 100% multigrain or sourdough bread\nFresh coriander leaves for garnish",
                    "Heat 1 tsp oil in a pan, add ginger, green chillies, and chopped onions until soft and translucent.\nAdd chopped tomatoes, turmeric, and salt. Cook for 2-3 minutes until tomatoes turn mushy.\nToss in crumbled paneer and garam masala. Sauté gently on medium heat for 2-3 minutes (avoid overcooking).\nGarnish with freshly chopped coriander and serve hot alongside a lightly toasted multigrain bread slice.",
                    360.0, 26.0, 24.0, 16.0,
                    "12 minutes"
                ),
                new RecipeEntity(
                    "bf-veg-moong-chilla",
                    "Spinach & Moong Dal Chilla with Mint Chutney",
                    "breakfast",
                    "veg,vegan,eggetarian",
                    "fat_loss,maintenance,high_energy,low_sugar",
                    "",
                    "100% plant-based, gluten-free, and rich in slow-digesting fiber to keep cravings away until lunchtime.",
                    "1/2 cup yellow moong dal (soaked for 2 hours and blended to a smooth batter)\n1/2 cup finely shredded fresh spinach (palak)\n1/2 tsp grated ginger & pinch of asafoetida (hing)\n1/2 tsp cumin powder & pink rock salt to taste\n1 tsp cold-pressed oil for pan-searing\n2 tbsp homemade mint-coriander chutney",
                    "Mix shredded spinach, ginger, hing, cumin powder, and salt directly into the blended moong dal batter.\nHeat a non-stick or seasoned cast iron tawa on medium heat and smear a few drops of oil.\nPour a ladle of batter and spread into a thin, round crepe (chilla). Cook for 2 minutes until crisp.\nFlip gently, lightly brown the second side, and serve warm with refreshing fresh mint chutney.",
                    275.0, 16.0, 38.0, 5.0,
                    "15 minutes"
                ),
                new RecipeEntity(
                    "bf-nonveg-egg-scramble",
                    "Herb Scrambled Eggs with Avocado & Whole Wheat Toast",
                    "breakfast",
                    "non-veg,eggetarian",
                    "fat_loss,muscle_gain,maintenance,high_energy",
                    "eggs,gluten",
                    "Top-tier bioavailable protein paired with heart-healthy monounsaturated fats for sustained alertness.",
                    "3 large organic eggs (or 2 whole eggs + 2 egg whites)\n1/4 ripe avocado (sliced or mashed)\n1 slice toasted whole wheat sourdough\n1/2 tsp unsalted butter or olive oil\nSea salt, cracked black pepper, and fresh chives to taste",
                    "Whisk eggs in a bowl with a pinch of salt and cracked pepper until frothy.\nMelt butter in a non-stick pan over low heat; pour in eggs and gently push with a spatula to form soft curds.\nRemove from heat while eggs are still creamy and tender; transfer immediately onto the toast.\nTop with sliced avocado, cracked pepper, and fresh chives.",
                    385.0, 24.0, 22.0, 21.0,
                    "10 minutes"
                ),
                new RecipeEntity(
                    "bf-vegan-tofu-scramble",
                    "Turmeric Tofu Scramble with Sautéed Mushrooms",
                    "breakfast",
                    "vegan,veg,eggetarian",
                    "fat_loss,muscle_gain,maintenance,low_sugar",
                    "soy,mushrooms",
                    "Clean vegan protein with anti-inflammatory turmeric and zero cholesterol for a nutrient-dense breakfast.",
                    "180g firm organic tofu (pressed and crumbled with a fork)\n1/2 cup sliced button mushrooms\n1/2 tsp ground turmeric & 1/2 tsp black pepper\n1 tbsp nutritional yeast (optional for savoury cheesy flavor)\n1 tsp olive oil\nSalt to taste and fresh cilantro",
                    "Heat olive oil in a skillet, add sliced mushrooms and sauté for 3 minutes until browned.\nAdd crumbled tofu, turmeric, black pepper, and nutritional yeast.\nSauté on medium flame for 4-5 minutes until warm and fragrant.\nSeason with salt, garnish with fresh herbs, and serve warm with fresh tomato slices.",
                    280.0, 23.0, 10.0, 14.0,
                    "12 minutes"
                ),

                // MID-MORNING SNACK (11:00 - 11:59)
                new RecipeEntity(
                    "mm-veg-sprouts-chaat",
                    "Zesty Moong Sprouts & Pomegranate Chaat",
                    "mid_morning",
                    "veg,vegan,eggetarian,non-veg",
                    "fat_loss,maintenance,high_energy,low_sugar,heart_healthy",
                    "",
                    "Crisp, refreshing, and enzymatically active to beat mid-morning hunger without any sugar crashes.",
                    "1 cup steamed green moong sprouts\n2 tbsp fresh pomegranate pearls\n1/4 cucumber & 1/4 tomato (finely diced)\n1/2 tsp roasted cumin powder & chaat masala\n1 tsp freshly squeezed lemon juice",
                    "Steam green moong sprouts for 3 minutes so they are tender yet retain their crunch.\nIn a medium bowl, combine sprouts, diced cucumber, tomato, and pomegranate pearls.\nSprinkle roasted cumin powder, chaat masala, and drizzle fresh lemon juice.\nToss well and enjoy immediately for an energizing snack.",
                    145.0, 9.0, 26.0, 1.0,
                    "5 minutes"
                ),
                new RecipeEntity(
                    "mm-veg-curd-walnuts",
                    "Greek Yogurt with Crushed Walnuts & Chia Seeds",
                    "mid_morning",
                    "veg,eggetarian",
                    "muscle_gain,fat_loss,maintenance,high_energy",
                    "dairy,nuts",
                    "Creamy probiotic protein paired with brain-boosting Omega-3 fatty acids for peak mid-morning focus.",
                    "150g plain unsweetened Greek yogurt (or hung curd)\n4 whole walnut halves (lightly crushed)\n1 tsp chia seeds\nPinch of cinnamon powder (optional)",
                    "Spoon chilled Greek yogurt into a bowl.\nTop with crushed walnuts and sprinkle chia seeds evenly.\nDust a pinch of cinnamon powder over the top and enjoy cold.",
                    195.0, 16.0, 8.0, 11.0,
                    "3 minutes"
                ),
                new RecipeEntity(
                    "mm-nonveg-boiled-eggs",
                    "Hard-Boiled Eggs with Black Pepper & Cucumber Rounds",
                    "mid_morning",
                    "non-veg,eggetarian",
                    "fat_loss,muscle_gain,maintenance",
                    "eggs",
                    "Zero-sugar, pure whole-food protein bite that holds you firmly until lunch.",
                    "2 hard-boiled eggs (peeled)\n1/2 chilled cucumber (sliced into thick rounds)\nBlack salt and freshly crushed black pepper to taste",
                    "Slice hard-boiled eggs in halves.\nArrange cucumber slices alongside egg halves.\nDust with black salt and freshly cracked black pepper.",
                    155.0, 13.0, 4.0, 10.0,
                    "5 minutes"
                ),

                // LUNCH (12:00 - 15:29)
                new RecipeEntity(
                    "lu-veg-rajma-brownrice",
                    "Slow-Cooked Kashmiri Rajma with Steamed Brown Rice",
                    "lunch",
                    "veg,vegan,eggetarian",
                    "muscle_gain,maintenance,high_energy",
                    "",
                    "A complete amino acid profile of legumes and whole grains with prebiotic fiber for sustained afternoon fuel.",
                    "1 cup cooked red kidney beans (rajma in spiced tomato-ginger gravy)\n3/4 cup cooked brown rice or jeera rice\n1 cup sliced onion, cucumber, and lemon salad\n1 tsp cold-pressed mustard oil\nFresh ginger juliennes & coriander",
                    "Simmer cooked rajma in ginger, garlic, tomato, and aromatic spices until rich and flavorful.\nPortion warm steamed brown rice onto your plate.\nLadle the thick rajma gravy generously over the rice.\nServe with a crunchy side salad squeezed with fresh lemon.",
                    420.0, 18.0, 72.0, 6.0,
                    "20 minutes"
                ),
                new RecipeEntity(
                    "lu-nonveg-chicken-bowl",
                    "Grilled Herb Chicken Breast with Quinoa & Tossed Greens",
                    "lunch",
                    "non-veg",
                    "muscle_gain,fat_loss,maintenance",
                    "",
                    "Delivers a massive 36g of lean muscle-repair protein with minimal saturated fat and low glycemic index.",
                    "160g chicken breast (cut into strips or butterflied)\n1/2 cup cooked quinoa or brown rice\n1 cup sautéed zucchini, broccoli, and cherry tomatoes\n1 tsp olive oil\nGarlic powder, dried oregano, paprika, and sea salt",
                    "Season chicken breast with garlic, oregano, paprika, salt, and half the olive oil.\nSear on a hot skillet for 5-6 minutes per side until juicy, golden, and thoroughly cooked.\nToss vegetables in the same pan for 2 minutes to soak up the savoury pan juices.\nPlate the grilled chicken over warm quinoa alongside the vibrant greens.",
                    440.0, 38.0, 32.0, 12.0,
                    "18 minutes"
                ),
                new RecipeEntity(
                    "lu-veg-paneer-roti-bowl",
                    "Tawa Paneer Tikka with 2 Phulkas & Cucumber Raita",
                    "lunch",
                    "veg,eggetarian",
                    "muscle_gain,maintenance,fat_loss",
                    "dairy,gluten",
                    "Traditional wholesome balance of complex carbohydrates, high dairy protein, and cooling gut probiotics.",
                    "140g fresh paneer (diced into bite-sized cubes)\n1/2 cup curd + 1/2 tsp kasuri methi + 1/2 tsp deewani masala (for marinade)\n2 whole wheat phulkas (without excess ghee)\n1/2 cup fresh cucumber & mint raita",
                    "Coat paneer cubes in spiced curd marinade and let sit for 5 minutes.\nRoast on a hot tawa with a light brush of oil until edges are charred and aromatic.\nWarm 2 whole wheat phulkas directly on flame.\nAssemble with cooling cucumber raita and sliced onions.",
                    460.0, 27.0, 48.0, 17.0,
                    "20 minutes"
                ),

                // EVENING SNACK (15:30 - 18:29)
                new RecipeEntity(
                    "es-veg-roasted-makhana",
                    "Turmeric & Pepper Roasted Makhana (Fox Nuts)",
                    "evening_snack",
                    "veg,vegan,eggetarian,non-veg",
                    "fat_loss,maintenance,heart_healthy,low_sugar",
                    "",
                    "Ultra-low calorie, crunchy, and mineral-dense to kill evening salt cravings without ruining dinner appetite.",
                    "2 cups raw makhana (fox nuts)\n1/2 tsp pure ghee or coconut oil\n1/4 tsp turmeric powder\n1/4 tsp roasted black pepper & pink Himalayan salt",
                    "Heat ghee or coconut oil in a wide heavy-bottomed kadai on low heat.\nAdd turmeric, salt, and freshly cracked pepper.\nTip in the makhana and dry-roast on low flame for 6-8 minutes until crisp and crunchy.\nLet cool slightly and enjoy with a cup of green tea.",
                    160.0, 5.0, 28.0, 3.0,
                    "8 minutes"
                ),
                new RecipeEntity(
                    "es-veg-sattu-drink",
                    "Chilled Roasted Chana Sattu Buttermilk",
                    "evening_snack",
                    "veg,vegan,eggetarian",
                    "fat_loss,muscle_gain,high_energy",
                    "",
                    "Nature’s desi protein shake: instant cooling hydration with 11g pure plant protein and insoluble fiber.",
                    "3 tbsp roasted Bengal gram sattu powder\n1 glass chilled water (or 1/2 cup light curd + water)\n1/4 tsp black salt & roasted jeera powder\n1 tsp lemon juice & finely chopped fresh mint",
                    "In a tall glass or shaker, add sattu powder and a splash of water to form a lump-free paste.\nPour in remaining chilled water, black salt, and roasted jeera powder.\nStir vigorously, squeeze lemon juice, and garnish with fresh mint leaves.\nSip slowly for instant rejuvenating energy.",
                    140.0, 11.0, 21.0, 2.0,
                    "4 minutes"
                ),
                new RecipeEntity(
                    "es-nonveg-egg-toast",
                    "Boiled Egg White Chaat with Mint & Sev Crunch",
                    "evening_snack",
                    "non-veg,eggetarian",
                    "fat_loss,muscle_gain",
                    "eggs",
                    "16g of pure lean protein with near-zero fat to fuel your evening workout or commute.",
                    "4 hard-boiled egg whites (chopped into cubes)\n1 tbsp finely chopped onion & tomato\n1/2 tsp chaat masala & pinch of roasted cumin\n1 tsp lemon juice & fresh coriander",
                    "Boil and peel eggs; separate the whites and chop into bite-sized cubes.\nIn a small bowl, toss egg whites with onions, tomatoes, and coriander.\nSeason with chaat masala, cumin, and a squeeze of lemon juice.\nEat fresh for a tangy high-protein boost.",
                    110.0, 16.0, 4.0, 1.0,
                    "5 minutes"
                ),

                // DINNER (18:30 - 22:00)
                new RecipeEntity(
                    "dn-veg-paneer-veggies",
                    "Pan-Seared Paneer & Sautéed Mediterranean Veggies",
                    "dinner",
                    "veg,eggetarian",
                    "fat_loss,muscle_gain,maintenance",
                    "dairy",
                    "Slow-digesting casein protein that repairs tissues through the night while keeping carbohydrates low.",
                    "130g low-fat fresh paneer (cut into thick cubes)\n1 cup mixed bell peppers, zucchini, and french beans\n1 tsp olive oil or cold-pressed sesame oil\n1/2 tsp cumin seeds & black pepper to taste\nPinch of oregano and pink rock salt",
                    "Heat 1 tsp oil in a pan and splutter cumin seeds.\nAdd chopped vegetables and sauté on high heat for 3-4 minutes until tender-crisp.\nAdd paneer cubes, salt, oregano, and black pepper.\nGently toss for 2 minutes until paneer is warm and lightly golden. Enjoy warm.",
                    310.0, 23.0, 14.0, 17.0,
                    "12 minutes"
                ),
                new RecipeEntity(
                    "dn-veg-moong-khichdi",
                    "Comforting Moong Dal & Spinach Khichdi with Curd",
                    "dinner",
                    "veg,eggetarian",
                    "fat_loss,maintenance,heart_healthy,high_energy",
                    "dairy",
                    "Very gentle on the stomach late at night, preventing acid reflux while delivering comforting clean protein.",
                    "1/4 cup yellow split moong dal (washed)\n2 tbsp brown rice or rolled oats\n1 cup fresh spinach leaves (roughly chopped)\n1/2 tsp turmeric powder & pinch of hing (asafoetida)\n1 tsp pure cow ghee & 1/2 tsp jeera\n2 tbsp fresh homemade curd for serving",
                    "In a pressure cooker or pot, heat 1 tsp ghee and splutter cumin seeds with a pinch of hing.\nAdd washed moong dal, oats/rice, turmeric, salt, and 2.5 cups water.\nStir in the chopped spinach and cook for 2-3 whistles until porridge-soft.\nServe warm in a bowl accompanied by cooling fresh curd.",
                    295.0, 16.0, 42.0, 6.0,
                    "18 minutes"
                ),
                new RecipeEntity(
                    "dn-nonveg-fish-steamed",
                    "Steamed Fish Fillet with Lemon Garlic & Bok Choy",
                    "dinner",
                    "non-veg",
                    "fat_loss,muscle_gain,maintenance,heart_healthy",
                    "fish",
                    "Ultra-lean, easily digestible marine protein packed with anti-inflammatory Omega-3 for deep restorative sleep.",
                    "160g white fish fillet (basa, rohu, cod, or tilapia)\n1 cup bok choy or green beans\n1 tsp olive oil\n1 clove minced garlic & 1 tbsp fresh lemon juice\nCracked black pepper, sea salt, and fresh dill/coriander",
                    "Marinate fish fillet with minced garlic, lemon juice, salt, and black pepper for 5 minutes.\nSteam fish and greens together in a steamer basket or parchment pouch for 8-10 minutes.\nCheck that fish flakes easily with a fork.\nDrizzle with olive oil and serve fresh with lemon wedges.",
                    280.0, 34.0, 6.0, 10.0,
                    "15 minutes"
                ),
                new RecipeEntity(
                    "dn-vegan-tofu-soup",
                    "Warm Tofu & Edamame Clear Vegetable Broth",
                    "dinner",
                    "vegan,veg,eggetarian",
                    "fat_loss,maintenance,low_sugar",
                    "soy",
                    "Hydrating, low-calorie, and stomach-soothing with 21g complete plant protein and gut-friendly ginger broth.",
                    "150g soft/firm tofu (cut into bite-sized cubes)\n1/2 cup shelled edamame or green peas\n1 cup shredded cabbage, carrots, and mushrooms\n1 inch ginger (crushed) & 1 clove garlic\n2.5 cups vegetable broth or water\n1 tsp tamari / soy sauce & squeeze of lemon",
                    "In a soup pot, bring vegetable broth to a simmer with crushed ginger and garlic.\nAdd shredded cabbage, carrots, mushrooms, and edamame; simmer for 5 minutes.\nGently slide in tofu cubes and add soy sauce with a pinch of black pepper.\nSimmer for 2 more minutes, finish with fresh lemon, and sip piping hot.",
                    240.0, 21.0, 15.0, 9.0,
                    "14 minutes"
                ),

                // BEDTIME (After 22:00)
                new RecipeEntity(
                    "bt-veg-golden-milk",
                    "Warm Turmeric Golden Milk with Crushed Almonds",
                    "bedtime",
                    "veg,eggetarian",
                    "fat_loss,muscle_gain,maintenance,high_energy",
                    "dairy,nuts",
                    "Tryptophan and curcumin promote deep restful sleep while providing slow night-time recovery protein.",
                    "1 cup warm low-fat milk (or unsweetened almond milk for vegan)\n1/4 tsp organic turmeric powder\nPinch of black pepper (enhances curcumin absorption by 2000%)\nPinch of nutmeg or cardamom powder\n4 crushed raw almonds",
                    "Warm milk in a small saucepan over medium heat.\nWhisk in turmeric powder, black pepper, and cardamom.\nPour into a cup and top with crushed almonds.\nSip warm 30 minutes before sleep.",
                    145.0, 8.0, 12.0, 6.0,
                    "5 minutes"
                ),
                new RecipeEntity(
                    "bt-vegan-chamomile-walnuts",
                    "Calming Chamomile Infusion & Handful of Walnuts",
                    "bedtime",
                    "vegan,veg,eggetarian,non-veg",
                    "fat_loss,maintenance,heart_healthy",
                    "nuts",
                    "Zero sugar, herbal relaxation paired with natural plant melatonin to induce soothing circadian sleep.",
                    "1 cup hot brewed chamomile tea (caffeine-free)\n4 whole walnut halves (rich in natural melatonin)",
                    "Steep chamomile tea bag in hot water for 4-5 minutes.\nRemove tea bag without adding any sweetener.\nNibble the walnuts alongside warm tea.",
                    110.0, 3.0, 2.0, 10.0,
                    "4 minutes"
                )
            );

            recipeRepository.saveAll(recipes);
            System.out.println("🌱 [SEEDER] Successfully seeded " + recipes.size() + " recipes into Aiven MySQL database.");
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to seed recipes into Aiven MySQL: " + e.getMessage());
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
}
