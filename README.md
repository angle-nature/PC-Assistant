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
![主界面](https://github.com/angle-nature/PC-Assistant/blob/main/result/%E4%B8%BB%E7%95%8C%E9%9D%A2.jpg?raw=true)
![写入文件](https://github.com/angle-nature/PC-Assistant/blob/main/result/%E5%86%99%E5%85%A5%E6%96%87%E4%BB%B6.gif?raw=true)
![打开哔哩哔哩](https://github.com/angle-nature/PC-Assistant/blob/main/result/%E6%89%93%E5%BC%80%E5%93%94%E5%93%A9%E5%93%94%E5%93%A9.gif?raw=true)
![谷歌搜搜](https://github.com/angle-nature/PC-Assistant/blob/main/result/%E8%B0%B7%E6%AD%8C%E6%90%9C%E7%B4%A2.gif?raw=true)
