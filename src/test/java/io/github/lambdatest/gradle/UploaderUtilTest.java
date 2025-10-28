package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Minimal unit tests for {@link UploaderUtil} class. */
class UploaderUtilTest {

    @Test
    void constructor_ShouldNotBeInstantiable() {
        // When/Then - Utility class should not be instantiable
        assertThatThrownBy(
                        () -> {
                            // Use reflection to try to create instance
                            var constructor = UploaderUtil.class.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            constructor.newInstance();
                        })
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}
