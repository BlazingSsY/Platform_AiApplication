import { HashRouter, Navigate, Route, Routes } from 'react-router-dom';
import PortalLayout from '@/layouts/PortalLayout';
import ReviewLayout from '@/layouts/ReviewLayout';
import HomePage from '@/pages/portal/HomePage';
import ManagementPage from '@/pages/common/ManagementPage';
import ReviewHome from '@/pages/review/ReviewHome';
import UploadReviewPage from '@/pages/review/UploadReviewPage';
import ReviewListPage from '@/pages/review/ReviewListPage';
import ReviewResultPage from '@/pages/review/ReviewResultPage';
import RuleListPage from '@/pages/review/RuleListPage';
import ToolDownloadPage from '@/pages/review/ToolDownloadPage';
import StatisticsPage from '@/pages/review/StatisticsPage';
import FeedbackPage from '@/pages/review/FeedbackPage';
import LogPage from '@/pages/review/LogPage';
import { reviewModules } from '@/routes/reviewRoutes';

export default function App() {
  return (
    <HashRouter>
      <Routes>
        <Route element={<PortalLayout />}>
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/userList" element={<ManagementPage type="userList" />} />
          <Route path="/userAddEdit" element={<ManagementPage type="userList" />} />
          <Route path="/userDetail" element={<ManagementPage type="userList" />} />
          <Route path="/depList" element={<ManagementPage type="depList" />} />
          <Route path="/depAddEdit" element={<ManagementPage type="depList" />} />
          <Route path="/depDetail" element={<ManagementPage type="depList" />} />
          <Route path="/applicationManage" element={<ManagementPage type="applicationManage" />} />
          <Route path="/informationManage" element={<ManagementPage type="informationManage" />} />
          <Route path="/applicationList" element={<ManagementPage type="applicationList" />} />
          <Route path="/applicationDetail" element={<ManagementPage type="applicationList" />} />
          <Route path="/menu" element={<ManagementPage type="menu" />} />
          <Route path="/menuEdit" element={<ManagementPage type="menu" />} />
          <Route path="/appDetail" element={<ManagementPage type="applicationManage" />} />
        </Route>

        {reviewModules.map((module) => (
          <Route key={module.key} element={<ReviewLayout />}>
            <Route path={module.basePath} element={<Navigate to={module.homePath} replace />} />
            <Route path={module.homePath} element={<ReviewHome />} />
            <Route path={module.uploadPath} element={<UploadReviewPage />} />
            <Route path={module.listPath} element={<ReviewListPage />} />
            <Route path={module.detailPath} element={<ReviewListPage />} />
            <Route path={module.statisticsPath} element={<StatisticsPage />} />
            <Route path={module.toolPath} element={<ToolDownloadPage />} />
            <Route path={module.resultPath} element={<ReviewResultPage />} />
            <Route path={module.rulePath} element={<RuleListPage />} />
            <Route path={module.feedbackPath} element={<FeedbackPage />} />
            <Route path={module.logPath} element={<LogPage />} />
          </Route>
        ))}
      </Routes>
    </HashRouter>
  );
}
