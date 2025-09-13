#!/bin/bash

# 编译项目
mvn clean compile

# 检查编译是否成功
if [ $? -eq 0 ]; then
    echo "编译成功！"
    
    # 运行JavaFX应用程序
    echo "尝试运行应用程序..."
    mvn javafx:run
else
    echo "编译失败，请检查错误信息。"
fi