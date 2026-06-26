import circuitBanner from '@/assets/images/circuitReview/bannerV2.png';
import circuitLogo from '@/assets/images/circuitReview/logo-hz.png';
import codeBanner from '@/assets/images/codeReview/bannerV2.png';
import codeLogo from '@/assets/images/codeReview/logo-code.png';
import logicBanner from '@/assets/images/logicReview/ljscBanner.png';
import logicLogo from '@/assets/images/logicReview/logo-ljsc-ht.png';
import type { ReviewModuleConfig } from '@/types';

export const reviewModules: ReviewModuleConfig[] = [
  {
    key: 'circuit',
    title: '电路智能审查',
    shortTitle: '电路审查',
    basePath: '/warp',
    homePath: '/circuitReviewHome',
    uploadPath: '/documentReview',
    listPath: '/reviewList',
    detailPath: '/reviewDetail',
    statisticsPath: '/statisticalAnalysis',
    toolPath: '/toolDownload',
    resultPath: '/reviewResults',
    rulePath: '/ruleList',
    feedbackPath: '/feedback',
    logPath: '/viewLog',
    homeApi: '/circuitreview/v1/circuit-statistics/home',
    apiPrefix: '/circuitreview',
    banner: circuitBanner,
    logo: circuitLogo,
  },
  {
    key: 'code',
    title: '软件代码智能审查',
    shortTitle: '代码审查',
    basePath: '/codeWarp',
    homePath: '/codeReviewHome',
    uploadPath: '/sourceCodeDocumentReview',
    listPath: '/sourceCodeReviewList',
    detailPath: '/sourceCodeReviewDetail',
    statisticsPath: '/sourceCodeStatistics',
    toolPath: '/sourceCodeToolDownload',
    resultPath: '/sourceCodeReviewResults',
    rulePath: '/sourceCodeRuleList',
    feedbackPath: '/sourceCodeFeedback',
    logPath: '/sourceCodeViewLog',
    homeApi: '/sourcecodereview/v1/source-code-review-statistics/home',
    apiPrefix: '/sourcecodereview',
    banner: codeBanner,
    logo: codeLogo,
  },
  {
    key: 'logic',
    title: '硬件逻辑智能审查',
    shortTitle: '逻辑审查',
    basePath: '/logicWarp',
    homePath: '/logicReviewHome',
    uploadPath: '/logicDocumentReview',
    listPath: '/logicReviewList',
    detailPath: '/logicReviewDetail',
    statisticsPath: '/logicStatistics',
    toolPath: '/logicToolDownload',
    resultPath: '/logicReviewResults',
    rulePath: '/logicRuleList',
    feedbackPath: '/logicFeedback',
    logPath: '/logicViewLog',
    homeApi: '/logicreview/v1/logic-review-statistics/home',
    apiPrefix: '/logicreview',
    banner: logicBanner,
    logo: logicLogo,
  },
];

export const internalPaths = new Set<string>([
  '/home',
  '/userList',
  '/userAddEdit',
  '/userDetail',
  '/depList',
  '/depAddEdit',
  '/depDetail',
  '/applicationManage',
  '/informationManage',
  '/applicationList',
  '/applicationDetail',
  '/menu',
  '/menuEdit',
  '/appDetail',
  ...reviewModules.flatMap((module) => [
    module.basePath,
    module.homePath,
    module.uploadPath,
    module.listPath,
    module.detailPath,
    module.statisticsPath,
    module.toolPath,
    module.resultPath,
    module.rulePath,
    module.feedbackPath,
    module.logPath,
  ]),
]);

export function findModuleByPath(pathname: string) {
  return reviewModules.find((module) =>
    [
      module.basePath,
      module.homePath,
      module.uploadPath,
      module.listPath,
      module.detailPath,
      module.statisticsPath,
      module.toolPath,
      module.resultPath,
      module.rulePath,
      module.feedbackPath,
      module.logPath,
    ].includes(pathname),
  );
}
