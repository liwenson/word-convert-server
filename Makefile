APP_NAME=wordconvert
IMAGE=harbor.test.net/devops/$(APP_NAME)
VERSION=$(shell date '+%Y%m%d%H%M%S')
TAG=$(IMAGE):$(VERSION)

K8S_DIR=k8s

# 默认目标
all: build image


# Maven构建 JAR
build:
	mvn clean package -DskipTests

# Docker多阶段构建镜像
image:
	docker build -t $(TAG) -f deployment/Dockerfile .

# 推送镜像到仓库
push:
	docker push $(TAG)

# 运行本地镜像（调试用）

run:
	docker run -p 8080:8080 $(TAG)

# Kubernetes 部署
deploy:
	kubectl apply -f $(K8S_DIR)/word2pdf-deployment.yaml
	kubectl apply -f $(K8S_DIR)/word2pdf-service.yaml

# 清理
clean:
	mvn clean
