#!/bin/bash

# 编译和打包成可执行JAR文件
mvn clean package assembly:single

# 检查打包是否成功
if [ $? -eq 0 ]; then
    echo "可执行JAR文件打包成功！"
    
    # 查找生成的JAR文件
    JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | sort -r | head -n 1)
    echo "生成的JAR文件: $JAR_FILE"
    
    # 提示使用jpackage创建EXE文件
    echo "\n请在Windows系统上使用以下命令将JAR文件打包成EXE文件："
    echo "jpackage --name scmp-job-cs --input . --main-jar $JAR_FILE --main-class com.scmp.JavaFxApp --type exe"
    echo "\n注意："
    echo "1. jpackage工具需要JDK 14或更高版本（当前环境是JDK 18，满足要求）"
    echo "2. 要在Windows上运行jpackage，您需要安装WIX工具集"
    echo "3. 项目使用的是新版JavaFX Maven插件，没有jfx:native目标"\
      echo "4. 您可以使用以下命令创建自定义JRE运行时映像：mvn javafx:jlink"
else
    echo "打包失败，请检查错误信息。"
fi