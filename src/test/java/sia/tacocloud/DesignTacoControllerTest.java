package sia.tacocloud;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import sia.tacocloud.data.IngredientRepository;
import sia.tacocloud.data.OrderRepository;
import sia.tacocloud.data.TacoRepository;
import sia.tacocloud.data.UserRepository;
import sia.tacocloud.web.DesignTacoController;

@WebMvcTest(DesignTacoController.class)
public class DesignTacoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private List<Ingredient> ingredients;
    private Taco design;
    private User testUser;

    @MockitoBean
    private IngredientRepository ingredientRepository;

    @MockitoBean
    private TacoRepository designRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        ingredients = Arrays.asList(
                ing("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                ing("COTO", "Corn Tortilla", Ingredient.Type.WRAP),
                ing("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                ing("CARN", "Carnitas", Ingredient.Type.PROTEIN),
                ing("TMTO", "Diced Tomatoes", Ingredient.Type.VEGGIES),
                ing("LETC", "Lettuce", Ingredient.Type.VEGGIES),
                ing("CHED", "Cheddar", Ingredient.Type.CHEESE),
                ing("JACK", "Monterrey Jack", Ingredient.Type.CHEESE),
                ing("SLSA", "Salsa", Ingredient.Type.SAUCE),
                ing("SRCR", "Sour Cream", Ingredient.Type.SAUCE)
        );

        when(ingredientRepository.findAll()).thenReturn(ingredients);
        when(ingredientRepository.findById("FLTO")).thenReturn(Optional.of(ing("FLTO", "Flour Tortilla", Ingredient.Type.WRAP)));
        when(ingredientRepository.findById("GRBF")).thenReturn(Optional.of(ing("GRBF", "Ground Beef", Ingredient.Type.PROTEIN)));
        when(ingredientRepository.findById("CHED")).thenReturn(Optional.of(ing("CHED", "Cheddar", Ingredient.Type.CHEESE)));

        testUser = new User("testuser", "password", "Test User", "123 Street", "Test City", "TS", "12345", "123-456-7890");
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        design = new Taco();
        design.setName("Test Taco");
        design.setIngredients(Arrays.asList(
                ing("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                ing("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                ing("CHED", "Cheddar", Ingredient.Type.CHEESE)
        ));
    }

    private Ingredient ing(String id, String name, Ingredient.Type type) {
        return new Ingredient(id, name, type);
    }

    @Test
    @DisplayName("should show taco design form with categorized ingredients")
    @WithMockUser(username = "testuser")
    public void testShowDesignForm() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(model().attribute("wrap", ingredients.subList(0, 2)))
                .andExpect(model().attribute("protein", ingredients.subList(2, 4)))
                .andExpect(model().attribute("veggies", ingredients.subList(4, 6)))
                .andExpect(model().attribute("cheese", ingredients.subList(6, 8)))
                .andExpect(model().attribute("sauce", ingredients.subList(8, 10)))
                .andExpect(model().attribute("user", testUser));
    }

    @Test
    @DisplayName("should process taco design and redirect to /orders")
    @WithMockUser(username = "testuser")
    public void testProcessDesign() throws Exception {
        when(designRepository.save(design)).thenReturn(design);

        mockMvc.perform(post("/design")
                        .with(csrf())
                        .content("name=Test+Taco&ingredients=FLTO,GRBF,CHED")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("order", new Order())
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/current"));
    }

    @Test
    @DisplayName("should return to design form on validation error")
    @WithMockUser(username = "testuser")
    public void testProcessDesignWithValidationErrors() throws Exception {
        mockMvc.perform(post("/design")
                        .with(csrf())
                        .content("name=&ingredients=") // Empty fields
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("order", new Order()))
                .andExpect(status().isOk())
                .andExpect(view().name("design"));
    }

    @Test
    @DisplayName("should forbid request without CSRF token")
    @WithMockUser(username = "testuser")
    public void testProcessDesignWithoutCsrf() throws Exception {
        mockMvc.perform(post("/design")
                        .content("name=Test+Taco&ingredients=FLTO,GRBF,CHED")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isForbidden());
    }
}