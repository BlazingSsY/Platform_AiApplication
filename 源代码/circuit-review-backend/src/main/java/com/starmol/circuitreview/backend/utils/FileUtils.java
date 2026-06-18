package com.starmol.circuitreview.backend.utils;

import com.starmol.circuitreview.backend.bean.bo.FileCheckResult;
import com.starmol.circuitreview.backend.constant.DeploymentTypeEnum;

public class FileUtils {

    private static final String JZ_EXTENSION = ".atel";
    private static final String ACTRI_EXTENSION = ".tel";

    public static FileCheckResult checkCircuitFileExtension(Integer deploymentType, String fileName) {
        FileCheckResult result = new FileCheckResult();
        if(deploymentType.equals(DeploymentTypeEnum.JI_ZAI.getValue())) {
            if(fileName.toLowerCase().endsWith(JZ_EXTENSION) || fileName.toLowerCase().endsWith(ACTRI_EXTENSION)) {
                result.setValid(true);
            }
            else {
                result.setValid(false);
                result.setExpectedExtension(String.format("%s或者%s", JZ_EXTENSION, ACTRI_EXTENSION));
            }
        }
        else {
            if(fileName.toLowerCase().endsWith(ACTRI_EXTENSION)) {
                result.setValid(true);
            }
            else {
                result.setValid(false);
                result.setExpectedExtension(ACTRI_EXTENSION);
            }
        }
        return result;
    }



    /**
     * 提取文件后缀
     *
     * @param fileName 文件名
     * @return 文件后缀（包含点号）
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
}
