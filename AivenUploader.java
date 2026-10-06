import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AivenUploader {

    private static final String DB_URL = System.getenv().getOrDefault("SPRING_DATASOURCE_URL", 
            System.getProperty("db.url", "jdbc:mysql://localhost:3306/defaultdb"));
    private static final String DB_USER = System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", 
            System.getProperty("db.user", "root"));
    private static final String DB_PASS = System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", 
            System.getProperty("db.pass", ""));
    private static final String CSV_FILE = System.getProperty("csv.file", "uploaded_products.csv");

    public static void main(String[] args) {
        System.out.println("Connecting to Aiven Cloud MySQL database...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                System.out.println(" Connected to Aiven MySQL successfully!");

                // Check table columns to be sure
                try (ResultSet rs = conn.getMetaData().getColumns(null, null, "packaged_products", null)) {
                    System.out.println("Existing columns in 'packaged_products':");
                    while (rs.next()) {
                        System.out.print(rs.getString("COLUMN_NAME") + " ");
                    }
                    System.out.println();
                }

                String insertSql = "INSERT INTO packaged_products (" +
                        "barcode, product_name, brand, quantity, serving_size, categories, " +
                        "ingredients_text, allergens, diet_category, energy_kcal, protein, " +
                        "carbohydrates, sugar, fat, saturated_fat, trans_fat, fiber, sodium, " +
                        "raw_json, created_at, updated_at" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW()) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "product_name=VALUES(product_name), brand=VALUES(brand), quantity=VALUES(quantity), " +
                        "serving_size=VALUES(serving_size), categories=VALUES(categories), " +
                        "ingredients_text=VALUES(ingredients_text), allergens=VALUES(allergens), " +
                        "diet_category=VALUES(diet_category), energy_kcal=VALUES(energy_kcal), " +
                        "protein=VALUES(protein), carbohydrates=VALUES(carbohydrates), sugar=VALUES(sugar), " +
                        "fat=VALUES(fat), saturated_fat=VALUES(saturated_fat), trans_fat=VALUES(trans_fat), " +
                        "fiber=VALUES(fiber), sodium=VALUES(sodium), raw_json=VALUES(raw_json), updated_at=NOW()";

                int count = 0;
                try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE));
                     PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                    String line = br.readLine(); // Header
                    while ((line = br.readLine()) != null) {
                        List<String> fields = parseCsvLine(line);
                        if (fields.size() < 19) {
                            System.err.println("Skipping malformed line: " + line);
                            continue;
                        }

                        stmt.setString(1, fields.get(0).trim()); // barcode
                        stmt.setString(2, fields.get(1).trim()); // product_name
                        stmt.setString(3, fields.get(2).trim()); // brand
                        stmt.setString(4, fields.get(3).trim()); // quantity
                        stmt.setString(5, fields.get(4).trim()); // serving_size
                        stmt.setString(6, fields.get(5).trim()); // categories
                        stmt.setString(7, fields.get(6).trim()); // ingredients_text
                        stmt.setString(8, fields.get(7).trim()); // allergens
                        stmt.setString(9, fields.get(8).trim()); // diet_category
                        stmt.setObject(10, parseDouble(fields.get(9))); // energy_kcal
                        stmt.setObject(11, parseDouble(fields.get(10))); // protein
                        stmt.setObject(12, parseDouble(fields.get(11))); // carbohydrates
                        stmt.setObject(13, parseDouble(fields.get(12))); // sugar
                        stmt.setObject(14, parseDouble(fields.get(13))); // fat
                        stmt.setObject(15, parseDouble(fields.get(14))); // saturated_fat
                        stmt.setObject(16, parseDouble(fields.get(15))); // trans_fat
                        stmt.setObject(17, parseDouble(fields.get(16))); // fiber
                        stmt.setObject(18, parseDouble(fields.get(17))); // sodium
                        stmt.setString(19, fields.get(18).trim()); // raw_json

                        stmt.addBatch();
                        count++;
                    }

                    int[] results = stmt.executeBatch();
                    System.out.println(" Successfully inserted/updated " + results.length + " products into Aiven MySQL!");
                }

                // Verify count in database
                try (var s = conn.createStatement();
                     var r = s.executeQuery("SELECT count(*) FROM packaged_products")) {
                    if (r.next()) {
                        System.out.println(" Total products in Aiven MySQL 'packaged_products' table now: " + r.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> parseCsvLine(String line) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                list.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        list.add(sb.toString());
        return list;
    }
}
