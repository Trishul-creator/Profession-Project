package com.example.jeopardy.service;

import com.example.jeopardy.model.Question;
import com.example.jeopardy.model.Team;

import java.text.Normalizer;
import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final Map<Long, Question> questionsById = new HashMap<>();
    private final Map<String, List<Question>> questionsByCategory = new HashMap<>();
    private final Map<String, List<Question>> remainingByCategory = new HashMap<>();
    private final List<Team> teams = new ArrayList<>();
    private final Random random = new Random();

    public GameService() {
        loadQuestions();
        resetRemainingQuestions();
    }

    private void loadQuestions() {
        long id = 1L;

        // Doctor questions
        addQuestion(id++, "Doctor", "What is the name of the exam you must clear to get admission in MBBS in India?",
                "NEET (National Eligibility cum Entrance Test)");
        addQuestion(id++, "Doctor", "At what minimum age can you take the NEET exam?",
                "17 years");
        addQuestion(id++, "Doctor", "How many years does it take to complete an MBBS course in India?",
                "5.5 years");
        addQuestion(id++, "Doctor", "After MBBS, how many months of internship are required before you can practice?",
                "12 months");
        addQuestion(id++, "Doctor", "What does NEET stand for?",
                "National Eligibility cum Entrance Test");
        addQuestion(id++, "Doctor", "Which class do you need to pass before you can apply for MBBS?",
                "Class 12");
        addQuestion(id++, "Doctor", "Name one entrance exam that is taken after MBBS to become a specialist (PG course).",
                "NEET-PG");
        addQuestion(id++, "Doctor", "Can a student take MBBS in India without NEET? Why or why not?",
                "No, because NEET is compulsory for MBBS admission in India");
        addQuestion(id++, "Doctor", "What is the difference between an MBBS doctor and a BDS doctor?",
                "MBBS doctors treat general medical problems; BDS doctors are dentists who treat teeth and mouth problems");
        addQuestion(id++, "Doctor", "Which government body regulates medical colleges and doctors in India?",
                "National Medical Commission");

        // Lawyer questions
        addQuestion(id++, "Lawyer", "After which class can you apply for the 5-year law course?",
                "After Class 12");
        addQuestion(id++, "Lawyer", "How many years does a BA LLB course take to complete?",
                "5 years");
        addQuestion(id++, "Lawyer", "What does LLB stand for?",
                "Bachelor of Laws");
        addQuestion(id++, "Lawyer", "What is the minimum age to appear for CLAT?",
                "Around 17 to 18 years (after Class 12)");
        addQuestion(id++, "Lawyer", "Name one famous law college in India.",
                "National Law School of India University, Bengaluru");
        addQuestion(id++, "Lawyer", "How many National Law Universities (NLUs) are there in India approximately?",
                "Around 24");
        addQuestion(id++, "Lawyer", "What are some of the main subjects studied in law?",
                "Constitutional Law, Criminal Law, Civil Law, Contract Law");
        addQuestion(id++, "Lawyer", "What is the exam you must clear to practice law in India after completing your degree?",
                "All India Bar Examination");
        addQuestion(id++, "Lawyer", "What is the professional body that regulates lawyers in India?",
                "Bar Council of India");
        addQuestion(id++, "Lawyer", "Name one type of lawyer who works in court.",
                "Litigation lawyer");

        // Fashion Designer questions
        addQuestion(id++, "Fashion Designer", "What is the name of the main entrance exam to get admission in top fashion design colleges in India?",
                "NIFT entrance exam");
        addQuestion(id++, "Fashion Designer", "Name one natural fiber used in clothing",
                "Cotton");
        addQuestion(id++, "Fashion Designer", "What is a mood board in fashion design?",
                "A collage of images, colors and ideas used to show the theme or inspiration for a design");
        addQuestion(id++, "Fashion Designer", "Who is considered the \"father of modern haute couture\"?",
                "Charles Frederick Worth");
        addQuestion(id++, "Fashion Designer", "What is a boutique?",
                "A small shop that sells fashionable clothes and accessories");
        addQuestion(id++, "Fashion Designer", "What is the purpose of mood boards in fashion design?",
                "To collect and show ideas, colors and styles before designing clothes");
        addQuestion(id++, "Fashion Designer", "After which class can you apply for a fashion design course?",
                "After Class 12");
        addQuestion(id++, "Fashion Designer", "Name one synthetic fiber used in clothing",
                "Polyester");
        addQuestion(id++, "Fashion Designer", "What is a sketch in fashion design?",
                "A drawing of a clothing design");
        addQuestion(id++, "Fashion Designer", "What is a fashion trend?",
                "A style or look that is popular at a particular time");
    }

    private void addQuestion(long id, String category, String text, String answer) {
        Question q = new Question(id, category, text, answer);
        questionsById.put(id, q);
        questionsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(q);
    }

    private void resetRemainingQuestions() {
        remainingByCategory.clear();
        for (Map.Entry<String, List<Question>> entry : questionsByCategory.entrySet()) {
            remainingByCategory.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    public List<String> getCategories() {
        return new ArrayList<>(questionsByCategory.keySet());
    }

    public void startGame(List<String> teamNames) {
        teams.clear();
        if (teamNames != null) {
            for (String name : teamNames) {
                if (name != null && !name.trim().isEmpty()) {
                    teams.add(new Team(name.trim()));
                }
            }
        }
        // fresh set of questions for each new game
        resetRemainingQuestions();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Question getRandomQuestion(String category) {
        if (category == null) {
            return null;
        }
        List<Question> remaining = remainingByCategory.get(category);
        if (remaining == null || remaining.isEmpty()) {
            List<Question> all = questionsByCategory.get(category);
            if (all == null || all.isEmpty()) {
                return null;
            }
            remaining = new ArrayList<>(all);
            remainingByCategory.put(category, remaining);
        }
        int idx = random.nextInt(remaining.size());
        Question q = remaining.remove(idx);
        return q;
    }

    public boolean checkAnswer(long questionId, String teamName, String userAnswer) {
        Question q = questionsById.get(questionId);
        if (q == null || userAnswer == null || teamName == null) {
            return false;
        }
        String normalizedExpected = normalize(q.getAnswer());
        String normalizedGiven = normalize(userAnswer);

        boolean correct = normalizedExpected.equalsIgnoreCase(normalizedGiven)
                || normalizedExpected.contains(normalizedGiven)
                || normalizedGiven.contains(normalizedExpected);

        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(teamName.trim())) {
                int score = team.getScore();
                if (correct) {
                    score += 5;
                } else {
                    score -= 1;
                }
                team.setScore(score);
                break;
            }
        }
        return correct;
    }

    private String normalize(String s) {
        String result = Normalizer.normalize(s, Normalizer.Form.NFD);
        result = result.replaceAll("[^A-Za-z0-9]+", "").toLowerCase(Locale.ROOT);
        return result;
    }

    public String getCorrectAnswer(long questionId) {
        Question q = questionsById.get(questionId);
        return q != null ? q.getAnswer() : "";
    }
}
