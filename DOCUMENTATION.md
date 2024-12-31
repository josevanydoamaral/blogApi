
# Documentação Completa do Projeto

## Visão Geral do Projeto

Este projeto é uma aplicação web desenvolvida em **Kotlin** usando o framework **Ktor** para o backend, 
e **Firebase** para o banco de dados. A aplicação inclui funcionalidades de autenticação, CRUD para entidades,
e suporte para comentários e likes, dependendo do contexto do projeto (Blog ou API de Livraria).

---

## Estrutura do Projeto

```plaintext
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   ├── models         # Modelos de dados (User, Post, Comment, etc.)
│   │   │   ├── routes         # Rotas e endpoints
│   │   │   ├── services       # Lógica de negócios (CRUD, autenticação, etc.)
│   │   │   ├── utils          # Funções auxiliares (validações, hashing, etc.)
│   │   │   └── Application.kt # Ponto de entrada do aplicativo
│   │   ├── resources
│   │   │   └── application.conf # Configuração do Ktor
            └── firebase-service-account.json
│   └── test                   # Testes (opcional)
├── build.gradle               # Configuração do Gradle
├── README.md                  # Resumo do projeto e instruções básicas
└── DOCUMENTATION.md           # Documentação completa
```

---

## Funcionalidades

### **Autenticação**
- Implementação de autenticação básica usando Ktor e verificação de credenciais no Firebase.
- Senhas são armazenadas com hash (BCrypt).

### **Gerenciamento de Usuários**
- Adicionar usuários: `POST /users/add`
- Atualizar usuários: `PUT /users/{id}`
- Excluir usuários: `DELETE /users/delete/{id}`
- Listar usuários: `GET /users`

### **Gerenciamento de Posts (Blog API)**
- Criar post: `POST /posts/add` (requer autenticação)
- Atualizar post: `PUT /posts/update/{id}`
- Deletar post: `DELETE /posts/delete/{id}` (somente admin)
- Listar posts: `GET /posts`

### **Comentários**
- Listar comentários de um post: `GET /posts/{id}/comments`
- Adicionar comentário a um post: `POST /posts/{id}/addComment`

### **Likes**
- Listar likes de um post: `GET /posts/{id}/likes`
- Adicionar like a um post: `POST /posts/{id}/like`
- Remover like de um post: `POST /posts/{id}/dislike`

---

## Fluxo de Dados

1. **Requisição recebida**: O cliente (front-end ou cliente REST) envia uma requisição para o endpoint.
2. **Validação e autenticação**: O servidor valida os dados enviados e verifica se o usuário está autenticado, se necessário.
3. **Processamento da lógica de negócios**: A lógica correspondente é executada (CRUD, autenticação, etc.).
4. **Interação com o Firebase**: O servidor realiza operações de leitura/escrita no banco de dados.
5. **Resposta enviada**: O servidor retorna uma resposta apropriada ao cliente, geralmente no formato JSON.

---

## Endpoints da API

| Método | Endpoint                     | Descrição                                  | Autenticação |
|--------|-------------------------------|--------------------------------------------|--------------|
| GET    | /                            | Retorna a documentação da API             | Não          |
| GET    | /posts                       | Lista todos os posts                      | Não          |
| POST   | /posts/add                   | Adiciona um novo post                     | Sim          |
| PUT    | /posts/update/{id}           | Atualiza um post existente                | Sim          |
| DELETE | /posts/delete/{id}           | Deleta um post específico                 | Sim (Admin)  |
| GET    | /users                       | Lista todos os usuários                   | Sim          |
| POST   | /users/add                   | Adiciona um novo usuário                  | Sim          |
| DELETE | /users/delete/{id}           | Exclui um usuário específico              | Sim (Admin)  |
| POST   | /posts/{id}/addComment       | Adiciona um comentário a um post          | Sim          |
| GET    | /posts/{id}/likes            | Retorna o número de likes de um post      | Sim          |
| POST   | /posts/{id}/like             | Adiciona um like a um post                | Sim          |
| POST   | /posts/{id}/dislike          | Remove um like de um post                 | Sim          |

---

## Configuração do Ambiente

### **Requisitos de Software**
- **Java 11+**: Necessário para executar o Kotlin.
- **Gradle**: Ferramenta de build.
- **IntelliJ IDEA**: IDE recomendada para desenvolvimento Kotlin.
- **Firebase Account**: Para configurar o Firestore ou Realtime Database.

### **Passos para Configuração**
1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd <nome-do-repositorio>
   ```

2. Configure as credenciais do Firebase no arquivo `application.conf`:
   ```hocon
   firebase {
       project-id = "seu-project-id"
       api-key = "sua-api-key"
   }
   ```

3. Execute o projeto:
   ```bash
   ./gradlew run
   ```

4. Acesse a API no navegador ou no Postman:
   ```
   http://localhost:8080/
   ```

---

## Testando a Aplicação

1. Use uma ferramenta como **Postman** ou **Insomnia** para testar os endpoints da API.
2. Certifique-se de enviar as credenciais no cabeçalho para os endpoints que exigem autenticação.

---

## Referências

- [Documentação do Ktor](https://ktor.io/docs/)
- [Documentação do Firebase](https://firebase.google.com/docs)
- [Documentação do Kotlin](https://kotlinlang.org/docs/home.html)
