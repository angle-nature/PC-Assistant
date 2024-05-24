# 基于spring boot和ChatGPT API的PC智能助手

### 配置文件application.properties
- 必须修改：pythonPath变量，将其改成自己的python路径
- 可选修改：api_url和openai.api.key可换成官方地址和你的api_key,model也可更改（前提是自己的api_key能够使用）
### 输入方式
- 本系统支持文本输入和语音输入两种
### 依赖包
- java依赖：将pox.xml文件中的包下载即可
- python包：因为本项目的控制功能都是基于执行python脚本实现的，所以需要安装一些控制计算机的基础包，例如提供了与 Windows 管理工具基础结构 (WMI) 交互的功能的“wmi”包等等。
### 启动
- 配置好文件，安装了相应的包之后，运行PCAssistantApplication即可启动服务器
- 在浏览器中访问localhost:8080即可访问服务器
### 运行结果
