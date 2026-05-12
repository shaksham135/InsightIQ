# InsightIQ AI - Document & Multimedia Q&A Platform

InsightIQ is a powerful full-stack web application that allows users to upload PDF documents, audio, and video files, and interact with them using AI. It provides automated summaries, timestamp extraction for specific topics, and a clickable interface to jump to relevant parts of media files.

## 🚀 Features

- **Multilingual Processing**: Supports English, Hindi, and 99+ other languages.
- **File Uploads**: Supports PDFs, Audio (MP3, WAV), and Video (MP4).
- **AI Chat Assistant**: Ask questions about your uploaded files and get context-aware answers.
- **Smart Summarization**: Instantly generates and stores summaries to save costs and time.
- **Timestamp Navigation**: Automatically extracts topics with timestamps. Clicking a timestamp jumps to that exact moment in the player.
- **Responsive Design**: Works beautifully on mobile, tablet, and desktop screens.

## 🛠️ Tech Stack

- **Frontend**: React (Vite), Tailwind CSS, Axios.
- **Backend**: Java (Spring Boot), Maven.
- **Database**: MySQL.
- **AI APIs**: Groq (Llama 3.3 for chat/summary, Whisper-large-v3 for transcription).
- **Media Storage**: Cloudinary.

## 📋 Prerequisites

Before running the project, make sure you have the following installed:
- Node.js (v18+)
- Java JDK 17
- MySQL Server
- Maven (Optional, as Maven Wrapper is included)

## ⚙️ Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/shaksham135/InsightIQ.git
cd InsightIQ
```

### 2. Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Configure environment variables (see [Environment Variables](#-environment-variables) section below).
3. Build the project:
   ```bash
   ./mvnw clean package
   ```
4. Run the backend server:
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend will start at `http://localhost:8080`.

### 3. Frontend Setup
1. Open a new terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
   The frontend will start at `http://localhost:5173`.

---

## 🔑 Environment Variables

### Backend
Create or set the following environment variables on your system or deployment platform:

```text
GROQ_API_KEY=your_groq_api_key
CLOUDINARY_CLOUD_NAME=your_cloudinary_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

# Database (Defaults to localhost if not set)
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/insightiq?createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
```

### Frontend
Set this variable in Netlify or your production environment:
```text
VITE_API_URL=your_backend_url (e.g., https://your-app.onrender.com)
```

---

## 🔌 API Documentation

### Files
- `POST /api/files/upload`: Upload a file.
- `GET /api/files`: Get all uploaded files.
- `GET /api/files/{id}/summary`: Get the summary of a file.
- `GET /api/files/{id}/timestamp`: Get timestamps for a file.

### Chat
- `POST /api/chat`: Send a question and get an AI response based on the file context.

---

## 🧪 Testing
 
The backend has a comprehensive test suite with **95%+ code coverage** (JaCoCo). It includes unit tests for services and integration tests for controllers.
 
To run the tests and generate the coverage report:
```bash
cd backend
./mvnw clean test
```
The coverage report will be generated at `backend/target/site/jacoco/index.html`.
 
## 🐳 Docker Support

The project includes a `Dockerfile` for the backend. To build the image:
```bash
cd backend
docker build -t insightiq-backend .
```
