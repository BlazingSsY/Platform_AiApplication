/**
 * C语言代码显示组件的常量定义
 */

/**
 * 简单的Hello World示例（符合用户要求：高亮第三行）
 */
export const MOCK_C_HELLO_WORLD = `#include <stdio.h>

int main() {
    printf("Hello, World!\\n");
    return 0;
}`;

/**
 * Hello World高亮行配置（高亮第三行）
 */
export const HIGHLIGHT_LINES_HELLO_WORLD = [3];

/**
 * 复杂的C语言示例 - 数据结构操作
 */
export const MOCK_C_DATA_STRUCTURE = `#include <stdio.h>
#include <stdlib.h>

#define MAX_SIZE 100

// 定义结构体
typedef struct {
    int data;
    struct Node* next;
} Node;

// 函数声明
Node* createNode(int value);
void insertNode(Node** head, int value);
void printList(Node* head);
int searchList(Node* head, int target);
void freeList(Node* head);

int main() {
    Node* head = NULL;

    // 插入节点
    for (int i = 1; i <= 5; i++) {
        insertNode(&head, i * 10);
    }

    printf("链表元素：");
    printList(head);

    // 搜索元素
    int target = 30;
    int position = searchList(head, target);
    if (position != -1) {
        printf("元素 %d 在链表中的位置：%d\\n", target, position);
    } else {
        printf("元素 %d 未找到\\n", target);
    }

    // 释放内存
    freeList(head);

    return 0;
}

Node* createNode(int value) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    if (!newNode) {
        printf("内存分配失败！\\n");
        exit(1);
    }
    newNode->data = value;
    newNode->next = NULL;
    return newNode;
}

void insertNode(Node** head, int value) {
    Node* newNode = createNode(value);
    newNode->next = *head;
    *head = newNode;
}

void printList(Node* head) {
    Node* current = head;
    while (current != NULL) {
        printf("%d ", current->data);
        current = current->next;
    }
    printf("\\n");
}

int searchList(Node* head, int target) {
    Node* current = head;
    int position = 1;
    while (current != NULL) {
        if (current->data == target) {
            return position;
        }
        current = current->next;
        position++;
    }
    return -1;
}

void freeList(Node* head) {
    Node* current = head;
    Node* next;
    while (current != NULL) {
        next = current->next;
        free(current);
        current = next;
    }
}`;

/**
 * 复杂示例的高亮行配置（高亮函数定义和关键逻辑）
 */
export const HIGHLIGHT_LINES_DATA_STRUCTURE = [12, 25, 38, 45, 52];

/**
 * C语言算法示例 - 快速排序
 */
export const MOCK_C_QUICKSORT = `#include <stdio.h>

// 交换两个元素
void swap(int* a, int* b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

// 分区函数
int partition(int arr[], int low, int high) {
    int pivot = arr[high];  // 选择最后一个元素作为基准
    int i = (low - 1);      // 较小元素的索引

    for (int j = low; j <= high - 1; j++) {
        // 如果当前元素小于或等于基准
        if (arr[j] <= pivot) {
            i++;            // 增加较小元素的索引
            swap(&arr[i], &arr[j]);
        }
    }
    swap(&arr[i + 1], &arr[high]);
    return (i + 1);
}

// 快速排序主函数
void quickSort(int arr[], int low, int high) {
    if (low < high) {
        // pi是分区索引，arr[pi]现在在正确位置
        int pi = partition(arr, low, high);

        // 分别对分区进行排序
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}

// 打印数组
void printArray(int arr[], int size) {
    for (int i = 0; i < size; i++) {
        printf("%d ", arr[i]);
    }
    printf("\\n");
}

int main() {
    int arr[] = {10, 7, 8, 9, 1, 5};
    int n = sizeof(arr) / sizeof(arr[0]);

    printf("原始数组：");
    printArray(arr, n);

    quickSort(arr, 0, n - 1);

    printf("排序后数组：");
    printArray(arr, n);

    return 0;
}`;

/**
 * 快速排序示例的高亮行配置
 */
export const HIGHLIGHT_LINES_QUICKSORT = [5, 12, 22, 27, 35];

/**
 * 简单的文件操作示例
 */
export const MOCK_C_FILE_OPERATION = `#include <stdio.h>
#include <stdlib.h>

int main() {
    FILE *file;
    char filename[] = "test.txt";
    char text[] = "这是写入文件的测试内容\\n";
    char buffer[1024];

    // 写入文件
    file = fopen(filename, "w");
    if (file == NULL) {
        printf("无法创建文件 %s\\n", filename);
        return 1;
    }

    fputs(text, file);
    fclose(file);
    printf("文件写入成功\\n");

    // 读取文件
    file = fopen(filename, "r");
    if (file == NULL) {
        printf("无法打开文件 %s\\n", filename);
        return 1;
    }

    printf("文件内容：");
    while (fgets(buffer, sizeof(buffer), file) != NULL) {
        printf("%s", buffer);
    }

    fclose(file);

    // 删除文件
    if (remove(filename) == 0) {
        printf("\\n文件删除成功\\n");
    } else {
        printf("\\n文件删除失败\\n");
    }

    return 0;
}`;

