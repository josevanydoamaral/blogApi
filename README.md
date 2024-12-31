# blogApi

# Projeto Ktor + Firebase: Blog API

Este é um projeto de API de blog desenvolvido em Kotlin usando o framework Ktor e integrado ao Firebase Firestore para armazenamento de dados.

---

## Funcionalidades

- Gerenciamento de usuários com papéis (roles) e autenticação básica (Basic Authentication).
- Senhas hash (SHA-256) para maior segurança.
- Geração automática de IDs incrementais para usuários.
- Roteamento configurado para endpoints da API.

---

## Requisitos

Antes de começar, você precisa garantir que seu ambiente atenda aos seguintes requisitos:

- **Kotlin**: Versão 1.5 ou superior.
- **JDK**: Java Development Kit versão 11 ou superior.
- **Gradle**: 7.0 ou superior.
- **Firebase**: Uma conta e um projeto configurado no [Firebase Console](https://console.firebase.google.com/).
    - Ative o **Firestore** no modo de produção ou teste.
- **Dependências**:
    - Biblioteca do Ktor.
    - Firebase Admin SDK.
    - Outras dependências estão descritas no arquivo `build.gradle.kts`.

---

## Passo a Passo para Configuração

1. **Clone o Repositório**

   Clone o projeto para a sua máquina local:

   ```bash
   git clone https://github.com/josevanydoamaral/blogApi.git
   cd blogApi
   ```

2. **Configuração do Firebase**

    - Baixe o arquivo de credenciais `serviceAccountKey.json` do Firebase.
    - Coloque o arquivo na raiz do projeto.
    - Configure o Firebase no código, apontando para este arquivo no método de inicialização.

3. **Instale as Dependências**

   Execute o seguinte comando para baixar e instalar as dependências do projeto:

   ```bash
   ./gradlew build
   ```

4. **Inicie o Projeto**

   Para rodar o servidor Ktor, execute:

   ```bash
   ./gradlew run
   ```

   O servidor estará disponível em `http://localhost:8080`.

---

## Endereços Principais

- `POST /users`: Adiciona um novo usuário.
- `GET /users`: Obtém todos os usuários.
- `POST /login`: Valida as credenciais e autentica o usuário.

---

## Dicas de Desenvolvimento

- Certifique-se de usar senhas fortes e manter as credenciais de administrador seguras.
- Utilize uma ferramenta como **Postman** ou **cURL** para testar os endpoints da API.
- Para usar o Firestore em produção, ajuste as regras de segurança no [Firebase Console](https://console.firebase.google.com/).

---

# Endpoints API - BlogApi

Este projeto implementa uma API que fornece funcionalidades para gerenciar posts, usuários, comentários e likes. Abaixo, você encontrará uma descrição detalhada dos endpoints disponíveis.

## **Endpoints Públicos**

### GET /
- **Descrição:** Endpoint raiz. Pode ser usado para verificar o status da aplicação.
- **Resposta:** Retorna o conteúdo do README ou uma mensagem de boas-vindas.

### GET /posts
- **Descrição:** Retorna uma lista de todos os posts disponíveis.
- **Resposta:** Lista de posts no formato JSON.

---

## **Endpoints Autenticados**

### **POST /posts/add**
- **Descrição:** Adiciona um novo post.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários com a role `EDITOR` podem acessar.
- **Corpo da Requisição:**
    ```json
    {
    "id": 2,
    "title": "Tecnologia e inovação: tendências para 2025",
    "content": "Com o avanço constante da tecnologia, é importante acompanhar as tendências para o futuro. O ano de 2025 promete inovações em inteligência artificial, automação e sustentabilidade."
    }
    ```

### **DELETE /posts/delete/{id}**
- **Descrição:** Remove um post existente.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários com a role `ADMIN` podem acessar.

### **PUT /posts/update/{id}**
- **Descrição:** Atualiza um post existente.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.
- **Corpo da Requisição:**
    ```json
    {
        "id": 1,
        "title": "A importância da alimentação saudável para a mente e corpo",
        "content": "Alimentar-se bem não só melhora o corpo, mas também é crucial para a saúde mental. Alimentos ricos em nutrientes essenciais ajudam a melhorar o foco e o bem-estar emocional."
    }
    ```

---

## **Endpoints de Comentários**

### **GET /posts/{id}/comments**
- **Descrição:** Retorna os comentários de um post.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.

### **POST /posts/{id}/addComment**
- **Descrição:** Adiciona um comentário a um post.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.
- **Corpo da Requisição:**
    ```json
    {
        "id": 1,
        "content": "Vamos fazer um quarto teste",
        "author": "Filomena"
    }
    ```

---

## **Endpoints de Likes**

### **GET /posts/{id}/likes**
- **Descrição:** Retorna o número de likes de um post.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.

### **POST /posts/{id}/like**
- **Descrição:** Adiciona um like a um post.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.

### **POST /posts/{id}/dislike**
- **Descrição:** Remove um like de um post.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.

---

## **Endpoints de Usuários**

### **GET /users**
- **Descrição:** Retorna uma lista de todos os usuários.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.

### **POST /users/add**
- **Descrição:** Adiciona um novo usuário.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.
- **Corpo da Requisição:**
    ```json
    {
    "username" : "dome",
    "password" : "jose",
    "role" : "EDITOR",
    "isActive" : true
    }
    ```

### **DELETE /users/delete/{id}**
- **Descrição:** Remove um usuário.
- **Autenticação:** Basic Auth
- **Permissão:** Apenas usuários autenticados.

---

## Autenticação

- **Tipo de Autenticação:** Basic Auth
- **Credenciais:**
    - Nome de Usuário: `{username}`
    - Senha: `{password}`

---

## Erros Comuns

### 401 Unauthorized
- **Descrição:** Usuário não autenticado ou credenciais inválidas.

### 403 Forbidden
- **Descrição:** Usuário autenticado, mas sem permissão para acessar o recurso.

### 500 Internal Server Error
- **Descrição:** Erro interno no servidor.

---

## Contato

Para dúvidas ou suporte, entre em contato com o administrador do projeto.

