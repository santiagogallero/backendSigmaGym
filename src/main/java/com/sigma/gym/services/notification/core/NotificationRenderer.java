package com.sigma.gym.services.notification.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template renderer for notification messages with variable substitution
 */
@Component
@Slf4j
public class NotificationRenderer {
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    /**
     * Render template with variables
     * @param template template string with ${variable} placeholders
     * @param variables map of variables to substitute
     * @return rendered string
     */
    public String render(String template, Map<String, Object> variables) {
        if (!StringUtils.hasText(template)) {
            return "";
        }
        
        if (variables == null || variables.isEmpty()) {
            return template;
        }
        
        try {
            return replaceVariables(template, variables);
        } catch (Exception e) {
            log.warn("Failed to render template: {}, variables: {}", template, variables, e);
            return template; // Return original template if rendering fails
        }
    }
    
    /**
     * Render subject and body with variables
     * @param subject subject template
     * @param body body template
     * @param variables map of variables to substitute
     * @return rendered subject and body
     */
    public RenderedTemplate render(String subject, String body, Map<String, Object> variables) {
        String renderedSubject = render(subject, variables);
        String renderedBody = render(body, variables);
        
        return new RenderedTemplate(renderedSubject, renderedBody);
    }
    
    private String replaceVariables(String template, Map<String, Object> variables) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            
            String replacement;
            if (value != null) {
                replacement = value.toString();
                // Escape special regex characters in replacement
                replacement = replacement.replace("$", "\\$").replace("\\", "\\\\");
            } else {
                // Keep placeholder if variable not found
                replacement = matcher.group(0);
                log.debug("Variable '{}' not found in template variables", variableName);
            }
            
            matcher.appendReplacement(result, replacement);
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    /**
     * Extract variable names from template
     * @param template template string
     * @return set of variable names found in template
     */
    public java.util.Set<String> extractVariables(String template) {
        if (!StringUtils.hasText(template)) {
            return java.util.Set.of();
        }
        
        java.util.Set<String> variables = new java.util.HashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        
        return variables;
    }
    
    /**
     * Validate template has all required variables
     * @param template template string
     * @param requiredVariables required variable names
     * @return true if all required variables are present
     */
    public boolean validateTemplate(String template, java.util.Set<String> requiredVariables) {
        if (requiredVariables == null || requiredVariables.isEmpty()) {
            return true;
        }
        
        java.util.Set<String> templateVariables = extractVariables(template);
        return templateVariables.containsAll(requiredVariables);
    }
    
    /**
     * Rendered template result
     */
    public static class RenderedTemplate {
        private final String subject;
        private final String body;
        
        public RenderedTemplate(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getBody() {
            return body;
        }
    }
}
