package sia.tacocloud.web;
import java.security.Principal;
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
import sia.tacocloud.User;
import sia.tacocloud.data.IngredientRepository;
import sia.tacocloud.data.TacoRepository;
import sia.tacocloud.data.UserRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private final TacoRepository tacoRepo;
    private final UserRepository userRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo,  TacoRepository tacoRepo,  UserRepository userRepository) {
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
        this.userRepository = userRepository;
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name = "design")
    public Taco design() {
        return new Taco();
    }


    private void addIngredientsToModel(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(i -> ingredients.add(i));

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
    }

    @GetMapping
    public String showDesignForm(Model model, Principal principal) {
        addIngredientsToModel(model);
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Taco taco, Errors errors, @ModelAttribute Order order, Model model, Principal principal) {
        if (errors.hasErrors()) {
            addIngredientsToModel(model);
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            model.addAttribute("user", user);
            return "design";
        }

        Taco saved = tacoRepo.save(taco);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