/**
 * 文件操作示例的高亮行配置
 */
export const HIGHLIGHT_LINES_FILE_OPERATION = [8, 16, 25, 32, 38];

/**
 * 多语言示例代码集合
 */
export const MULTI_LANGUAGE_EXAMPLES = {
  cpp: `#include <iostream>
#include <vector>

using namespace std;

class Animal {
public:
    virtual void makeSound() = 0;
    virtual ~Animal() {}
};

class Dog : public Animal {
public:
    void makeSound() override {
        cout << "汪汪！" << endl;
    }
};

class Cat : public Animal {
public:
    void makeSound() override {
        cout << "喵喵！" << endl;
    }
};

int main() {
    vector<Animal*> animals;
    animals.push_back(new Dog());
    animals.push_back(new Cat());

    for (auto animal : animals) {
        animal->makeSound();
        delete animal;
    }

    return 0;
}`,

  java: `import java.util.ArrayList;
import java.util.List;

interface Animal {
    void makeSound();
}

class Dog implements Animal {
    @Override
    public void makeSound() {
        System.out.println("汪汪！");
    }
}

class Cat implements Animal {
    @Override
    public void makeSound() {
        System.out.println("喵喵！");
    }
}

public class Main {
    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog());
        animals.add(new Cat());

        for (Animal animal : animals) {
            animal.makeSound();
        }
    }
}`,

  python: `class Animal:
    def make_sound(self):
        pass

class Dog(Animal):
    def make_sound(self):
        print("汪汪！")

class Cat(Animal):
    def make_sound(self):
        print("喵喵！")

def main():
    animals = [Dog(), Cat()]

    for animal in animals:
        animal.make_sound()

if __name__ == "__main__":
    main()`,

  javascript: `class Animal {
    makeSound() {
        throw new Error("makeSound() must be implemented");
    }
}

class Dog extends Animal {
    makeSound() {
        console.log("汪汪！");
    }
}

class Cat extends Animal {
    makeSound() {
        console.log("喵喵！");
    }
}

function main() {
    const animals = [new Dog(), new Cat()];

    animals.forEach(animal => {
        animal.makeSound();
    });
}

main();`
};

/**
 * 默认配置
 */
export const DEFAULT_CONFIG = {
  CODE: MOCK_C_HELLO_WORLD,
  HIGHLIGHTED_LINES: HIGHLIGHT_LINES_HELLO_WORLD,
  SHOW_LINE_NUMBERS: true,
  LANGUAGE: 'c',
  THEME: 'light',
  DIALOG_TITLE: 'C语言代码显示',
  DIALOG_WIDTH: '800px'
};

/**
 * 测试用例生成相关的示例代码（符合用户需求的"测试用例生成"按钮）
 */
export const TEST_CASE_GENERATION_EXAMPLE = `// 测试用例生成示例
// 高亮第三行（printf语句）作为重点测试行

#include <stdio.h>
#include <assert.h>

// 被测试的函数：计算两个整数的和
int add(int a, int b) {
    return a + b;
}

// 测试用例1：正数相加
void test_add_positive() {
    int result = add(5, 3);
    printf("测试：5 + 3 = %d\\n", result);
    assert(result == 8);
}

// 测试用例2：负数相加
void test_add_negative() {
    int result = add(-5, -3);
    printf("测试：-5 + (-3) = %d\\n", result);
    assert(result == -8);
}

// 测试用例3：正负数相加
void test_add_mixed() {
    int result = add(5, -3);
    printf("测试：5 + (-3) = %d\\n", result);
    assert(result == 2);
}

int main() {
    printf("开始执行测试用例...\\n");

    test_add_positive();
    test_add_negative();
    test_add_mixed();

    printf("所有测试用例通过！\\n");
    return 0;
}`;

/**
 * 测试用例示例的高亮行配置（高亮第三行和其他关键行）
 */
export const HIGHLIGHT_LINES_TEST_CASE = [3, 8, 13, 18, 27];

/**
 * 常用C语言关键字（用于扩展功能）
 */
export const C_KEYWORDS = [
  'auto', 'break', 'case', 'char', 'const', 'continue', 'default',
  'do', 'double', 'else', 'enum', 'extern', 'float', 'for', 'goto',
  'if', 'inline', 'int', 'long', 'register', 'restrict', 'return',
  'short', 'signed', 'sizeof', 'static', 'struct', 'switch',
  'typedef', 'union', 'unsigned', 'void', 'volatile', 'while'
] as const;