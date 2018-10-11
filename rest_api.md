## 数据类型说明
* Question
	* 数据格式
		```
    	{
    		"id":           问题ID(String),
			"seq":          保留(String),
			"chapId":       保留(String),
			"chap":         保留(String),
			"knowledgeId":  保留(String),
			"knowledge":    保留(String),
			"type":         问题类型(int),
			"desc":         问题描述(String),
			"option":       保留(String),
			"answer":       标准答案(String),
			"diffi":        保留(double),
			"source":       保留(String)
	    }
		```
    * 字段说明
    	* type: 
    		* null: 旧测试题
    		* 0: 保留
    		* 1: 判断题
    		* 2: 填空题
    		* 3: 比较题 

* Class
	* 数据格式
		```
        {
			"id":           课堂ID(String),
			"name":         课堂名称(String),
			"type":         保留(String),
			"major":        保留(String),
			"teacherId":    教师ID(String),
			"studentIds":   学生ID列表(String List),
			"questionIds":  问题ID列表(String List),
			"memo":         保留(String)
        }
        ```

* VoiceInfo
	* 数据格式
	    ```
        {
            "questionId":   问题ID(String),
            "question":     问题描述(String),
            "voiceId":      录音ID(String),
            "answer":       录音识别结果(String),
            "feedback":     申诉修正(String)
        }
        ```

## 用户接口

