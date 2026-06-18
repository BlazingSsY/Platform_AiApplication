/**
 * 代码显示组件的属性接口
 */
export interface CodeDisplayProps {
  /** 要显示的代码字符串 */
  code: string;
  /** 需要高亮的行号数组（从1开始） */
  highlightedLines: number[];
  /** 是否显示行号，默认true */
  showLineNumbers?: boolean;
  /** 代码语言类型，支持 'c' | 'cpp' | 'java' | 'python' | 'javascript' | 'typescript' | 'html' | 'css' | 'json' */
  language?: string;
  /** 最大高度，默认'400px' */
  maxHeight?: string;
}

/**
 * 代码行数据结构
 */
export interface CodeLine {
  /** 行号（从1开始） */
  lineNumber: number;
  /** 该行的代码内容 */
  content: string;
  /** 是否高亮显示 */
  isHighlighted: boolean;
}

/**
 * Dialog包装器组件的属性接口
 */
export interface CodeDisplayDialogProps {
  /** 控制Dialog显示/隐藏的v-model */
  modelValue: boolean;
  /** Dialog标题 */
  title?: string;
  /** Dialog宽度，默认'800px' */
  width?: string;
  /** 是否可通过点击modal关闭Dialog，默认false */
  closeOnClickModal?: boolean;
  /** Dialog关闭时的回调，默认空函数 */
  onClose?: () => void;
  /** Dialog打开时的回调，默认空函数 */
  onOpen?: () => void;
  /** 显示在footer区域的文字 */
  footerText?: string;
  /** 代码显示组件的属性 */
  codeDisplayProps: Omit<CodeDisplayProps, 'maxHeight'> & {
    maxHeight?: CodeDisplayProps['maxHeight'];
  };
}

/**
 * 语言显示名称映射
 */
export interface LanguageDisplayMap {
  [key: string]: string;
}

/**
 * 预设的语言显示名称映射
 */
export const LANGUAGE_DISPLAY_MAP: LanguageDisplayMap = {
  'c': 'C',
  'cpp': 'C++',
  'java': 'Java',
  'python': 'Python',
  'javascript': 'JavaScript',
  'typescript': 'TypeScript',
  'html': 'HTML',
  'css': 'CSS',
  'json': 'JSON',
  'go': 'Go',
  'rust': 'Rust',
  'php': 'PHP',
  'sql': 'SQL',
  'shell': 'Shell',
  'yaml': 'YAML',
  'xml': 'XML',
  'markdown': 'Markdown'
};


// Light主题配置常量
export const LIGHT_THEME_CONFIG = {
  name: 'light' as const,
  backgroundColor: '#f6f8fa',
  textColor: '#24292e',
  borderColor: '#e1e4e8',
  highlightBackground: 'rgba(255, 235, 59, 0.18)',
  highlightBorder: '#ffc107',
  lineNumberColor: '#6a737d',
  hoverBackground: 'rgba(0, 0, 0, 0.03)'
};

/**
 * 获取语言显示名称的函数类型
 */
export type GetLanguageDisplayNameFn = (language: string) => string;

/**
 * 解析代码为行数组的函数类型
 */
export type ParseCodeToLinesFn = (code: string, highlightedLines: number[]) => CodeLine[];