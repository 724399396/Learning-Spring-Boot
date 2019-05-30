package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.util.SimpleMethodInvocation;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;

@Slf4j
@RequiredArgsConstructor
public class Authorization {
    private final ISpringWebFluxContext context;
    private final SecurityExpressionHandler<MethodInvocation> handler;

    public boolean expr(String accessExpression) {
        Authentication authentication =
                (Authentication) this.context.getExchange().getPrincipal().block();

        log.debug("Checking if user \"{}\" meets expr \"{}\".",
                new Object[]{
                        (authentication == null ? null : authentication.getName()),
                        accessExpression
                });

        /**
         * In case this expression is specified as a standard
         * variable expression (${...}), clean it.
         */
        String expr =
                ((accessExpression != null &&
                        accessExpression.startsWith("${") &&
                        accessExpression.endsWith("}")) ?
                        accessExpression.substring(2, accessExpression.length() - 1) :
                        accessExpression);
        try {
            if (ExpressionUtils.evaluateAsBoolean(
                    handler.getExpressionParser().parseExpression(expr),
                    handler.createEvaluationContext(authentication,
                            new SimpleMethodInvocation()))) {
                log.debug("Checked \"{}\" for user \"{}\". " +
                        "Access GRANTED", new Object[]{
                        accessExpression,
                        (authentication == null ? null : authentication.getName())});
                return true;
            } else {
                log.debug("Checked \"{}\" for user \"{}\". " +
                                "Access DENIED",
                        new Object[]{
                                accessExpression,
                                (authentication == null ?
                                        null : authentication.getName())});
                return false;
            }
        } catch (ParseException e) {
            throw new TemplateProcessingException(
                    "An error happend parse \"" + expr + "\"", e
            );
        }
    }
}
