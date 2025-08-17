package com.sigma.gym.utils;

import com.sigma.gym.repository.WorkoutPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs from workout plan names
 * Handles slug uniqueness per owner and provides methods for slug conflict resolution
 */
@Component
public class SlugUtils {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    // Pattern to match non-alphanumeric characters except hyphens
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\-]");
    
    // Pattern to match multiple consecutive hyphens
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");
    
    // Maximum slug length
    private static final int MAX_SLUG_LENGTH = 100;

    /**
     * Generate a base slug from a workout plan name
     * @param name The workout plan name
     * @return A URL-friendly slug
     */
    public String generateBaseSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "workout-plan";
        }

        // Convert to lowercase and remove accents
        String slug = removeAccents(name.toLowerCase().trim());
        
        // Replace spaces and special characters with hyphens
        slug = slug.replaceAll("\\s+", "-");
        slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("");
        
        // Remove multiple consecutive hyphens
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
        
        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");
        
        // Ensure slug is not empty
        if (slug.isEmpty()) {
            slug = "workout-plan";
        }
        
        // Truncate if too long
        if (slug.length() > MAX_SLUG_LENGTH) {
            slug = slug.substring(0, MAX_SLUG_LENGTH);
            // Remove trailing hyphen if truncation created one
            slug = slug.replaceAll("-+$", "");
        }
        
        return slug;
    }

    /**
     * Generate a unique slug for a workout plan within an owner's scope
     * @param ownerId The owner's ID
     * @param name The workout plan name
     * @return A unique slug
     */
    public String generateUniqueSlug(Long ownerId, String name) {
        String baseSlug = generateBaseSlug(name);
        
        // Check if base slug is already unique
        if (!workoutPlanRepository.existsByOwner_IdAndSlug(ownerId, baseSlug)) {
            return baseSlug;
        }
        
        // Find all similar slugs to determine the next available suffix
        List<String> similarSlugs = workoutPlanRepository.findSimilarSlugsByOwnerId(ownerId, baseSlug);
        
        // Find the highest suffix number
        int maxSuffix = 0;
        Pattern suffixPattern = Pattern.compile("^" + Pattern.quote(baseSlug) + "-(\\d+)$");
        
        for (String existingSlug : similarSlugs) {
            if (existingSlug.equals(baseSlug)) {
                maxSuffix = Math.max(maxSuffix, 1);
            } else {
                var matcher = suffixPattern.matcher(existingSlug);
                if (matcher.matches()) {
                    int suffix = Integer.parseInt(matcher.group(1));
                    maxSuffix = Math.max(maxSuffix, suffix);
                }
            }
        }
        
        // Generate next available slug
        return baseSlug + "-" + (maxSuffix + 1);
    }

    /**
     * Generate a unique slug for updating an existing workout plan
     * Excludes the current plan from uniqueness check
     * @param ownerId The owner's ID
     * @param name The new workout plan name
     * @param currentSlug The current slug to exclude from conflict check
     * @return A unique slug
     */
    public String generateUniqueSlugForUpdate(Long ownerId, String name, String currentSlug) {
        String baseSlug = generateBaseSlug(name);
        
        // If the base slug is the same as current, keep it
        if (baseSlug.equals(currentSlug)) {
            return currentSlug;
        }
        
        // Check if base slug is unique (excluding current)
        List<String> similarSlugs = workoutPlanRepository.findSimilarSlugsByOwnerId(ownerId, baseSlug);
        similarSlugs.remove(currentSlug); // Exclude current slug
        
        if (similarSlugs.isEmpty() || !similarSlugs.contains(baseSlug)) {
            return baseSlug;
        }
        
        // Find the highest suffix number (excluding current slug)
        int maxSuffix = 0;
        Pattern suffixPattern = Pattern.compile("^" + Pattern.quote(baseSlug) + "-(\\d+)$");
        
        for (String existingSlug : similarSlugs) {
            if (existingSlug.equals(baseSlug)) {
                maxSuffix = Math.max(maxSuffix, 1);
            } else {
                var matcher = suffixPattern.matcher(existingSlug);
                if (matcher.matches()) {
                    int suffix = Integer.parseInt(matcher.group(1));
                    maxSuffix = Math.max(maxSuffix, suffix);
                }
            }
        }
        
        // Generate next available slug
        return baseSlug + "-" + (maxSuffix + 1);
    }

    /**
     * Validate that a slug follows the correct format
     * @param slug The slug to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }
        
        // Check length
        if (slug.length() > MAX_SLUG_LENGTH) {
            return false;
        }
        
        // Check format: only lowercase letters, numbers, and hyphens
        // Cannot start or end with hyphen, no consecutive hyphens
        return slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$");
    }

    /**
     * Remove accents and diacritical marks from text
     * @param text The text to process
     * @return Text without accents
     */
    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        
        // Normalize to NFD (canonical decomposition)
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        
        // Remove diacritical marks
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Convert a slug back to a readable name (for display purposes)
     * @param slug The slug to convert
     * @return A readable name
     */
    public String slugToReadableName(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return "Workout Plan";
        }
        
        // Replace hyphens with spaces and capitalize words
        String[] words = slug.split("-");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }
        
        return result.toString();
    }
}
