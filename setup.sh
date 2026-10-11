#!/usr/bin/env bash
set -e

echo "Criando estrutura de diretorios..."

mkdir -p docs/ddd
mkdir -p docs/architecture/adrs
mkdir -p docs/security
mkdir -p src/main/resources/db/migration

mkdir -p src/main/java/br/com/fiap/soat/oficina/domain/entity
mkdir -p src/main/java/br/com/fiap/soat/oficina/domain/valueobject
mkdir -p src/main/java/br/com/fiap/soat/oficina/domain/exception
mkdir -p src/main/java/br/com/fiap/soat/oficina/domain/repository

mkdir -p src/main/java/br/com/fiap/soat/oficina/application/usecase
mkdir -p src/main/java/br/com/fiap/soat/oficina/application/dto

mkdir -p src/main/java/br/com/fiap/soat/oficina/infrastructure/config
mkdir -p src/main/java/br/com/fiap/soat/oficina/infrastructure/security
mkdir -p src/main/java/br/com/fiap/soat/oficina/infrastructure/persistence

mkdir -p src/main/java/br/com/fiap/soat/oficina/presentation/controller
mkdir -p src/main/java/br/com/fiap/soat/oficina/presentation/request
mkdir -p src/main/java/br/com/fiap/soat/oficina/presentation/response
mkdir -p src/main/java/br/com/fiap/soat/oficina/presentation/handler

mkdir -p src/test/java/br/com/fiap/soat/oficina/domain
mkdir -p src/test/java/br/com/fiap/soat/oficina/integration
mkdir -p src/test/resources

echo "Estrutura criada com sucesso!"