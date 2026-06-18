<template>
  <div class="code-display-dialog-test">
    <h1>代码显示对话框测试</h1>
    <div class="test-container">
      <div class="test-controls">
        <div class="form-group">
          <label for="language">语言:</label>
          <el-select v-model="language" placeholder="请选择语言">
            <el-option label="C" value="c"/>
            <el-option label="C++" value="cpp"/>
            <el-option label="Java" value="java"/>
          </el-select>
        </div>

        <div class="form-group">
          <el-button type="primary" @click="showTestCode1">
            显示测试代码1
          </el-button>
          <el-button type="success" @click="showTestCode2">
            显示测试代码2
          </el-button>
          <el-button type="warning" @click="showTestCode3">
            显示测试代码3
          </el-button>
        </div>

        <div class="form-group">
          <label for="highlightedLines">高亮行号:</label>
          <el-input
            v-model="highlightedLinesInput"
            placeholder="例如: 1,3,5-8,10"
            @change="updateHighlightedLines"
          />
          <small class="helper-text">多个行号用逗号分隔，范围用连字符（如 5-8）</small>
        </div>

        <div class="form-group">
          <el-checkbox v-model="showLineNumbers">显示行号</el-checkbox>
        </div>
      </div>

      <div class="test-description">
        <h3>测试说明</h3>
        <p>这个页面用于测试 <code>CodeDisplayDialog</code> 组件的功能。</p>
        <ul>
          <li>点击不同的按钮可以显示不同的测试代码</li>
          <li>可以修改代码语言</li>
          <li>可以设置高亮显示的行号</li>
          <li>可以控制是否显示行号</li>
        </ul>
      </div>
    </div>

    <!-- CodeDisplayDialog 组件 -->
    <CodeDisplayDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      :width="dialogWidth"
      :code-display-props="codeDisplayProps"
      :footerText="footerText"
      @ok="handleDialogOk"
      @cancel="handleDialogCancel"
      @line-click="handleLineClick"
      @copy="handleCodeCopy"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import CodeDisplayDialog from '@/views/codeReview/components/CodeDisplay/CodeDisplayDialog.vue';

// 对话框状态
const dialogVisible = ref(false);
const dialogTitle = ref('代码显示对话框测试');
const dialogWidth = ref('960px');
const footerText = ref(`软件检测实验室在建立软件测试体系或申请cnas/cma相关资质时，需要依据相关标准，使用有效的方法开展检验检测活动，GJB-8114是一部嵌入式软件安全测试相关的国家标准，本系列文章我们就针对GJB-8114《C/C++语言编程安全子集》的具体内容进行分析解读。`)

// 代码显示属性
const language = ref('c');
const showLineNumbers = ref(true);
const highlightedLinesInput = ref('1,3,5-8,10');
const highlightedLines = ref<number[]>([1, 3, 5, 6, 7, 8, 10]);

// 解析高亮行号输入
const updateHighlightedLines = () => {
  const lines: number[] = [];
  const parts = highlightedLinesInput.value.split(',');

  for (const part of parts) {
    const trimmed = part.trim();
    if (!trimmed) continue;

    if (trimmed.includes('-')) {
      const [startStr, endStr] = trimmed.split('-').map(s => s.trim());
      const start = parseInt(startStr, 10);
      const end = parseInt(endStr, 10);

      if (!isNaN(start) && !isNaN(end) && start <= end) {
        for (let i = start; i <= end; i++) {
          lines.push(i);
        }
      }
    } else {
      const lineNum = parseInt(trimmed, 10);
      if (!isNaN(lineNum)) {
        lines.push(lineNum);
      }
    }
  }

  highlightedLines.value = lines;
};

// 计算属性：代码显示属性
const codeDisplayProps = computed(() => ({
  code: currentCode.value,
  highlightedLines: highlightedLines.value,
  showLineNumbers: showLineNumbers.value,
  language: language.value,
  theme: 'light' as const,
  maxHeight: '400px'
}));

// 测试代码
const testCode1 = `#include <stdio.h>
#include <stdlib.h>

// 简单的C语言示例
int main() {
    int n = 10;
    int sum = 0;

    // 计算1到10的和
    for (int i = 1; i <= n; i++) {
        sum += i;
    }

    printf("1到10的和是: %d\\n", sum);

    // 返回成功
    return 0;
}

void exampleFunction() {
    // 这是一个示例函数
    int x = 5;
    int y = 10;
    int result = x + y;

    if (result > 10) {
        printf("结果大于10\\n");
    } else {
        printf("结果小于等于10\\n");
    }
}`;

