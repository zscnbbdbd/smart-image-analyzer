# 🧠 Smart Image Analyzer — 智能图片分析系统

基于 **Spring Boot + PyTorch** 双栈架构的 AI 图片分类系统。
用户上传图片 → Spring Boot 后端 → Python AI 微服务（ResNet-18）→ 返回 Top-5 分类结果并持久化存储。

---

## ✨ 功能特性

- 图片上传与预览 — 支持拖拽上传、点击选择，实时预览
- AI 智能分类 — 基于 ResNet-18 预训练模型，识别 1000 种物体类别
- Top-5 概率展示 — 可视化置信度条形图，结果一目了然
- 分析历史记录 — 所有分析记录持久化存储，支持历史回溯
- 微服务架构 — Spring Boot + FastAPI 双服务独立部署，松耦合

---

## 技术栈

| 模块 | 技术 | 说明 |
|------|------|------|
| 后端 | Java 21 + Spring Boot 3.3 | RESTful API、Thymeleaf、JPA |
| AI 引擎 | Python 3.12 + FastAPI | PyTorch、TorchVision、ResNet-18 |
| 数据库 | H2 (开发) / MySQL (生产) | JPA/Hibernate 自动建表 |
| 部署 | Docker + Docker Compose | 容器化一键启动 |
| 前端 | Thymeleaf + Bootstrap 5 | 响应式交互 UI |

---

## 快速开始

### 方式一：本地运行

#### 1) 启动 AI 服务

```bash
cd ai-service
pip install -r requirements.txt
python -m app.main
```

AI 服务将在 http://localhost:8000 启动。
首次运行会自动下载 ResNet-18 预训练模型（约 45MB）。

#### 2) 启动 Spring Boot 后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/image-analyzer-backend-1.0.0.jar
```

#### 3) 访问系统

打开浏览器访问 **http://localhost:8080**

### 方式二：Docker 一键部署

```bash
docker compose up --build
```

之后访问 http://localhost:8080 即可使用。

---

## 项目结构

```
image-analyzer/
  ai-service/                  Python AI 推理微服务
    app/main.py                FastAPI 应用
    app/imagenet_classes.txt   ImageNet 1000 类标签
    requirements.txt
    Dockerfile
  backend/                     Spring Boot Java 后端
    src/main/java/com/smartimage/
      ImageAnalyzerApplication.java
      controller/ImageController.java
      service/AnalysisService.java
      model/AnalysisRecord.java
      repository/AnalysisRecordRepository.java
      config/RestClientConfig.java
    src/main/resources/
      application.yml
      templates/index.html
      templates/history.html
      static/css/style.css
    pom.xml
    Dockerfile
  docker-compose.yml
  README.md
```

---

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 首页 |
| GET | /history | 分析历史页面 |
| POST | /upload | 上传图片进行 AI 分析 |
| GET | /api/history | 获取历史记录 (JSON) |
| GET | /api/health | 系统健康检查 |

### /upload 请求示例

```bash
curl -X POST -F "file=@cat.jpg" -F "top_k=5" http://localhost:8080/upload
```

### 响应示例

```json
{
  "filename": "cat.jpg",
  "topK": 5,
  "predictions": [
    {"rank": 1, "classId": 281, "label": "tabby cat", "confidence": 0.9231}
  ]
}
```

---

## License

MIT
