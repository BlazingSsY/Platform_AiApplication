# portal-backend - DAIP门户的后端服务

## 项目简介

Portal Backend 是一个基于 Spring Boot 的数智平台后端服务。

### 核心功能模块

- **代码审查后台服务** - 提供电路文件上传、管理、多版本管理、电路审查、审查结果查询及审查结果统计功能
- **电路审查后台服务** - 提供代码文件上传、管理、代码审查、审查结果查询及审查结果统计功能

### 技术架构

- **框架**: Spring Boot 2.7.5
- **数据库**: 达梦数据库 (DM8)
- **ORM**: PostgreSQL
- **文件存储**: MinIO 对象存储
- **API文档**: Swagger/OpenAPI 3
- **认证**: JWT Token
- **构建工具**: Maven
- **容器化**: Docker

## 环境要求

### 开发环境

- **JDK**: 17 或更高版本
- **Maven**: 3.6+
- **IDE**: IntelliJ IDEA 或 Eclipse (推荐 IntelliJ IDEA)

### 运行环境

- **Java Runtime**: OpenJDK 17+
- **数据库**: 达梦数据库 8.1.3.62+
- **对象存储**: MinIO 服务
- **操作系统**: Linux/Windows/macOS


### 三方服务功能

#### 代码审查三方服务API功能描述
```
总览：

1. HTTP 统一请求头：
X-Trace-Id：调用跟踪
    希望在中间服务层生成调用id，打印到日志中：%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{0} - %msg%n
    统一响应体增加调用id
X-User-Id：登陆用户id
    如果可以增加一个获取用户信息的接口，后续如果有涉及到需要获取用户信息的可以拿到

2.统一相应格式：
{
    "code": {status_code}, // 200表示成功
    "message": "错误提示信息",
    "debugId": {X-Trace-Id},
    "timestamp": 1, // 时间戳
    "data": 具体响应数据，下面所有接口响应都写这个
}

=========

api接口：

1. 上传文件
    接口：/code/review/upload
    请求方式：POST
    请求参数：
        {
            "file": 文件流对象
        }

    响应结果：
        {
            "reviewId": "唯一id"
        }

2. 开始审查
    接口：/code/review/execute/{接口1返回的reviewId}
    请求方式：GET

3. 获取审查结果：
    接口：/code/review/result/{接口1返回的reviewId}
    请求方式：GET
    响应结果：
        {
            "duration": 230  // 单位秒
            "status": 1/2, //1 表示审校中正在进行中，2表示审校中完成
            "filesResult": [{
                "fileName": "文件名",
                "ruleId": "规则id",
                "rule": "规则描述",
                "status": 1/2/3, //1表示没有违反规则的，2表示有违反规则的，3表示正在审校中
                "language": "JAVA/C_PLUS/C",
                "codes": [{
                    "code": "报错代码",
                    "lineNumber": "行号: 多行是5-10这样展示（表示5到10行），单行只有一个数字",
                    "errorReason": "错误原因",
                    "modifySuggest": "修改建议"
                }]
            }]
        }

4. 获取源代码：
    接口：/code/review/source/code/{接口1返回的reviewId}
    请求方式：GET
    响应结果：
        [{
            "filename": "文件名",
            "language": "JAVA/C_PLUS/C",
            "code": "代码内容"
        }]

5. 获取Export下载链接
    接口：/code/review/download/url/{接口1返回的reviewId}
    请求方式：GET
    响应结果：
        {
            "sourceCode": "源代码下载链接",
            "jsonResult": "json结果下载链接",
            "excelResult": "excel结果下载链接"
        }

6. 获取所有规则：
    描述:返回所有的规则，并显示规则详细信息，规则类型以及是否被选中等。
    接口：/code/review/rule/all
    请求方式：POST
    请求参数:
    {    
          "pageNum": 1,
          "pageSize": 10,
          "filter": {
            "language": ["JAVA"], //语言类型筛选，可多选,默认不限，下拉框内容从元数据接口[接口9]
            "selectStatus": ["1"], //选中结果筛选，可多选,默认不限，下拉框内容从元效据接口[接口9]selectstatus获取
            "ruleType": ["1"], //规则类型筛选，可多选,默认不限，下拉框内容从元数据接口[接口9]ruleType获取
            "desc": "接口" //默认空
          }
    }
    响应参数：
        {
        	"total": 491,  //总计记录数(即:总规则数)
			"current": 1,  //当前页
			"pages": 5,    //总页数	
            "size": 100, // 规则集大小
            "rules": [{
                "id": "规则id",
                "desc": "规则描述",
                "language": "JAVA/C_PLUS/C", // 枚举JAVA  C  C_PLUS
                "selectStatus": 0/1 // 0标识未选中的规则，1标识选中的规则.
                "ruleType":"规则类型”，//显示规则类型
            }]
        }

7. 规则选择接口.
   接口:/code/review/rule/select
   请求方式: POST
   请求参效:
        {
            "ids": ["选中的 id1","选中的 id2", ....] // 选中的全部规则ID
        }

8.  规则详情信息展示接口
    描述:用于获取规则对应的解释，正反例描述等信息。
	接口:/code/review/rule/details
	请求方式:POST
	请求参数:
        {
            "ids": ["id1","id2"] // 需要展示规则详情的 id 列表
        }
    响应参效:
        {
            "rules": [{
                "id": "规则id",
                "explain": "规则解释",
                "currectExample": "遂循准则的示例内容",
                "errorExample": "违反准则的示例内容"
            }]
        }
        
9. 获取元数据信息
   描述:用于获取元数据信息
   接口:/code/review/metadata
   请求方式:GET
   响应参数:
        {
           "language":["c++"，"c语言"，"java语言"，“不限"],
           "selectStatus": ["0"，"1"，"不限”],  //1标识规则被选中，
           "ruleType":["1"，"2"，"不限”]    //多选框,默认全选
        }    
```

