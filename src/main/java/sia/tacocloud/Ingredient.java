package sia.tacocloud;

// lombok responsible for generating methods like getter, setter, toString, hashCode at run time
// we can choose not to use it, but it comes in handy.
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Ingredient {

    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }

}
