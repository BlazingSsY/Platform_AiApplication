# logic-review-backend - 逻辑审查后端服务

## 项目简介

Logic Backend 是一个基于 Spring Boot 的数智平台后端服务。

### 核心功能模块

- **逻辑审查后台服务** - 提供电路文件上传、管理、多版本管理、电路审查、审查结果查询及审查结果统计功能

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

#### 逻辑审查三方服务API功能描述
```
总览：

1. HTTP 统一请求头：
X-Trace-Id：调用跟踪
    希望在中间服务层生成调用id，打印到日志中：%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{0} - %msg%n
    统一响应体增加调用id (如果定时任务调用,传入: "pollingforjobprogress")
    
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
    接口：/code/review/upload?reviewId={同名文件的reviewId(新文件这个值可以不填)}
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
    接口：/logic/review/execute/{接口1返回的reviewId}
    请求方式：GET

3. 获取审查结果：    
    接口：/logic/review/result/{接口1返回的reviewId}?version=2026-02-02 11:13:11
    请求方式：GET
    响应结果：
        {
            "duration": 230     // 单位秒
            "status": 1/2,      //1 表示审校中正在进行中，2表示审校中完成， 3表示审校失败，4表示审校错误
            "filesSize":1,//文件数
            "filesLine":9892,//文件行
            "useRuleSize":17//使用规则数
            "questions":103//问题数量 
            "passFileNum": 50 //通过文件数
            "reviewStatus" :1,//1:通过2:不通过                        
            "filesResult": [{
                "fileName": "文件名",
                "ruleId": "规则id",
                "rule": "规则描述",
                "ruleSource": "所内规则",
                "ruleType": "规则类型" 
                "status": 1/2/3, //1表示没有违反规则的，2表示有违反规则的，3表示正在审校中
                "language": "JAVA/C_PLUS/C",
                "codes": [{
                    "questionId": "问题id",
                    "code": "报错代码",
                    "lineNumber": "行号: 多行是5-10这样展示（表示5到10行），单行只有一个数字",
                    "errorReason": "错误原因",
                    "modifySuggest": "修改建议",
                    "recheckConclusion": "复核结论",
                    "questionDesc": "问题描述",
                    "recheckStatus": 1/2/3   //复核状态 1:未复核 2:复核中 3:复核完成。
                    "recheckResultStatus": 1 //复核状态 1:通过 2:拒绝  
                    "rejectReason": "拒绝理由",
                    "recheckUserId": "复核处理用户Id"       
                }]
            }]
        }

4. 获取源代码：
    接口：/logic/review/source/code/{接口1返回的reviewId}
    请求方式：POST
    响应结果：
        [{
            "filename": "文件名",
            "language": "JAVA/C_PLUS/C",
            "code": "代码内容"
        }]

5. 获取Export下载链接
    接口：/logic/review/download/url/{接口1返回的reviewId}
    请求方式：GET
    响应结果：
        {
            "sourceCode": "源代码下载链接",
            "jsonResult": "json结果下载链接",
            "excelResult": "excel结果下载链接"
        }

6. 获取所有规则：
    描述:返回所有的规则，并显示规则详细信息，规则类型以及是否被选中等。
    接口：/logic/review/rule/all
    请求方式：POST
    请求参数:
    {    
          "filter": {
            "language": ["JAVA"], //语言类型筛选，可多选,默认不限，下拉框内容从元数据接口[接口9]
            "selectStatus": ["1"], //选中结果筛选，可多选,默认不限，下拉框内容从元效据接口[接口9]selectstatus获取
            "ruleType": ["1"], //规则类型筛选，可多选,默认不限，下拉框内容从元数据接口[接口9]ruleType获取
            "ruleSource":  ["不限"]或["所内规则"]  .. // 规则来源筛选，可多选，默认不限，下拉框内容从元数据接口[接口10]ruleSource装取
            "desc": "接口" //默认空
          }
    }
    响应参数：
        {
        	"total": 491,  //总计记录数(即:总规则数)
        	"selectSize": 49,  //选中规则集大小(即:总规则数)
            "rules": [{
                "id": "规则id",
                "desc": "规则描述",
                "language": ["C++", "C"], //适用语言, JAVA/C/C++
                "ruleSource": "所内规则"
                "selectStatus": 0/1 // 0标识未选中的规则，1标识选中的规则.
                "ruleType":"规则类型”，//显示规则类型
                "explain":"对规则进行解释，包括机理说明等信息"
                "mustSelect": 0/1  //1:必选 0:非必选
            }]
        }

7. 规则选择接口.
   接口:/logic/review/rule/select
   请求方式: POST
   请求参效:
        {
            "ids": ["选中的 id1","选中的 id2", ....] // 选中的全部规则ID
        }

8.  规则详情信息展示接口
    描述:用于获取规则对应的解释，正反例描述等信息。
	接口:/logic/review/rule/details
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
                "correctExample": "遂循准则的示例内容",
                "errorExample": "违反准则的示例内容"
            }]
        }
        
9. 获取元数据信息
   描述:用于获取元数据信息
   接口:/logic/review/metadata
   请求方式:GET
   响应参数:
        {
           "language":["c++"，"c语言"，"java语言"，“不限"],
           "selectStatus": ["0"，"1"，"不限”],  //1标识规则被选中，
           "ruleType":["1"，"2"，"不限”]    //多选框,默认全选
           "ruleSource": ["所内规则"，"8114规则"，"不限”] 
        }    
        
11. 审查列表信息
  描述: 用于查询上传文件的相关属性数据。
  接口：/logic/review/summary/msg
  请求方式：POST
  请求参数：                    
        json
        {
            "reviewId": "审查id" // 接口1的返回结果
        }
  响应参数：
        {
            "filesSize": 1,         // 文件数
            "filesLine": 9892,      // 文件行数
            "useRuleSize": 17,      // 使用规则数
            "questions": 103        // 问题数量
        }
        
        
12. 停止审查
  描述: 用于停止审查
  接口：/logic/review/stop/接口1返回的参数
  请求方式：GET
  响应参数：
        {
            "stopStatus": 1 // 1.停止成功 0.停止失败
        }
    
    
13. 任务排队信息查询
  描述: 用于查询任务排队信息
  接口：/logic/review/wait/task/接口1返回的参数
  请求方式：GET
  响应参数：
        {
            "taskNum": 1 // 标识当前任务执行前，需要等待多少任务执行完成
        }        
        
14. 审查进度查询       
  描述: 查询审查进度
  接口：/logic/review/process/接口1返回的参数
  请求方式：GET
  响应参数：
        {
            "totalFilesize":7,  // 总文件数 
            "finishFilesize":4, // 已完成审查文件数
			"fileName": "a.c",  // 正在审查文件
            "fileRule": [{
                "ruleId": "规则id",
                "rule": "规则描述",
                "ruleSource": "所内规则",
                "ruleType": "规则类型" 
                "status": 1/2/3, //1表示没有违反次规则的，2表示有违反规则的，3表示正在审校中
            }]
        } 
        
15. 获取审查的文件的代码
  描述: 用于获取审查的文件的代码
  接口：/logic/review/source/code/接口1返回的参数?fileName=test.zip?offset=12-30?version=2026-02-02 11:13:11
  请求方式：GET
  响应参数：
        {
           "sourceCode": "代码内容",      // 代码内容
           "dataType": "all/part",      //all标识完整代码，part标识局部代码 (粉碎的文件这里类型为part)
           "offset": "偏移量",
           "lines":总行数 (原文件的行数)          
        }        
        
16. 获取日志接口(只有admin账户才能调用这个接口)
  描述: 用于获取日志接口      
  接口:/logic/review/log
  请求方式:GET
  请求参数:
  {
     num:1000 //默认显示后1000行，范围0到5000
  }
  响应参数:
  {
     "code":200 //状态:200,403,500
     "message":"成功" //成功，权限不足，获取日志失败
     "data":[
        "message1",
        "message2"//多条message，权限不足或者失败为空
     ],
     "timestamp": 
     "debugId": ""
  }

16. 查询代码版本列表接口
  描述: 用于获取代码文件的版本列表  
  接口:/logic/review/version/list/接口1返回的参数
  请求方式:GET
  响应参数:
  {
      "versionList" : ["2026-02-01 13:23:11", "2026-02-01 10:23:11"]
  }  
  
17. 结果复查请求接口
  描述: 创建复查请求  
  接口:/logic/review/recheck
  请求方式: POST
  请求参数:
  {
      "reviewId": "审查Id",
      "questionIds": ["11111111111","2222222222222"]
      "version":  "版本",
      "recheckConclusion": "审查结论",
      "questionDesc": "问题描述"  
  }  


18.复核结果列表 
接口:/logic/review/recheck/list
请求方式: POST
请求参数: 
   {
      "reviewIds":["code-xxxxxxxx","code-xxxxxx"],
      "submitUserIds":["admin","admin1"],
      "recheckStatus":1 // 1:待复核 2:复核中 3:已复核
      "curUserRoleType": 1,//1:管理员,2:机载领导,3:领导,4:普通用户,5:专家;
      "pageSize"; 100,
      "pageNum": 1
   }    
请求参数:
   {
        "records": [{
            "reviewId":"审查Id"
            "recheckStatus":1/2/3// 复核状态1:未复核 2:复核中3:已复核。
            "time":2026-03-11 14:55,//复核提交时间
            "submitUserId":"复核提交用户Id"
        }] 
        "total": 500,  
        "pageSize": 100,
        "pageNum": 1,
        "totalPage": 5     
   }      
   
   
19.查看复核详情(如果version为空则返回最后一个版本)
接口:/logic/review/recheck/detail/接口1返回的参数&version=xxxxx&&curUserRoleType=1
请求方式: GET
响应参数
    {
        version: ""，//版本
        recheckTime: "",// 复核时间
        filesResult":[{
            "fileName": "",  //文件名
            "ruleId": "规则id",
            "rule": "规则描述",
            "ruleSource": "所内规则",
            "status"; 1/2/3,  //1表示没有们进反规则的，2表示们违反规喇的，3表示正在审核中
            "Language": ""
            "explain": "",
            "codes":[{
                "questionId":  "问题id",
                "code"; "报错代码",
                "lineNumber": "行号:多行是5-10这腿示(表示5到10行)，单行贝行一个数字",
                "errorReason": "错误原因",
                "modifySuggest": "修改建议",
                "recheckConclusion": "复核结论",
                "questionDesc": "问题描述",
                "recheckResultStatus": 1,  //复核核状态1:通过 2:未通过
                "rejectReason":"拒绝理由",
                "recheckUserId": "复核处理用户ID"
            }]
        }]    
    }

20.复核结果详情列表(这次先不做)
接口:/logic/review/recheck/records/接口1返回的参数
请求方式:GET
请求参数:
   {
      "records":[{
          "reviewId":"审查Id",
          "recheckStatus":1/2/3// 复核状态1:未复核 2:复核中3:已复核。
          "time":2026-03-11 14:55, //复核提交时间
          "version":"2025-03-10 12:22" //版本
      }]
   }

21.复核评审结果提交
接口: /logic/review/recheck/result/submit
请求方式:POST
请求参数:
    {
       "reviewId": "审查ID",
       "version": "版本",
       "rejectReason": "rejectReason",
       "questionIds": ["11111111111" "2222222222222"],
       "recheckResultStatus": 1  //1:通过 2:拒绝
    }


22.粉碎文件文件
接口:/logic/review/delete/接口1返回的参数
请求方式:GET
响应参数:
    {
       "delete": 1//1:可粉碎，2:不可粉碎
       "desc": ""//如果为 delete=1填充粉碎成功， delete=2填充不可粉碎原因
    }
  
  
```