* 用户登录
    * `/user/login`
        * 请求方式
            * `GET`
        
        * 参数列表
            * `code`: 微信登录返回的code
    
        * 返回值
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "userType": 用户类型(String),
                "openid": openid(String),
                "token": token(String)
            }
            ```
    
        * 错误代码说明
            * 0: 登录成功，`token`字段携带后续会话token
            * 1: 未注册的用户，`openid`字段携带注册用openid
            * 10: 微信服务器拒绝登录

* 学生注册
    * `/user/registerStudent`
        * 请求方式
            * `POST`: application/json
             
        * 参数列表
            * `openid`: 注册用openid
            
        * 请求数据
        ```
        {
            "name": 学生姓名(String),
            "no": 学号(String),
            "className": 班级名称
        }
        ```
        
        * 返回值
        ```
        {
            "err": 错误代码(int),
            "errMsg": 错误信息(String),
            "userType": 用户类型(String),
            "openid": openid(String),
            "token": token(String)
        }
        ```
        
        * 错误代码说明
            * 0: 登录成功，`token`字段携带后续会话token
            * 3: 注册失败，可能已经注册过
        
* 教师注册 
    * `/user/registerTeacher`
        * 请求方式
            * `POST`: application/json
             
        * 参数列表
            * `openid`: 注册用openid
            
        * 请求数据
        ```
        {
            "name": 学生姓名(String)
        }
        ```
            
        * 返回值
        ```
        {
            "err": 错误代码(int),
            "errMsg": 错误信息(String),
            "userType": 用户类型(String),
            "openid": openid(String),
            "token": token(String)
        }
        ```
        
        * 错误代码说明
            * 0: 登录成功，`token`字段携带后续会话token
            * 3: 注册失败，可能已经注册过

---
## 学生接口

* 获取所有可用课堂 
    * `/student/{token}/getAllClasses`
        * 请求方式
            * `GET`
             
        * URL参数
            * `token`: 会话token
            
        * 返回值
            * 所有课堂信息
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "classes": [    课堂列表(Class List)
                    Class 1,
                    Class 2,
                    ...
                ]
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`classes`字段携带课堂信息列表
            * 3: 失败，无效token
            * 20: 失败，无法获得课堂信息

* 获取已加入的课堂 
    * `/student/{token}/getClasses`
        * 请求方式
            * `GET`
             
        * URL参数
            * `token`: 会话token
            
        * 返回值
            * 所有课堂信息
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "classes": [    课堂列表(Class List)
                    Class 1,
                    Class 2,
                    ...
                ]
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`classes`字段携带课堂信息列表
            * 3: 失败，无效token
            * 20: 失败，无法获得课堂信息

* 加入课堂 
    * `/student/{token}/joinClass`
        * 请求方式
            * `GET`
             
        * URL参数
            * `token`: 会话token
        * 参数列表
            * `classId`: 课堂ID
            
        * 返回值
            * 当前学生所有课堂信息
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "classes": [    课堂列表(Class List)
                    Class 1,
                    Class 2,
                    ...
                ]
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`classes`字段携带课堂信息列表
            * 3: 失败，无效token
            * 20: 失败，无法获得课堂信息
            
* 离开课堂 
    * `/student/{token}/leaveClass`
        * 请求方式
            * `GET`
             
        * URL参数
            * `token`: 会话token
        * 参数列表
            * `classId`: 课堂ID
            
        * 返回值
            * 当前学生所有课堂信息
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "classes": [    课堂列表(Class List)
                    Class 1,
                    Class 2,
                    ...
                ]
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`classes`字段携带课堂信息列表
            * 3: 失败，无效token
            * 20: 失败，无法获得课堂信息

* 获取课堂所有问题 
    * `/student/{token}/getQuestion`
        * 请求方式
            * `GET`
             
        * URL参数
            * `token`: 会话token
        * 参数列表
            * `classId`: 课堂ID
            
        * 返回值
            * 课堂所有问题列表
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "questions": [      问题列表(Question List)
                    Question 1,
                    Question 2,
                    ...
                ]
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`questions`字段携带问题信息列表
            * 3: 失败，无效token
            * 20: 失败，无法获得问题信息

* 上传录音 
    * `/student/{token}/uploadVoice`
        * 请求方式
            * `POST`: multipart/form-data
             
        * URL参数
            * `token`: 会话token
            
        * 请求数据
            * `questionId`: 问题ID
            * `openPageTime`: 打开问题页时间
            * `startRecTime`: 录音开始时间
            * `stopRecTime`: 录音结束时间
            * `uploadFile`: 录音二进制文件
            
        * 返回值
            * 语音识别结果
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "data": 语音识别结果(String)
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`data`字段携带语音识别结果
            * 3: 失败，无效token
            * 21: 失败，未找到指定问题
            * 30: 失败，服务器接受到空文件
            * 500: 失败，服务器内部错误

* 下载录音
    * `/student/{token}/downloadVoice`
        * 请求方式
            * `GET`
             
        * URL参数
            * `token`: 会话token
            
        * 参数列表
            * `voiceId`: 录音ID
            
        * 返回值
            * 成功: `mp3`格式录音文件
            * 错误: null
        
        * 错误原因说明
            * 无效token
            * 未找到指定录音文件
            * 录音文件不属于此学生
            * 服务器内部错误

* 获取历史录音列表 
    * `/student/{token}/getVoiceList`
        * 请求方式
            * `GET`
            
        * URL参数
            * `token`: 会话token
            
        * 返回值
            * 已上传的录音文件信息列表
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "voiceInfos": [     录音信息列表(VoiceInfo List)
                    VoiceInfo 1,
                    VoiceInfo 2,
                    ...
                ]
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`voiceInfos`字段携带录音信息列表
            * 3: 失败，无效token
            * 20: 失败，无法获得录音信息

* 录音识别申诉 
    * `/student/{token}/feedbackVoice`
        * 请求方式
            * `GET`
            
        * URL参数
            * `token`: 会话token
            
        * 参数列表
            * `voiceId`: 录音ID
            * `feedback`: 修正内容
            
        * 返回值
            * 申诉的录音信息
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "voiceInfo": 录音信息(VoiceInfo)
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`voiceInfo`字段携带修正的录音信息
            * 3: 失败，无效token
            * 20: 失败，无法获得录音信息

* 获取答案相似度`(临时)` 
    * `/student/{token}/getAccuracyTmp`
        * 请求方式
            * `GET`
            
        * 参数列表
            * `answer`: 待评价答案
            * `questionId`: 问题ID
            
        * 返回值
            * 答案与问题标准答案相似度(0 ~ 1)
            * 返回值格式：
            ```
            {
                "err": 错误代码(int),
                "errMsg": 错误信息(String),
                "data": 相似度(String)
            }
            ```
        
        * 错误代码说明
            * 0: 成功,`data`字段携带相似度

---
## 教师接口

* 添加课堂 
    * `/teacher/{token}/addClass`
        * 请求方式
            * `POST`
             
        * URL参数
            * `token`: 会话token
            
        * 请求数据
        ```
        {
            "name": 课堂名称(String)
        }
        ``` 
            
        * 返回值
        ```
        {
            "err": 错误代码(int),
            "errMsg": 错误信息(String),
            "clazz": 课堂信息(Class)
        }
        ```
        
        * 错误代码说明
            * 0: 成功，`clazz`字段携带课堂信息
            * 20: 失败，服务器无法获取到数据
* 添加问题 
    * `/teacher/{token}/addQuestion`
        * 请求方式
            * `POST`: application/json
             
        * URL参数
            * `token`: 会话token
            
        * 请求数据
        
        请求数据为`Question`数据类型，有以下要求:
        
            1. `id`字段不填
            2. `desc`字段必填
            3. `answer`字段必填
            4. 其他字段选填
        
        ```
        {
            "desc": 问题描述(String),
            "answer": 标准答案(String),
            ...
        }
        ```
            
        * 返回值
        ```
        {
            "err": 错误代码(int),
            "errMsg": 错误信息(String),
            "question": 问题信息(Question)
        }
        ```
        
        * 错误代码说明
            * 0: 成功，`question`字段携带课堂信息
            * 20: 失败，服务器无法获取到数据

* 修改问题 
    * `/teacher/{token}/modQuestion`
        * 请求方式
            * `POST`: application/json
             
        * URL参数
            * `token`: 会话token
            
        * 请求数据
        
        请求数据为`Question`数据类型，有以下要求:
        
            1. `id`字段必填，且为已经存在的问题id
            2. `desc`字段必填
            3. `answer`字段必填
            4. 其他字段选填
        
        ```
        {
            "id": 问题id(String),
            "desc": 问题描述(String),
            "answer": 标准答案(String),
            ...
        }
        ```
            
        * 返回值
        ```
        {
            "err": 错误代码(int),
            "errMsg": 错误信息(String),
            "question": 问题信息(Question)
        }
        ```
        
        * 错误代码说明
            * 0: 成功，`question`字段携带课堂信息
            * 21: 失败，未找到此id，或者有必填字段未填写