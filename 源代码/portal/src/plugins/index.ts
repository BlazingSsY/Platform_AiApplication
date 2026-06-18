/*
 * @Description:
 * @Author: hui
 * @Date: 2021-05-11 11:01:51
 * @LastEditTime: 2024-03-01 10:08:49
 */
import { downLoadFile, formatFileSize, parseFileSizeUnit } from './auth';
import date from './date';
import { setSession, getSession, removeSession } from './session';
import { ElMessageBox, ElMessage } from 'element-plus';

export default function installPlugins(app: any) {
	app.config.globalProperties.conversionTime = date;
	app.config.globalProperties.setSession = setSession;
	app.config.globalProperties.getSession = getSession;
	app.config.globalProperties.removeSession = removeSession;
	app.config.globalProperties.downLoadFile = downLoadFile;
	app.config.globalProperties.formatFileSize = formatFileSize;
	app.config.globalProperties.parseFileSizeUnit = parseFileSizeUnit;
	app.config.globalProperties.$confirm = ElMessageBox;
	app.config.globalProperties.$message = ElMessage;
}
