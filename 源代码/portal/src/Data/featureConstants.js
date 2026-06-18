/* eslint-disable  */
import circuit from '@/assets/images/circuit.png';
import circuitHover from '@/assets/images/circuit-hover.png';
import code from '@/assets/images/code-review.png';
import codeHover from '@/assets/images/code-review-hover.png';
import tcg from '@/assets/images/tcg.png';
import tcgHover from '@/assets/images/tcg-hover.png';
import sag from '@/assets/images/sag.png';
import sagHover from '@/assets/images/sag-hover.png';
import bglr from '@/assets/images/bglr.png';
import bglrHover from '@/assets/images/bglr-hover.png';
import elg from '@/assets/images/elg.png';
import elgHover from '@/assets/images/elg-hover.png';
import erds from '@/assets/images/erds.png';
import erdsHover from '@/assets/images/erds-hover.png';
import trr from '@/assets/images/trr.png';
import trrHover from '@/assets/images/trr-hover.png';

import pcb from '@/assets/images/pcb.png';
import pcbHover from '@/assets/images/pcb-hover.png';
import pcba from '@/assets/images/pcba.png';
import pcbaHover from '@/assets/images/pcba-hover.png';

import dsr from '@/assets/images/dsr.png';
import dsrHover from '@/assets/images/dsr-hover.png';
import crr from '@/assets/images/crr.png';
import crrHover from '@/assets/images/crr-hover.png';
import asc from '@/assets/images/asc.png';
import ascHover from '@/assets/images/asc-hover.png';
import pdkb from '@/assets/images/pdkb.png';
import pdkbHover from '@/assets/images/pdkb-hover.png';

import cn from '@/assets/images/cn.png';
import cnHover from '@/assets/images/cn-hover.png';
import icn from '@/assets/images/icn.png';
import icnHover from '@/assets/images/icn-hover.png';
import qs from '@/assets/images/qs.png';
import qsHover from '@/assets/images/qs-hover.png';
import rl from '@/assets/images/rl.png';
import rlHover from '@/assets/images/rl-hover.png';


// 设计
export const designFeatures = [
    {
        icon: circuitHover,
        hoverIcon: circuitHover,
        title: '电路原理图智能审查',
        subtitle: 'Intelligent Review of Circuit Diagrams',
        router:`/circuitReviewHome`,
        isAuth:true
    },
    {
        icon: codeHover,
        hoverIcon: codeHover,
        title: '软件代码智能审查',
        subtitle: 'Intelligent Review of Software Code',
        router:`/codeReviewHome`,
        isAuth:true
    },
    // {
    //     icon: tcg,
    //     hoverIcon: tcgHover,
    //     title: '测试用例生成',
    //     subtitle: 'Test Case Generation',
    // },
    // {
    //     icon: bglr,
    //     hoverIcon: bglrHover,
    //     title: '黑灰名单审查',
    //     subtitle: 'Black and Grey List Review',
    // },
    {
        icon: elgHover,
        hoverIcon: elgHover,
        title: '硬件逻辑智能审查',
        subtitle: 'Intelligent Review of Hardware Logic',
        router:`/logicReviewHome`,
        isAuth:true
    },
    {
        icon: erds,
        hoverIcon: erdsHover,
        title: '产品经济性审查',
        subtitle: 'Product Cost-Effectiveness Review'
    }
    // ,
    // {
    //     icon: trr,
    //     hoverIcon: trrHover,
    //     title: '型号规则审查',
    //     subtitle: 'Type Rule Review',
    // }
];

// 生产
export const productionFeatures = [
    {
        icon: pcb,
        hoverIcon: pcbHover,
        title: '电装工艺生成',
        subtitle: 'Electrical Assembly Process Generation'
    },
    {
        icon: pcba,
        hoverIcon: pcbaHover,
        title: 'PCBA智能智检',
        subtitle: 'Intelligent Inspection for PCBA'
    }
];

// 管理
export const managementFeatures = [
    {
        icon: pdkb,
        hoverIcon: pdkbHover,
        title: 'OfficeAI办公插件',
        subtitle: 'OfficeAI Plugin'
    },
    {
        icon: dsr,
        hoverIcon: dsrHover,
        title: '文档保密审查',
        subtitle: 'Document Confidentiality Review'
    },
    {
        icon: crr,
        hoverIcon: crrHover,
        title: '合同风险审查',
        subtitle: 'Contract Risk Review'
    },
    {
        icon: asc,
        hoverIcon: ascHover,
        title: '航空标准云',
        subtitle: 'Aviation Standard Cloud'
    }
    // ,
    // {
    //     icon: pdkb,
    //     hoverIcon: pdkbHover,
    //     title: '私域知识库',
    //     subtitle: 'Private Domain Knowledge Base'
    // }
];

// 算力
export const computingFeatures = [
    {
        icon: cn,
        hoverIcon: cnHover,
        title: '金航网',
        subtitle: 'Jin Hang Network'
    },
    {
        icon: icn,
        hoverIcon: icnHover,
        title: '工控网',
        subtitle: 'Gong Kong Network'
    },
    // {
    //     icon: qs,
    //     hoverIcon: qsHover,
    //     title: '千山',
    //     subtitle: 'Qianshan'
    // },
    // {
    //     icon: rl,
    //     hoverIcon: rlHover,
    //     title: '资源清单',
    //     subtitle: 'Resource List'
    // }
];
// 系统
export const systemFeatures = [
    {
        icon: cn,
        hoverIcon: cnHover,
        title: '用户管理',
        subtitle: 'Campus Network',
        router:`/userList`,
        isAuth:true
    },
    {
        icon: cn,
        hoverIcon: cnHover,
        title: '组织管理',
        subtitle: 'Campus Network',
        router:`/depList`,
        isAuth:true
    },
    // {
    //     icon: cn,
    //     hoverIcon: cnHover,
    //     title: '菜单管理',
    //     subtitle: 'Campus Network',
    //     router:`/menu`,
    //     isAuth:true
    // }
];

// 一级菜单
export const dropdownItems = [
    {
        name: '设计',
        features: designFeatures
    },
    {
        name: '生产',
        features: productionFeatures
    },
    {
        name: '管理',
        features: managementFeatures
    },
    {
        name: '算力',
        features: computingFeatures
    },
    {
        name: '系统',
        features: systemFeatures
    },
];
