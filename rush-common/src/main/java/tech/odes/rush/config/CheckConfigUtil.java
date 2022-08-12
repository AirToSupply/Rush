package tech.odes.rush.config;

import org.apache.commons.lang3.StringUtils;
import tech.odes.rush.common.exception.RushException;

import java.util.*;
import java.util.stream.Collectors;

public class CheckConfigUtil {

    private CheckConfigUtil() {
    }

    public static CheckResult checkAllExists(Map config, String... params) {
        List<String> missingParams = Arrays.stream(params)
                .filter(param -> !isValidParam(config, param))
                .collect(Collectors.toList());

        if (!missingParams.isEmpty()) {
            String errorMsg = String.format("please specify [%s] as non-empty",
                    String.join(",", missingParams));
            return CheckResult.error(errorMsg);
        } else {
            return CheckResult.success();
        }
    }

    public static void assertAllExists(Map config, String... params) {
        CheckResult checkResult = checkAllExists(config, params);
        if (!checkResult.isSuccess()) {
            throw new RushException(checkResult.getMsg());
        }
    }

    public static CheckResult checkAllNotEmpty(String... params) {
        List<String> missingParams = Arrays.stream(params)
                .filter(param -> !isEmptyParam(param))
                .collect(Collectors.toList());

        if (!missingParams.isEmpty()) {
            String errorMsg = String.format("please specify [%s] as non-empty",
                    String.join(",", missingParams));
            return CheckResult.error(errorMsg);
        } else {
            return CheckResult.success();
        }
    }

    public static void assertAllNotEmpty(String... params) {
        CheckResult checkResult = checkAllNotEmpty(params);
        if (!checkResult.isSuccess()) {
            throw new RushException(checkResult.getMsg());
        }
    }

    public static CheckResult checkAtLeastOneExists(Map config, String... params) {
        if (params.length == 0) {
            return CheckResult.success();
        }

        List<String> missingParams = new LinkedList<>();
        for (String param : params) {
            if (!isValidParam(config, param)) {
                missingParams.add(param);
            }
        }

        if (missingParams.size() == params.length) {
            String errorMsg = String.format("please specify at least one config of [%s] as non-empty",
                String.join(",", missingParams));
            return CheckResult.error(errorMsg);
        } else {
            return CheckResult.success();
        }
    }

    public static boolean isValidParam(Map config, String param) {
        return config.containsKey(param);
    }

    public static boolean isEmptyParam(String param) {
        return StringUtils.isBlank(param);
    }

    public static CheckResult mergeCheckResults(CheckResult... checkResults) {
        List<CheckResult> notPassConfig = Arrays.stream(checkResults)
            .filter(item -> !item.isSuccess()).collect(Collectors.toList());
        if (notPassConfig.isEmpty()) {
            return CheckResult.success();
        } else {
            String errMessage = notPassConfig.stream().map(CheckResult::getMsg)
                .collect(Collectors.joining(","));
            return CheckResult.error(errMessage);
        }
    }
}
