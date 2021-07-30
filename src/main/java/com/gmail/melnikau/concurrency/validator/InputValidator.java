package com.gmail.melnikau.concurrency.validator;

import com.gmail.melnikau.concurrency.constant.Constants;
import com.gmail.melnikau.concurrency.constant.Messages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class InputValidator {

    private static final Logger LOGGER = LogManager.getLogger();

    private InputValidator() {

    }

    public static Map<String, Integer> getIntProperties(String configFileName) {
        Optional<Properties> properties = getProperties(configFileName);

        return properties.map(InputValidator::validateProperties).orElse(null);
    }

    private static Optional<Properties> getProperties(String propertiesFile) {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(propertiesFile);

        try {
            properties.load(stream);

            return Optional.of(properties);
        } catch (IOException e) {
            LOGGER.error(String.format(Messages.ERROR_LOADING_RESOURCES_MESSAGE, propertiesFile));

            return Optional.empty();
        } catch (NullPointerException e) {
            LOGGER.error(String.format(Messages.FILE_NOT_FOUND_MESSAGE, propertiesFile));

            return Optional.empty();
        }
    }

    private static Map<String, Integer> validateProperties(Properties properties) {
        return Constants.REQUIRED_PARAMETERS
                .stream()
                .collect(
                        Collectors.toMap(
                                propertyName -> propertyName, propertyName -> {
                                    String propertyValue = properties.computeIfAbsent(propertyName, Constants.DEFAULT_PARAMETERS_MAP::get).toString();
                                    return validatePropertyRange(propertyName, propertyValue);
                                }
                        )
                );
    }

    private static int validatePropertyRange(String propertyName, String propertyValue) {
        boolean isInputInteger = Pattern
                .compile(Constants.REG_EXP_FOR_NON_NEGATIVE_INTEGER_NUMBERS)
                .matcher(propertyValue)
                .matches();

        if (isInputInteger && Integer.parseInt(propertyValue) >= Constants.DEFAULT_PARAMETERS_MAP.get(propertyName)) {

            return Integer.parseInt(propertyValue);
        } else {
            LOGGER.error(String.format(Messages.INCORRECT_PROPERTY_VALUE, propertyName, propertyValue));

            return Constants.DEFAULT_PARAMETERS_MAP.get(propertyName);
        }
    }

}