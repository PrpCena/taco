//package sia.tacocloud.data;
//
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCreator;
//import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//import org.springframework.stereotype.Repository;
//import sia.tacocloud.Taco;
//import sia.tacocloud.Ingredient;
//
//import java.sql.Timestamp;
//import java.sql.Types;
//import java.util.Arrays;
//import java.util.Date;
//
//@Repository
//public class JdbcTacoRepository implements TacoRepository {
//
//    private JdbcTemplate jdbc;
//
//    @Autowired
//    public JdbcTacoRepository(JdbcTemplate jdbc) {
//        this.jdbc = jdbc;
//    }
//
//
//    @Override
//    public Taco save(Taco taco) {
//        long tacoId = saveTacoInfo(taco);
//        taco.setId(tacoId);
//
//        for(Ingredient ingredient: taco.getIngredients()){
//            saveIngredientToTaco(ingredient, tacoId);
//        }
//
//        return taco;
//    }
//
//
//    private long saveTacoInfo(Taco taco) {
//        taco.setCreatedAt(new Date());
//
//        PreparedStatementCreatorFactory pscFactory =
//                new PreparedStatementCreatorFactory(
//                        "insert into Taco(name, createdAt) values (?, ?)",
//                        Types.VARCHAR, Types.TIMESTAMP
//                );
//
//        // ✅ Enable returning generated keys
//        pscFactory.setReturnGeneratedKeys(true);
//
//        PreparedStatementCreator psc = pscFactory.newPreparedStatementCreator(
//                Arrays.asList(taco.getName(), new Timestamp(taco.getCreatedAt().getTime()))
//        );
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbc.update(psc, keyHolder);
//
//        Number key = keyHolder.getKey();
//        if (key == null) {
//            throw new IllegalStateException("Failed to retrieve generated key for taco insert.");
//        }
//
//        return key.longValue();
//    }
//
//
//    private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
//        jdbc.update("insert into Taco_Ingredients (taco, ingredient) " + "values (?, ?)", tacoId, ingredient.getId());
//    }
//}