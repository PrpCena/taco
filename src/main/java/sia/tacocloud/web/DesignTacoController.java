package sia.tacocloud.web;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.Errors;

import jakarta.validation.Valid;

import sia.tacocloud.Order;
import sia.tacocloud.Taco;
import sia.tacocloud.Ingredient;
import sia.tacocloud.Ingredient.Type;
import sia.tacocloud.data.IngredientRepository;
import sia.tacocloud.data.TacoRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private final TacoRepository tacoRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo,  TacoRepository tacoRepo) {
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }
    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    // 🟢 Utility method to add ingredient groups to the model
    private void addIngredientsToModel(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(i -> ingredients.add(i));

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
    }

    @GetMapping
    public String showDesignForm(Model model) {
        addIngredientsToModel(model);
        model.addAttribute("design", new Taco());
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid @ModelAttribute("design") Taco design, Errors errors, @ModelAttribute Order order, Model model) {
        if (errors.hasErrors()) {
            addIngredientsToModel(model); // 🟢 re-populate data on error
            return "design";
        }

        Taco saved = tacoRepo.save(design);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
