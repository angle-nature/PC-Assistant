const recognition = new webkitSpeechRecognition();
recognition.continuous = true; // 持续监听
recognition.interimResults = true; // 实时结果
recognition.lang = 'zh-CN'; // 语言设置

let isWakeWordDetected = false;
const wakeWord = "小蔡同学"; // 设定唤醒词

recognition.onresult = function(event) {
    let interimTranscript = '';
    let finalTranscript = '';

    for (let i = event.resultIndex; i < event.results.length; ++i) {
        if (event.results[i].isFinal) {
            finalTranscript += event.results[i][0].transcript;
        } else {
            interimTranscript += event.results[i][0].transcript;
        }
    }

    if (!isWakeWordDetected) {
        if (finalTranscript.toLowerCase().includes(wakeWord)) {
            isWakeWordDetected = true;
            console.log('Wake word detected. Entering command mode.');
        }
    } else {
        if (finalTranscript.trim()) {
            console.log('Command detected:', finalTranscript);
            sendCommand(finalTranscript);
            isWakeWordDetected = false; // 重置唤醒词检测
        }
    }
};

recognition.onerror = function(event) {
    console.error('Speech recognition error', event);
    recognition.stop();
    recognition.start(); // 发生错误时重新启动监听
};

recognition.onend = function() {
    recognition.start(); // 当识别结束时重新启动监听
};

document.getElementById('userInput').addEventListener('input', function() {
    const sendButton = document.getElementById('sendButton');
    if (this.value.trim() === '') {
        sendButton.disabled = true;
        sendButton.classList.add('disabled');
    } else {
        sendButton.disabled = false;
        sendButton.classList.remove('disabled');
    }
});

function appendMessage(message, isUser) {
    const chatBox = document.getElementById('chatBox');
    const placeholder = document.getElementById('placeholder');
    if (placeholder) {
        placeholder.style.display = 'none'; // 隐藏占位元素
    }

    const messageWrapper = document.createElement('div');
    messageWrapper.classList.add('message-wrapper');
    if (isUser) {
        messageWrapper.classList.add('user-wrapper');
    } else {
        messageWrapper.classList.add('bot-wrapper');
    }

    // 创建头像元素
    const avatar = document.createElement('img');
    avatar.src = '/images/avatar.png';
    // avatar.alt = isUser ? 'User Avatar' : 'Bot Avatar'; // 添加alt属性
    avatar.classList.add('avatar'); // 添加头像的样式

    const messageDiv = document.createElement('div');
    messageDiv.textContent = message;
    messageDiv.classList.add('message');
    if (isUser) {
        messageDiv.classList.add('user-message');
    } else {
        messageDiv.classList.add('bot-message');
        messageWrapper.appendChild(avatar);
    }

    messageWrapper.appendChild(messageDiv);
    chatBox.appendChild(messageWrapper);
    chatBox.scrollTop = chatBox.scrollHeight;  // 自动滚动到最新消息
}

document.getElementById('userInput').addEventListener('keypress', function(event) {
    // 如果按下的键是回车键
    if (event.key === 'Enter') {
        sendCommand(); // 调用发送消息的函数
    }
});

function sendCommand() {
    const userInput = document.getElementById('userInput');
    const message = userInput.value.trim();
    console.log(message)
    if (!message) return;

    appendMessage(message, true);

    fetch('/api/control/execute', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: message })
    })
        .then(response => response.json())
        .then(data => {
            const responseMessage = data.response;
            appendMessage(responseMessage, false);
            speak(responseMessage); // 调用语音播报函数
        });

    userInput.value = '';
    const sendButton = document.getElementById('sendButton');
    sendButton.disabled = true; // 发送后禁用Send按钮
    sendButton.classList.add('disabled'); // 发送后变成灰色
}

function startListening() {
    recognition.start();
}

function speak(text) {
    const utterance = new SpeechSynthesisUtterance(text);
    speechSynthesis.speak(utterance);
}