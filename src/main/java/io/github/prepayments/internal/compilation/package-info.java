/**
 * This package contains logic for compilation of prepayments-data into prepayment-entries and amortization-entries
 * according to the number of corresponding number of periods. The package contains all logic including configuration
 * of the spring-batch processors, chains of compilation-processors and their interfaces.
 * Services used here however can be found in the services package, while the interfaces coded here are meant
 * to be called from the resource-controller package.
 *
 * Normally these assertions need to be backed by something robust like arch-unit but it is my believe that we
 * can be behave like gentlemen and just follow these rules. We will do that once we attain MVP
 *
 * TODO create Arch-unit tests for package rules
 */
package io.github.prepayments.internal.compilation;