const testCode2 = `// Java示例代码
import java.util.ArrayList;
import java.util.List;

public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {
public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {public class Calculator {
    // 计算阶乘
    public static int factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    // 判断素数
    public static boolean isPrime(int n) {public static boolean isPrime(int n) {public static boolean isPrime(int n) {public static boolean isPrime(int n) {public static boolean isPrime(int n) {public static boolean isPrime(int n) {public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;

        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println("5的阶乘是: " + factorial(5));System.out.println("5的阶乘是: " + factorial(5));System.out.println("5的阶乘是: " + factorial(5));System.out.println("5的阶乘是: " + factorial(5));System.out.println("5的阶乘是: " + factorial(5));System.out.println("5的阶乘是: " + factorial(5));System.out.println("5的阶乘是: " + factorial(5));
        System.out.println("17是素数吗? " + isPrime(17));
    }
}`;

const testCode3 = `// C++示例代码
#include <iostream>
#include <vector>
#include <cmath>
#include <numeric>

class DataProcessor {
private:
    std::vector<double> data;

public:
    DataProcessor(const std::vector<double>& inputData) : data(inputData) {}

    double calculateMean() const {
        if (data.empty()) return 0.0;
        double sum = std::accumulate(data.begin(), data.end(), 0.0);
        return sum / data.size();
    }

    double calculateStd() const {
        if (data.empty()) return 0.0;

        double mean = calculateMean();
        double sumSquaredDiff = 0.0;

        for (double value : data) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }

        return std::sqrt(sumSquaredDiff / data.size());
    }

    std::vector<double> filterByThreshold(double threshold) const {
        std::vector<double> result;
        for (double value : data) {
            if (value > threshold) {
                result.push_back(value);
            }
        }
        return result;
    }
};

int fibonacci(int n) {
    if (n <= 0) return 0;
    if (n == 1) return 1;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

int main() {
    DataProcessor processor({1.2, 2.3, 3.4, 4.5, 5.6});

    std::cout << "平均值: " << processor.calculateMean() << std::endl;
    std::cout << "标准差: " << processor.calculateStd() << std::endl;
    std::cout << "斐波那契(10): " << fibonacci(10) << std::endl;

    return 0;
}`;

const currentCode = ref('');


// 显示测试代码的方法
const showTestCode1 = () => {
  currentCode.value = testCode1;
  language.value = 'c';
  dialogTitle.value = 'C语言代码测试';
  dialogVisible.value = true;
};

const showTestCode2 = () => {
  currentCode.value = testCode2;
  language.value = 'java';
  dialogTitle.value = 'Java代码测试';
  dialogVisible.value = true;
};

const showTestCode3 = () => {
  currentCode.value = testCode3;
  language.value = 'cpp';
  dialogTitle.value = 'C++代码测试';
  dialogVisible.value = true;
};

// 对话框事件处理
const handleDialogOk = () => {
  // ElMessage.success('确认按钮被点击');
  dialogVisible.value = false;
};

const handleDialogCancel = () => {
  // ElMessage.info('取消按钮被点击');
  dialogVisible.value = false;
};

const handleLineClick = (lineNumber: number) => {
  // ElMessage.info(`点击了第 ${lineNumber} 行`);
};

const handleCodeCopy = (code: string) => {
  // ElMessage.success(`已复制代码到剪贴板，共 ${code.length} 个字符`);
};

// 初始化高亮行号
updateHighlightedLines();
</script>

<style scoped lang="scss">
.code-display-dialog-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  h1 {
    color: #333;
    text-align: center;
    margin-bottom: 30px;
  }
}

.test-container {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.test-controls {
  flex: 1;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .form-group {
    margin-bottom: 20px;

    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color: #333;
    }

    .el-select,
    .el-input {
      width: 100%;
    }

    .helper-text {
      display: block;
      margin-top: 4px;
      color: #666;
      font-size: 12px;
    }

    .el-button {
      margin-right: 10px;
      margin-bottom: 10px;
    }
  }
}

.test-description {
  flex: 1;
  background-color: #e8f4fd;
  padding: 20px;
  border-radius: 8px;

  h3 {
    color: #0066cc;
    margin-bottom: 15px;
  }

  p, li {
    color: #333;
    line-height: 1.6;
  }

  ul {
    padding-left: 20px;
  }

  li {
    margin-bottom: 8px;
  }

  code {
    background-color: #f1f1f1;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
  }
}
</style>