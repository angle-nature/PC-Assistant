package voiceassistant.controller;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import voiceassistant.service.ChatService;
import voiceassistant.model.CommandRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/control")
public class ControlController {

    @Autowired
    private ChatService chatService;
    @Value("${pythonPath}")
    private String pythonPath;

    @PostMapping("/execute")
    public ResponseEntity<Map<String, String>> executeCommand(@RequestBody CommandRequest commandRequest) {
        String gptResponse = chatService.getChatResponse(commandRequest.getMessage());
        boolean isScript = parseCommand(gptResponse);
        String result = isScript ? executeLocalCommand():gptResponse;
        Map<String, String> response = new HashMap<>();
        response.put("response", result);
        return ResponseEntity.ok(response);
    }

    private boolean parseCommand(String gptResponse) {
        // 从ChatGPT的回复中解析出具体的命令
        System.out.println("Parsing command from GPT response: \n" + gptResponse);

        // 定义正则表达式，匹配包含```python和```之间的内容
        Pattern pattern = Pattern.compile("```python(.+?)```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(gptResponse);

        // 判断是否匹配到脚本部分
        if (matcher.find()) {
            // 提取脚本部分
            String pythonScript = matcher.group(1).trim();

            // 将脚本存储到本地文件
            String fileName = "scripts/script.py";
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                String encoding = "import sys\nsys.stdout.reconfigure(encoding='utf-8')\n";
                writer.write(encoding);
                // 定义匹配用户名的正则表达式
                String regex = "(?<=\\\\\\\\Users\\\\\\\\|/User/)[^\\\\\\\\/]+(?=\\\\\\\\|/)";
                Pattern pattern1 = Pattern.compile(regex, Pattern.DOTALL);
                Matcher matcher1 = pattern1.matcher(pythonScript);
                pythonScript = matcher1.replaceAll("'+os.getlogin()+'");
                pythonScript = pythonScript.replaceAll("桌面", "Desktop");
                writer.write(pythonScript);
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            // 如果没有匹配到脚本部分，则保留原文本

            return false;
        }
    }

    private String executeLocalCommand() {
        // 调用本地脚本
        String scriptPath = "scripts/script.py";
        StringBuilder printOutput = new StringBuilder();
        try {
            // 构建进程的命令
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);

            // 启动进程并等待其终止
            Process process = processBuilder.start();
            process.waitFor();

            // 读取进程的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                printOutput.append(line).append("\n");
            }

            // 检查进程的退出状态
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return (printOutput.length() == 0?"完成啦！":printOutput.toString());
            } else {
                return "小蔡同学还不会呢~";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "小蔡同学宕机了！";
        }
    }
}
