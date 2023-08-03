package com.example.BuySellApplication.functions;

import com.example.BuySellApplication.configurations.DataUnavailableException;
import com.example.BuySellApplication.models.User;
import com.example.BuySellApplication.services.ProductService;
import com.example.BuySellApplication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/recommendations")
    public String showRecommendations(Model model, Principal principal) {
        User currentUser = userService.getUserByPrincipal(principal);
        if (currentUser == null) {
            // Handle the case when the user is not logged in
            return "redirect:/login"; // Or return an appropriate view for non-logged-in users
        }

        List<Long> recommendedProductIds = recommendationService.getRecommendedProductIds(currentUser);
        model.addAttribute("recommendedProducts", recommendedProductIds);
        model.addAttribute("user", currentUser);

        return "recommendations";
    }
    @ExceptionHandler(DataUnavailableException.class)
    public ResponseEntity<String> handleDataUnavailableException(DataUnavailableException ex) {
        // Custom response for data unavailability
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
