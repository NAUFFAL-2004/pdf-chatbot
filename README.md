# PDF Chatbot 🧾💬

A full-stack web application that allows users to upload PDF documents and interact with their content through a real-time chat interface. The system is built using **React** for the frontend and **Spring Boot** for the backend, with PDF text extraction powered by **Apache PDFBox**.

---

## ⭐ Project Highlights

- End-to-end full-stack application (React + Spring Boot)
- Real-time question answering from uploaded PDFs
- Text extraction using Apache PDFBox
- REST API based communication between frontend and backend
- Clean and interactive chat-style UI
- Modular and easily extendable architecture
- Can be upgraded to AI/LLM-based semantic search in future

---

## 🔍 Overview

The application follows a **client–server architecture** with a clear separation between the presentation layer, business logic, and data management.

### Frontend (React)
- Runs as a single-page application in the browser  
- Allows users to upload PDF files  
- Sends user questions via HTTP REST APIs  
- Displays user and bot messages in chat format  
- Shows PDF upload and connection status  

### Backend (Spring Boot)
- `/api/pdf/upload`  
  Receives a PDF file, extracts text using Apache PDFBox, splits the text into fixed-size chunks, and stores them in memory using a unique `pdfId`.

- `/api/chat`  
  Receives a question along with `pdfId`, finds the most relevant text chunk using keyword-based scoring, and returns a short answer wrapped in a `ChatResponse` JSON object.

This design allows the system to be easily extended with AI models or a persistent database in the future without changing the user interface.

---

## 🏗️ System Architecture

See the following files for detailed architecture:
- `docs/overview.pdf`
- `docs/architecture-diagram.png`

### High-Level Flow
1. User opens the React UI in the browser  
2. User uploads a PDF → frontend calls `POST /api/pdf/upload`  
3. Backend extracts and stores PDF text chunks in memory  
4. User asks a question → frontend calls `POST /api/chat` with question and `pdfId`  
5. Backend finds the best matching chunk and returns the answer  
6. Frontend displays the response as chat bubbles  

---

## 💻 User Interface Preview

Below is the working UI of the PDF Chatbot application:

<img width="1916" height="999" alt="image" src="https://github.com/user-attachments/assets/c3221e45-b737-4b4e-a83c-be3a5cda32bc" />


### UI Features
- Clean and responsive layout  
- PDF file selection and upload  
- Real-time chat interaction  
- Input box for user questions  
- Status indicator (Online / PDF Loaded)  

---

## 🛠️ Tech Stack

- **Frontend:** React (SPA), Axios / Fetch  
- **Backend:** Spring Boot (Java)  
- **PDF Processing:** Apache PDFBox  
- **Data Store:** In-memory `Map<pdfId, List<String>>`  
- **Build Tools:** Maven (backend), npm/yarn (frontend)  
- **Version Control:** Git & GitHub  

---

## 🚀 Getting Started

### Backend (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run

✅ Backend & Frontend Run URLs
Backend runs on:
http://localhost:8080

Frontend runs on:
http://localhost:3000


✅ Frontend Setup Commands
cd frontend
npm install
npm start



✅ How to Use the Application

1. Click **Choose PDF**
2. Select a PDF file
3. Click **Upload PDF**
4. Type your question in the input box
5. Click **Send** to receive the answer



