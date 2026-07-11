# Smart Image Analyzer — 智能图片分析系统

Based on Spring Boot + PyTorch dual-stack AI image classification system.
Upload Image → Spring Boot Backend → Python AI Microservice (ResNet-18) → Top-5 Results

[![Java](https://img.shields.io/badge/Java-21-blue)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)]()
[![PyTorch](https://img.shields.io/badge/PyTorch-2.2-red)]()

---

## Features

- Image upload with drag-and-drop and preview
- AI classification using pretrained ResNet-18 (1000 classes)
- Top-5 results with confidence visualization
- Analysis history with database persistence
- Microservice architecture (Spring Boot + FastAPI)

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, JPA, H2/MySQL
- **AI Engine**: Python, FastAPI, PyTorch 2.2, ResNet-18
- **Deployment**: Docker Compose
- **Frontend**: Thymeleaf, Bootstrap 5

## Quick Start

### Local Development

**1) Start AI Service**

`ash
cd ai-service
pip install -r requirements.txt
python -m app.main
`

Use CPU-only PyTorch if you don't have CUDA:
`ash
pip install torch==2.2.0 torchvision==0.17.0 --index-url https://download.pytorch.org/whl/cpu
`

**2) Start Backend**

`ash
cd backend
mvn clean package -DskipTests
java -jar target/image-analyzer-backend-1.0.0.jar
`

**3) Open Browser**

Visit http://localhost:8080

### Docker

`ash
docker compose up --build
`

Visit http://localhost:8080

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | / | Home page |
| POST | /upload | Upload image for analysis |
| GET | /api/health | Health check |
| GET | /api/history | Analysis history (JSON) |

## License

MIT
