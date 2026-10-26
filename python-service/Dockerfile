FROM python:3.12-slim
WORKDIR /app
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    poppler-utils \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*
COPY python-service/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY python-service/ .
EXPOSE 50051
CMD ["python", "grpc_server.py"]
