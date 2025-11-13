# Profession Project

This project is a Jeopardy-style game that features 3 professions and asks random questions about each.  
**Main languages:** Java, JavaScript, CSS, HTML  
**Author:** [Trishul-creator](https://github.com/Trishul-creator)

## Repository Structure

- `jeopardybackend/`: Java backend REST API (Spring Boot, Maven)
- `jeopardyfrontend/`: JavaScript frontend (React + CSS)

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/Trishul-creator/Profession-Project.git
cd Profession-Project
```

---

### 2. Start the Backend

Ensure you have Java (JDK 17+) and Maven installed.

```bash
cd jeopardybackend
mvn clean install
mvn spring-boot:run
```

The backend server should launch at `http://localhost:8080`.

---

### 3. Start the Frontend

In a new terminal, ensure you have Node.js (v16+) and npm installed.

```bash
cd jeopardyfrontend
npm install
npm start
```

The React app will start (typically at `http://localhost:3000`) and will connect to the backend.

---

## Usage

1. Open your web browser at [http://localhost:3000](http://localhost:3000)
2. Play the Jeopardy game, choose a profession, and answer random questions!

---

## Contributing

Currently, this project is maintained solely by [Trishul-creator](https://github.com/Trishul-creator).  
Feel free to fork and submit pull requests!

---

## License

[MIT](./LICENSE) (or your actual license, if any)
